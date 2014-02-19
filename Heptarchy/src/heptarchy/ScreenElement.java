package heptarchy;

import java.awt.Graphics;
import java.awt.event.KeyEvent;

public abstract class ScreenElement {
	private int[] rectSC;
	
	public final void setRectSC(int x, int y, int w, int h) {
		rectSC = new int[4];
		rectSC[0] = x;
		rectSC[1] = y;
		rectSC[2] = w;
		rectSC[3] = h;

	}
	
	public final int[] getRectSC() {
		return rectSC;
	}
	
	public final boolean contains(int[] coordinateSC) {
		if ((coordinateSC[0] > getRectSC()[0] && coordinateSC[0] <= getRectSC()[0]
				+ getRectSC()[2])
				&& (coordinateSC[1] > getRectSC()[1] && coordinateSC[1] <= getRectSC()[1]
						+ getRectSC()[3])) {
			return true;
		} else {
			return false;
		}
	}
	
	public abstract void processMouseClick(int[] clickCoordinatesSC);
	public abstract void processMouseMovement(int[] cursorCoordinatesSC);
	public abstract void processMouseWheelEvent(int notches, int[] cursorPositionSC);
	public abstract boolean attemptToPassKeyPressed(KeyEvent e);
	public abstract boolean attemptToPassKeyReleased(KeyEvent e);
	public abstract boolean attemptToPassKeyTyped(KeyEvent e);
	public abstract void render(Graphics g);
}
