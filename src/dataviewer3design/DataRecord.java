package dataviewer3;

public class DataRecord {
	private String country;
	private String state;
	private Integer year;
	private Integer month;
	private double temp;
	
	
	public DataRecord(String country, String state, Integer year, Integer month, double temp) {
		this.country = country;
		this.state = state;
		this.year = year;
		this.month = month;
		this.temp = temp;
	}
	
	public String getCountry(){
		return country;
	}
	
	public String getState() {
		return state;
	}

	public Integer getYear() {
		return year;
	}

	public Integer getMonth() {
		return month;
	}

	public double getTemp() {
		return temp;
	}
	
	
}
