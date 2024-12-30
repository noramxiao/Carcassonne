
public class FeatureSystem {
	
	// for all Systems
	private int[]meeples;
	protected boolean isTileAdded;
	protected boolean isScored;
	private String name;
	
	public FeatureSystem(String n, int numPlayers) {
		meeples = new int[numPlayers];
//		for(int i = 0; i<4; i++) {
//			meeples[i] = -1;
//		}
		name = n;
	}
	
	// for All Systems
	public boolean isClaimed() {
		for (int k: meeples)
			if (k>0)
				return true;
		return false;
	}
	public boolean isTileAdded() {return isTileAdded;}
	public void setTileAdded(Boolean b) {isTileAdded=b;}
	public boolean isScored() {return isScored;}
	public void scrap() {isScored=true;}
	public int[] getMeepleArray() {return meeples;}
	public void addMeeple(int i) {meeples[i]++;}
	public void addMeeples(int[]meep) {
		for (int i=0; i<meep.length; i++)
			meeples[i]+=meep[i];
	}
/*
 * How to merge
 * IF feature has already been claimed, check if mySystem == the system you are trying to add it to.
 * IF they are not the same object, then you need to merge. 
 * 
 * Make sure to check isScored first (if it is already scored then just ignore that system)
 */

/*
 * public int[] getMeepleArray();
	public int inGameScore();
	public int endGameScore();
	public boolean isScored();
	public void scrap();
	public boolean ready();
	public boolean isClaimed();
	public boolean isTileAdded();
	public void setTileAdded(Boolean b);
	public void addMeeple(int i);
 */

	public String meepleList() {
		String ret = "";
		for (int i : meeples)
			if (i>-1)
				ret += i + " ";
		return ret;
	}
	public String name() {return name;}
}
