package dataviewer1orig;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JOptionPane;

import edu.du.dudraw.Draw;
import edu.du.dudraw.DrawListener;

public class DataViewerApp implements DrawListener {
	// Private constants (alphabetical)
	private final static double 	DATA_WINDOW_BORDER = 50.0;
	private final static String 	DEFAULT_COUNTRY = "United States";
	private final static boolean	DO_DEBUG = true;
	private final static boolean	DO_TRACE = false;
	private final static double 	EXTREMA_PCT = 0.1;
	private final static int 		FILE_COUNTRY_IDX = 4;
	private final static int 		FILE_DATE_IDX = 0;
	private final static int 		FILE_NUM_COLUMNS = 5;
	private final static int 		FILE_STATE_IDX = 3;
	private final static int 		FILE_TEMPERATURE_IDX = 1;
	private final static int 		FILE_UNCERTAINTY_IDX = 2;
    private final static int 		GUI_MODE_MAIN_MENU = 0;
    private final static int 		GUI_MODE_DATA = 1;
    private final static double		MENU_STARTING_X = 40.0;
	private final static double 	MENU_STARTING_Y = 90.0;
	private final static double 	MENU_ITEM_SPACING = 5.0;
	private final static String[] 	MONTH_NAMES = { "", // 1-based
			"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
	private final static int		RECORD_MONTH_IDX = 1;
	private final static int		RECORD_STATE_IDX = 3;
	private final static int		RECORD_TEMPERATURE_IDX = 2;
	private final static int		RECORD_YEAR_IDX = 0;
	private final static double		TEMPERATURE_MAX_C = 30.0;
	private final static double		TEMPERATURE_MIN_C = -10.0;
	private final static double		TEMPERATURE_RANGE = TEMPERATURE_MAX_C - TEMPERATURE_MIN_C;
	private final static String[] 	VISUALIZATION_MODES = { "Raw", "Extrema (within 10% of min/max)" }; //Use Visitor
	private final static int 		VISUALIZATION_RAW_IDX = 0;
	private final static int		VISUALIZATION_EXTREMA_IDX = 1;
	private final static int 		WINDOW_HEIGHT = 720;
	private final static String 	WINDOW_TITLE = "DataViewer Application";
	private final static int 		WINDOW_WIDTH = 1320; // should be a multiple of 12
		
	// Instance variables (alphabetized)
	
    // data storage
    private final String m_dataFile;
    private List<List<Object>> m_dataRaw;
    private SortedSet<String> m_dataStates;
    private SortedSet<String> m_dataCountries;
    private SortedSet<Integer> m_dataYears;
    
    // GUI-related settings    
    private int m_guiMode = GUI_MODE_MAIN_MENU; // Menu by default
    
    // user selections
    private String m_selectedCountry = DEFAULT_COUNTRY;
    private Integer m_selectedEndYear;
    private String m_selectedState;
    private Integer m_selectedStartYear;
    private String m_selectedVisualization = VISUALIZATION_MODES[0];
    
    // plot-related data
	private TreeMap<Integer, SortedMap<Integer,Double>> m_plotData = null;
	private TreeMap<Integer,Double> m_plotMonthlyMaxValue = null;
	private TreeMap<Integer,Double> m_plotMonthlyMinValue = null;
	
	// Window-variables
    private Draw m_window;
    
	/**
	 * Constructor sets up the window and loads the specified data file.
	 */
    public DataViewerApp(String dataFile) throws FileNotFoundException {
    	// save the data file name for later use if user switches country
    	m_dataFile = dataFile;
    	
        // Setup the DuDraw board
        m_window = new Draw(WINDOW_TITLE);
        m_window.setCanvasSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        m_window.enableDoubleBuffering(); // Too slow otherwise -- need to use .show() later
       
		// Add the mouse/key listeners
        m_window.addListener(this);
        
        // Load data
        loadData();
        
        // draw the screen for the first time -- this will be the main menu
	    update();
    }
 
    /**
     * For debugging.  Use 'trace' for older debugging messages that you don't want to see.
     * 
     * Output is shown based on the M_DO_TRACE constant.
     */
    private void trace(String format, Object...args) {
    	if(DO_TRACE) {
    		System.out.print("TRACE: ");
    		System.out.println(String.format(format, args));
    	}
    }
    
    /**
     * For informational output.
     * @param format
     * @param args
     */
    private void info(String format, Object... args) {
    	System.out.print("INFO: ");
    	System.out.println(String.format(format, args));
    }
    
    /**
     * For error output.
     * @param format
     * @param args
     */
    private void error(String format, Object... args) {
    	System.out.print("ERROR: ");
    	System.out.println(String.format(format, args));
    }
    
    /**
     * For debugging output.  Output is controlled by the DO_DEBUG constant.
     * @param format
     * @param args
     */
    private void debug(String format, Object... args) {
    	if(DO_DEBUG) {
    		System.out.print("DEBUG: ");
    		System.out.println(String.format(format, args));
    	}
    }
    
    /**
     * Utility function to pull a year integer out of a date string.  Supports M/D/Y and Y-M-D formats only.
     * 
     * @param dateString
     * @return
     */
    private Integer parseYear(String dateString) {
    	Integer ret = null;
    	if(dateString.indexOf("/") != -1) {
    		// Assuming something like 1/20/1823
    		String[] parts = dateString.split("/");
    		if(parts.length == 3) {
	    		ret = Integer.parseInt(parts[2]);
    		}
    	}
    	else if(dateString.indexOf("-") != -1) {
    		// Assuming something like 1823-01-20
    		String[] parts = dateString.split("-");
    		if(parts.length == 3) {
    			ret = Integer.parseInt(parts[0]);
    		}
    	}
    	else {
    		throw new RuntimeException(String.format("Unexpected date delimiter: '%s'", dateString));
    	}
    	if(ret == null) {
    		trace("Unable to parse year from date: '%s'", dateString);
    	}
    	return ret;
    }
    
    private List<Object> getRecordFromLine(String line) {
        List<String> rawValues = new ArrayList<String>();
        try (Scanner rowScanner = new Scanner(line)) {
            rowScanner.useDelimiter(",");
            while (rowScanner.hasNext()) {
                rawValues.add(rowScanner.next());
            }
        }
        m_dataCountries.add(rawValues.get(FILE_COUNTRY_IDX));
        if(rawValues.size() != FILE_NUM_COLUMNS) {
        	trace("malformed line '%s'...skipping", line);
        	return null;
        }
        else if(!rawValues.get(FILE_COUNTRY_IDX).equals(m_selectedCountry)) {
        	trace("skipping non-USA record: %s", rawValues);
        	return null;
        }
        else {
        	trace("processing raw data: %s", rawValues.toString());
        }
        try {
        	// Parse these into more useful objects than String
        	List<Object> values = new ArrayList<Object>(4);
        	
        	Integer year = parseYear(rawValues.get(FILE_DATE_IDX));
        	if(year == null) {
        		return null;
        	}
        	values.add(year);
        	
        	Integer month = parseMonth(rawValues.get(FILE_DATE_IDX));
        	if(month == null) {
        		return null;
        	}
        	values.add(month);
        	values.add(Double.parseDouble(rawValues.get(FILE_TEMPERATURE_IDX)));
        	//not going to use UNCERTAINTY yet
        	//values.add(Double.parseDouble(rawValues.get(FILE_UNCERTAINTY_IDX)));
        	values.add(rawValues.get(FILE_STATE_IDX));
        	// since all are the same country
        	//values.add(rawValues.get(FILE_COUNTRY_IDX));
        	
        	// if we got here, add the state to the list of states
        	m_dataStates.add(rawValues.get(FILE_STATE_IDX));
        	m_dataYears.add(year);
        	return values;
        }
        catch(NumberFormatException e) {
        	trace("unable to parse data line, skipping...'%s'", line);
        	return null;
        }
    }
    
    private Integer parseMonth(String dateString) {
    	Integer ret = null;
    	if(dateString.indexOf("/") != -1) {
    		// Assuming something like 1/20/1823
    		String[] parts = dateString.split("/");
    		if(parts.length == 3) {
	    		ret = Integer.parseInt(parts[0]);
    		}
    	}
    	else if(dateString.indexOf("-") != -1) {
    		// Assuming something like 1823-01-20
    		String[] parts = dateString.split("-");
    		if(parts.length == 3) {
    			ret = Integer.parseInt(parts[1]);
    		}
    	}
    	else {
    		throw new RuntimeException(String.format("Unexpected date delimiter: '%s'", dateString));
    	}
    	if(ret == null || ret.intValue() < 1 || ret.intValue() > 12) {
    		trace("Unable to parse month from date: '%s'", dateString);
    		return null;
    	}
    	return ret;
	}

	private void loadData() throws FileNotFoundException {
		// reset the data storage in case this is a re-load
		m_dataRaw = new ArrayList<List<Object>>();
	    m_dataStates = new TreeSet<String>();
	    m_dataCountries = new TreeSet<String>();
	    m_dataYears = new TreeSet<Integer>();
	    m_plotData = null;

    	try (Scanner scanner = new Scanner(new File(m_dataFile))) {
    	    while (scanner.hasNextLine()) {
    	    	List<Object> record = getRecordFromLine(scanner.nextLine());
    	    	if(record != null) {
    	    		m_dataRaw.add(record);
    	    	}
    	    }
    	    // update selections (not including country) for the newly loaded data
            m_selectedState = m_dataStates.first();
            m_selectedStartYear = m_dataYears.first();
            m_selectedEndYear = m_dataYears.last();

            info("loaded %d data records", m_dataRaw.size());
            info("loaded data for %d states", m_dataStates.size());
            info("loaded data for %d years [%d, %d]", m_dataYears.size(), m_selectedStartYear, m_selectedEndYear);
    	}
    }
	
	private void updatePlotData() {
		//debug("raw data: %s", m_rawData.toString());
		// plot data is a map where the key is the Month, and the value is a sorted map where the key
		// is the year. 
		m_plotData = new TreeMap<Integer,SortedMap<Integer,Double>>();
		for(int month = 1; month <= 12; month++) {
			// any year/months not filled in will be null
			m_plotData.put(month, new TreeMap<Integer,Double>());
		}
		// now run through the raw data and if it is related to the current state and within the current
		// years, put it in a sorted data structure, so that we 
		// find min/max year based on data 
		m_plotMonthlyMaxValue = new TreeMap<Integer,Double>();
		m_plotMonthlyMinValue = new TreeMap<Integer,Double>();
		for(List<Object> rec : m_dataRaw) {
			String state = (String)rec.get(RECORD_STATE_IDX);
			Integer year = (Integer)rec.get(RECORD_YEAR_IDX);
			
			// Check to see if they are the state and year range we care about
			if (state.equals(m_selectedState) && 
			   ((year.compareTo(m_selectedStartYear) >= 0 && year.compareTo(m_selectedEndYear) <= 0))) {
						
				// Ok, we need to add this to the list of values for the month
				Integer month = (Integer)rec.get(RECORD_MONTH_IDX);
				Double value = (Double)rec.get(RECORD_TEMPERATURE_IDX);
				
				if(!m_plotMonthlyMinValue.containsKey(month) || value.compareTo(m_plotMonthlyMinValue.get(month)) < 0) {
					m_plotMonthlyMinValue.put(month, value);
				}
				if(!m_plotMonthlyMaxValue.containsKey(month) || value.compareTo(m_plotMonthlyMaxValue.get(month)) > 0) {
					m_plotMonthlyMaxValue.put(month, value);
				}
	
				m_plotData.get(month).put(year, value);
			}
		}
		//debug("plot data: %s", m_plotData.toString());
	}
    
    @Override
	public void update() {    	
    	if(m_guiMode == GUI_MODE_MAIN_MENU) {
    		drawMainMenu();
    	}
    	else if(m_guiMode == GUI_MODE_DATA) {
    		drawData();
    	}
    	else {
    		throw new IllegalStateException(String.format("Unexpected drawMode=%d", m_guiMode));
    	}
        // for double-buffering
        m_window.show();
    }
    
    private void drawMainMenu() {
    	m_window.clear(Color.WHITE);

    	String[] menuItems = {
    			"Type the menu number to select that option:",
    			"",
    			String.format("C     Set country: [%s]", m_selectedCountry),
    			String.format("T     Set state: [%s]", m_selectedState),
    			String.format("S     Set start year [%d]", m_selectedStartYear),
    			String.format("E     Set end year [%d]", m_selectedEndYear),
    			String.format("V     Set visualization [%s]", m_selectedVisualization),
    			String.format("P     Plot data"),
    			String.format("Q     Quit"),
    	};
    	
    	// enable drawing by "percentage" with the menu drawing
        m_window.setXscale(0, 100);
		m_window.setYscale(0, 100);
		
		// draw the menu
    	m_window.setPenColor(Color.BLACK);
		
		drawMenuItems(menuItems);
    }

	private void drawMenuItems(String[] menuItems) {
		double yCoord = MENU_STARTING_Y;
		
		for(int i=0; i<menuItems.length; i++) {
			m_window.textLeft(MENU_STARTING_X, yCoord, menuItems[i]);
			yCoord -= MENU_ITEM_SPACING;
		}
	}
    
    private void drawData() {
    	// Give a buffer around the plot window
        m_window.setXscale(-DATA_WINDOW_BORDER, WINDOW_WIDTH+DATA_WINDOW_BORDER);
		m_window.setYscale(-DATA_WINDOW_BORDER, WINDOW_HEIGHT+DATA_WINDOW_BORDER);

    	// gray background
    	m_window.clear(Color.LIGHT_GRAY);

    	// white plot area
		m_window.setPenColor(Color.WHITE);
		m_window.filledRectangle(WINDOW_WIDTH/2.0, WINDOW_HEIGHT/2.0, WINDOW_WIDTH/2.0, WINDOW_HEIGHT/2.0);  

    	m_window.setPenColor(Color.BLACK);
    	
    	double nCols = 12; // one for each month
    	double nRows = m_selectedEndYear - m_selectedStartYear + 1; // for the years
    	
    	debug("nCols = %f, nRows = %f", nCols, nRows);
 		
        double cellWidth = WINDOW_WIDTH / nCols;
        double cellHeight = WINDOW_HEIGHT / nRows;
        
        debug("cellWidth = %f, cellHeight = %f", cellWidth, cellHeight);
        
        boolean extremaVisualization = m_selectedVisualization.equals(VISUALIZATION_MODES[VISUALIZATION_EXTREMA_IDX]);
        info("visualization: %s (extrema == %b)", m_selectedVisualization, extremaVisualization);
        
        for(int month = 1; month <= 12; month++) {
            double fullRange = m_plotMonthlyMaxValue.get(month) - m_plotMonthlyMinValue.get(month);
            double extremaMinBound = m_plotMonthlyMinValue.get(month) + EXTREMA_PCT * fullRange;
            double extremaMaxBound = m_plotMonthlyMaxValue.get(month) - EXTREMA_PCT * fullRange;


            // draw the line separating the months and the month label
        	m_window.setPenColor(Color.BLACK);
        	double lineX = (month-1.0)*cellWidth;
        	m_window.line(lineX, 0.0, lineX, WINDOW_HEIGHT);
        	m_window.text(lineX+cellWidth/2.0, -DATA_WINDOW_BORDER/2.0, MONTH_NAMES[month]);
        	
        	// there should always be a map for the month
        	SortedMap<Integer,Double> monthData = m_plotData.get(month);
        	
        	for(int year = m_selectedStartYear; year <= m_selectedEndYear; year++) {

        		// month data structure might not have every year
        		if(monthData.containsKey(year)) {
        			Double value = monthData.get(year);
        			
        			double x = (month-1.0)*cellWidth + 0.5 * cellWidth;
        			double y = (year-m_selectedStartYear)*cellHeight + 0.5 * cellHeight;
        			
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
        			m_window.setPenColor(cellColor);
        			trace("month = %d, year = %d -> (%f, %f) with %s", month, year, x, y, cellColor.toString());
        			m_window.filledRectangle(x, y, cellWidth/2.0, cellHeight/2.0);
        		}
        	}
        }
        
        // draw the labels for the y-axis
        m_window.setPenColor(Color.BLACK);

        double labelYearSpacing = (m_selectedEndYear - m_selectedStartYear) / 5.0;
        double labelYSpacing = WINDOW_HEIGHT/5.0;
        // spaced out by 5, but need both the first and last label, so iterate 6
        for(int i=0; i<6; i++) {
        	int year = (int)Math.round(i * labelYearSpacing + m_selectedStartYear);
        	String text = String.format("%4d", year);
        	
        	m_window.textRight(0.0, i*labelYSpacing, text);
        	m_window.textLeft(WINDOW_WIDTH, i*labelYSpacing, text);
        }
     
        // draw rectangle around the whole data plot window
        m_window.rectangle(WINDOW_WIDTH/2.0, WINDOW_HEIGHT/2.0, WINDOW_WIDTH/2.0, WINDOW_HEIGHT/2.0);
        
        // put in the title
        String title = String.format("%s, %s from %d to %d. Press 'M' for Main Menu.  Press 'Q' to Quit.",
        		m_selectedState, m_selectedCountry, m_selectedStartYear, m_selectedEndYear);
        m_window.text(WINDOW_WIDTH/2.0, WINDOW_HEIGHT+DATA_WINDOW_BORDER/2.0, title);
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
    	double pct = (value - TEMPERATURE_MIN_C) / TEMPERATURE_RANGE;
    	trace("converted %f raw value to %f %%", value, pct);
    
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
        
        trace("converting %f to [%d, %d, %d]", value, r, g, b);

		return new Color(r, g, b);
	}

	// Below are the mouse/key listeners
    /**
     * Handle key press.  Q always quits.  Otherwise process based on GUI mode.
     */
	@Override public void keyPressed(int key) {
		boolean needsUpdate = false;
		boolean needsUpdatePlotData = false;
		trace("key pressed '%c'", (char)key);
		// regardless of draw mode, 'Q' or 'q' means quit:
		if(key == 'Q') {
			System.out.println("Bye");
			System.exit(0);
		}
		else if(m_guiMode == GUI_MODE_MAIN_MENU) {
			if(key == 'P') {
				// plot the data
				m_guiMode = GUI_MODE_DATA;
				if(m_plotData == null) {
					// first time going to render data need to generate the plot data
					needsUpdatePlotData = true;
				}
				needsUpdate = true;
			}
			else if(key == 'C') {
				// set the Country
				Object selectedValue = JOptionPane.showInputDialog(null,
			             "Choose a Country", "Input",
			             JOptionPane.INFORMATION_MESSAGE, null,
			             m_dataCountries.toArray(), m_selectedCountry);
				
				if(selectedValue != null) {
					info("User selected: '%s'", selectedValue);
					if(!selectedValue.equals(m_selectedCountry)) {
						// change in data
						m_selectedCountry = (String)selectedValue;
						try {
							loadData();
						}
						catch(FileNotFoundException e) {
							// convert to a runtime exception since
							// we can't add throws to this method
							throw new RuntimeException(e);
						}
						needsUpdate = true;
						needsUpdatePlotData = true;
					}
				}
			}

			else if(key == 'T') {
				// set the state
				Object selectedValue = JOptionPane.showInputDialog(null,
			             "Choose a State", "Input",
			             JOptionPane.INFORMATION_MESSAGE, null,
			             m_dataStates.toArray(), m_selectedState);
				
				if(selectedValue != null) {
					info("User selected: '%s'", selectedValue);
					if(!selectedValue.equals(m_selectedState)) {
						// change in data
						m_selectedState = (String)selectedValue;
						needsUpdate = true;
						needsUpdatePlotData = true;
					}
				}
			}
			else if(key == 'S') {
				// set the start year
				Object selectedValue = JOptionPane.showInputDialog(null,
			             "Choose the start year", "Input",
			             JOptionPane.INFORMATION_MESSAGE, null,
			             m_dataYears.toArray(), m_selectedStartYear);
				
				if(selectedValue != null) {
					info("User seleted: '%s'", selectedValue);
					Integer year = (Integer)selectedValue;
					if(year.compareTo(m_selectedEndYear) > 0) {
						error("new start year (%d) must not be after end year (%d)", year, m_selectedEndYear);
					}
					else {
						if(!m_selectedStartYear.equals(year)) {
							m_selectedStartYear = year;
							needsUpdate = true;
							needsUpdatePlotData = true;
						}
					}
				}
			}
			else if(key == 'E') {
				// set the end year
				Object selectedValue = JOptionPane.showInputDialog(null,
			             "Choose the end year", "Input",
			             JOptionPane.INFORMATION_MESSAGE, null,
			             m_dataYears.toArray(), m_selectedEndYear);
				
				if(selectedValue != null) {
					info("User seleted: '%s'", selectedValue);
					Integer year = (Integer)selectedValue;
					if(year.compareTo(m_selectedStartYear) < 0) {
						error("new end year (%d) must be not be before start year (%d)", year, m_selectedStartYear);
					}
					else {
						if(!m_selectedEndYear.equals(year)) {
							m_selectedEndYear = year;
							needsUpdate = true;
							needsUpdatePlotData = true;
						}
					}
				}
			}
			else if(key == 'V') {
				// set the visualization
				Object selectedValue = JOptionPane.showInputDialog(null,
						"Choose the visualization mode", "Input",
						JOptionPane.INFORMATION_MESSAGE, null,
						VISUALIZATION_MODES, m_selectedVisualization);

				if(selectedValue != null) {
					info("User seleted: '%s'", selectedValue);
					String visualization = (String)selectedValue;
					if(!m_selectedVisualization.equals(visualization)) {
						m_selectedVisualization = visualization;
						needsUpdate = true;
					}
				}
			}

		}
		else if (m_guiMode == GUI_MODE_DATA) {
			if(key == 'M') {
				m_guiMode = GUI_MODE_MAIN_MENU;
				needsUpdate = true;
			}
		}
		else {
			throw new IllegalStateException(String.format("unexpected mode: %d", m_guiMode));
		}
		if(needsUpdatePlotData) {
			// something changed with the data that needs to be plotted
			updatePlotData();
		}
		if(needsUpdate) {
			update();
		}
	}

	@Override
	public void keyReleased(int key) {}

	@Override
	public void keyTyped(char key) {}

	@Override
	public void mouseClicked(double x, double y) {}
	
	@Override
	public void mouseDragged(double x, double y) {}

	@Override
	public void mousePressed(double x, double y) {}

	@Override
	public void mouseReleased(double x, double y) {}    
}