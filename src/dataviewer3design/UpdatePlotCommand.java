package dataviewer3;

import java.util.List;

public class UpdatePlotCommand extends KeyCommand{

	public UpdatePlotCommand(DataViewerUIHandler d) {
		super(d);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute(List<KeyCommand> commandList) {
		UIHandler.data.updatePlotData(UIHandler.getCountry(), UIHandler.getState(), UIHandler.getStartYear(), UIHandler.getEndYear());
		
	}


}
