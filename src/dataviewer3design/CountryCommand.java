package dataviewer3;

import java.io.FileNotFoundException;
import java.util.List;

import javax.swing.JOptionPane;

public class CountryCommand extends KeyCommand{

	public CountryCommand(DataViewerUIHandler d) {
		super(d);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute(List<KeyCommand> commandList) {
		// set the Country
		Object selectedValue = JOptionPane.showInputDialog(null,
	             "Choose a Country", "Input",
	             JOptionPane.INFORMATION_MESSAGE, null,
	             UIHandler.data.getCountries().toArray(), UIHandler.getCountry());
		
		if(selectedValue != null) {
			OutputToConsole.info("User selected: '%s'", selectedValue);
			if(!selectedValue.equals(UIHandler.getCountry())) {
				// change in data
				UIHandler.setCountry((String)selectedValue);
				try {
					UIHandler.data.loadData(UIHandler.getCountry());
		    	    // update selections (not including country) for the newly loaded data
		            UIHandler.setState(UIHandler.data.getStates().first());
		            UIHandler.setStartYear(UIHandler.data.getYears().first());
		            UIHandler.setEndYear(UIHandler.data.getYears().last());

		            OutputToConsole.info("loaded data for %d states", UIHandler.data.getStates().size());
		            OutputToConsole.info("loaded data for %d years [%d, %d]", UIHandler.data.getYears().size(), UIHandler.getStartYear(), UIHandler.getEndYear());
				}
				catch(FileNotFoundException e) {
					// convert to a runtime exception since
					// we can't add throws to this method
					throw new RuntimeException(e);
				}
				commandList.add(new UpdateCommand(UIHandler));
				commandList.add(new UpdatePlotCommand(UIHandler));
			}
		}
		
	}


}
