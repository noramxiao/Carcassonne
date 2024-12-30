
public class RiverSystem {
	
	private int legalX, legalY;
	private int prevEnter, prevExit; //0 (N),1 (E),2 (S),3 (W), for consecutive turns
	
	public RiverSystem() {
		legalX = 0;
		legalY = -1;
		prevEnter = -1;
		prevExit = 2;
	}
	
	public boolean checkRiver(int x, int y, int e, int l, int r) {
		e = adjust(e, r);
		l = adjust(l, r);
		
		if(x!=legalX) {return false;}
		if(y!=legalY) {return false;}
		
		if((Math.abs(e-l)==1 || Math.abs(e-l)==3) && (Math.abs(prevEnter-prevExit)==1 || Math.abs(prevEnter-prevExit)==3)) { //if it's a turn and the previous tile was also a turn
			return e!=prevEnter && l!=prevEnter;
		}
		return true;
	}
	public void addRiver(int e, int l, int r) {
		e = adjust(e, r);
		l = adjust(l, r);
		
		int side; //set to the side that the next placed river should enter from
		switch(prevExit) {
		case 0:
			side = 2;
			break;
		case 1:
			side = 3;
			break;
		case 2: 
			side = 0;
			break;
		default:
			side = 1;
		}
		
		if(e==side) {
			prevEnter = e;
			prevExit = l;
		} else {
			prevEnter = l;
			prevExit = e;
		}
		
		//adjust legalX and legalY
		switch(prevExit) {
		case 0:
			legalY++;
			break;
		case 1:
			legalX++;
			break;
		case 2:
			legalY--;
			break;
		default:
			legalX--;
		}
	}
	private int adjust(int edge, int rotate) { //adjusts for rotation
		for(int i=0;i<rotate;i++) {
			edge--;
			if(edge<0) {edge=3;}
		}
		return edge;
	}
	
	public int getLegalX() {return legalX;}
	public int getLegalY() {return legalY;}
}
