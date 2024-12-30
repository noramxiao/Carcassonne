import java.util.ArrayList;
import java.util.Collections;

public class RoadSystem extends FeatureSystem {
	
	// for road Only
	private ArrayList<Road> roads;
	private int tileCount;
	
	//for Road only
	public RoadSystem(int numPlayers) {
		super("roadsystem", numPlayers);
		roads = new ArrayList<Road>();
	}
	public void add(Road r) {
		roads.add(r);
		r.setMySystem(this);
		if (!isTileAdded()) {
			tileCount++;
			setTileAdded(true);
		}
	}
	public boolean ready() {
		for(Road k: roads)
			if (!k.finished())
				return false;
		return true;
	}
	public void merge(RoadSystem r) {
		ArrayList<Road>r_roads = r.getRoads();
		for (Road k: r_roads)
			k.setMySystem(this);
		roads.addAll(r_roads);
		tileCount+=r.inGameScore();
		ArrayList<Integer> ints = new ArrayList<>();
		for (Road k: roads)
			ints.add(k.getMyTile());
		Collections.sort(ints);
		int prev = -1;
		for (int k: ints) {
			if (k==prev)
				tileCount--;
			prev=k;
		}
		System.out.println(ints+" "+tileCount);
	}
	public void clearMeeples() {
		for(Road r: roads) {
			r.clearMeeple();
		}
	}
	public int inGameScore() {return tileCount;}
	public int endGameScore() {return tileCount;}
	public ArrayList<Road> getRoads(){return roads;}
}
