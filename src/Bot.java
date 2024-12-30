import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class Bot extends Player{
	
	private GameState gs;
	private Board board;
	private boolean tileSelected, meepleSelected;
	
	public Bot(int i, GameState g) {
		super(i);
		gs = g;
		numMeeples = 8;
	}
	
	public void selectTileLoc() {
		TreeSet<Coordinate> possible = gs.getSuggested();
		int ind = (int) (Math.random()*possible.size());
		Iterator<Coordinate> iter = possible.iterator();
		Coordinate coord = new Coordinate(0,0);
		for(int i=0; i<=ind; i++) { //find random legal tile location
			coord = iter.next();
		}
		// set to legal rotation
		Tile current = gs.getCurrentTile();
		int x = coord.x();
		int y = coord.y();
		for(int i=0; i<4; i++) { 
			if(board.checkLegal(current,x,y)) {
				break;
			}
			current.rotate();
		}
		
		gs.setCurrentX(x);
		gs.setCurrentY(y);
		tileSelected = true;
	}
	public void selectMeeple() {
		if(numMeeples>0) {
			Set<Integer> labels = gs.getFeatureLabels();
			if(labels.size()>0) {
				int ind = (int) (Math.random() * labels.size());
				Iterator<Integer> iter = labels.iterator();
				for(int i=0;i<ind-1;i++) {
					iter.next();
				}
				gs.selectFeature(iter.next());
			}
		}
		meepleSelected = true;
	}
	
	public boolean tileSelected() {return tileSelected;}
	public boolean meepleSelected() {return meepleSelected;}
	public void setMeepleSelected(boolean b) {meepleSelected = b;} 
	public void addBoard(Board b) {board = b;}
}
