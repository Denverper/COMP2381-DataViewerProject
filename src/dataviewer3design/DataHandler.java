package dataviewer3;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class DataHandler{
	
	private final static String 	DEFAULT_COUNTRY = "United States";
	private String m_selectedCountry = DEFAULT_COUNTRY;
	
	// data storage
    private final String m_dataFile;
    private List<DataRecord> m_dataRaw;
    private SortedSet<String> m_dataStates;
    private SortedSet<String> m_dataCountries;
    private SortedSet<Integer> m_dataYears;

    // plot-related data
 	private TreeMap<Integer, SortedMap<Integer,Double>> m_plotData = null;
 	private TreeMap<Integer,Double> m_plotMonthlyMaxValue = null;
 	private TreeMap<Integer,Double> m_plotMonthlyMinValue = null;
 	
 	public DataHandler(String file) throws FileNotFoundException{
 		m_dataFile = file;
 
 	}
 	
 	public void loadData(String country) throws FileNotFoundException 
 	{
		DataRecordFactory dataCreate = new DataRecordFactory();
		// reset the data storage in case this is a re-load
		m_selectedCountry = country;
		m_dataRaw = new ArrayList<DataRecord>();
	    m_dataStates = new TreeSet<String>();
	    m_dataCountries = new TreeSet<String>();
	    m_dataYears = new TreeSet<Integer>();
	    m_plotData = null;
		m_dataCountries.add(m_selectedCountry);


    	try (Scanner scanner = new Scanner(new File(m_dataFile))) {
    		//skip first line so that it does not create a record
    		if (scanner.hasNextLine()) {
    			scanner.nextLine();
    		}
    		
    	    while (scanner.hasNextLine()) {
    	    	try {
        	    	DataRecord record = dataCreate.createDataRecord(scanner.nextLine(), country);
        	    	if (record.getCountry().equals(m_selectedCountry)) {
        	    		m_dataRaw.add(record);
        	    		if (! m_dataStates.contains(record.getState()))
        	    		{
            	    		m_dataStates.add(record.getState());
            	    		System.out.println(record.getState());
        	    		}
            	        if (! m_dataYears.contains(record.getYear())) 
            	        {
            	        	m_dataYears.add(record.getYear());
            	        }
    	    		}
    	    		else 
    	    		{
        	    		m_dataCountries.add(record.getCountry());
    	    		}
    	    	}
    	    	catch (RecordParsingException e){
    	    		System.out.println(e.getMessage());
    	    	}
    	    }
    	}
 	}
 	
 	public void updatePlotData(String selectedCountry, String selectedState, Integer start, Integer end) {
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
		for(DataRecord rec : m_dataRaw) {
			String state = rec.getState();
			Integer year = rec.getYear();
			
			// Check to see if they are the state and year range we care about
			if (state.equals(selectedState) && 
			   ((year.compareTo(start) >= 0 && year.compareTo(end) <= 0))) {
						
				// Ok, we need to add this to the list of values for the month
				Integer month = rec.getMonth();
				Double value = rec.getTemp();
				
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
 	
 	public TreeMap<Integer, SortedMap<Integer, Double>> getPlotData() {
 		return m_plotData;
 	}
 	
 	public SortedSet<Integer> getYears() {
 		return m_dataYears;
 	}
 	
 	public SortedSet<String> getStates() {
 		return m_dataStates;
 	}
 	
 	public SortedSet<String> getCountries() {
 		return m_dataCountries;
 	}
 	
 	public TreeMap<Integer, Double> getMonthlyMin() {
 		return m_plotMonthlyMinValue;
 	}
 	
 	public TreeMap<Integer, Double> getMonthlyMax() {
 		return m_plotMonthlyMaxValue;
 	}
}
