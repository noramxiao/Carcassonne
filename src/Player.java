public class Player implements Comparable<Player>{
	protected int ID;
	protected int numMeeples;
	protected int incompleteCity, incompleteMonastery, incompleteRoad, farm; //SCORING ATTRIBUTES
	protected int completeCity, completeMonastery, completeRoad; //SCORING ATTRIBUTES
	protected boolean scoreChanged; //true if score has just been updated
	protected int scoreInc; //what the score was incremented by, 0 if not
	
	public Player(int i) {
		ID = i;
		numMeeples = 8;
	}
	
	public void setNumMeeples(int i) {numMeeples = i;}
	public int getNumMeeples() {return numMeeples;}
	public int getID() {return ID;}
	public int getScoreInc() {return scoreInc;}
	public boolean scoreChanged() {return scoreChanged;}
	public void resetScoreChanged() {scoreChanged = false; scoreInc = 0;}
	
	public void addCompleteCity(int i) {completeCity += i; scoreChanged = true; scoreInc = i;}
	public void addCompleteRoad(int i) {completeRoad += i; scoreChanged = true; scoreInc = i;}
	public void addCompleteMonastery(int i) {completeMonastery += i; scoreChanged = true; scoreInc = i;}
	
	public void addIncompleteCity(int i) {incompleteCity += i;}
	public void addIncompleteRoad(int i) {incompleteRoad += i;}
	public void addIncompleteMonastery(int i) {incompleteMonastery += i;}
	
	public void addFarm(int i) {farm += i;}
	
	public int getScore() {
		return incompleteCity+incompleteMonastery+incompleteRoad+farm+completeCity+completeMonastery+completeRoad;
	}
	
	public String getScoreDist() {
		String s = "Complete Cities: " + completeCity
				+ ";Complete Roads: "+ completeRoad
				+ ";Complete Monasteries: "+ completeMonastery
				+ ";Incomplete Cities: "+ incompleteCity 
				+ ";Incomplete Roads: "+ incompleteRoad 
				+ ";Incomplete Monasteries: "+ incompleteMonastery
				+ ";Farms: "+ farm;
		return s;
	}
	
	public int compareTo(Player p) {
		int diff = getScore() - p.getScore();
		if(diff==0) {
			return 1;
		}
		return diff;
	}
}