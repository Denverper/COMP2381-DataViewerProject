package dataviewer3;

import java.io.FileNotFoundException;
import edu.du.dudraw.Draw;
import edu.du.dudraw.DrawListener;

public class DataViewerUIHandler implements DrawListener {
	
	protected DataHandler data;
	
	public final static String[] 	VISUALIZATION_MODES = { "Raw", "Extrema (within 10% of min/max)" }; //Use Visitor
	public final static int		VISUALIZATION_EXTREMA_IDX = 1;
	public final static int 		WINDOW_HEIGHT = 720;
	public final static String 	WINDOW_TITLE = "DataViewer Application";
	public final static int 		WINDOW_WIDTH = 1320; // should be a multiple of 12
	public final static double		MENU_STARTING_X = 40.0;
	public final static double 	MENU_STARTING_Y = 90.0;
	public final static double 	MENU_ITEM_SPACING = 5.0;
	public final static double 	DATA_WINDOW_BORDER = 50.0;
	public final static double 	EXTREMA_PCT = 0.1;
	
	public final static double		TEMPERATURE_MAX_C = 30.0;
	public final static double		TEMPERATURE_MIN_C = -10.0;
	public final static double		TEMPERATURE_RANGE = TEMPERATURE_MAX_C - TEMPERATURE_MIN_C;
	
    // user selections
    private String m_selectedCountry = 	DEFAULT_COUNTRY;
    private final static String 	DEFAULT_COUNTRY = "United States";
    private Integer m_selectedEndYear;
    private String m_selectedState;
    private Integer m_selectedStartYear;
    private String m_selectedVisualization = VISUALIZATION_MODES[0];
    
    public final static String[] 	MONTH_NAMES = { "", // 1-based
			"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
	
    
    // Window-variables    
    public Draw m_window;
    
    //Used for GUI state, menu vs plot
	protected UIState state;
	
	
	public DataViewerUIHandler(DataHandler d) throws FileNotFoundException {
		m_window = new Draw(WINDOW_TITLE);
        m_window.setCanvasSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        m_window.enableDoubleBuffering(); // Too slow otherwise -- need to use .show() later
       
        state = UIMenu.create(this);
		// Add the mouse/key listeners
        m_window.addListener(this);
        
        data = d;
        // Load data
        data.loadData(m_selectedCountry);
     // update selections (not including country) for the newly loaded data
        m_selectedState = data.getStates().first();
        m_selectedStartYear = data.getYears().first();
        m_selectedEndYear = data.getYears().last();

        OutputToConsole.info("loaded data for %d states", data.getStates().size());
        OutputToConsole.info("loaded data for %d years [%d, %d]", data.getYears().size(), m_selectedStartYear, m_selectedEndYear);
        
        
        update();
	}
	
	
	
	private void draw() 
	{
		state.draw();
	}
	
	
    
	// Below are the mouse/key listeners
    /**
     * Handle key press.  Q always quits.  Otherwise process based on GUI mode.
     */
	@Override public void keyPressed(int key) {
		OutputToConsole.trace("key pressed '%c'", key);
		state.proccessKey(Character.valueOf((char)key).toString());
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

	@Override
	public void update() {    	
    	draw();
        // for double-buffering
        m_window.show();
    }
	
	// user selections
	public String getCountry() {
		return m_selectedCountry;
	}
	
	public String getState() {
		return m_selectedState;
	}
	
	public Integer getEndYear() {
		return m_selectedEndYear;
	}
	
	public Integer getStartYear() {
		return m_selectedStartYear;
	}
	
	public String getVizualization() {
		return m_selectedVisualization;
	}
	
	//setters
	public void setCountry(String c) {
		m_selectedCountry = c;
	}
	
	public void setState(String c) {
		m_selectedState = c;
	}
	
	public void setEndYear(Integer s) {
		m_selectedEndYear = s;
	}
	
	public void setStartYear(Integer e) {
		m_selectedStartYear = e;
	}
	
	public void setVizualization(String v) {
		m_selectedVisualization = v;
	}
		
}
