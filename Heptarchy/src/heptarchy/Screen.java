package heptarchy;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferStrategy;

public class Screen extends Canvas implements MouseListener,
		MouseMotionListener, MouseWheelListener, KeyListener {

	private static final long serialVersionUID = 1L;
	private BufferStrategy strategy;
	private ScreenElement[] screenElements;

	public Screen(Frame enclosingFrame, World world) {
		super();
        setIgnoreRepaint(true);
        
        enclosingFrame.add(this);
        createBufferStrategy(2);
        strategy = getBufferStrategy();
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        addMouseWheelListener(this);
        
        //Test code
        this.setSize(988+200, 40+400+200);
        MapDisplay mapDisplay = new MapDisplay();
        mapDisplay.setRectSC(0, 40, 988, 600);
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
	
	
	@Override
	public void paint(Graphics g) {
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
	
	public BufferStrategy getStrategy() {
        return strategy;
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
	
	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

}
