
public class MonasterySystem extends FeatureSystem {
	
	//for Monastery only
	private Monastery myMon; 
	
	public MonasterySystem(Monastery m, int numPlayers) {
		super("monasterysystem", numPlayers);
		myMon = m;
	}
	public boolean ready() {
		Tile t = myMon.getMonTile();
		Tile[] neighbors = t.getNeighbors();
		for (int i=0; i<4; i++) {
			if (neighbors[i]==null) {
				//System.out.print("no tile at ("+t.getX()+","+t.getY()+")'s "+i+"; ");
				return false;
			}
			else if (neighbors[i].getNeighbors()[(i+1)%4]==null) {
				//System.out.print("no tile at ("+t.getX()+","+t.getY()+")'s "+((i+1)%4)+" of "+i);
				return false;
			}
		}
		System.out.print("ready!");
		return true;
	}
	public void clearMeeples() {
		myMon.clearMeeple();
	}
	public Tile getMyTile() {
		return myMon.getMonTile();
	}
	public int inGameScore() {
		return 9;
	}
	public int endGameScore() {
		//TO BE IMPLEMENTED
		// actually... do tis in board
		return 0;
	}
}
