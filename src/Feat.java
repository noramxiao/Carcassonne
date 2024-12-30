public class Feat {
	
	private String name;
	public Feat(String n) {name = n; myMeeple = -1;}
	public String getName() {return name;}
	private String info;
	

	// for all features
	protected boolean isMeeplePainted;
	protected boolean isNumberPainted;
	protected boolean isSelected;
	protected int myMeeple;
	protected boolean integrated;
	protected FeatureSystem mySystem;
	private boolean looked; //for scoring
	
	// for all features
	public boolean isMeeplePainted() {return isMeeplePainted;}
	public void setMeeplePainted(Boolean b) {isMeeplePainted=b;}
	public boolean isNumberPainted() {return isNumberPainted;}
	public void setNumberPainted(Boolean b) {isNumberPainted=b;}
	public boolean isSelected() {return isSelected;}
	public void setIsSelected(Boolean b) {isSelected=b;}
	public int getMeeple() {return myMeeple;}
	public void setMeeple(int i) {
		myMeeple=i;
		if (mySystem==null)
			System.out.println(name+" "+"ERROR = NULL SYSTEM");
		mySystem.addMeeple(i);
	}
	public void clearMeeple() {myMeeple = -1;}
	public boolean systemClaimed() {
		if (mySystem==null)
			return false;
		if (name.equals("city")) {
			//System.out.println("There are "+((CitySystem) mySystem).numCities()+"in this citysyst");
			//int num=0;
			//for (City x: ((CitySystem) mySystem).getCities())
				//if (x.myMeeple>-1)
					//num++;
			//System.out.println("City meeples: "+num);
		}
		//System.out.println("This "+name+" system meeples: "+mySystem.meepleList());
		return mySystem.isClaimed();
	}
	public boolean hasSystem() {return mySystem!=null;}
	//public boolean isIntegrated() {return mySystem!=null;}
	public FeatureSystem getMySystem() {return mySystem;}
	public void setMySystem(FeatureSystem s) {mySystem=s;}
	
	public boolean looked() {return looked;}
	public void setLooked(boolean b) {looked=b;}
	/* public String name();
	public boolean isMeeplePainted();
	public void setMeeplePainted(Boolean b);
	public boolean isNumberPainted();
	public void setNumberPainted(Boolean b);
	public boolean isSelected();
	public void setSelected(Boolean b);
	public int getMeeple();
	public void setMeeple(int i);
	public boolean systemClaimed();
	public boolean isIntegrated(); */
	
	private boolean tested;
	public boolean tested() {return tested;}
	public void setTested(boolean b) {tested=b;}
	public void setInfo(int x, int y, int r) {
		info=null;
		//info = name+" tile @ ("+x+","+y+") r="+r;
	}
	public String getInfo() {
		if (info==null)
				return "";
		return info;}
}
