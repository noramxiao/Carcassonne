
public class City extends Feat {
	
	// city unique things
	private boolean hasCoat; 
	private boolean[] edgeDone;
	private int[] edges;
	private int myTileID;
	
	public City(int[] edges, boolean hc, int g) {
		super("city");
		hasCoat = hc;
		edgeDone = new boolean[13];
		for (int i=0; i<edgeDone.length; i++)
			edgeDone[i]=true;
		for (int x: edges)
			edgeDone[x]=false;
		myMeeple=-1;
		this.edges = edges;
		myTileID = g;
	}
	public int getMyTile() {return myTileID;}
	public String name() {return "city";}
	public int[] getEdges() {return edges;}
	
	// for integrating into system
	public void join(int index) { //note that index represents the index of feats that this city is in
		edgeDone[index]=true;
	}
	//public void setMySystem(CitySystem syst) {mySystem = syst;}
	public CitySystem getCitySyst() {return (CitySystem) mySystem;}
	public String cityScoringDebugger() {
		String x = "";
		for (int i=0;i<edgeDone.length;i++)
			if (edgeDone[i]==false)
				x+=" "+i;
		return x;
	}
	
	// for helping system determine if it is finished
	public boolean finished() {
		for(boolean b: edgeDone)
			if (b==false)
				return false;
		return true;
	}

	// coat of Arms
	public int getCoats() {
		if (hasCoat)
			return 1;
		return 0;
	}
}