package dataviewer3;

import java.util.List;

public class MenuCommand extends KeyCommand{

	public MenuCommand(DataViewerUIHandler d) {
		super(d);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute(List<KeyCommand> commandList) {
		UIHandler.state = UIHandler.state.menu();
		commandList.add(new UpdateCommand(UIHandler));		
	}



}
