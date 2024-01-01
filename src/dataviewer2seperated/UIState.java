package dataviewer2;


public abstract class UIState{
	
	public abstract UIState menu();
	
	public abstract UIState plot();
		
	public abstract boolean isMenu();
	
	public abstract void proccessKey();
		
}
