public class River extends Feat{
	private int enter, leave;
	
	public River(int e, int l) {
		super("river");
		integrated = false;
		enter = e;
		leave = l;
	}
	
	public int getEnter() {return enter;}
	public int getLeave() {return leave;}
	public boolean isStopped() {return enter==0;}
}