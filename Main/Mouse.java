package Main;

import java.awt.event.*;


/**
 * mouseListener class needed for Painter class
 */
public class Mouse implements MouseListener, MouseMotionListener {


	private static int mouseX 		= -1;
	private static int mouseY 		= -1;
	private static int mouseButton  = -1;


	@Override
	public void mouseClicked(MouseEvent e) {
		mouseButton = e.getButton();
	}


	@Override
	public void mousePressed(MouseEvent e) {
		mouseButton = e.getButton();
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		mouseButton = -1;
	}


	@Override
	public void mouseDragged(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}


	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}


	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {}


	public static int getMouseX() {
		return mouseX;
	}


	public static int getMouseY() {
		return mouseY;
	}


	public static int getMouseButton() {
		return mouseButton;
	}

}
