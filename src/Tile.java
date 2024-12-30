public class Tile implements Comparable<Tile> {

	private int ID; 
	private Feat[] feats;
	private int rotation; 
	private int x,y;
	private Tile[]neighbors;
	private boolean botAdjusted;
	
	public Tile(int k) {
		ID = k;
		x = 0; y = 0;
		feats = new Feat[13];
		neighbors = new Tile[4];
	}
	
	// adding Features to a tile
	public void addMonastery() {
		feats[0] = new Monastery(this);
	}
	public void addRiver(int start, int end) {
		River r = new River(start, end);
		if (start!=0)
			feats[start]=r;
		feats[end]=r;
	}
	public void addRoad(int start, int end) {
		Road r = new Road(start, end, ID);
		if (start!=0)
			feats[start]=r;
		feats[end]=r;
	}
	public void addCity(int[] edges, boolean coat) {
		City c = new City(edges, coat, ID);
		for (int i: edges)
			feats[i] = c;
	}
	public void addFarm(int[] edges) {
		Farm f = new Farm(edges);
		for (int i: edges) {
			feats[i]=f;
		}
		int neighbor = edges[0]-1;
		if(neighbor==0) {
			neighbor = 12;
		}
		if(feats[neighbor].getName().equals("city")) {
			f.addAdjCity((City) feats[neighbor]);
		}
		
		neighbor = edges[edges.length-1]+1;
		if(neighbor==13) {
			neighbor = 1;
		}
		if(feats[neighbor].getName().equals("city")) {
			f.addAdjCity((City) feats[neighbor]);
		}
	}
	
	//Accessors and modifiers
	public int getID() {return ID;}
	
	// Features
	public Feat[] getFeatureArray() {return feats;}
	public Feat getFeature(int i) {
		if (i<feats.length)
			return feats[i];
		return null;
	}
	
	//location and rotation
	public void rotate() {rotation = (rotation+1)%4;}
	public int getRotation() {return rotation;}
	public void setX(int k) {x=k;}
	public void setY(int k) {y=k;}
	public int getX() {return x;}
	public int getY() {return y;}
	
	//river
	public boolean hasRiver() {
		for(Feat f: feats) {
			if(f!=null && f.getName().equals("river") && ID!=0) {
				return true;
			}
		}
		return false;
	}
	public River getRiver() { //for checking cons. turns
		if(hasRiver()) {
			for(Feat f: feats) {
				if(f!= null && f.getName().equals("river")) {
					return (River)f;
				}
			}
		}
		return null;
	}
	
	//neighbors
	public Tile[] getNeighbors() {return neighbors;}
	public void setNeighbor(int loc, Tile t) {neighbors[loc]=t;}
	
	public boolean botAdjusted() {return botAdjusted;}
	public void setBotAdjusted(boolean b) {botAdjusted = b;}
	
	//compare
	public int compareTo(Tile t) {
		if (x!=t.getX())
			return x-t.getX();
		return y-t.getY();
	}
}
