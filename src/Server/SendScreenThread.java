package Server;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;


class SendScreenThread extends Thread{
	private OutputStream out;
	private Rectangle screenRect;
	private Robot robot;
	
	SendScreenThread(OutputStream out, Rectangle screenRect, Robot robot){
		this.out = out;
		this.screenRect = screenRect;
		this.robot = robot;
	}
	@Override
	public void run() {
	    synchronized(this){
	    	 while(true) {
	  	    	try {
	  	    		BufferedImage capture = robot.createScreenCapture(screenRect);
	  	    		
	  	    		ImageIO.write(capture, "jpg", out);
	  	        	out.flush();
	  			} catch (Exception e) {
	  				System.out.println("Server error");
	  			}
	 	     }
	    
	    }
	 
	}
}