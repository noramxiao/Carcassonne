public class Road extends Feat {

	// for all Feats
//	private boolean isMeeplePainted;
//	private boolean isNumberPainted;
//	private boolean isSelected;
//	private int myMeeple;
//	public boolean integrated;
	
	// reference to mySystem
	//private RoadSystem mySystem;
	
	// for tracking whether the road is joined
	private boolean enterCont;
	private boolean exitCont; 
	private int enter;
	private int exit; 
	private int myTileID;
	
	public Road(int en, int ex, int i) {
		super("road");
		enter = en;
		exit = ex;
		enterCont = false;
		exitCont = false;
		if (enter==0)
			enterCont=true;
		myMeeple=-1;
		myTileID = i;
	}
	public int getMyTile() {return myTileID;}
	// for integrating into system
	public void join(int index) {
		if (index==enter)
			enterCont=true;
		else if (index==exit)
			exitCont=true;
	}
//	public void setMySystem(RoadSystem syst) {mySystem = syst;}
	public RoadSystem getRoadSyst() {return (RoadSystem) mySystem;}
	
	// for determining if system is finished
	public boolean finished() {return enterCont==true&&exitCont==true;}

}
