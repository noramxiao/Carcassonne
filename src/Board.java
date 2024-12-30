import java.util.TreeSet;
import java.util.ArrayList;

public class Board {

	private TreeSet<Tile> placed;
	private int minX, minY, maxX, maxY;
	private ArrayList<FeatureSystem> incomplete;
	private Player[] players;
	private RiverSystem riverSyst;
	
	public Board(Player [] p) {
		placed = new TreeSet<Tile>();
		incomplete = new ArrayList<FeatureSystem>();
		players=p;
		riverSyst = new RiverSystem();
	}

	// end game scoring 
	public void endGameScore() {
		for (FeatureSystem g: incomplete) {
			//System.out.print(g.name()+" ");
			int points = getEndPoints(g);
			//System.out.println(points);
			
			int max = 1;
			for (int k: g.getMeepleArray())
				if (k>max)
					max=k;
			for (int i = 0; i < players.length; i++) {
				if (g.getMeepleArray()[i] == max) {
					switch (g.name()) {
					case "farmsystem":
						players[i].addFarm(points);
						break;
					case "roadsystem":
						players[i].addIncompleteRoad(points);
						break;
					case "citysystem":
						players[i].addIncompleteCity(points);
						break;
					case "monasterysystem":
						players[i].addIncompleteMonastery(points);
						break;
					}
				}
			}
		}
	}
	private int getEndPoints(FeatureSystem g) {
		switch(g.name()) {
		case "farmsystem": {
			FarmSystem fs = (FarmSystem) g;
			return fs.endGameScore();
		}
		case "roadsystem": {
			RoadSystem rs = (RoadSystem) g;
			return rs.endGameScore();
		}
		case "citysystem": {
			CitySystem cs = (CitySystem )g;
			return cs.endGameScore();
		}
		case "monasterysystem": {
			int completed = 1; 
			Tile t = ((MonasterySystem) g).getMyTile();
			for (Tile x: t.getNeighbors())
				if (x!=null)
					completed++;
			Tile x = getTileAt(t.getX()+1, t.getY()+1);
			if (x!=null) {completed++;}
			x = getTileAt(t.getX()+1, t.getY()-1);
			if (x!=null) {completed++;}
			x = getTileAt(t.getX()-1, t.getY()-1);
			if (x!=null) {completed++;}
			x = getTileAt(t.getX()-1, t.getY()+1);
			if (x!=null) {completed++;}
			return completed;
		}
		}
		return 0;
	}
	//scoring during game
	public String inGameScore(Tile t) {
		String finished = "";
		String s = "";
		//System.out.print("In Game Score At "+t.getX()+" "+t.getY() +"; ");
		for (Feat f: t.getFeatureArray()) {
			s = scoringHelper(f);
			if(!s.equals("-1")) {finished += s;}
		}
		for (Feat f: t.getFeatureArray())
			if (f!=null)
				f.setLooked(false);
		for (int i=0; i<4; i++) {
			Tile n = t.getNeighbors()[i];
			if (n != null) {
				s = scoringHelper(n.getFeature(0));
				if(!s.equals("-1")) {finished += s;}
				//System.out.print(i);
				Tile x = n.getNeighbors()[(i + 1) % 4];
				
				if (x!=null)
					s = scoringHelper(x.getFeature(0));
					if(!s.equals("-1")) {finished += s;}
			}
		}
		
		for (Feat f: t.getFeatureArray())
			if (f!=null)
				f.setLooked(false);
		return finished;
	}
	private String scoringHelper(Feat f) {
		String type = "-1"; //-1 if no system is ready, 5 if monastery, 6 road, 7 city
		
		if (f==null)
			return type;
		else if (f.looked())
			return type;
		f.setLooked(true);
		FeatureSystem syst = f.getMySystem();
		
		boolean remove = false;
		switch (f.getName()) {
		case "farm":
			return type;
		case "city": {
			CitySystem cs = (CitySystem) syst;
			if (cs.ready()) {
				//System.out.print("READY ");
				type = "7";
				givePoints("city", cs.inGameScore(), cs.getMeepleArray());
				cs.scrap();
				cs.clearMeeples();
				remove = true;
			}
			break;
		}
		case "road": {
			RoadSystem rs = (RoadSystem) syst;
			if (rs.ready()) {
				type = "6";
				givePoints("road", rs.inGameScore(), rs.getMeepleArray());
				rs.scrap();
				rs.clearMeeples();
				remove=true;
			}
			break;
		}
		case "monastery": {
			MonasterySystem ms = (MonasterySystem) syst;
			//System.out.println();
			if (ms.ready()) {
				type = "5";
				givePoints("monastery", ms.inGameScore(), ms.getMeepleArray());
				ms.scrap();
				ms.clearMeeples();
				remove=true;
			}
			f.setLooked(false);
		}
		}
		if (remove) {
			for (int i=0; i<incomplete.size(); i++) {
				if (incomplete.get(i)==syst) {
					incomplete.remove(syst);
				}
			}
		}	
		return type;
	}
	private void givePoints(String name, int points, int[]meeples) {
		//System.out.print("Give Points to " + name);
		int max = 1;
		for (int i: meeples)
			if (i>max)
				max=i;
		for (int i=0; i<meeples.length; i++) {
			//System.out.print(meeples[i]+" "+max+", ");
			if (meeples[i]==max) {
				givePointsHelper(points, players[i], name);
				//System.out.print(" Added "+points+" to Player "+i);
			}
			players[i].setNumMeeples(players[i].getNumMeeples()+meeples[i]);
		}
	}
	public void givePointsHelper(int points, Player p, String name) {
		switch (name) {
			case "city": {
				p.addCompleteCity(points);
				break;
			}
			case "road":
				p.addCompleteRoad(points);
				break;
			case "monastery":
				p.addCompleteMonastery(points);
				break;
		}
	}
	
	//incorporating, and adding
	public void addTile(Tile t) {
		placed.add(t);
		if(t.hasRiver()) {
			River r = t.getRiver();
			int enter = r.getEnter();
			int leave = r.getLeave();
			enter = (int) (enter/3.0 - 2.0/3.0);
			leave = (int) (leave/3.0 - 2.0/3.0);
			riverSyst.addRiver(enter, leave, t.getRotation());
		}
		minX = Math.min(minX, t.getX());
		maxX = Math.max(maxX, t.getX());
		minY = Math.min(minY, t.getY());
		maxY = Math.max(maxY, t.getY());
	}
	
	private ArrayList<FeatureSystem> systs; //used in the incorporate tile method only, plus relevant helper methods
	public void incorporateTile(Tile t) {
		systs = new ArrayList<FeatureSystem>();
		int xx = t.getX();
		int yy = t.getY();
		
		//Part A: neighbors stuff
		Tile[] neighbors = {getTileAt(xx,yy+1), getTileAt(xx+1,yy),
				getTileAt(xx,yy-1), getTileAt(xx-1,yy)};
		for (int a=0; a<4; a++) {
			t.setNeighbor(a, neighbors[a]);
			if(neighbors[a]!=null) {
				neighbors[a].setNeighbor((a+2)%4, t);
			}
		}
		
		if (t.getFeature(0)!=null)
			createFeatureSystem(t, 0);
		
		//Part B: integrating 
		for (int i=1; i<13; i++) {
			//System.out.print(i+"\t");
			int whichNeighbor = ((i-1)/3 + (4-t.getRotation()))%4; //so neighbors[whichNeighbor]
			Feat feat = t.getFeature(i);
			feat.setInfo(t.getX(), t.getY(), t.getRotation());
			//System.out.print(i+" "+feat.getInfo());
			if (neighbors[whichNeighbor]==null) {
				if (!feat.hasSystem()) {
					createFeatureSystem(t, i);
					//System.out.print("created feature system");
				}
			}
			else {
				Tile neighbor = neighbors[whichNeighbor];
				int adj = (i)%3;
				if (adj==0)
					adj=3;
				int neighborloc = (((whichNeighbor+2)%4+neighbor.getRotation())%4)*3 +(4-adj);
				Feat n_feat = neighbor.getFeature(neighborloc);
				if (!feat.hasSystem()) {
					//non merge case 1: the feature at i does not have a system yet
					addFeatureToSystem(feat, i, n_feat.getMySystem(), n_feat, neighborloc);
				}
				else if (feat.getMySystem()==n_feat.getMySystem()){
					//nonmerge case 2: the feature at i has a system already, and it's the same as before. just do appropriate joins
					switch(feat.getName()) {
					case ("city"): {
						((City)feat).join(i);
						((City)n_feat).join(neighborloc);
						break;
					}
					case ("road"): {
						((Road)feat).join(i);
						((Road)n_feat).join(neighborloc);
						break;
					}
					}
				}
				else {
					//System.out.print(" merged ");
					switch(feat.getName()) {
					case ("city"): {
						((City) feat).setMySystem(cityMerge((CitySystem) feat.getMySystem(), (CitySystem) n_feat.getMySystem()));
						((City) feat).join(i);
						((City)n_feat).join(neighborloc);
						break;
					}
					case ("road"): {
						((Road) feat).setMySystem(roadMerge((RoadSystem) feat.getMySystem(), (RoadSystem) n_feat.getMySystem()));
						((Road)feat).join(i);
						((Road)n_feat).join(neighborloc);
						break;
					}
					case ("farm"): {
						((Farm) feat).setMySystem(farmMerge((FarmSystem) feat.getMySystem(), (FarmSystem) n_feat.getMySystem()));
					}
					}
				}
			}
			//System.out.println("HAS SYSTEM?" + feat.hasSystem());
		}
		//System.out.println();
		for (FeatureSystem fs: systs) {
			fs.setTileAdded(false);
		}
	}
	private void createFeatureSystem(Tile t, int loc) {
		Feat feat = t.getFeature(loc);
		String name = feat.getName();
		switch(name) {
		case ("farm"): {
			Farm f = (Farm) feat;
			FarmSystem fs = new FarmSystem(players.length);
			f.setMySystem(fs);
			fs.addFarm(f);
			incomplete.add(fs);
			systs.add(fs);
			break;
		}
		case ("city"): {
			City c = (City) feat;
			CitySystem cs = new CitySystem(players.length);
			c.setMySystem(cs);
			cs.add(c);
			incomplete.add(cs);
			systs.add(cs);
			break;
		}
		case ("road"): {
			Road r = (Road) feat;
			RoadSystem rs = new RoadSystem(players.length);
			r.setMySystem(rs);
			rs.add(r);
			incomplete.add(rs);
			systs.add(rs);
			break;
		}
		case ("monastery"): {
			Monastery m = (Monastery) feat;
			MonasterySystem ms = new MonasterySystem(m, players.length);
			m.setMySystem(ms);
			incomplete.add(ms);
			systs.add(ms);
			break;
		}
			
		default: 
			return;
		}
	}
	private void addFeatureToSystem(Feat feat, int loc, FeatureSystem syst, Feat n_feat, int n_loc) {
		feat.setMySystem(n_feat.getMySystem());
		String name = feat.getName();
		switch (name) {
		case "farm": {
			Farm f = (Farm) feat;
			FarmSystem fs = (FarmSystem) syst;
			fs.addFarm(f);
			f.setMySystem(fs);
			systs.add(fs);
			break;
		}
		case "city": {
			City c = (City) feat;
			CitySystem cs = (CitySystem) syst;
			cs.add(c);
			c.join(loc);
			c.setMySystem(cs);
			c = (City) n_feat;
			c.join(n_loc);
			systs.add(cs);
			break;
		}
		case "road": {
			Road r = (Road) feat;
			RoadSystem rs = (RoadSystem) syst;
			rs.add(r);
			r.join(loc);
			r.setMySystem(rs);
			r = (Road) n_feat;
			r.join(n_loc);
			systs.add(rs);
			break;
		}
		default: 
			return;
		}
	}
	private RoadSystem roadMerge(RoadSystem rs1, RoadSystem rs2) {
		rs1.merge(rs2);
		FeatureSystem syst = (FeatureSystem) rs2;
		for (int i=0; i<incomplete.size(); i++) {
			if (incomplete.get(i)==syst) {
				incomplete.remove(i);
				i--;
			}
		}
		rs1.addMeeples(rs2.getMeepleArray());
		return rs1;
	}
	private CitySystem cityMerge(CitySystem cs1, CitySystem cs2) {
		cs1.merge(cs2);
		FeatureSystem syst = (FeatureSystem) cs2;
		for (int i=0; i<incomplete.size(); i++) {
			if (incomplete.get(i)==syst) {
				incomplete.remove(i);
				i--;
			}
		}
		cs1.addMeeples(cs2.getMeepleArray());
		return cs1;
	}
	private FarmSystem farmMerge(FarmSystem fs1, FarmSystem fs2) {
		fs1.merge(fs2);
		FeatureSystem syst = (FeatureSystem) fs2;
		for (int i=0; i<incomplete.size(); i++) {
			if (incomplete.get(i)==syst) {
				incomplete.remove(i);
				i--;
			}
		}
		fs1.addMeeples(fs2.getMeepleArray());
		return fs1;
	}

	// checking if there is a legal position 
	public TreeSet<Coordinate> findSuggested(Tile current) {
		TreeSet<Coordinate> coords = new TreeSet<Coordinate>();
		
		for (int x=minX-1; x<maxX+2; x++) {
			for (int y=minY-1; y<maxY+2; y++) {
				for (int r=0; r<4; r++) {
					if (checkLegal(current, x, y)) {
						coords.add(new Coordinate(x,y));
					}
					current.rotate();
				}
			}
		}
		
		return coords;
	}
	
	//checking legality 
	public boolean checkLegal(Tile current, int xx, int yy) {
		//check river
		if(current.hasRiver()) {
			if(!checkRiver(current, xx, yy)) {return false;}
		}
		
		boolean touchesOne = false;
		if (getTileAt(xx,yy)!=null)
			return false;
		Tile[] borderingTiles = {getTileAt(xx,yy+1), getTileAt(xx+1,yy),
				getTileAt(xx,yy-1), getTileAt(xx-1,yy)};
		for (int i=0; i<4; i++) {
			if (borderingTiles[i]!=null) {
				touchesOne=true;
				if (!checkSide(i, current, borderingTiles[i])) {
					return false;
				}
			}
		}
		return touchesOne;
	}
	private boolean checkRiver(Tile current, int x, int y) {
		River r = current.getRiver();
		int enter = r.getEnter();
		int leave = r.getLeave();
		enter = (int) (enter/3.0 - 2.0/3.0);
		leave = (int) (leave/3.0 - 2.0/3.0);
				
		if(!riverSyst.checkRiver(x, y, enter, leave, current.getRotation())) {
			return false;
		}
		return true;
	}
	private boolean checkSide(int a, Tile current, Tile border) {
		int b = (a+2)%4;
		int acheck =  ((a+current.getRotation())%4)*3 + 1;
		int bcheck = ((b+border.getRotation())%4)*3 + 3;
		for (int i=0; i<3; i++) {
			String str1 = current.getFeature(acheck).getName();
			String str2 = border.getFeature(bcheck).getName();
			if (!str1.equals(str2))
				return false;
			acheck++;
			bcheck--;
		}
		return true;
	}
	
	//very useful helper method
	public Tile getTileAt(int xx, int yy) {
		//BRUTE FORCE. WILL TRY TO MAKE IT SMOOTHER LATER. 
		for (Tile t: placed)
			if (t.getX()==xx && t.getY()==yy)
				return t;
			else if (t.getX()>=xx && t.getY()>yy)
				return null;
		return null;
	}
	
	public int getTileSize() {
		int maxLength = Math.max(maxX-minX, maxY-minY)+3;
		return 733/maxLength;
	}
	public int getCenterX() {
		double xCoord = (maxX+minX)/2.0;
		int tileSize = getTileSize();
		return (674-tileSize/2)-(int)(xCoord*tileSize);
	}
	public int getCenterY() {
		double yCoord = (maxY+minY)/2.0;
		int tileSize = getTileSize();
		return (422-tileSize/2)+(int)(yCoord*tileSize);
	}
	public int getMaxX() {return maxX;}
	public int getMaxY() {return maxY;}
	public int getMinX() {return minX;}
	public int getMinY() {return minY;}
	public TreeSet<Tile> getPlacedTiles(){return placed;}
	public RiverSystem getRiverSyst() {return riverSyst;}
}
