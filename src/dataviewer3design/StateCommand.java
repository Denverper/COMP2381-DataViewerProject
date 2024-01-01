package dataviewer3;

import java.util.List;

import javax.swing.JOptionPane;

public class StateCommand extends KeyCommand{

	public StateCommand(DataViewerUIHandler d) {
		super(d);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute(List<KeyCommand> commandList) {
		// set the state
		Object selectedValue = JOptionPane.showInputDialog(null,
	             "Choose a State", "Input",
	             JOptionPane.INFORMATION_MESSAGE, null,
	             UIHandler.data.getStates().toArray(),  UIHandler.getState());
		
		if(selectedValue != null) {
			OutputToConsole.info("User selected: '%s'", selectedValue);
			if(!selectedValue.equals(UIHandler.getState())) {
				// change in data
				UIHandler.setState((String)selectedValue);
				commandList.add(new UpdateCommand(UIHandler));
				commandList.add(new UpdatePlotCommand(UIHandler));
			}
		}
		
	}


}
