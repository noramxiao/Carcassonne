import java.util.ArrayList;

public class FarmSystem extends FeatureSystem {

	//farm-unique attributes
	private ArrayList<Farm> farms;
	
	//farm-unique methods 
	public FarmSystem(int numPlayers) {
		super("farmsystem", numPlayers);
		farms = new ArrayList<Farm>();
	}
	public void addFarm(Farm f) {
		farms.add(f);
	}
	public int inGameScore() {return 0;}
	public int endGameScore() {
		int completed = 0;
		ArrayList<City> adjcities = new ArrayList<>();
		ArrayList<CitySystem> scoredCitySyst = new ArrayList<>();
		for (Farm f: farms)
			adjcities.addAll(f.getAdjCity());
		for (City c: adjcities) {
			CitySystem cs = c.getCitySyst();
			if (!cs.farmScored()) {
				cs.setFarmScored(true);
				if (cs.ready())
					completed++;
				scoredCitySyst.add(cs);
			}
		}
		for (CitySystem cs: scoredCitySyst)
			cs.setFarmScored(false);
		return completed*3;
	}
	public boolean ready() {
		return false;
	}
	public void merge(FarmSystem fs) {
		ArrayList<Farm> fs_farms = fs.getFarms();
		for (Farm f: fs_farms)
			f.setMySystem(this);
		farms.addAll(fs_farms);
		
	}
	public ArrayList<Farm> getFarms(){return farms;}
}
