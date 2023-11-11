package Client;
import java.awt.Dimension;
import java.awt.Point;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;

import Server.Data.Commands;

public class Data implements Serializable{
	Dimension screensize;
	Point mouse_coordinates;
	Commands cmd;
	int keyCode;
	int rotation;
	public enum Commands {
		PRESS_MOUSE,
		RELEASE_MOUSE,
		PRESS_KEY,
		RELEASE_KEY,
		MOVE_MOUSE,
		MOVE_MOUSEWHEEL;
	}
	public Data(Dimension screensize, Point mouse_coordinates){
		this.screensize = screensize;
		this.mouse_coordinates = mouse_coordinates;
	}
	public Data(Commands cmd, Point mouse_coordinates, int rotation){
		this.cmd = cmd;
		this.mouse_coordinates = mouse_coordinates;
		this.rotation = rotation;

	}
	public Data(Commands cmd, int keyCode){
		this.cmd = cmd;
		this.keyCode = keyCode;
	} 
}
