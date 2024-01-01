package dataviewer2;

public class UIPlot extends UIState{
	private static UIPlot thePlot;
	
	private UIPlot() {}
	
	@Override
	public UIState menu() {
		return UIMenu.create();
	}

	@Override
	public UIState plot() {
		return this;
	}

	@Override
	public boolean isMenu() {
		return false;
	}
	
	public static UIState create() {
		if (thePlot == null) {
			thePlot = new UIPlot();
		}
		return thePlot;	
	}

	@Override
	public void proccessKey() {
		// TODO Auto-generated method stub
		
	}

}
