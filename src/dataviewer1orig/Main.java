package dataviewer1orig;

import java.io.FileNotFoundException;
 
public class Main {
    public static void main(String[] args) throws FileNotFoundException {
    	String data = "data/GlobalLandTemperaturesByState.csv";
    	//String data = "data/sample.csv";
        new DataViewerApp(data);
    }
}

