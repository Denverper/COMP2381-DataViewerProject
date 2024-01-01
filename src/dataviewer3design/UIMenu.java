package dataviewer3;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

public class UIMenu extends UIState{
	private static UIMenu theMenu;
	private static final HashMap<String, KeyCommand> keyMap = new HashMap<String, KeyCommand>();
	
	public UIMenu(DataViewerUIHandler d) {
		super(d);
		{
			keyMap.put("P", new PlotCommand(UIHandler));
			keyMap.put("Q", new QuitCommand(UIHandler));
			keyMap.put("C", new CountryCommand(UIHandler));  
			keyMap.put("T", new StateCommand(UIHandler));  
			keyMap.put("S", new StartCommand(UIHandler));  
			keyMap.put("E", new EndCommand(UIHandler));  
			keyMap.put("V", new VizualizationCommand(UIHandler));  
	   };
	}

   
		   

	
	@Override
	public UIState menu() {
		return this;
	}

	@Override
	public UIState plot() {
		return UIPlot.create(UIHandler);
	}


	@Override
	public boolean isMenu() {
		return true;
	}

	public static UIState create(DataViewerUIHandler d) {
		if (theMenu == null) {
			theMenu = new UIMenu(d);
		}
		return theMenu;	
	}


	@Override
	public void proccessKey(String key) {
		commandList = new ArrayList<KeyCommand>();
		if (keyMap.containsKey(key)) {
			commandList.add(keyMap.get(key));
			proccessCommands();
		}
	}
	
	@Override
	public void proccessCommands() {
		for (int i=0; i<commandList.size();i++) {
			commandList.get(i).execute(commandList);
		}
	}
	


	@Override
	public void draw() {
		UIHandler.m_window.clear(Color.WHITE);

    	String[] menuItems = {
    			"Type the menu number to select that option:",
    			"",
    			String.format("C     Set country: [%s]", UIHandler.getCountry()),
    			String.format("T     Set state: [%s]", UIHandler.getState()),
    			String.format("S     Set start year [%d]", UIHandler.getStartYear()),
    			String.format("E     Set end year [%d]", UIHandler.getEndYear()),
    			String.format("V     Set visualization [%s]", UIHandler.getVizualization()),
    			String.format("P     Plot data"),
    			String.format("Q     Quit"),
    	};
    	
    	// enable drawing by "percentage" with the menu drawing
    	UIHandler.m_window.setXscale(0, 100);
    	UIHandler.m_window.setYscale(0, 100);
		
		// draw the menu
    	UIHandler.m_window.setPenColor(Color.BLACK);
		
		drawMenuItems(menuItems);
		
	}
	
	private void drawMenuItems(String[] menuItems) {
		double yCoord = DataViewerUIHandler.MENU_STARTING_Y;
		
		for(int i=0; i<menuItems.length; i++) {
			UIHandler.m_window.textLeft(DataViewerUIHandler.MENU_STARTING_X, yCoord, menuItems[i]);
			yCoord -= DataViewerUIHandler.MENU_ITEM_SPACING;
		}
	}
	
	

}
