package dataviewer3;

import java.util.List;

import javax.swing.JOptionPane;

public class StartCommand extends KeyCommand{

	public StartCommand(DataViewerUIHandler d) {
		super(d);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute(List<KeyCommand> commandList) {
		// set the start year
		Object selectedValue = JOptionPane.showInputDialog(null,
	             "Choose the start year", "Input",
	             JOptionPane.INFORMATION_MESSAGE, null,
	             UIHandler.data.getYears().toArray(), UIHandler.getStartYear());
		
		if(selectedValue != null) {
			OutputToConsole.info("User seleted: '%s'", selectedValue);
			Integer year = (Integer)selectedValue;
			if(year.compareTo(UIHandler.getEndYear()) > 0) {
				OutputToConsole.error("new start year (%d) must not be after end year (%d)", year, UIHandler.getEndYear());
			}
			else {
				if(!UIHandler.getStartYear().equals(year)) {
					UIHandler.setStartYear(year);
					commandList.add(new UpdateCommand(UIHandler));
					commandList.add(new UpdatePlotCommand(UIHandler));
				}
			}
		}
		
	}

}
