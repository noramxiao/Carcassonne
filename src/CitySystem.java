import java.util.ArrayList;
import java.util.Collections;

public class CitySystem extends FeatureSystem {
	
	// for City only 
	private ArrayList<City> cities;
	private int coats;
	private boolean farmScored;
	private int tileCount;
	
	//for city only 
	public CitySystem(int numPlayers) {
		super("citysystem", numPlayers);
		cities = new ArrayList<City>();
//		meeples = new int[4];
//		for (int i=0; i<4; i++) 
//			meeples[i]=-1;
		coats=0;
	}
	public void add(City c) {
		cities.add(c);
		c.setMySystem(this);
		if (!isTileAdded()) {
			tileCount++;
			setTileAdded(true);
		}
		coats+=c.getCoats();
	}
	public boolean ready() {
		for(City c: cities) {
			System.out.print(c.cityScoringDebugger());
			if (!c.finished())
				return false;
		}
		//System.out.println("citysystem is ready. Tilecount = "+tileCount+" Coats "+coats+" Score "+inGameScore());
		return true;
	}
	public void merge(CitySystem c) {
		ArrayList<City> c_cities = c.getCities();
		for (City k: c_cities)
			k.setMySystem(this);
		cities.addAll(c_cities);
		tileCount+=c.tileCount();
		coats += c.numCoats();
		ArrayList<Integer> ints = new ArrayList<>();
		for (City k: cities)
			ints.add(k.getMyTile());
		Collections.sort(ints);
		System.out.print(ints);
		int prev = -1;
		for (int k: ints) {
			if (k==prev)
				tileCount--;
			prev=k;
		}
		System.out.print(" "+tileCount);
	}
	public void clearMeeples() {
		for(City c: cities) {
			c.clearMeeple();
		}
	}
	public int numCoats() {return coats;}
	public int tileCount() {return tileCount;}
	public int inGameScore() {return 2*tileCount+2*coats;}
	public int endGameScore() {return tileCount+coats;}
	public ArrayList<City> getCities() {return cities;}
	public boolean farmScored() {return farmScored;}
	public void setFarmScored(boolean b) {farmScored=b;}
	public int numCities() {
		return cities.size();
	}

}
