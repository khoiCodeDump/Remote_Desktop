package File_Transfer;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import static java.nio.file.StandardWatchEventKinds.*;


public class FTPServer {
    private static final int PORT = 9999;
    private static final String FOLDER_FOR_TRANSFER = System.getProperty("user.home") + "/Desktop/" + "Files_for_Transfer";
	public static void main(String[] args) throws IOException {
		
		try (ServerSocket server = new ServerSocket(9980)) {
			System.out.println("Waiting for client...");
			Socket s = server.accept();
			System.out.println("Client accepted");
			
			WatchDirServer watchService = null;
			ObjectInputStream in = new ObjectInputStream(s.getInputStream());
			ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
			try {
	            File folder = new File(FOLDER_FOR_TRANSFER );
	            folder.mkdir();
	            watchService = new WatchDirServer(folder.toPath(), true, out);
	            watchService.processEvents();

	          } catch (IOException e) {
	            System.out.println("An error occurred.");
	            e.printStackTrace();
	          }
			
			while(true) {
				FileMetaData data = (FileMetaData) in.readObject();
				String path = processPath(data.child);

				if(data.eventKind == ENTRY_DELETE) {
					
					File toBeDeleted = new File(FOLDER_FOR_TRANSFER + path);
					if(toBeDeleted.isDirectory()) {
						deleteFilesRecursive(toBeDeleted);
					}
					else {
						toBeDeleted.delete();
					}
				}
				else if(data.eventKind == ENTRY_CREATE){
					
					if(data.fileSize == -1) {
						File dir = new File(path);
						dir.mkdir();
					}
					else {

						FileReceiver receiver = new FileReceiver(PORT, new FileWriter(FOLDER_FOR_TRANSFER + path), data.fileSize);
						new Thread() {
						    public void run() {
						        try {
						            receiver.receive();
						        } catch (IOException e) {
						        
						        }
						    }
						}.start();
						out.writeObject(null);

					}
				}
				else if(data.eventKind == ENTRY_MODIFY) {
					FileReceiver receiver = new FileReceiver(PORT, new FileWriter(FOLDER_FOR_TRANSFER + path), data.fileSize);
					new Thread() {
					    public void run() {
					        try {
					            receiver.receive();
					        } catch (IOException e) {
					        
					        }
					    }
					}.start();
					out.writeObject(null);
				}
			}
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	private static void deleteFilesRecursive(File file) {
		File[] files = file.listFiles();
		for(File curFile : files) {
			if(curFile.isDirectory()) {
				deleteFilesRecursive(curFile);
			}
			else curFile.delete();
		}
		file.delete();
	}
	private static String processPath(String path) {
		String[] paths = path.split("/");
		boolean foundRequiredPath = false;
		String processedPath = "";
		for(int i=0; i<paths.length; i++) {
			if(foundRequiredPath) {
				processedPath += "/" + paths[i];
			}
			if(paths[i].equals("Files_for_Transfer")) {
				foundRequiredPath = true;
			}
			
		}
		return processedPath;
	}
}