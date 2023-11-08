import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.imgscalr.Scalr;

import Server.Data;
import Server.Data.Commands;

public class Desktop_view extends JFrame implements KeyListener, MouseMotionListener, MouseListener, MouseWheelListener {

	private JPanel contentPane;
	private BufferedImage image;
	private int scaledWidth, scaledHeight;
	private ObjectOutputStream out;
	/**
	 * Create the frame.
	 */
	public Desktop_view(OutputStream outStream) {
		setExtendedState(getExtendedState()|JFrame.MAXIMIZED_BOTH);	
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, scaledWidth, scaledHeight);
		try {
			out = new ObjectOutputStream(outStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		 
		contentPane = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
	            protected void paintComponent(Graphics g) {
	                super.paintComponent(g);
	                g.drawImage(image, 0, 0, null);
	                
	         }
		};
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.addMouseListener(this);
		contentPane.addMouseMotionListener(this);
		contentPane.addKeyListener(this);
		contentPane.addMouseWheelListener(this);
		setContentPane(contentPane);
		
	}
	public synchronized void updateImage(BufferedImage image) {

		this.image = Scalr.resize(image, Scalr.Method.SPEED, Scalr.Mode.AUTOMATIC, scaledWidth, scaledHeight, Scalr.OP_ANTIALIAS);
		repaint();
		
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
		Commands cmd = Data.Commands.PRESS_MOUSE;
		try {
			out.writeObject( new Data(cmd,e.getButton()));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		Point curPoint = e.getPoint();
		Commands cmd = Data.Commands.RELEASE_MOUSE;
		try {
			out.writeObject( new Data(cmd, e.getButton()));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		Point curPoint = e.getPoint();
		Commands cmd = Data.Commands.MOVE_MOUSE;
		try {
			out.writeObject( new Data(cmd, curPoint, 0));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		int keyCode= e.getKeyCode();
		Commands cmd = Data.Commands.PRESS_KEY;
		try {
			out.writeObject( new Data(cmd, keyCode));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		int keyCode= e.getKeyCode();
		Commands cmd = Data.Commands.RELEASE_KEY;
		try {
			out.writeObject( new Data(cmd, keyCode));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		// TODO Auto-generated method stub
		
		Commands cmd = Data.Commands.RELEASE_KEY;
		try {
			out.writeObject( new Data(cmd, null, e.getWheelRotation() ));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
}
