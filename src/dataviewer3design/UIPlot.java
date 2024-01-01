package dataviewer3;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.SortedMap;

public class UIPlot extends UIState{
	private static UIPlot thePlot;
	private static final HashMap<String, KeyCommand> keyMap = new HashMap<String, KeyCommand>();
	
	private UIPlot(DataViewerUIHandler d) {
		super(d);
		{
			keyMap.put("M", new MenuCommand(UIHandler));
			keyMap.put("Q", new QuitCommand(UIHandler));  
	    };
	}


	
	@Override
	public UIState menu() {
		return UIMenu.create(UIHandler);
	}

	@Override
	public UIState plot() {
		return this;
	}

	@Override
	public boolean isMenu() {
		return false;
	}
	
	public static UIState create(DataViewerUIHandler d) {
		if (thePlot == null) {
			thePlot = new UIPlot(d);
		}
		return thePlot;	
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
		// Give a buffer around the plot window
        UIHandler.m_window.setXscale(-DataViewerUIHandler.DATA_WINDOW_BORDER, DataViewerUIHandler.WINDOW_WIDTH+DataViewerUIHandler.DATA_WINDOW_BORDER);
		UIHandler.m_window.setYscale(-DataViewerUIHandler.DATA_WINDOW_BORDER, DataViewerUIHandler.WINDOW_WIDTH+DataViewerUIHandler.DATA_WINDOW_BORDER);

    	// gray background
    	UIHandler.m_window.clear(Color.LIGHT_GRAY);

    	// white plot area
		UIHandler.m_window.setPenColor(Color.WHITE);
		UIHandler.m_window.filledRectangle(DataViewerUIHandler.WINDOW_WIDTH/2.0, DataViewerUIHandler.WINDOW_WIDTH/2.0, DataViewerUIHandler.WINDOW_WIDTH/2.0, DataViewerUIHandler.WINDOW_WIDTH/2.0);  

    	UIHandler.m_window.setPenColor(Color.BLACK);
    	
    	double nCols = 12; // one for each month
    	double nRows = UIHandler.getEndYear() - UIHandler.getStartYear() + 1; // for the years
    	
    	OutputToConsole.debug("nCols = %f, nRows = %f", nCols, nRows);
 		
        double cellWidth = DataViewerUIHandler.WINDOW_WIDTH / nCols;
        double cellHeight = DataViewerUIHandler.WINDOW_WIDTH / nRows;
        
        OutputToConsole.debug("cellWidth = %f, cellHeight = %f", cellWidth, cellHeight);
        
        boolean extremaVisualization = UIHandler.getVizualization().equals(DataViewerUIHandler.VISUALIZATION_MODES[DataViewerUIHandler.VISUALIZATION_EXTREMA_IDX]);
        OutputToConsole.info("visualization: %s (extrema == %b)", UIHandler.getVizualization(), extremaVisualization);
        
        for(int month = 1; month <= 12; month++) {
            double fullRange = UIHandler.data.getMonthlyMax().get(month) - UIHandler.data.getMonthlyMin().get(month);
            double extremaMinBound = UIHandler.data.getMonthlyMin().get(month) + DataViewerUIHandler.EXTREMA_PCT * fullRange;
            double extremaMaxBound = UIHandler.data.getMonthlyMax().get(month) - DataViewerUIHandler.EXTREMA_PCT * fullRange;


            // draw the line separating the months and the month label
        	UIHandler.m_window.setPenColor(Color.BLACK);
        	double lineX = (month-1.0)*cellWidth;
        	UIHandler.m_window.line(lineX, 0.0, lineX, DataViewerUIHandler.WINDOW_WIDTH);
        	UIHandler.m_window.text(lineX+cellWidth/2.0, -DataViewerUIHandler.DATA_WINDOW_BORDER/2.0, DataViewerUIHandler.MONTH_NAMES[month]);
        	
        	// there should always be a map for the month
        	SortedMap<Integer,Double> monthData = UIHandler.data.getPlotData().get(month);
        	
        	for(int year = UIHandler.getStartYear(); year <= UIHandler.getEndYear(); year++) {

        		// month data structure might not have every year
        		if(monthData.containsKey(year)) {
        			Double value = monthData.get(year);
        			
        			double x = (month-1.0)*cellWidth + 0.5 * cellWidth;
        			double y = (year-UIHandler.getStartYear())*cellHeight + 0.5 * cellHeight;
        			
        			Color cellColor = null;
        			
        			// get either color or grayscale depending on visualization mode
        			if(extremaVisualization && value > extremaMinBound && value < extremaMaxBound) {
        				cellColor = getDataColor(value, true);
        			}
        			else if(extremaVisualization) {
        				// doing extrema visualization, show "high" values in red "low" values in blue.
        				if(value >= extremaMaxBound) {
        					cellColor = Color.RED;
        				}
        				else {
        					cellColor = Color.BLUE;
        				}
        			}
        			else {
        				cellColor = getDataColor(value, false);
        			}
        			
        			// draw the rectangle for this data point
        			UIHandler.m_window.setPenColor(cellColor);
        			OutputToConsole.trace("month = %d, year = %d -> (%f, %f) with %s", month, year, x, y, cellColor.toString());
        			UIHandler.m_window.filledRectangle(x, y, cellWidth/2.0, cellHeight/2.0);
        		}
        	}
        }
        
        // draw the labels for the y-axis
        UIHandler.m_window.setPenColor(Color.BLACK);

        double labelYearSpacing = (UIHandler.getEndYear() - UIHandler.getStartYear()) / 5.0;
        double labelYSpacing = DataViewerUIHandler.WINDOW_WIDTH/5.0;
        // spaced out by 5, but need both the first and last label, so iterate 6
        for(int i=0; i<6; i++) {
        	int year = (int)Math.round(i * labelYearSpacing + UIHandler.getStartYear());
        	String text = String.format("%4d", year);
        	
        	UIHandler.m_window.textRight(0.0, i*labelYSpacing, text);
        	UIHandler.m_window.textLeft(DataViewerUIHandler.WINDOW_WIDTH, i*labelYSpacing, text);
        }
     
        // draw rectangle around the whole data plot window
        UIHandler.m_window.rectangle(DataViewerUIHandler.WINDOW_WIDTH/2.0, DataViewerUIHandler.WINDOW_WIDTH/2.0, DataViewerUIHandler.WINDOW_WIDTH/2.0, DataViewerUIHandler.WINDOW_WIDTH/2.0);
        
        // put in the title
        String title = String.format("%s, %s from %d to %d. Press 'M' for Main Menu.  Press 'Q' to Quit.",
        		UIHandler.getState(), UIHandler.getCountry(), UIHandler.getStartYear(), UIHandler.getEndYear());
        UIHandler.m_window.text(DataViewerUIHandler.WINDOW_WIDTH/2.0, DataViewerUIHandler.WINDOW_WIDTH+DataViewerUIHandler.DATA_WINDOW_BORDER/2.0, title);
		
	}
	
	/**
     * Return a Color object based on the value passed in.
     * @param value - controls the color
     * @param doGrayscale - if true, return a grayscale value (r, g, b are all equal);
     * 	otherwise return a range of red to green.
     * @return null is value is null, otherwise return a Color object
     */
    private Color getDataColor(Double value, boolean doGrayscale) {
    	if(null == value) {
    		return null;
    	}
    	double pct = (value - DataViewerUIHandler.TEMPERATURE_MIN_C) / DataViewerUIHandler.TEMPERATURE_RANGE;
    	OutputToConsole.trace("converted %f raw value to %f %%", value, pct);
    
    	if (pct > 1.0) {
            pct = 1.0;
        }
        else if (pct < 0.0) {
            pct = 0.0;
        }
        int r, g, b;
        // Replace the color scheme with my own
        if (!doGrayscale) {
        	r = (int)(255.0 * pct);
        	g = 0;
        	b = (int)(255.0 * (1.0-pct));
        	
        } else {
        	// Grayscale for the middle extema
        	r = g = b = (int)(255.0 * pct);
        }
        
        OutputToConsole.trace("converting %f to [%d, %d, %d]", value, r, g, b);

		return new Color(r, g, b);
	}


}
