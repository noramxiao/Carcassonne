
public class Coordinate implements Comparable<Coordinate>{
	private int x, y;
	
	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int x() {return x;}
	public int y() {return y;}

	public int compareTo(Coordinate c) {
		if(x==c.x() && y==c.y) {
			return 0;
		}
		return 1;
	}
}
