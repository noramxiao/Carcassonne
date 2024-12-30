import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

public class Panel extends JPanel implements KeyListener {
	private GameState gs;
	private BufferedImage[] tileImgs;
	private BufferedImage title;
	
	public Panel() {
		gs = new GameState();
		tileImgs = new BufferedImage[84];
		addKeyListener(this);
		
		try {
			title = ImageIO.read(Panel.class.getResource("/resources/title.png"));
			for(int i = 0; i < tileImgs.length; i++)
			{
				tileImgs[i] = ImageIO.read(Panel.class.getResource("/resources/"+i+".JPG"));
			}
		}
		catch (Exception E) {
			return;
		}
	}
	
	public void paint(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		g.setColor(Color.white);
		g.fillRect(0,0,getWidth(),getHeight());
		g.setColor(Color.black);
		g2.setStroke(new BasicStroke(8));
		g.drawRect(0, 0, getWidth(), getHeight());
		if(gs.getStart()) {
			paintStart(g);
		}
		else if(gs.getBoard()) {
			paintBoard(g);
		}
		else if(gs.getMeeple()) {
			paintMeeple(g);
		}
		else if(gs.getBoardDisplay()) {
			paintBoardDisplay(g);
		}
		else if(gs.getEnd()) {
			paintEnd(g);
		}
		
		if(gs.getErrorMsg()!=0) {
			paintErrorMsg(g);
		}
	}
	
 	private void paintStart(Graphics g) {
 		g.drawImage(title, 248, 55, 576, 161, null); //title image
 		
 		Graphics2D g2 = (Graphics2D) g;
 		
 		//instructions
 		g2.setStroke(new BasicStroke(1));
 		g.setColor(Color.black);
 		g.drawRect(83, 248, 935, 395); //instruction box
 		g.setFont(new Font("default",Font.BOLD, 14));
 		
 		g2.setStroke(new BasicStroke(8));
 		g.setColor(Color.red);
 		g.fillOval(466, 340, 45, 45);
 		g.drawOval(466, 279, 42, 42);
 		
 		g2.setStroke(new BasicStroke(1));
 		g.setColor(Color.black);
 		g.drawString("A, S, D, W keys", 405, 430);
 		g.drawString("R key", 472, 480);
 		g.drawString("ENTER key", 435, 525);
 		g.drawString("BACKSPACE key", 400, 570);
 		g.drawString("NO CLICKING", 497, 618);
 		
 		//select bot or not
 		if(gs.bot()) {
 			g.setColor(Color.black);
 			g.drawRoundRect(300, 682, 150, 50, 20, 20);
 			g.setColor(Color.red);
 			g.drawString("AI: ON", 340, 712);
 		} else {
 			g.setColor(Color.black);
 			g.drawRoundRect(300, 682, 150, 50, 20, 20);
 			g.drawString("AI: OFF", 340, 712);
 		}
 		g.setColor(Color.black);
 		g.setFont(new Font("default",Font.PLAIN, 14));
 		g.drawString("Press B to play", 310, 750);
 		g.drawString("against computer", 310, 770);
 		
 		 //press enter to play box
 		g.setFont(new Font("default",Font.BOLD, 14));
 		g.setColor(Color.black);
 		g.fillRoundRect(505, 682, 220, 50,20,20);
 		g.setColor(Color.white);
 		g.drawString("Press ENTER to Play", 545, 712);
 		g.setColor(Color.black);
 		
 		g.drawLine(550, 268, 550, 590);
 		g.setFont(new Font("default",Font.PLAIN, 14));
 		g.drawString("Farm Meeple", 578, 303);
 		g.drawString("Regular Meeple", 578, 376);
 		g.drawString("Move tile", 578, 430);
 		g.drawString("Rotate tile", 578, 480);
 		g.drawString("Place tile", 578, 525);
 		g.drawString("Discard tile", 578, 570);
 	}
 	
 	private void paintBoard(Graphics g) {
 		paintInfo(g);
 		paintTiles(g);
 	}
 	
 	private void paintMeeple(Graphics g) {
 		paintInfo(g);
 		Graphics2D g2 = (Graphics2D) g;
 		Tile t = gs.getCurrentTile();
 		g.drawImage(tileImgs[t.getID()], 436, 144, 534, 534, null);
 		
 		//paint switch to gameboard box
 		if(!gs.botTurn()) {
 			g.setColor(Color.black);
 	 		g.fillRoundRect(746, 7, 220, 55,20,20);
 	 		g.setColor(Color.white);
 	 		g.setFont(new Font("default", Font.PLAIN, 16));
 	 		g.drawString("Press B to view Board", 770, 40);
 		}
 		
 		//paint Feature labels
 		Feat feat; int tileRotation; int edge;
 		int meepleSize = 106;
 		int x = 438;
 		int y = 146;
 		gs.resetLabel();
 		for(int i = 0; i < 13; i++) {
 			feat = t.getFeature(i);
 			
 			if(feat!=null && !feat.isNumberPainted() && !feat.getName().equals("river")) {
 				tileRotation = t.getRotation();
 				
 				//if meeple is on city or farm, paint at median edge
				int edge2 = i;
				if(feat.getName().equals("farm")) {
					Farm temp = (Farm) feat;
					int[] edges = temp.getEdges();
					edge2 = edges[edges.length/2];
				} else if(feat.getName().equals("city")) {
					City temp = (City) feat;
					int[] edges = temp.getEdges();
					edge2 = edges[edges.length/2];
				}
				
				edge = ((edge2-1)/3+(4-tileRotation))%4; //magic formula!
				edge = ((edge2-1)%3) + 1 + 3 * edge;
				if(i==0) {edge = 0;} //adjust for monastery
				
				//set x to the x coord of the meeple in pixels
				if(edge < 10) {
					x += meepleSize;
					x -= (int) (.01 * meepleSize);
					if(edge!=1 && edge!=9) {
						x += meepleSize;
						x += (int) (.02 * meepleSize);
						if(edge >= 3 && edge <= 7) {
							x += meepleSize;
 							x += (int) (.02 * meepleSize);
 							if(edge >= 4 && edge <= 6) {
 								x += meepleSize;
 								x -= (int) (.03 * meepleSize);
 							}
						}
					}
				}
						
				//set y to the y coord of the meeple in pixels
				if(edge==0 || edge >= 4) {
					y += meepleSize;
					y -= (int) (.02 * meepleSize);
					if((edge >= 5 && edge <=11) || edge==0) {
						y += meepleSize;
						y += (int) (.02 * meepleSize);
						if(edge >= 6 && edge <= 10) {
							y += meepleSize;
							y += (int) (.02 * meepleSize);
							if(edge >= 7 && edge <= 9) {
								y += meepleSize;
								y -= (int) (.03 * meepleSize);
							}
						}
					}
				}
				
				g2.setStroke(new BasicStroke(3));
				//paint circle
				if(feat.isSelected()) {
					g.setColor(Color.black);
					g.fillOval(x, y, meepleSize, meepleSize);
					if(gs.botTurn()) {
						g.setColor(Color.yellow);
					} else {
						g.setColor(Color.white);
					}
					g.drawOval(x, y, meepleSize, meepleSize);
					g.setFont(new Font("default",Font.BOLD,50));
					g.drawString(""+gs.getCurrentLabel(), x+40, y+70);
				} else if(feat.systemClaimed()) {
					g.setColor(Color.gray);
					g.drawOval(x, y, meepleSize, meepleSize);
					g.setFont(new Font("default",Font.BOLD,50));
					g.drawString(""+gs.getCurrentLabel(), x+40, y+70);
				} else {
					g.setColor(Color.black);
					g.drawOval(x, y, meepleSize, meepleSize);
					g.setFont(new Font("default",Font.BOLD,50));
					g.drawString(""+gs.getCurrentLabel(), x+40, y+70);
				}
				g2.setStroke(new BasicStroke(1));
				
				//housekeeping
				gs.addLabel(feat);
				feat.setNumberPainted(true);
				String meepleMessage = gs.getMeepleMessage();
				g.setColor(Color.black);
				g.setFont(new Font("default", Font.BOLD, 32));
				g.drawString(meepleMessage, 650, 740);
				x = 438; y = 146;
 			}
 		}
 		
 		//reset isNumberPainted for all features
 		for(int i=0; i<13; i++) {
 			if(t.getFeature(i)!=null) {
 				t.getFeature(i).setNumberPainted(false);
 			}
 		}
 		
 		//Bot selects meeple if it's bot turn
 		if(gs.botTurn() && !((Bot)gs.getPlayer(1)).meepleSelected()) {
 			((Bot) gs.getPlayer(1)).selectMeeple();
 			repaint();
 		}
 	}
 	
 	private void paintBoardDisplay(Graphics g) {
 		paintInfo(g);
 		paintTiles(g);
 		
 		//paint switch to place meeple box
 		g.setColor(Color.black);
 		g.fillRoundRect(746, 7, 220, 55,20,20);
 		g.setColor(Color.white);
 		g.setFont(new Font("default", Font.PLAIN, 16));
 		g.drawString("Press B to return", 780, 40);
 	}
 	
 	private void paintEnd(Graphics g) {
 		
 		paintTiles(g);
 		TreeSet<Player> playerScores = gs.getPlayerSet();
 		
 		//WHO WON
 		int winner = gs.getWinner();
 		g.setFont(new Font("default", Font.BOLD, 26));
 		g.setColor(Color.red);
 		g.drawString(getPlayerName(winner)+" wins!", 600,40);
 		
 		//SCOREBOARD
 		Graphics2D g2 = (Graphics2D) g;
 		g2.setStroke(new BasicStroke(1));
 		g.setColor(Color.black);
 		g.drawRect(28, 31, 248, 735);
 		g.setFont(new Font("default", Font.BOLD, 16));
 		g.drawString("Scoreboard", 105, 55);
 		int x = 52; int y = 83;
 		for(Player p: playerScores.descendingSet()) {
 			g.setFont(new Font("default", Font.PLAIN, 14));
 			int score = p.getScore();
 			String playerName = getPlayerName(p.getID());
 			g.drawString(playerName+": "+score, x, y); //ex. Red: 5
 			
 			//distribution
 			g.setFont(new Font("default", Font.PLAIN, 12));
 			x += 20;
 			y += 20;
 			String[] scoreDist = p.getScoreDist().split(";");
 			for(String t: scoreDist) {
 				g.drawString(t, x, y);
 				y+=20;
 			}
 			
 			x = 52;
 			y+=19;
 		}
 	}
 	
 	private void paintInfo(Graphics g) {
 		Graphics2D g2 = (Graphics2D) g;
 		g2.setStroke(new BasicStroke(1));
 		
 		//Scoreboard
 		g.setColor(Color.black);
 		g.drawRect(28, 61, 258, 281);
 		g.setFont(new Font("default", Font.BOLD, 18));
 		g.drawString("Scoreboard", 98, 90);
 		g.setFont(new Font("default", Font.PLAIN, 14));
 		g.drawString(gs.getNumLeft()+" Tiles left", 112, 110); //# of tiles left
 		g.setFont(new Font("default", Font.PLAIN, 16));
 		int y = 145;
 		TreeSet<Player> players = gs.getPlayerSet();
 		for(Player p: players.descendingSet()) {
 			if(p.scoreChanged()) {
 				g.setFont(new Font("default", Font.BOLD, 16));
 			}
 			g.drawString(getPlayerName(p.getID())+": "+p.getScore(), 54, y);
 			g.setFont(new Font("default", Font.PLAIN, 16));
 			y += 57;
 		}
 		
 		//Current player & number of meeples they have left
 		int whoseTurn = gs.getWhoseTurn();
 		String currentPlayer = getPlayerName(whoseTurn);
 		int numMeeplesLeft = gs.getPlayer(whoseTurn).getNumMeeples();
 		g.setColor(getPlayerColor(whoseTurn)); 
 		g.fillOval(239, 8, 50, 50); //colored circle
 		g.setFont(new Font("default", Font.BOLD, 28));
 		g.setColor(Color.black);
 		if(gs.bot()) {
 			g.drawString(currentPlayer, 42, 44);
 		} else {
 			g.drawString("Player " +currentPlayer, 42, 44);
 		}
 		g.drawString(""+numMeeplesLeft, 254, 43);
 		
 		//Instructions
 		g.setColor(Color.black);
 		g.setFont(new Font("default", Font.BOLD, 18));
 		g.drawRect(28, 367, 258, 404);
 		
 		g.drawString("Use Keyboard to", 75, 395);
 		g.drawString("interact (no mouse!)", 60, 420);
 		g.drawString("MOVE", 50, 465);
 		g.drawString("ROTATE", 50, 530);
 		g.drawString("PLACE", 50, 595);
 		g.drawString("SELECT", 50, 660);
 		g.drawString("DISCARD", 50, 735);
 		
 		g.drawLine(75, 437, 230, 437);
 		
 		g.setFont(new Font("default", Font.PLAIN,18));
 		g.drawString("tile: A, S, D, W", 110, 465);
 		g.drawString("tile: R key", 132, 530);
 		g.drawString("tile/meeple: Enter", 115, 595);
 		g.drawString("meeple: Number", 125, 660);
 		g.drawString("keys", 50, 685);
 		g.drawString("tile: Backspace", 140, 735);
 	}
 	
 	private void paintTiles(Graphics g) {
 		TreeSet<Tile> placedTiles = gs.getPlacedTiles();
 		int tileSize = gs.getTileSize();
 		int centerX = gs.getCenterX();
 		int centerY = gs.getCenterY();
 		int tileID, x, y;
 		
 		//PAINT PLACED TILES
 		for(Tile t: placedTiles) {
 			tileID = t.getID();
 			x = centerX + t.getX() * tileSize; //x coord of tile in pixels
 			y = centerY - t.getY() * tileSize; //y coord of tile in pixels
 			g.drawImage(tileImgs[tileID], x, y, tileSize, tileSize, null);
 			
 			//paint any meeples already put on tile
 			Feat feat;
 			int tileRotation;
 			int meepleSize = tileSize / 5;
 			int edge;
 			for(int i = 0; i < 13; i++) {
 				feat = t.getFeature(i);
 				if(feat != null && feat.getMeeple()!=-1 && feat.isMeeplePainted()==false) {
 					tileRotation = t.getRotation();
 					
 					//if meeple is on city or farm, paint at median edge
 					int edge2 = i;
 					if(feat.getName().equals("farm")) {
 						Farm temp = (Farm) feat;
 						int[] edges = temp.getEdges();
 						edge2 = edges[edges.length/2];
 					} else if(feat.getName().equals("city")) {
 						City temp = (City) feat;
 						int[] edges = temp.getEdges();
 						edge2 = edges[edges.length/2];
 					}
 					
 					edge = ((edge2-1)/3+(4-tileRotation))%4; //magic formula!
 					edge = ((edge2-1)%3) + 1 + 3 * edge;
 					if(i==0) {edge = 0;} //adjust for monastery
 					
 					g.setColor(getPlayerColor(feat.getMeeple()));
 					
 					//set x to the x coord of the meeple in pixels
 					if(edge < 10) {
 						x += meepleSize;
 						x -= (int) (.01 * meepleSize);
 						if(edge!=1 && edge!=9) {
 							x += meepleSize;
 							x += (int) (.02 * meepleSize);
 							if(edge >= 3 && edge <= 7) {
 								x += meepleSize;
 	 							x += (int) (.02 * meepleSize);
 	 							if(edge >= 4 && edge <= 6) {
 	 								x += meepleSize;
 	 								x -= (int) (.03 * meepleSize);
 	 							}
 							}
 						}
 					}
 							
 					//set y to the y coord of the meeple in pixels
 					if(edge==0 || edge >= 4) {
 						y += meepleSize;
 						y -= (int) (.02 * meepleSize);
 						if((edge >= 5 && edge <=11) || edge==0) {
 							y += meepleSize;
 							y += (int) (.02 * meepleSize);
 							if(edge >= 6 && edge <= 10) {
 								y += meepleSize;
 								y += (int) (.02 * meepleSize);
 								if(edge >= 7 && edge <= 9) {
 									y += meepleSize;
 									y -= (int) (.03 * meepleSize);
 								}
 							}
 						}
 					}
 					
 					//paint meeple
 					if(feat.getName().equals("farm")) {
 						Graphics2D g2 = (Graphics2D) g;
 						g2.setStroke(new BasicStroke(tileSize/15));
 						g.drawOval(x, y, meepleSize, meepleSize);
 					}
 					else {
 						g.fillOval(x, y, meepleSize, meepleSize);
 					}
 					
 					feat.setMeeplePainted(true);
 				}
 			}
 			
 			//meeples are all painted. set meeplePainted to false for each feature for the next time the tile is painted.
 			for(int i = 0; i < 13; i++) {
 				feat = t.getFeature(i);
 				if(feat!=null && feat.isMeeplePainted()) {
 					feat.setMeeplePainted(false);
 				}
 			}
 		}
 		
 		Graphics2D g2 = (Graphics2D) g;
 		
 		//PAINT SUGGESTED PLACEMENTS
 		if(!gs.botTurn() && !gs.getEnd()) {
 			TreeSet<Coordinate> suggested = gs.getSuggested();
 			for(Coordinate c: suggested) {
 				x = centerX + c.x() * tileSize;
 				y = centerY - c.y() * tileSize;
 				g.setColor(Color.green);
 				g2.setStroke(new BasicStroke(tileSize / 15));
 				g.drawRect(x, y, tileSize, tileSize);
 			}
 		}
 		
 		//PAINT CURRENT TILE TO BE PLACED
 		x = centerX + gs.getCurrentX() * tileSize;
 		y = centerY - gs.getCurrentY() * tileSize;
		if (gs.getCurrentTile() != null) {
			Tile current = gs.getCurrentTile();
			
			if(gs.botTurn() && !current.botAdjusted()) { //rotate image if bot turn
				for(int r=0; r< current.getRotation(); r++) {
					tileImgs[current.getID()] = rotateImg(tileImgs[current.getID()]);
				}
				current.setBotAdjusted(true);
			}
			g.drawImage(tileImgs[current.getID()], x, y, tileSize, tileSize, null);
			
			// set color of border, green if legal, red if illegal, yellow if placed by bot
			if(gs.botTurn()) {
				g.setColor(Color.yellow);
			} else if (gs.isLegal()) { 
				g.setColor(Color.green);
			} else {
				g.setColor(Color.red);
			}
			
			if(!gs.getEnd()) {
				g2.setStroke(new BasicStroke(tileSize / 15));
				g.drawRect(x, y, tileSize, tileSize);
			}
		}
 	}
 	
 	private void paintErrorMsg(Graphics g) {
 		//box
 		g.setColor(Color.white);
 		g.fillRoundRect(315, 156, 750, 480, 60, 60);
 		g.setColor(Color.red);
 		Graphics2D g2 = (Graphics2D) g;
 		g2.setStroke(new BasicStroke(8));
 		g.drawRoundRect(315, 156, 750, 480, 60, 60);
 		
 		//error message
 		g.setColor(Color.black);
 		g.setFont(new Font("default", Font.PLAIN, 14));
 		int errorNum = gs.getErrorMsg();
 		String[] errorMsg = getErrorMsg(errorNum);
 		int y = 225;
 		for(String s: errorMsg) {
 			g.drawString(s, 425, y);
 			y += 40;
 		}
 		
 		//Press ENTER to continue
 		g.setColor(Color.black);
 		g.fillRoundRect(565, 550, 260, 45, 20, 20);
 		g.setColor(Color.white);
 		g.setFont(new Font("default", Font.PLAIN, 18));
 		g.drawString("Press ", 586, 577);
 		g.setFont(new Font("default", Font.BOLD, 18));
 		g.drawString("ENTER", 636, 577);
 		g.setFont(new Font("default", Font.PLAIN, 18));
 		g.drawString("to continue", 706, 577);
 	}
 	private String[] getErrorMsg(int i) {
 		String[] msg;
 		switch(i) {
 		case 1:
 			msg = new String[2];
 			msg[0] = "This tile cannot be placed anywhere legally.";
 			msg[1] = "Press Backspace to discard.";
 			return msg;
 		case 2:
 			msg = new String[3];
 			msg[0] = "This tile is in an illegal position or rotation.";
 			msg[1] = "If it's a river tile, check that it's not a consecutive turn in the same direction.";
 			msg[2] = "Place the tile once the border turns green.";
 			return msg;
 		case 3:
 			msg = new String[1];
 			msg[0] = "You don't have any meeples left to place.";
 			return msg;
 		case 4:
 			msg = new String[8];
 			msg[0] = "PLACING MEEPLES:";
 			msg[1] = "";
 			msg[2] = "Each feature has been labeled with a number.";
 			msg[3] = "Type in the corresponding number to select a meeple.";
 			msg[4] = "Type the same number again to deselect a meeple.";
 			msg[5] = "If a feature's label is gray, it has already been claimed.";
 			msg[6] = "Press Enter to place a selected meeple.";
 			msg[7] = "If you do not wish to place a meeple, ensure that no meeple is selected and press Enter.";
 			return msg;
 		case 8:
 			msg = new String[3];
 			msg[0] = "GAME OVER";
 			msg[1] = "";
 			msg[2] = getPlayerName(gs.getWinner()) + " wins!";
 			return msg;
 		case 9:
 			msg = new String[3];
 			msg[0] = "The computer automatically place tiles and meeples.";
 			msg[1] = "Press Enter after each computer move to continue.";
 			msg[2] = "Computer moves are shown in yellow.";
 			return msg;
 		default: //finished monastery, road, or city
 			String message = "";
 			String s = String.valueOf(i);
 			for(int a = 0; a < s.length(); a++) { //add in which featuresystems were scored
 				String type = s.substring(a,a+1);
 				switch(type) {
 				case "5":
 					message += "Monastery completed and scored.;";
 					break;
 				case "6":
 					message += "Road completed and scored.;";
 					break;
 				case "7":
 					message += "City completed and scored.;";
 				}
 			}
 			message += ";";
 			
 			for(int a=0; a<gs.getPlayerSet().size();a++) {
 				Player p = gs.getPlayer(a);
 				if(p.scoreChanged()) {
 					message += p.getScoreInc() + " points given to " + getPlayerName(a) + " and meeple returned.;";
 				}
 			}
 			
 			return message.split(";");
 		}
 	}
 	
 	private String getPlayerName(int i)
 	{
 		if(gs.bot()) {
 			if(i==0) {
 				return "You";
 			}
 			return "Computer";
 		}
 		switch(i) {
 		case 0:
 			return "Red";
 		case 1:
 			return "Yellow";
 		case 2:
 			return "Green";
 		case 3:
 			return "Blue";
 		default:
 			return "NOT A PLAYER";
 		}
 	}
 	
 	private Color getPlayerColor(int i)
 	{
 		switch(i) {
 		case 0:
 			return Color.RED;
 		case 1:
 			return Color.YELLOW;
 		case 2:
 			return Color.GREEN;
 		case 3:
 			return Color.CYAN;
 		default:
 			return Color.BLACK;
 		}
 	}
 	
 	private BufferedImage rotateImg(BufferedImage img)
 	{
 		double rads = Math.toRadians(270);
		double sin = Math.abs(Math.sin(rads));
		double cos = Math.abs(Math.cos(rads));
		int w = (int) Math.floor(img.getWidth() * cos + img.getHeight() * sin);
		int h = (int) Math.floor(img.getHeight() * cos + img.getWidth() * sin);
		BufferedImage rotatedImage = new BufferedImage(w, h, img.getType());
		AffineTransform at = new AffineTransform();
		at.translate(w / 2, h / 2);
		at.rotate(rads, 0, 0);
		at.translate(-img.getWidth() / 2, -img.getHeight() / 2);
		AffineTransformOp rotateOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		rotateOp.filter(img, rotatedImage);
		return rotateOp.filter(img, rotatedImage);
 	}
 	
	public void addNotify() {
		super.addNotify();
		requestFocus();
	}
 	public void keyPressed(KeyEvent e) {
 		int key = e.getKeyChar();
 		int key2 = e.getKeyCode();
 		if(!gs.botTurn() && gs.getErrorMsg()==0) {
 			switch(key) {
 			case(97): 
				gs.leftKey();
				repaint();
				break;
 			case(65):
 				gs.leftKey();
 				repaint();
 				break;
 			case(115):
 				gs.downKey();
				repaint();
				break;
 			case(83):
 				gs.downKey();
				repaint();
				break;
 			case(100):
 				gs.rightKey();
				repaint();
				break;
 			case(68):
 				gs.rightKey();
				repaint();
				break;
 			case(119):
 				gs.upKey();
				repaint();
				break;
 			case(87):
 				gs.upKey();
				repaint();
				break;
 			case(10):
 				gs.enterKey();
 				repaint();
 				break;
 			case(114):  //rotate
 				if(gs.getBoard()) {
 					Tile currentTile = gs.getCurrentTile();
 					if(gs.getBoard() && currentTile!=null) {
 						int currentTileID = currentTile.getID();
 						tileImgs[currentTileID] = rotateImg(tileImgs[currentTileID]);
 						gs.rotateKey();
 						repaint();
 					}
 				}
 				break;
 			case (82): //rotate (capital R)
 				if(gs.getBoard()) {
 					Tile currentTile = gs.getCurrentTile();
 					if(gs.getBoard() && currentTile!=null) {
 						int currentTileID = currentTile.getID();
 						tileImgs[currentTileID] = rotateImg(tileImgs[currentTileID]);
 						gs.rotateKey();
 						repaint();
 					}
 				}
 				break;
 			case(8): 
 				gs.discardKey();
 				repaint();
 				break;
 			case(98):
 				gs.BKey();
 				repaint();
 				break;
 			case(66):
 				gs.BKey();
 				repaint();
 				break;
 			case(120):
 				gs.scoreKey();
 				repaint();
 			default:
 				if(key >= 49 && key <= 56)
 				{
 					gs.numberKey(key-48);
 					repaint();
 				}
 			}
 		} else {
 			if(key==10) {
 				gs.enterKey();
 				repaint();
 			}
 		}
		
 	}
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {} 
}