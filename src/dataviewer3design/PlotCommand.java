package dataviewer3;

import java.util.List;

public class PlotCommand extends KeyCommand{

	public PlotCommand(DataViewerUIHandler d) {
		super(d);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute(List<KeyCommand> commandList) {
		// plot the data
		UIHandler.state = UIHandler.state.plot();
		if(UIHandler.data.getPlotData() == null) {
			// first time going to render data need to generate the plot data
			commandList.add(new UpdatePlotCommand(UIHandler));
		}
		commandList.add(new UpdateCommand(UIHandler));
		
	}


}
