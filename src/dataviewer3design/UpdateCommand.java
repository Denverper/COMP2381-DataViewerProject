package dataviewer3;

import java.util.List;

public class UpdateCommand extends KeyCommand{

	public UpdateCommand(DataViewerUIHandler d) {
		super(d);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute(List<KeyCommand> commandList) {
		UIHandler.update();
		
	}



}
