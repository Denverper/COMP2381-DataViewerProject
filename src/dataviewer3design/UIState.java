package dataviewer3;

import java.util.List;

public abstract class UIState {
	protected List<KeyCommand> commandList;
	protected DataViewerUIHandler UIHandler;
	
	public UIState(DataViewerUIHandler d) {
		UIHandler = d;
	}
	
	public abstract UIState menu();
	
	public abstract UIState plot();
		
	public abstract boolean isMenu();
	
	public abstract void draw();
	
	public abstract void proccessKey(String key);
	
	public abstract void proccessCommands();
		
}
