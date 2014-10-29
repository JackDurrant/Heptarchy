package heptarchy;

/**
*
* @author Jack Durrant
*/
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class InternalScreen extends BufferedImage {
	private ScreenElement[] screenElements;

	public InternalScreen(World world) {
		super(988+200, 40+400+200, TYPE_INT_RGB);
		
        MapDisplay mapDisplay = new MapDisplay();
        mapDisplay.setRectSC(0, 40, 988, 600);
        mapDisplay.passScreenDimensions(this.getWidth(), this.getHeight());
        mapDisplay.initialize(world);
        addScreenElement(mapDisplay);
        StatusBar statusBar = new StatusBar();
        statusBar.setRectSC(0, 0, 988, 40);
        addScreenElement(statusBar);
        PanelBar panelBar = new PanelBar();
        panelBar.setRectSC(988, 0, 200, 40);
        addScreenElement(panelBar);
        SidePanel sidePanel = new SidePanel();
        sidePanel.setRectSC(988, 40, 200, 400);
        addScreenElement(sidePanel);
        Minimap minimap = new Minimap();
        minimap.setRectSC(988, 440, 200, 200);        
        minimap.initialize(world, mapDisplay);
        addScreenElement(minimap);
	}
	
	
	public void render(Graphics g) {
		int numberOfScreenElements = screenElements.length;
		
		g.setColor(Color.red);
        g.fillRect(0, 0, 988+200, 40+400+200);
        g.setColor(Color.BLACK);
		
		for(int i=0; i<numberOfScreenElements; i++) {
			if(screenElements[i].getRectSC()!=null) {
				screenElements[i].render(g);
			}
		}
		
		//g.setColor(Color.black);
		//g.drawLine(10, 0, 10, 600+40);
	}
	
	public void addScreenElement(ScreenElement newScreenElement) {
		if(screenElements==null) {
			screenElements = new ScreenElement[1];
			screenElements[0] = newScreenElement;
		} else {
			int previousNumberOfScreenElements = screenElements.length;
			ScreenElement[] tempScreenElements =
					new ScreenElement[previousNumberOfScreenElements+1];
			
			for(int i=0; i<previousNumberOfScreenElements; i++) {
				tempScreenElements[i] = screenElements[i];
			}
			tempScreenElements[previousNumberOfScreenElements] = newScreenElement;
			
			screenElements = tempScreenElements;
		}
	}
	
	public void passKeyPressed(KeyEvent e) {
		boolean keyPressResolved = false;
		
		int numberOfScreenElements = screenElements.length;
		for(int i=0; i<numberOfScreenElements; i++) {
			keyPressResolved = screenElements[i].attemptToPassKeyPressed(e);
			
			if(keyPressResolved) {
				break;
			}
		}
	}
	
	public void passKeyReleased(KeyEvent e) {
		boolean keyReleaseResolved = false;
		
		int numberOfScreenElements = screenElements.length;
		for(int i=0; i<numberOfScreenElements; i++) {
			keyReleaseResolved = screenElements[i].attemptToPassKeyReleased(e);
			
			if(keyReleaseResolved) {
				break;
			}
		}
	}

	public void passKeyTyped(KeyEvent e) {
		boolean keyTypeResolved = false;
		
		int numberOfScreenElements = screenElements.length;
		for(int i=0; i<numberOfScreenElements; i++) {
			keyTypeResolved = screenElements[i].attemptToPassKeyTyped(e);
			
			if(keyTypeResolved) {
				break;
			}
		}
	}

	public void passMouseWheelMoved(int notches, int[] cursorPositionSC) {		
		int numberOfScreenElements = screenElements.length;
		for(int i=0; i<numberOfScreenElements; i++) {
			if(screenElements[i].contains(cursorPositionSC)==true) {
				screenElements[i].processMouseWheelEvent(notches, cursorPositionSC);
				break;
			}
		}
	}

	public void passMouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub

	}
	
	public void passMouseMoved(int[] cursorCoordinatesSC) {		
		int numberOfScreenElements = screenElements.length;
		for(int i=0; i<numberOfScreenElements; i++) {
			screenElements[i].processMouseMovement(cursorCoordinatesSC);
		}
	}
	
	public void passMouseClicked(int[] clickCoordinatesSC) {		
		int numberOfScreenElements = screenElements.length;
		for(int i=0; i<numberOfScreenElements; i++) {
			if(screenElements[i].contains(clickCoordinatesSC)==true) {
				screenElements[i].processMouseClick(clickCoordinatesSC);
				break;
			}
		}
	}

	public void passMouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void passMouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void passMousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void passMouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

}
