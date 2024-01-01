package dataviewer2;


public class UIMenu extends UIState{
	private static UIMenu theMenu;
	
	private UIMenu() {}
	
	
	@Override
	public UIState menu() {
		return this;
	}

	@Override
	public UIState plot() {
		return UIPlot.create();
	}


	@Override
	public boolean isMenu() {
		return true;
	}

	public static UIState create() {
		if (theMenu == null) {
			theMenu = new UIMenu();
		}
		return theMenu;	
	}


	@Override
	public void proccessKey() {
		// TODO Auto-generated method stub
		
	}
	
	

}
