import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class ReceiveScreenThread extends Thread{
	private InputStream in;
	private BufferedImage image;
	private Desktop_view remote_view;
	ReceiveScreenThread(InputStream in, Desktop_view remote_view){
		this.in = in;
		image = null;
		this.remote_view = remote_view;
	}
	@Override
	public void run() {
		while(true) {
	    	try {
	    		byte[] bytes = new byte[1024*1024];
				int count = 0;
				do{
					count+=in.read(bytes,count,bytes.length-count);
				}while(!(count>4 && bytes[count-2]==(byte)-1 && bytes[count-1]==(byte)-39));

				image = ImageIO.read(new ByteArrayInputStream(bytes));			    
				remote_view.updateImage(image);
			        
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	}
}
