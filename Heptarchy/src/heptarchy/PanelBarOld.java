package heptarchy;

/**
 *
 * @author Jack Durrant
 */
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

public class PanelBarOld extends Canvas {
	private static final long serialVersionUID = 1L;
	private BufferStrategy strategy;
    private int[] screenDimensions;
    
    public PanelBarOld() {
        super();
        setIgnoreRepaint(true);
    }
    
    public void initialize() {
        createBufferStrategy(2);
        strategy = getBufferStrategy();
    }

    public BufferStrategy getStrategy() {
        return strategy;
    }
    
    public void setScreenDimensions(int x, int y) {
        screenDimensions = new int[2];
        screenDimensions[0] = x;
        screenDimensions[1] = y;
        setSize(new Dimension(x, y));
    }
    
    @Override
    public void update(Graphics g){
        g.setColor(Color.gray);
        g.fillRect(0, 0, screenDimensions[0], screenDimensions[1]);
    }
    
}