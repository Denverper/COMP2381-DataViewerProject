package dataviewer3;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class DataRecordFactory{
	
	private String country;
	private String state;
	private Integer year;
	private Integer month;
	private double temp;
	
	private final static int 		FILE_COUNTRY_IDX = 4;
	private final static int 		FILE_DATE_IDX = 0;
	private final static int 		FILE_NUM_COLUMNS = 5;
	private final static int 		FILE_STATE_IDX = 3;
	private final static int 		FILE_TEMPERATURE_IDX = 1;
	
	
	public DataRecord createDataRecord(String line, String selectedCountry) throws RecordParsingException {
		List<String> rawValues = new ArrayList<String>();
        try (Scanner rowScanner = new Scanner(line)) {
            rowScanner.useDelimiter(",");
            while (rowScanner.hasNext()) {
                rawValues.add(rowScanner.next());
            }
        }
        country = rawValues.get(FILE_COUNTRY_IDX);
        if(rawValues.size() != FILE_NUM_COLUMNS) {
        	OutputToConsole.trace("malformed line '%s'...skipping", line);
    		throw new RecordParsingException("");
        }
        else if(!rawValues.get(FILE_COUNTRY_IDX).equals(selectedCountry)) {
        	OutputToConsole.trace("skipping non-USA record: %s", rawValues);
        }
        else {
        	OutputToConsole.trace("processing raw data: %s", rawValues.toString());
        }
        try {
        	// Parse these into more useful objects than String        	
        	year = parseYear(rawValues.get(FILE_DATE_IDX));
        	if(year == null) {
        		throw new RecordParsingException("No Year");
        	}
        	
        	month = parseMonth(rawValues.get(FILE_DATE_IDX));
        	if(month == null) {
        		throw new RecordParsingException("No Month");
        	}
        	temp = Double.parseDouble(rawValues.get(FILE_TEMPERATURE_IDX));
        	//not going to use UNCERTAINTY yet
        	//values.add(Double.parseDouble(rawValues.get(FILE_UNCERTAINTY_IDX)));
        	state=rawValues.get(FILE_STATE_IDX);
        	// since all are the same country
        	//values.add(rawValues.get(FILE_COUNTRY_IDX));
        	
        	return new DataRecord(country,state,year,month,temp);
        }
        catch(NumberFormatException e) {
        	OutputToConsole.trace("unable to parse data line, skipping...'%s'", line);
        	throw new RecordParsingException(String.format("Skipping Line: '%s'", line));
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
    		OutputToConsole.trace("Unable to parse year from date: '%s'", dateString);
    	}
    	return ret;
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
    		OutputToConsole.trace("Unable to parse month from date: '%s'", dateString);
    		return null;
    	}
    	return ret;
	}
}
