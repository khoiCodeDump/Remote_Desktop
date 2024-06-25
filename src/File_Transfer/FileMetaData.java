package File_Transfer;

import java.nio.file.WatchEvent.Kind;

public class FileMetaData {
	protected Kind<?> eventKind;
	protected String child;
	protected long fileSize;
	
	FileMetaData(Kind<?> eventKind, String child, long fileSize){
		this.eventKind = eventKind;
		this.child = child;
		this.fileSize = fileSize;
	}

	
}
