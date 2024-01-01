package dataviewer3;

import java.util.List;

import javax.swing.JOptionPane;

public class VizualizationCommand extends KeyCommand{

	public VizualizationCommand(DataViewerUIHandler d) {
		super(d);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute(List<KeyCommand> commandList) {
		// set the visualization
		Object selectedValue = JOptionPane.showInputDialog(null,
				"Choose the visualization mode", "Input",
				JOptionPane.INFORMATION_MESSAGE, null,
				DataViewerUIHandler.VISUALIZATION_MODES, UIHandler.getVizualization());

		if(selectedValue != null) {
			OutputToConsole.info("User seleted: '%s'", selectedValue);
			String visualization = (String)selectedValue;
			if(!UIHandler.getVizualization().equals(visualization)) {
				UIHandler.setVizualization(visualization);
				commandList.add(new UpdateCommand(UIHandler));
			}
		}
		
	}



}
