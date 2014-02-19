package heptarchy;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferStrategy;

public class ExternalScreen extends Canvas implements MouseListener,
		MouseMotionListener, MouseWheelListener, KeyListener {

	private static final long serialVersionUID = 1L;
	private BufferStrategy strategy;
	private InternalScreen internalScreen;
	private float xConversionFactor;
	private float yConversionFactor;

	public ExternalScreen(Frame enclosingFrame, World world) {
		super();
        setIgnoreRepaint(true);        
        enclosingFrame.add(this);
        createBufferStrategy(2);
        strategy = getBufferStrategy();
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        addMouseWheelListener(this);
        
        resizeScreen(988+200, 40+400+200);
        
        internalScreen = new InternalScreen(world);
        
        requestFocus();
	}
	
	//Calling this resize makes stack overflow error?
	public void resizeScreen(int width, int height) {
		setSize(width, height);
		
		//Calculate external/internal screen conversion factors
        xConversionFactor = ((float) 1188)/((float) width);
        yConversionFactor = ((float) 640)/((float) height);
	}
	
	@Override
	public void paint(Graphics g) {
		g.setColor(Color.red);
        g.fillRect(0, 0, 988+200, 40+400+200);
        g.setColor(Color.BLACK);
        
        Graphics2D iG = (Graphics2D) internalScreen.getGraphics();
        internalScreen.render(iG);
        iG.dispose();
        
        g.drawImage(internalScreen, 0, 0, this.getWidth(), this.getHeight(), null);
        
		//g.setColor(Color.black);
		//g.drawLine(10, 0, 10, 600+40);
	}
	
	public BufferStrategy getStrategy() {
        return strategy;
    }
	
	public int xConvertESCtoSC(int xESC) {		
		return (int) (xConversionFactor * ((float) xESC));
	}
	
	public int yConvertESCtoSC(int yESC) {
		return (int) (yConversionFactor * ((float) yESC));
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		internalScreen.passKeyPressed(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		internalScreen.passKeyReleased(e);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		internalScreen.passKeyTyped(e);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int notches = e.getWheelRotation();
		int[] cursorPositionSC = {xConvertESCtoSC(e.getX()), yConvertESCtoSC(e.getY())};
		internalScreen.passMouseWheelMoved(notches, cursorPositionSC);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		internalScreen.passMouseDragged(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		int[] cursorCoordinatesSC = {xConvertESCtoSC(e.getX()), yConvertESCtoSC(e.getY())};
		internalScreen.passMouseMoved(cursorCoordinatesSC);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		int[] clickCoordinatesSC = {xConvertESCtoSC(e.getX()), yConvertESCtoSC(e.getY())};
		internalScreen.passMouseClicked(clickCoordinatesSC);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		internalScreen.passMouseEntered(e);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		internalScreen.passMouseExited(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		internalScreen.passMousePressed(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		internalScreen.passMouseReleased(e);
	}

}
