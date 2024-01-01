package dataviewer3;

import java.util.List;

public class QuitCommand extends KeyCommand{

	public QuitCommand(DataViewerUIHandler d) {
		super(d);
	}

	@Override
	public void execute(List<KeyCommand> commandList) {
		System.out.println("Bye");
		System.exit(0);
		
	}


	
	
}

