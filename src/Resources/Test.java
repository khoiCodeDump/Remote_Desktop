package Resources;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Test {
    private static final int PORT = 9999;
 
	public static void main(String[] args) throws IOException {
		
	
			try {
	            File folder = new File( System.getProperty("user.home") + "/Desktop/" + "Files_for_Transfer");
	            folder.mkdir();
	            new WatchDir(folder.toPath(), true).processEvents();

	          } catch (IOException e) {
	            System.out.println("An error occurred.");
	            e.printStackTrace();
	          }
			
			
//			DataInputStream in = new DataInputStream(s.getInputStream());
//			
//			String fileName = in.readUTF();
//			long fileSize = in.readLong();
//			
//			
//			FileReceiver receiver = new FileReceiver(PORT, new FileWriter("C:\\Users\\khoiv\\Desktop\\" + fileName), fileSize);
//
//			new Thread() {
//			    public void run() {
//			        try {
//			            receiver.receive();
//			        } catch (IOException e) {
//			        
//			        }
//			    }
//			}.start();
//			
			 
//		}
	}

}
