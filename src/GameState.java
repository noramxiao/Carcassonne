import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Scanner;
import java.util.Set;

public class GameState {
	
	private Board gameBoard;
	private boolean start, board, meeple, boardDisplay, end;
	private boolean bot, botTurn;
	private Player[] players;
	private int whoseTurn;
	private ArrayList<Tile> tiles;
	private TreeSet<Coordinate> suggested; //all coordinates with legal position
	private Tile currentTile; //stores Tile being placed
	private int currentX, currentY; //currentTile’s location
	private boolean isLegal; //whether currentTile is legal
	private TreeMap<Integer, Feat> featureLabel; //key is the label is displayed in the panel, value is the Feature located at the edge
	private int currentLabel; //this is the number that will be incremented by 1 each time a feature is labeled and the number is painted.
	private Feat featureSelected; //Feature the player has selected
	private int errorMsg;
	private boolean discard; //if there is a legal position/rotation for a river tile. if true, tile must be discarded
	
	public GameState() {
		start = true; board = false; meeple = false; boardDisplay = false; end = false;
		errorMsg = 0;
		
		//READ IN TILEDATA AND INSTANTIATE TILES
		ArrayList<Tile> riverTiles = new ArrayList<Tile>();
		ArrayList<Tile> regularTiles = new ArrayList<Tile>();
		
		String tileData = getTileData();
		Scanner input = new Scanner(tileData);
		
		for(int i = 0; i < 84; i++) {
			String[] tileInfo = input.nextLine().split(" ");
			Tile t = new Tile(i);
			int f = Integer.parseInt(tileInfo[1]);
			
			//add every feature to tile
			String[] featureInfo;
			for(int j=0; j<f; j++) {
				featureInfo = input.nextLine().split(" ");
				
				String featureType = featureInfo[0];
				if(featureType.equals("field")) {
					int[] edges = new int[featureInfo.length-1];
					for(int k=0; k<edges.length; k++) {
						edges[k] = Integer.parseInt(featureInfo[k+1]);
					}
					t.addFarm(edges);
				} else if(featureType.equals("city")) {
					int[] edges = new int[featureInfo.length-2];
					for(int k=0; k<edges.length; k++) {
						edges[k] = Integer.parseInt(featureInfo[k+1]);
					}
					boolean coatOfArms = false;
					if(featureInfo[featureInfo.length-1].equals("true")) {
						coatOfArms = true;
					}
					t.addCity(edges, coatOfArms);
				} else if(featureType.equals("road")) {
					t.addRoad(Integer.parseInt(featureInfo[1]), Integer.parseInt(featureInfo[2]));
				} else if(featureType.equals("river")) {
					t.addRiver(Integer.parseInt(featureInfo[1]), Integer.parseInt(featureInfo[2]));
				} else {
					t.addMonastery();
				}
			}
			
			//if river tile, add to riverTiles. else, add to regularTiles
			if(i<12) {
				riverTiles.add(t);
			} else {
				regularTiles.add(t);
			}
		}
		
		//SHUFFLE ARRAYLIST OF TILES
		tiles = shuffle(riverTiles, regularTiles);
	}
	private ArrayList<Tile> shuffle(ArrayList<Tile> river, ArrayList<Tile> reg) {
		ArrayList<Tile> shuffled = new ArrayList<Tile>();
		Tile riverSource = river.remove(0);
		Tile riverEnd = river.remove(0);
		
//		//add river tiles in correct order (source -> shuffled river tiles -> end)
		shuffled.add(riverSource);
		Collections.shuffle(river);
		for(Tile t: river) {
			shuffled.add(t);
		}
		shuffled.add(riverEnd);
		
		//shuffle and add all regular tiles
		Collections.shuffle(reg);
		for(Tile t: reg) {
			shuffled.add(t);
		}
		return shuffled;
	}
	private void instantiate(boolean bot) {
		//INSTANTIATE ATTRIBUTES
		if(bot) {
			players = new Player[2];
			players[0] = new Player(0);
			players[1] = new Bot(1, this);
			whoseTurn = 0;
		} else {
			players = new Player[4];
			for(int i = 0; i < 4; i++) {
				players[i] = new Player(i);
			}
			whoseTurn = (int) (Math.random() * players.length);
		}
		gameBoard = new Board(players);
		if(bot) { //add instantiated board to bot
			Bot b = (Bot) players[1];
			b.addBoard(gameBoard);
		}
		currentTile = tiles.remove(0);
		gameBoard.addTile(currentTile); //add river source tile
		gameBoard.incorporateTile(currentTile);
		currentTile = tiles.remove(0); //add next random river tile
		currentX = 0; currentY = -1;
		featureLabel = new TreeMap<Integer, Feat>();
		suggested = new TreeSet<Coordinate>();
		suggested = gameBoard.findSuggested(currentTile);
	}
	

	private void nextTurn(boolean disc) {
		int numLeft = getNumLeft();
		if (!disc) {
			gameBoard.addTile(currentTile);
		}
		if (numLeft > 0) {
			//RESET EVERYTHING
			whoseTurn++;
			whoseTurn %= players.length;
			currentTile = tiles.remove(0);
			suggested = gameBoard.findSuggested(currentTile);
			meeple = false;
			board = true;
			if(bot && whoseTurn==1) {
				botTurn = true;
				botNextTurn();
			} else if (bot){
				botTurn = false;
			}
			if(!bot || (bot && !botTurn)){
				if(numLeft >= 73) {
					currentX = suggested.first().x();
					currentY = suggested.first().y();
				} else {
					currentX = (gameBoard.getMinX()+gameBoard.getMaxX())/2;
					currentY = (gameBoard.getMinY()+gameBoard.getMaxY())/2;
				}
				isLegal = gameBoard.checkLegal(currentTile, currentX, currentY);
			}

			//CHECK RIVER TILE
			if (numLeft <= 82 && numLeft >= 73) {
				checkRiverTile();
			} else if (numLeft < 73) {
				discard = suggested.size()==0;
			}
			
			//SET ERROR MESSAGE IF TILE NEEDS TO BE DISCARDED
			if (discard) {
				errorMsg = 1;
			}
		} else {
			endGame();
		}
	}
	private void botNextTurn() {
		Bot b = (Bot) players[1];
		b.selectTileLoc();
	}
	private void checkRiverTile() {
		RiverSystem syst = gameBoard.getRiverSyst();
		int x = syst.getLegalX();
		int y = syst.getLegalY();
		discard = true;
		for (int r = 0; r < 4; r++) {
			if (gameBoard.checkLegal(currentTile, x, y)) {
				discard = false;
			}
			currentTile.rotate();
		}
	}
	private void endGame() {
		//GAME ENDS
		errorMsg = 8;
		gameBoard.endGameScore();
		meeple = false;
		end = true;
		board=false;
		currentTile=null;
	}
	
	//KEY INPUT METHODS
	public void upKey() {
		if(currentTile!=null && board) {
			currentY++;
			currentY = Math.min(currentY, gameBoard.getMaxY()+1);
			isLegal = gameBoard.checkLegal(currentTile, currentX, currentY);
		}
	}
	public void downKey() {
		if(currentTile!=null && board) {
			currentY--;
			currentY = Math.max(currentY, gameBoard.getMinY()-1);
			isLegal = gameBoard.checkLegal(currentTile, currentX, currentY);
		}
	}
	public void rightKey() {
		if(currentTile!=null && board) {
			currentX++;
			currentX = Math.min(currentX, gameBoard.getMaxX()+1);
			isLegal = gameBoard.checkLegal(currentTile, currentX, currentY);
		}
	}
	public void leftKey() {
		if(currentTile!=null && board) {
			currentX--;
			currentX = Math.max(currentX, gameBoard.getMinX()-1);
			isLegal = gameBoard.checkLegal(currentTile, currentX, currentY);
		}
	}
	public void enterKey() {
		if(errorMsg!=0) {
			errorMsg = 0;
		} else if (board && !isLegal) { //if illegal tile is attempted to place
			errorMsg = 2;
		} else if(start) { //move from start page to game board
			start = false;
			board = true;
			instantiate(bot);
		} else if(board && isLegal && currentTile !=null && errorMsg==0) { //move from game board to meeple placing
			currentTile.setX(currentX);
			currentTile.setY(currentY);
			board = false; meeple = true;
			gameBoard.incorporateTile(currentTile);
			
			for(Player p: players) { //reset any players whose score just changed
				p.resetScoreChanged();
			}
			
			if(getNumLeft()==82) { //set error message to show placing meeple explanation
				errorMsg = 4;
			}
		} else if(meeple && errorMsg==0) {
			if(featureSelected!=null) { //meeple placing to game board
				featureSelected.setMeeple(whoseTurn);
				featureSelected = null;
				featureLabel = new TreeMap<Integer, Feat>();
				players[whoseTurn].setNumMeeples(players[whoseTurn].getNumMeeples()-1);
			}
			if(botTurn) {
				((Bot)players[1]).setMeepleSelected(false);
			}
			String s = gameBoard.inGameScore(currentTile);
			if(!s.equals("")) {
				errorMsg = Integer.parseInt(s);
			}
			nextTurn(false);
			if(getNumLeft()==81 && botTurn) {
				errorMsg = 9;
			}
		}
	}
	public void rotateKey() {
		if(board && currentTile != null) {
			currentTile.rotate();
			isLegal = gameBoard.checkLegal(currentTile, currentX, currentY);
		}
	}
	public void numberKey(int i) {
		if(meeple && players[whoseTurn].getNumMeeples()==0) { //if player runs out of meeples
			errorMsg = 3;
		} else if(meeple && featureLabel.keySet().contains(i) && players[whoseTurn].getNumMeeples()>0) { //if it is a viable number and player has at least 1 meeple
			//cases— 1. no meeple is selected 2. discarding already selected meeple 3.meeple is selected, switch to other meeple
			if(featureSelected==null) { //case 1
				featureSelected = featureLabel.get(i);
				featureSelected.setIsSelected(true);
			} else if(featureSelected != null) {
				if(featureLabel.get(i)==featureSelected) { //case 2
					featureSelected.setIsSelected(false);
					featureSelected = null;
				} else { //case 3
					featureSelected.setIsSelected(false);
					featureSelected = featureLabel.get(i);
					featureSelected.setIsSelected(true);
				}
				
			}
		}
	}
	public void discardKey() {
		if(discard && board) {
			nextTurn(true);
		}
	}
	public void BKey() {
		if(start) {
			if(bot) {
				bot = false;
			} else {
				bot = true;
			}
		}
		else if(meeple) {
			meeple = false;
			boardDisplay = true;
		} else if(boardDisplay) {
			boardDisplay = false;
			meeple = true;
		}
	}
	public void scoreKey() {
		if(!start) {
			gameBoard.endGameScore();
			errorMsg = 8;
			board = false;
			boardDisplay = false;
			meeple = false;
			end = true;
			currentTile = null;
		}
	}

	//MODIFIERS
	public void addLabel(Feat f) {
		if(!f.systemClaimed()) {
			featureLabel.put(currentLabel, f);
		}
		currentLabel++;
	}
	public void resetLabel() {currentLabel = 1;}
	
	//BOT MODIFIERS
	public void setCurrentX(int x) {currentX = x;}
	public void setCurrentY(int y) {currentY = y;}
	public void selectFeature(int i) {
		featureSelected = featureLabel.get(i);
		featureSelected.setIsSelected(true);
	}
	
	//ACCESSORS
	public boolean botTurn() {return botTurn;}
	//ACCESSORS
	public boolean bot() {return bot;}
	public int getWinner() {
		TreeSet<Player> playerScores = getPlayerSet();
 		return playerScores.last().getID();
	}
	public TreeSet<Player> getPlayerSet() {
		TreeSet<Player> playerScores = new TreeSet<Player>();
		for(Player p: players) {
			playerScores.add(p);
		}
 		return playerScores;
	}
	public Player getPlayer(int i) {return players[i];}
	public int getNumLeft(){return tiles.size();}
	public String getMeepleMessage() {
		if(featureSelected!=null) {
			String name = featureSelected.getName();
			return name.toUpperCase();
		}
		return "None";
	}
	public TreeSet<Coordinate> getSuggested() {return suggested;}
	public Tile getCurrentTile() {return currentTile;}
	public int getWhoseTurn() {return whoseTurn;}
	public TreeSet<Tile> getPlacedTiles() {return gameBoard.getPlacedTiles();}
	public int getTileSize() {return gameBoard.getTileSize();}
	public int getCenterX() {return gameBoard.getCenterX();}
	public int getCenterY() {return gameBoard.getCenterY();}
	public boolean isLegal() {return isLegal;}
	public int getCurrentX() {return currentX;}
	public int getCurrentY() {return currentY;}
	public int getCurrentLabel() {return currentLabel;}
	public Set<Integer> getFeatureLabels() {return featureLabel.keySet();}
	public int getErrorMsg() {return errorMsg;}
	public boolean getStart() {return start;}
	public boolean getBoard() {return board;}
	public boolean getMeeple() {return meeple;}
	public boolean getBoardDisplay() {return boardDisplay;}
	public boolean getEnd() {return end;}
	private String getTileData() {
		return "0 2\n"
				+ "river 0 8\n"
				+ "field 1 2 3 4 5 6 7 9 10 11 12\n"
				+ "1 2\n"
				+ "river 0 8\n"
				+ "field 1 2 3 4 5 6 7 9 10 11 12\n"
				+ "2 3\n"
				+ "river 5 11\n"
				+ "field 1 2 3 4 12\n"
				+ "field 6 7 8 9 10\n"
				+ "3 3\n"
				+ "river 5 11\n"
				+ "field 1 2 3 4 12\n"
				+ "field 6 7 8 9 10\n"
				+ "4 3\n"
				+ "river 8 11\n"
				+ "field 1 2 3 4 5 6 7 12\n"
				+ "field 9 10\n"
				+ "5 3\n"
				+ "river 8 11\n"
				+ "field 1 2 3 4 5 6 7 12\n"
				+ "field 9 10\n"
				+ "6 6\n"
				+ "monastery\n"
				+ "river 5 11\n"
				+ "road 0 8\n"
				+ "field 6 7\n"
				+ "field 1 2 3 4 12\n"
				+ "field 9 10\n"
				+ "7 6 \n"
				+ "road 2 8\n"
				+ "river 5 11\n"
				+ "field 3 4\n"
				+ "field 1 12\n"
				+ "field 6 7\n"
				+ "field 9 10\n"
				+ "8 5\n"
				+ "road 2 5\n"
				+ "river 8 11\n"
				+ "field 1 6 7 12\n"
				+ "field 3 4\n"
				+ "field 9 10\n"
				+ "9 7\n"
				+ "road 0 8\n"
				+ "river 5 11\n"
				+ "city 1 2 3 false\n"
				+ "field 4\n"
				+ "field 6 7\n"
				+ "field 9 10\n"
				+ "field 12\n"
				+ "10 5\n"
				+ "river 5 11\n"
				+ "city 1 2 3 false\n"
				+ "city 7 8 9 false\n"
				+ "field 4 12\n"
				+ "field 6 10\n"
				+ "11 4\n"
				+ "river 8 11\n"
				+ "city 1 2 3 4 5 6 false\n"
				+ "field 7 12\n"
				+ "field 9 10\n"
				+ "12 2\n"
				+ "monastery \n"
				+ "field 1 2 3 4 5 6 7 8 9 10 11 12\n"
				+ "13 2\n"
				+ "monastery \n"
				+ "field 1 2 3 4 5 6 7 8 9 10 11 12\n"
				+ "14 2\n"
				+ "monastery \n"
				+ "field 1 2 3 4 5 6 7 8 9 10 11 12\n"
				+ "15 2\n"
				+ "monastery \n"
				+ "field 1 2 3 4 5 6 7 8 9 10 11 12\n"
				+ "16 3\n"
				+ "monastery\n"
				+ "road 0 8\n"
				+ "field 1 2 3 4 5 6 7 9 10 11 12\n"
				+ "17 3\n"
				+ "monastery\n"
				+ "road 0 8\n"
				+ "field 1 2 3 4 5 6 7 9 10 11 12\n"
				+ "18 3\n"
				+ "road 5 11\n"
				+ "field 1 2 3 4 12\n"
				+ "field 6 7 8 9 10\n"
				+ "19 3 \n"
				+ "road 5 11\n"
				+ "field 1 2 3 4 12\n"
				+ "field 6 7 8 9 10\n"
				+ "20 3\n"
				+ "road 5 11\n"
				+ "field 1 2 3 4 12\n"
				+ "field 6 7 8 9 10\n"
				+ "21 3\n"
				+ "road 5 11\n"
				+ "field 1 2 3 4 12\n"
				+ "field 6 7 8 9 10\n"
				+ "22 3\n"
				+ "road 5 11\n"
				+ "field 1 2 3 4 12\n"
				+ "field 6 7 8 9 10\n"
				+ "23 3\n"
				+ "road 5 11\n"
				+ "field 1 2 3 4 12\n"
				+ "field 6 7 8 9 10\n"
				+ "24 3\n"
				+ "road 5 11\n"
				+ "field 1 2 3 4 12\n"
				+ "field 6 7 8 9 10\n"
				+ "25 3\n"
				+ "road 5 11\n"
				+ "field 1 2 3 4 12\n"
				+ "field 6 7 8 9 10\n"
				+ "26 3\n"
				+ "road 8 11\n"
				+ "field 1 2 3 4 5 6 7 12\n"
				+ "field 9 10\n"
				+ "27 3\n"
				+ "road 8 11\n"
				+ "field 1 2 3 4 5 6 7 12\n"
				+ "field 9 10\n"
				+ "28 3\n"
				+ "road 8 11\n"
				+ "field 1 2 3 4 5 6 7 12\n"
				+ "field 9 10\n"
				+ "29 3\n"
				+ "road 8 11\n"
				+ "field 1 2 3 4 5 6 7 12\n"
				+ "field 9 10\n"
				+ "30 3\n"
				+ "road 8 11\n"
				+ "field 1 2 3 4 5 6 7 12\n"
				+ "field 9 10\n"
				+ "31 3\n"
				+ "road 8 11\n"
				+ "field 1 2 3 4 5 6 7 12\n"
				+ "field 9 10\n"
				+ "32 3\n"
				+ "road 8 11\n"
				+ "field 1 2 3 4 5 6 7 12\n"
				+ "field 9 10\n"
				+ "33 3\n"
				+ "road 8 11\n"
				+ "field 1 2 3 4 5 6 7 12\n"
				+ "field 9 10\n"
				+ "34 3\n"
				+ "road 8 11\n"
				+ "field 1 2 3 4 5 6 7 12\n"
				+ "field 9 10\n"
				+ "35 6\n"
				+ "road 0 5\n"
				+ "road 0 8\n"
				+ "road 0 11\n"
				+ "field 1 2 3 4 12\n"
				+ "field 6 7\n"
				+ "field 9 10\n"
				+ "36 6\n"
				+ "road 0 5\n"
				+ "road 0 8\n"
				+ "road 0 11\n"
				+ "field 1 2 3 4 12\n"
				+ "field 6 7\n"
				+ "field 9 10\n"
				+ "37 6\n"
				+ "road 0 5\n"
				+ "road 0 8\n"
				+ "road 0 11\n"
				+ "field 1 2 3 4 12\n"
				+ "field 6 7\n"
				+ "field 9 10\n"
				+ "38 6\n"
				+ "road 0 5\n"
				+ "road 0 8\n"
				+ "road 0 11\n"
				+ "field 1 2 3 4 12\n"
				+ "field 6 7\n"
				+ "field 9 10\n"
				+ "39 8\n"
				+ "road 0 2\n"
				+ "road 0 5\n"
				+ "road 0 8\n"
				+ "road 0 11\n"
				+ "field 1 12\n"
				+ "field 3 4\n"
				+ "field 6 7\n"
				+ "field 9 10\n"
				+ "40 2\n"
				+ "city 1 2 3 false\n"
				+ "field 4 5 6 7 8 9 10 11 12\n"
				+ "41 2\n"
				+ "city 1 2 3 false\n"
				+ "field 4 5 6 7 8 9 10 11 12\n"
				+ "42 2\n"
				+ "city 1 2 3 false\n"
				+ "field 4 5 6 7 8 9 10 11 12\n"
				+ "43 2\n"
				+ "city 1 2 3 false\n"
				+ "field 4 5 6 7 8 9 10 11 12\n"
				+ "44 2\n"
				+ "city 1 2 3 false\n"
				+ "field 4 5 6 7 8 9 10 11 12\n"
				+ "45 4\n"
				+ "road 5 11\n"
				+ "city 1 2 3 false\n"
				+ "field 4 12\n"
				+ "field 6 7 8 9 10\n"
				+ "46 4\n"
				+ "road 5 11\n"
				+ "city 1 2 3 false\n"
				+ "field 4 12\n"
				+ "field 6 7 8 9 10\n"
				+ "47 4\n"
				+ "road 5 11\n"
				+ "city 1 2 3 false\n"
				+ "field 4 12\n"
				+ "field 6 7 8 9 10\n"
				+ "48 4\n"
				+ "road 5 11\n"
				+ "city 1 2 3 false\n"
				+ "field 4 12\n"
				+ "field 6 7 8 9 10\n"
				+ "49 4\n"
				+ "road 8 11\n"
				+ "city 1 2 3 false\n"
				+ "field 4 5 6 7 12\n"
				+ "field 9 10\n"
				+ "50 4\n"
				+ "road 8 11\n"
				+ "city 1 2 3 false\n"
				+ "field 4 5 6 7 12\n"
				+ "field 9 10\n"
				+ "51 4\n"
				+ "road 8 11\n"
				+ "city 1 2 3 false\n"
				+ "field 4 5 6 7 12\n"
				+ "field 9 10\n"
				+ "52 4\n"
				+ "road 5 8\n"
				+ "city 1 2 3 false\n"
				+ "field 4 9 10 11 12\n"
				+ "field 6 7\n"
				+ "53 4\n"
				+ "road 5 8\n"
				+ "city 1 2 3 false\n"
				+ "field 4 9 10 11 12\n"
				+ "field 6 7\n"
				+ "54 4\n"
				+ "road 5 8\n"
				+ "city 1 2 3 false\n"
				+ "field 4 9 10 11 12\n"
				+ "field 6 7\n"
				+ "55 7\n"
				+ "road 0 5\n"
				+ "road 0 8\n"
				+ "road 0 11\n"
				+ "city 1 2 3 false\n"
				+ "field 4 12\n"
				+ "field 6 7\n"
				+ "field 9 10\n"
				+ "56 7\n"
				+ "road 0 5\n"
				+ "road 0 8\n"
				+ "road 0 11\n"
				+ "city 1 2 3 false\n"
				+ "field 4 12\n"
				+ "field 6 7\n"
				+ "field 9 10\n"
				+ "57 7\n"
				+ "road 0 5\n"
				+ "road 0 8\n"
				+ "road 0 11\n"
				+ "city 1 2 3 false\n"
				+ "field 4 12\n"
				+ "field 6 7\n"
				+ "field 9 10\n"
				+ "58 3\n"
				+ "city 4 5 6 10 11 12 false\n"
				+ "field 1 2 3\n"
				+ "field 7 8 9\n"
				+ "59 2\n"
				+ "city 1 2 3 4 5 6 false\n"
				+ "field 7 8 9 10 11 12\n"
				+ "60 2\n"
				+ "city 1 2 3 4 5 6 false\n"
				+ "field 7 8 9 10 11 12\n"
				+ "61 2\n"
				+ "city 1 2 3 4 5 6 false\n"
				+ "field 7 8 9 10 11 12\n"
				+ "62 3\n"
				+ "city 1 2 3 false\n"
				+ "city 7 8 9 false\n"
				+ "field 4 5 6 10 11 12\n"
				+ "63 3 \n"
				+ "city 1 2 3 false\n"
				+ "city 7 8 9 false\n"
				+ "field 4 5 6 10 11 12\n"
				+ "64 3\n"
				+ "city 1 2 3 false\n"
				+ "city 7 8 9 false\n"
				+ "field 4 5 6 10 11 12\n"
				+ "65 3\n"
				+ "city 1 2 3 false\n"
				+ "city 4 5 6 false\n"
				+ "field 7 8 9 10 11 12\n"
				+ "66 3\n"
				+ "city 1 2 3 false\n"
				+ "city 4 5 6 false\n"
				+ "field 7 8 9 10 11 12\n"
				+ "67 4\n"
				+ "road 8 11\n"
				+ "city 1 2 3 4 5 6 false\n"
				+ "field 7 12\n"
				+ "field 9 10\n"
				+ "68 4\n"
				+ "road 8 11\n"
				+ "city 1 2 3 4 5 6 false\n"
				+ "field 7 12\n"
				+ "field 9 10\n"
				+ "69 4\n"
				+ "road 8 11\n"
				+ "city 1 2 3 4 5 6 false\n"
				+ "field 7 12\n"
				+ "field 9 10\n"
				+ "70 3\n"
				+ "city 4 5 6 10 11 12 true\n"
				+ "field 1 2 3 \n"
				+ "field 7 8 9\n"
				+ "71 3\n"
				+ "city 4 5 6 10 11 12 true\n"
				+ "field 1 2 3 \n"
				+ "field 7 8 9\n"
				+ "72 2\n"
				+ "city 1 2 3 4 5 6 true\n"
				+ "field 7 8 9 10 11 12\n"
				+ "73 2\n"
				+ "city 1 2 3 4 5 6 true\n"
				+ "field 7 8 9 10 11 12\n"
				+ "74 4\n"
				+ "road 8 11\n"
				+ "city 1 2 3 4 5 6 true\n"
				+ "field 7 12\n"
				+ "field 9 10\n"
				+ "75 4\n"
				+ "road 8 11\n"
				+ "city 1 2 3 4 5 6 true\n"
				+ "field 7 12\n"
				+ "field 9 10\n"
				+ "76 2\n"
				+ "city 1 2 3 4 5 6 10 11 12 false\n"
				+ "field 7 8 9\n"
				+ "77 2\n"
				+ "city 1 2 3 4 5 6 10 11 12 false\n"
				+ "field 7 8 9\n"
				+ "78 2\n"
				+ "city 1 2 3 4 5 6 10 11 12 false\n"
				+ "field 7 8 9\n"
				+ "79 4\n"
				+ "road 0 8\n"
				+ "city 1 2 3 4 5 6 10 11 12 false\n"
				+ "field 7\n"
				+ "field 9\n"
				+ "80 2\n"
				+ "city 1 2 3 4 5 6 10 11 12 true\n"
				+ "field 7 8 9\n"
				+ "81 4\n"
				+ "road 0 8\n"
				+ "city 1 2 3 4 5 6 10 11 12 true\n"
				+ "field 7\n"
				+ "field 9\n"
				+ "82 4\n"
				+ "road 0 8\n"
				+ "city 1 2 3 4 5 6 10 11 12 true\n"
				+ "field 7\n"
				+ "field 9\n"
				+ "83 1\n"
				+ "city 1 2 3 4 5 6 7 8 9 10 11 12 true";
	}
 }
