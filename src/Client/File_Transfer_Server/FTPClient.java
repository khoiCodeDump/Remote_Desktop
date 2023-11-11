package Client.File_Transfer_Server;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FTPClient {
    private static final int PORT = 9960;
 
	public static void main(String[] args) throws IOException {
	    Socket socket = new Socket("192.168.0.104", 9970 );
	    try {
            File folder = new File( System.getProperty("user.home") + "/Desktop/" + "Files_for_Transfer");
            folder.mkdir();
            new WatchDirClient(folder.toPath(), true, socket.getOutputStream(), socket.getInputStream(), PORT).processEvents();

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
	    
		try (ServerSocket server = new ServerSocket(9980)) {
			System.out.println("Waiting for client...");
			Socket s = server.accept();
			System.out.println("Client accepted");
			
			
			
			
			
			DataInputStream in = new DataInputStream(s.getInputStream());
			
			String fileName = in.readUTF();
			long fileSize = in.readLong();
			
			
			FileReceiver receiver = new FileReceiver(PORT, new FileWriter("C:\\Users\\khoiv\\Desktop\\" + fileName), fileSize);

			new Thread() {
			    public void run() {
			        try {
			            receiver.receive();
			        } catch (IOException e) {
			        
			        }
			    }
			}.start();
			
			 
		}
	}

}

