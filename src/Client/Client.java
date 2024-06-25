package Client;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.HeadlessException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import Server.Data;


public class Client {
	static Desktop_view remote_view;
	
	public static void main(String[] args) throws IOException {
		InetAddress host = InetAddress.getLocalHost();
	    try (Socket socket = new Socket(host.getHostName(), 9876)) {
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
//	    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
   
			try {
				Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			    Point mouse_pos = MouseInfo.getPointerInfo().getLocation();
			    oos.writeObject(new Data(screenSize, mouse_pos));
				
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			remote_view = new Desktop_view(socket.getOutputStream());
  
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						remote_view.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			InputStream in = socket.getInputStream();
			ReceiveScreenThread getScreen = new ReceiveScreenThread(in, remote_view);
			getScreen.setDaemon(true);
			getScreen.start();
		} catch (HeadlessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	}
	
}
