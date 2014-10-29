/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
import java.awt.Font;
//import java.awt.FontMetrics;

public class StatusBarOld extends Canvas {
	private static final long serialVersionUID = 1L;
	private BufferStrategy strategy;
    private int[] screenDimensions;
    private Font font = new Font("Arial", Font.PLAIN, 40);
    
    public StatusBarOld() {
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
        
        int txSC = 10;
        int tySC = 32;
        g.setColor(Color.pink);
        g.setFont(font);
        
        g.drawString("Northumbria"
                + "           King Æthlfrith"
                + "           500 AD", txSC, tySC);
    }
}