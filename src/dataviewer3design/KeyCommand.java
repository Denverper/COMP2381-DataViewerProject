package dataviewer3;

import java.util.List;

public abstract class KeyCommand {
	protected DataViewerUIHandler UIHandler;
	
	public KeyCommand(DataViewerUIHandler d) {
		UIHandler = d;

	}
	
	public abstract void execute(List<KeyCommand> commandList);
}
