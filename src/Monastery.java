public class Monastery extends Feat {
	// attributes for Monastery only 
	//MonasterySystem mySystem; 
	private Tile myTile;
	
	//methods for Monastery only 
	public String name() {return "monastery";}
	public Monastery(Tile t) {
		super("monastery");
		myTile = t;
	}
	public FeatureSystem getMonasterySyst() {
		return (MonasterySystem) mySystem;
	}
	public Tile getMonTile() {
		return myTile;
	}
	public void setMySystem(MonasterySystem m) {
		mySystem = m;
	}
}