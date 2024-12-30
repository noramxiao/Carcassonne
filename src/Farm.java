import java.util.ArrayList;

public class Farm extends Feat {
	private int[] edges;
	
	//for Farm only
	//private FarmSystem mySystem;
	private ArrayList<City> adjcity;
	
	//for Farm only
	public Farm(int[] e) {
		super("farm");
		edges = e;
		adjcity = new ArrayList<City>();
		myMeeple = -1;
	}
	public void addAdjCity(City c) {
		if (!adjcity.contains(c))
			adjcity.add(c);
	}
	public ArrayList<City> getAdjCity(){return adjcity;}
//	public void setMySystem(FarmSystem fs) {super.mySystem = fs;}
	public FarmSystem getFarmSyst() {return (FarmSystem) super.mySystem;}
	
	// for all features
	public int[] getEdges() {return edges;}
}