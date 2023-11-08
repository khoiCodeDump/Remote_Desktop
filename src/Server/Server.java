package Server;
import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import org.imgscalr.Scalr;


public class Server {
	 private static ServerSocket server;
	 //socket server port on which it will listen
	 private static int port = 9876;
	 public static void main(String args[]) throws IOException, ClassNotFoundException{
	 //create the socket server object
		 server = new ServerSocket(port);
//	     server.setReuseAddress(true);
       	 Socket s = server.accept();
       	 
         Robot robot = null;
         ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
         ObjectInputStream in = new ObjectInputStream(s.getInputStream());
         Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

         float screenHRatio = screenSize.height;
         float screenWRatio = screenSize.width;

         try {
            robot = new Robot();
            Data data = (Data) in.readObject();
            screenHRatio = screenHRatio/data.screensize.height;
            screenWRatio = screenWRatio/data.screensize.width;
        	
        	
         } catch (AWTException e1) {
			e1.printStackTrace();
         }
         
         Rectangle screenRect = new Rectangle(screenSize);
 	     SendScreenThread daemonThread = new SendScreenThread(s.getOutputStream(), screenRect, robot);
 	     daemonThread.setDaemon(true);
 	     daemonThread.start();
 	     
 	     while(true) {
 	    	//listen for mouse events then update mouse
 	    	Data data = (Data) in.readObject();
 	    	if(data.cmd == Data.Commands.PRESS_MOUSE) {
 	    		robot.mousePress(data.keyCode);
 	    	}
 	    	else if(data.cmd == Data.Commands.RELEASE_MOUSE) {
 	    		robot.mouseRelease(data.keyCode);

 	    	}
 	    	else if(data.cmd == Data.Commands.PRESS_KEY) {
 	    		robot.keyPress(data.keyCode);
 	    	}
 	    	else if(data.cmd == Data.Commands.RELEASE_KEY) {
 	    		robot.keyRelease(data.keyCode);
 	    	}
 	    	else if(data.cmd == Data.Commands.MOVE_MOUSEWHEEL) {
 	    		robot.mouseWheel(data.rotation);
 	    	}
 	    	else if(data.cmd == Data.Commands.MOVE_MOUSE) {
 	    		
 	    		robot.mouseMove( (int) (data.mouse_coordinates.x*screenWRatio), (int) (data.mouse_coordinates.y*screenHRatio));

 	    	}
 	     }
	        
	 }
	   
}



