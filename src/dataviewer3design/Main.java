package dataviewer3;

import java.io.FileNotFoundException;
 
public class Main {
    public static void main(String[] args) throws FileNotFoundException {
    	String data = "data/GlobalLandTemperaturesByState.csv";
    	//String data = "data/sample.csv";
    	DataHandler dataReader = new DataHandler(data);
    	DataViewerUIHandler dataUI = new DataViewerUIHandler(dataReader);
    }
}

