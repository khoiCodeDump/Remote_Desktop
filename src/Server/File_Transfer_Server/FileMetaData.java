package Server.File_Transfer_Server;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

public class FileMetaData {
	protected WatchEvent.Kind eventKind;
	protected String child;
	protected long fileSize;
	
	FileMetaData(WatchEvent.Kind eventKind, String child, long fileSize){
		this.eventKind = eventKind;
		this.child = child;
		this.fileSize = fileSize;
	}
	
}
