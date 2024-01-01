package dataviewer3;

import java.util.List;

import javax.swing.JOptionPane;

public class EndCommand extends KeyCommand{

	public EndCommand(DataViewerUIHandler d) {
		super(d);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute(List<KeyCommand> commandList) {
		// set the end year
		Object selectedValue = JOptionPane.showInputDialog(null,
	             "Choose the end year", "Input",
	             JOptionPane.INFORMATION_MESSAGE, null,
	             UIHandler.data.getYears().toArray(), UIHandler.getEndYear());
		
		if(selectedValue != null) {
			OutputToConsole.info("User seleted: '%s'", selectedValue);
			Integer year = (Integer)selectedValue;
			if(year.compareTo(UIHandler.getStartYear()) < 0) {
				OutputToConsole.error("new end year (%d) must be not be before start year (%d)", year, UIHandler.getStartYear());
			}
			else {
				if(!UIHandler.getEndYear().equals(year)) {
					UIHandler.setEndYear(year);
					commandList.add(new UpdateCommand(UIHandler));
					commandList.add(new UpdatePlotCommand(UIHandler));
				}
			}
		}
		
	}



}
