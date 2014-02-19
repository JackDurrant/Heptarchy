package heptarchy;

/**
 *
 * @author Jack Durrant
 */
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;
//import java.awt.FontMetrics;
import java.awt.event.KeyEvent;

public class StatusBar extends ScreenElement {
	private int[] cursorCoordinatesSC = null;
	private Font font = new Font("Arial", Font.PLAIN, 40);

	public StatusBar() {
		//Constructor
	}

	@Override
	public void processMouseClick(int[] clickCoordinatesSC) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void processMouseMovement(int[] cursorCoordinatesSC) {
		if(this.contains(cursorCoordinatesSC)) {
			this.cursorCoordinatesSC = cursorCoordinatesSC;
		} else {
			this.cursorCoordinatesSC = null;
		}
	}
	
	@Override
	public void processMouseWheelEvent(int notches, int[] cursorPositionSC) {
		//Do nothing
	}
	
	@Override
	public boolean attemptToPassKeyPressed(KeyEvent e) {
		return false;
	}
	
	@Override
	public boolean attemptToPassKeyReleased(KeyEvent e) {
		return false;
	}
	
	@Override
	public boolean attemptToPassKeyTyped(KeyEvent e) {
		return false;
	}

	@Override
	public void render(Graphics g) {
		g.setColor(Color.gray);
        g.fillRect(getRectSC()[0], getRectSC()[1], getRectSC()[2], getRectSC()[3]);
        
        int txSC = 10;
        int tySC = 32;
        g.setColor(Color.pink);
        g.setFont(font);
        
        g.drawString("Northumbria"
                + "           King Æthlfrith"
                + "           500 AD", txSC, tySC);
		
	}
}