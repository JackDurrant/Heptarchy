package heptarchy;

/**
 *
 * @author Jack Durrant
 */
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

public class MinimapOld extends Canvas implements MouseListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BufferStrategy strategy;
    private BufferedImage minimapImage;
    private BufferedImage minimapConstructionImage;
    private boolean minimapToUpdate = true;
    private int[] screenDimensions;
    private double scalingFactor;
    private int[] pendingClickCoordinatesSC = null;
    
    public MinimapOld() {
        super();
        setIgnoreRepaint(true);
    }
    
    public void initialize(World world) {
        createBufferStrategy(2);
        strategy = getBufferStrategy();
        addMouseListener(this);
        
        double xRatio = world.getWorldDimensions()[0]/screenDimensions[0];
        double yRatio = world.getWorldDimensions()[1]/screenDimensions[1];
        if(xRatio > yRatio){
            scalingFactor = xRatio;
        } else {
            scalingFactor = yRatio;
        }
        
        int wAC = world.getWorldDimensions()[0];
        int hAC = world.getWorldDimensions()[1];
        int wMD = (int) (((double) wAC)/scalingFactor);
        int hMD = (int) (((double) hAC)/scalingFactor);
        minimapImage = new BufferedImage(wMD, hMD, BufferedImage.TYPE_INT_ARGB);
        minimapConstructionImage = new BufferedImage(wAC, hAC, BufferedImage.TYPE_INT_ARGB);
    }

    public BufferStrategy getStrategy() {
        return strategy;
    }
    
    public int[] getMouseClick() {
        int[] tempCoordinatesSC;
        tempCoordinatesSC = pendingClickCoordinatesSC;
        pendingClickCoordinatesSC = null;
        return tempCoordinatesSC;
    }
    
    public void setScreenDimensions(int x, int y) {
        screenDimensions = new int[2];
        screenDimensions[0] = x;
        screenDimensions[1] = y;
        setSize(new Dimension(x, y));
    }
    
    public int convertACtoMC(int AC){
        return (int) (((double)AC)/scalingFactor);
    }
    
    public int xConvertMCtoSC(int MC){
        return MC + ((screenDimensions[0] - minimapImage.getWidth())/2);
    }
    
    public int yConvertMCtoSC(int MC){
        return MC + ((screenDimensions[1] - minimapImage.getHeight())/2);
    }
    
    public int convertMDtoSD(int MD){
        return MD;
    }
    
    public int xConvertACtoSC(int AC){
        return (int) (((double) AC)/scalingFactor) + ((screenDimensions[0]-minimapImage.getWidth())/2);
    }
    
    public int yConvertACtoSC(int AC){
        return (int) (((double) AC)/scalingFactor) + ((screenDimensions[1]-minimapImage.getHeight())/2);
    }
    
    public int convertADtoSD(int AD){
        return (int) (((double)AD)/scalingFactor);
    }
    
    public int xConvertSCtoAC(int SC) {
        return (int) scalingFactor*(SC - ((screenDimensions[0]-minimapImage.getWidth())/2));
    }
    
    public int yConvertSCtoAC(int SC) {
        return (int) scalingFactor*(SC - ((screenDimensions[1]-minimapImage.getHeight())/2));
    }
    
    //Methods from MouseListener
    @Override
    public void mouseClicked(MouseEvent e) {
        pendingClickCoordinatesSC = new int[2];
        pendingClickCoordinatesSC[0] = e.getX();
        pendingClickCoordinatesSC[1] = e.getY();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //System.out.println("Mouse Pressed.");
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //System.out.println("Mouse Released.");
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //System.out.println("Mouse Entered.");
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //System.out.println("Mouse Exited.");
    }
    
    public void processClick(World world, MapDisplayOld map ) {
        int[] clickCoordinatesSC;
        int[] clickCoordinatesAC = new int[2];
        int[] newViewCoordinatesAC = new int[2];
        int maxLimitAC;
        clickCoordinatesSC = getMouseClick();

        //Abort if there has been no mouse click
        if (clickCoordinatesSC == null) {
            return;
        }

        //Convert clickCoordinatesSC to clickCoordinatesAC
        clickCoordinatesAC[0] = xConvertSCtoAC(clickCoordinatesSC[0]);
        clickCoordinatesAC[1] = yConvertSCtoAC(clickCoordinatesSC[1]);
        
        //Check if AC value is legal viewPosition, abort if not
        maxLimitAC = world.getWorldDimensions()[0];
        if((clickCoordinatesAC[0] < 0) || (clickCoordinatesAC[0] > maxLimitAC)){
            return;
        }
        maxLimitAC = world.getWorldDimensions()[1];
        if((clickCoordinatesAC[1] < 0) || (clickCoordinatesAC[1] > maxLimitAC)){
            return;
        }

        //Use clickCoordinatesAC to set where view should go
        newViewCoordinatesAC[0] = clickCoordinatesAC[0] - map.getViewWidthAC()/2;
        newViewCoordinatesAC[1] = clickCoordinatesAC[1] - map.getViewHeightAC()/2;
        map.setViewPosition(newViewCoordinatesAC);
    }
    
    public void updateMinimapImage(World world){
        if(minimapToUpdate == false){
            return;
        }
        
        Graphics2D gC = (Graphics2D) minimapConstructionImage.getGraphics();
        int xAC, yAC, wAC, hAC;
        for (int provinceNo = 0; provinceNo < world.getNoOfProvinces(); provinceNo++) {
            xAC = world.getProvince(provinceNo).getRect()[0];
            yAC = world.getProvince(provinceNo).getRect()[1];
            wAC = world.getProvince(provinceNo).getRect()[2];
            hAC = world.getProvince(provinceNo).getRect()[3];
            
            gC.drawImage(world.getProvince(provinceNo).getImage(), xAC, yAC, wAC, hAC, null);
        }
        
        
        Graphics2D g = (Graphics2D) minimapImage.getGraphics();
        int xMC, yMC, wMC, hMC;
        xMC = convertACtoMC(0);
        yMC = convertACtoMC(0);
        wMC = convertACtoMC(world.getWorldDimensions()[0]);
        hMC = convertACtoMC(world.getWorldDimensions()[1]);
        g.drawImage(minimapConstructionImage, xMC, yMC, wMC, hMC, null);
        
        minimapToUpdate = false;
    }
    
    public void update(Graphics g, MapDisplayOld map){
        int xSC, ySC, wSC, hSC;
        xSC = xConvertMCtoSC(0);
        ySC = yConvertMCtoSC(0);
        wSC = convertMDtoSD(minimapImage.getWidth());
        hSC = convertMDtoSD(minimapImage.getHeight());

        g.clearRect(0, 0, screenDimensions[0], screenDimensions[1]);
        g.setColor(Color.WHITE);
        g.fillRect(xSC, ySC, wSC, hSC);
        g.drawImage(minimapImage, xSC, ySC, wSC, hSC, null);
        int vPRect[] = map.getViewPositionRectAC();
        g.setColor(Color.BLACK);
        
        xSC = xConvertACtoSC(vPRect[0]);
        ySC = yConvertACtoSC(vPRect[1]);
        wSC = convertADtoSD(vPRect[2]);
        hSC = convertADtoSD(vPRect[3]);
        g.drawRect(xSC, ySC, wSC, hSC);
    }
}
