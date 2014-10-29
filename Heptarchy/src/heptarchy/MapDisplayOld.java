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
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

public class MapDisplayOld extends Canvas implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BufferStrategy strategy;
    private int[] screenDimensions;
    private int[] mapBoundsAC;
    private int provinceSelected = -1;
    private int provinceSelectionPhasor;
    private int[] pendingClickCoordinatesSC = null;
    private int viewZoom = 2;
    private int[] viewCoordinatesAC = {0, 0};
    private int minimumPermittedZoom = 1;
    private int maximumPermittedZoom = 10;
    private int viewMovementSpeed = 50;
    private double cursorMovementAreaProportion = 0.0;
    private int[] cursorCoordinatesSC = null;
    private boolean viewMovingUp = false;
    private boolean viewMovingDown = false;
    private boolean viewMovingLeft = false;
    private boolean viewMovingRight = false;

    public MapDisplayOld() {
        super();
        setIgnoreRepaint(true);
    }
    
    public void initialize(World world) {
        createBufferStrategy(2);
        strategy = getBufferStrategy();
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        addMouseWheelListener(this);
        mapBoundsAC = world.getWorldDimensions();
    }

    public BufferStrategy getStrategy() {
        return strategy;
    }

    public int getScreenWidth() {
        return screenDimensions[0];
    }

    public int getScreenHeight() {
        return screenDimensions[1];
    }
    public int getProvinceSelected() {
        return provinceSelected;
    }

    public int getProvinceSelectionPhasor() {
        return provinceSelectionPhasor;
    }

    public int[] getMouseClick() {
        int[] tempCoordinatesSC;
        tempCoordinatesSC = pendingClickCoordinatesSC;
        pendingClickCoordinatesSC = null;
        return tempCoordinatesSC;
    }
    
    public int[] getCursorPosition() {
        int[] tempCoordinatesSC;
        tempCoordinatesSC = cursorCoordinatesSC;
        cursorCoordinatesSC = null;
        return tempCoordinatesSC;
    }

    public int getViewZoom() {
        return viewZoom;
    }

    public int[] getViewCoordinatesAC() {
        return viewCoordinatesAC;
    }
    
    public int[] getViewDisplacmentSC() {
        int[] viewDisplacmentSC = new int[2];
        if((getScreenWidth() > getViewZoom()*mapBoundsAC[0]) ||
                (getScreenHeight() > getViewZoom()*mapBoundsAC[1])){
            viewDisplacmentSC[0] = ((getViewZoom()*getScreenWidth())-mapBoundsAC[0])/(2*getViewZoom());
            viewDisplacmentSC[1] = ((getViewZoom()*getScreenHeight())-mapBoundsAC[1])/(2*getViewZoom());
        } else {
            viewDisplacmentSC[0] = 0;
            viewDisplacmentSC[1] = 0;
        }
        return viewDisplacmentSC;
    }
    
    public void setScreenDimensions(int x, int y) {
        screenDimensions = new int[2];
        screenDimensions[0] = x;
        screenDimensions[1] = y;
        setSize(new Dimension(x, y));
    }

    public void setProvinceSelected(int provinceNo) {
        provinceSelected = provinceNo;
    }

    public void resetProvinceSelectionPhasor() {
        provinceSelectionPhasor = -20;
    }

    public void cycleProvinceSelectionPhasor() {
        if (provinceSelectionPhasor == 19) {
            provinceSelectionPhasor = -20;
        } else {
            provinceSelectionPhasor = provinceSelectionPhasor + 1;
        }
    }
    
    public void setViewPosition(int[] viewPositionAC) {
        viewCoordinatesAC[0] = viewPositionAC[0];
        viewCoordinatesAC[1] = viewPositionAC[1];
        validateCurrentViewPosition();
    }
    
    public int xConvertACtoSC(int AC){
        return (AC - viewCoordinatesAC[0] + (getViewDisplacmentSC()[0]*viewZoom)) * viewZoom;
    }
    
    public int yConvertACtoSC(int AC){
        return (AC - viewCoordinatesAC[1] + (getViewDisplacmentSC()[1]*viewZoom)) * viewZoom;
    }
    
    public int convertADtoSD(int AD){
        return AD * viewZoom;
    }
    
    public int xConvertSCtoAC(int SC){
        return (SC/viewZoom) + viewCoordinatesAC[0] - (getViewDisplacmentSC()[0]*viewZoom);
    }
    
    public int yConvertSCtoAC(int SC){
        return (SC/viewZoom) + viewCoordinatesAC[1] - (getViewDisplacmentSC()[1]*viewZoom);
    }
    
    public int convertSDtoAD(int SD){
        return SD/viewZoom;
    }
    
    public int getViewXPositionAC() {
        return viewCoordinatesAC[0];
    }
    
    public int getViewYPositionAC() {
        return viewCoordinatesAC[1];
    }
    
    public int getViewWidthAC() {
        return convertSDtoAD(screenDimensions[0]);
    }
    
    public int getViewHeightAC() {
        return convertSDtoAD(screenDimensions[1]);
    }
    
    public int[] getViewPositionRectAC() {
        int [] rect = new int[4];
        rect[0] = viewCoordinatesAC[0];
        rect[1] = viewCoordinatesAC[1];
        rect[2] = convertSDtoAD(screenDimensions[0] - 2*(getViewDisplacmentSC()[0]));
        rect[3] = convertSDtoAD(screenDimensions[1] - 2*(getViewDisplacmentSC()[1]));
        return rect;
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
    
    //Methods from MouseMotionListener
    @Override
    public void mouseDragged(MouseEvent e) {
        //System.out.println("Mouse Dragged.");
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        cursorCoordinatesSC = new int[2];
        cursorCoordinatesSC[0] = e.getX();
        cursorCoordinatesSC[1] = e.getY();
    }

    //Methods from KeyListener
    @Override
    public void keyTyped(KeyEvent e) {
        //System.out.println("Key Typed.");
    }

    @Override
    public void keyPressed(KeyEvent e) {
        //viewMovementFlags
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            viewMovingUp = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            viewMovingDown = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            viewMovingLeft = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            viewMovingRight = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            provinceSelected = -1;
        }

        //viewZoom
        if (e.getKeyCode() == KeyEvent.VK_EQUALS) {
            zoomViewIn();
        }
        if (e.getKeyCode() == KeyEvent.VK_MINUS) {
            zoomViewOut();
        }

        //viewMovementFlags
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            viewMovingUp = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            viewMovingDown = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            viewMovingLeft = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            viewMovingRight = false;
        }
    }

    //Method from MouseWheelListener
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int notches = e.getWheelRotation();
        int[] cursorPosition = {e.getX(), e.getY()};
        if (notches < 0) {
            zoomViewIn(cursorPosition);
        } else if (notches > 0) {
            zoomViewOut();
        }
    }
    
    public void validateCurrentViewPosition() {
        if (viewCoordinatesAC[0] < 0) {
            viewCoordinatesAC[0] = 0;
        }
        if (viewCoordinatesAC[1] < 0) {
            viewCoordinatesAC[1] = 0;
        }
        if (getViewXPositionAC() + getViewWidthAC() > mapBoundsAC[0]) {
            viewCoordinatesAC[0] = mapBoundsAC[0] - getViewWidthAC()
                    + convertSDtoAD(2*(getViewDisplacmentSC()[0]));
        }
        if (getViewYPositionAC() + getViewHeightAC() > mapBoundsAC[1]) {
            viewCoordinatesAC[1] = mapBoundsAC[1] - getViewHeightAC()
                    + convertSDtoAD(2*(getViewDisplacmentSC()[1]));
        }
    }

    public void moveViewPositionUp() {
        viewCoordinatesAC[1] = viewCoordinatesAC[1] - (viewMovementSpeed / viewZoom);
        validateCurrentViewPosition();
    }

    public void moveViewPositionDown() {
        viewCoordinatesAC[1] = viewCoordinatesAC[1] + (viewMovementSpeed / viewZoom);
        validateCurrentViewPosition();
    }

    public void moveViewPositionLeft() {
        viewCoordinatesAC[0] = viewCoordinatesAC[0] - (viewMovementSpeed / viewZoom);
        validateCurrentViewPosition();
    }

    public void moveViewPositionRight() {
        viewCoordinatesAC[0] = viewCoordinatesAC[0] + (viewMovementSpeed / viewZoom);
        validateCurrentViewPosition();
    }

    public void zoomViewIn() {
        int[] centeredViewPositionAC = new int[2];
        centeredViewPositionAC[0] = viewCoordinatesAC[0] + (getScreenWidth() / (2 * viewZoom));
        centeredViewPositionAC[1] = viewCoordinatesAC[1] + (getScreenHeight() / (2 * viewZoom));
        if (viewZoom != maximumPermittedZoom) {
            viewZoom++;
            viewCoordinatesAC[0] = centeredViewPositionAC[0] - (getScreenWidth() / (2 * viewZoom));
            viewCoordinatesAC[1] = centeredViewPositionAC[1] - (getScreenHeight() / (2 * viewZoom));
        }
    }

    public void zoomViewIn(int[] cursorPosition) {
        int[] cursorPositionAC = new int[2];
        cursorPositionAC[0] = viewCoordinatesAC[0] + (cursorPosition[0] / viewZoom);
        cursorPositionAC[1] = viewCoordinatesAC[1] + (cursorPosition[1] / viewZoom);
        if (viewZoom != maximumPermittedZoom) {
            viewZoom++;
            viewCoordinatesAC[0] = cursorPositionAC[0] - (cursorPosition[0]/viewZoom);
            viewCoordinatesAC[1] = cursorPositionAC[1] - (cursorPosition[1]/viewZoom);
            validateCurrentViewPosition();
        }
    }

    public void zoomViewOut() {
        int[] centeredViewPositionAC = new int[2];
        centeredViewPositionAC[0] = viewCoordinatesAC[0] + (getScreenWidth() / (2 * viewZoom));
        centeredViewPositionAC[1] = viewCoordinatesAC[1] + (getScreenHeight() / (2 * viewZoom));
        if (viewZoom != minimumPermittedZoom) {
            viewZoom--;
            viewCoordinatesAC[0] = centeredViewPositionAC[0] - (getScreenWidth() / (2 * viewZoom));
            viewCoordinatesAC[1] = centeredViewPositionAC[1] - (getScreenHeight() / (2 * viewZoom));
            validateCurrentViewPosition();
        }
    }
    
    public boolean provinceOnScreen(World world, int provinceNo) {
        int[] screenRectAC = getViewPositionRectAC();
        int[] provinceRectAC = world.getProvince(provinceNo).getRect();

        if (provinceRectAC[0] > screenRectAC[0] + screenRectAC[2]
                || provinceRectAC[1] > screenRectAC[1] + screenRectAC[3]
                || provinceRectAC[1] + provinceRectAC[3] < screenRectAC[1]
                || provinceRectAC[0] + provinceRectAC[2] < screenRectAC[0]) {
            return false;
        }

        return true;
    }
    
    public void processClick(World world) {
        int[] clickCoordinatesSC;
        int[] clickCoordinatesAC = new int[2];
        clickCoordinatesSC = getMouseClick();

        //Abort if there has been no mouse click
        if (clickCoordinatesSC == null) {
            return;
        }

        //Convert clickCoordinatesSC to clickCoordinatesAC
        clickCoordinatesAC[0] = xConvertSCtoAC(clickCoordinatesSC[0]);
        clickCoordinatesAC[1] = yConvertSCtoAC(clickCoordinatesSC[1]);

        //Use clickCoordinatesAC to see what province is selected, if valid position
        if ((clickCoordinatesAC[0] >= 0) && (clickCoordinatesAC[0] <= mapBoundsAC[0])) {
            if ((clickCoordinatesAC[1] >= 0) && (clickCoordinatesAC[1] <= mapBoundsAC[1])) {
                provinceSelected = world.getProvinceAtPoint(clickCoordinatesAC);
                resetProvinceSelectionPhasor();
            }
        }
    }
    
    public void processCursorPosition(){
        int[] cursorCoordinatesSC = getCursorPosition();
        int widthScreenMovingLimit;
        int minLimitScreenMovingArea;
        int maxLimitScreenMovingArea;
        
        if (cursorCoordinatesSC == null) {
            return;
        }
        
        //x dimension
        widthScreenMovingLimit = 
                (int) (cursorMovementAreaProportion * screenDimensions[0]);
        minLimitScreenMovingArea = 0 + widthScreenMovingLimit;
        maxLimitScreenMovingArea = screenDimensions[0] - widthScreenMovingLimit;
        if (cursorCoordinatesSC[0] <  minLimitScreenMovingArea) {
            viewMovingLeft = true;
            viewMovingRight = false;
        } else if (cursorCoordinatesSC[0] >  maxLimitScreenMovingArea) {
            viewMovingRight = true;
            viewMovingLeft = false;
        } else {
            viewMovingLeft = false;
            viewMovingRight = false;
        }
        
        //y dimension
        widthScreenMovingLimit = 
                (int) (cursorMovementAreaProportion * screenDimensions[1]);
        minLimitScreenMovingArea = 0 + widthScreenMovingLimit;
        maxLimitScreenMovingArea = screenDimensions[1] - widthScreenMovingLimit;
        if (cursorCoordinatesSC[1] <  minLimitScreenMovingArea) {
            viewMovingUp = true;
            viewMovingDown = false;
        } else if (cursorCoordinatesSC[1] >  maxLimitScreenMovingArea) {
            viewMovingDown = true;
            viewMovingUp = false;
        } else {
            viewMovingUp = false;
            viewMovingDown = false;
        }   
    } 
    
    public void processViewMovement() {
        if (viewMovingUp == true){
            moveViewPositionUp();
        }
        if (viewMovingDown == true){
            moveViewPositionDown();
        }
        if (viewMovingLeft == true){
            moveViewPositionLeft();
        }
        if (viewMovingRight == true){
            moveViewPositionRight();
        }
    }

    public void drawBackgroundToScreen(Graphics g, World world) {
        BufferedImage backgroundSubsection;
        int[] viewDisplacmentSC = getViewDisplacmentSC();

        if ((viewDisplacmentSC[0] == 0) && (viewDisplacmentSC[1] == 0)) {
            backgroundSubsection = world.getBackgroundImage().getSubimage(viewCoordinatesAC[0], viewCoordinatesAC[1], getScreenWidth() / viewZoom, getScreenHeight() / viewZoom);
            g.drawImage(backgroundSubsection, 0, 0, getScreenWidth(), getScreenHeight(), null);
        } else {
            backgroundSubsection = world.getBackgroundImage();
            //This line should draw 'background to interface' image
            g.clearRect(0, 0, getScreenWidth(), getScreenHeight());
            g.drawImage(backgroundSubsection, viewDisplacmentSC[0], viewDisplacmentSC[1], getScreenWidth() - (2 * viewDisplacmentSC[0]), getScreenHeight() - (2 * viewDisplacmentSC[1]), null);
        }
    }

    public void drawProvincesToScreen(Graphics g, World world) {
        int xSC, ySC, wSC, hSC;

        for (int provinceNo = 0; provinceNo < world.getNoOfProvinces(); provinceNo++) {
            if (provinceOnScreen(world, provinceNo)) {
                xSC = xConvertACtoSC(world.getProvince(provinceNo).getRectX());
                ySC = yConvertACtoSC(world.getProvince(provinceNo).getRectY());
                wSC = convertADtoSD(world.getProvince(provinceNo).getRectW());
                hSC = convertADtoSD(world.getProvince(provinceNo).getRectH());

                g.drawImage(world.getProvince(provinceNo).getImage(), xSC, ySC, wSC, hSC, null);
            }
        }
    }
    
    public void drawProvinceBordersToScreen(Graphics g, World world) {
        int xSC, ySC, wSC, hSC;

        for (int provinceNo = 0; provinceNo < world.getNoOfProvinces(); provinceNo++) {
            if (provinceOnScreen(world, provinceNo)) {
                xSC = xConvertACtoSC(world.getProvince(provinceNo).getRectX());
                ySC = yConvertACtoSC(world.getProvince(provinceNo).getRectY());
                wSC = convertADtoSD(world.getProvince(provinceNo).getRectW());
                hSC = convertADtoSD(world.getProvince(provinceNo).getRectH());

                g.drawImage(world.getProvince(provinceNo).getBordersImage(), xSC, ySC, wSC, hSC, null);
            }
        }
    }

    public void drawProvinceHighlightToScreen(Graphics g, World world) {
        BufferedImage selectedProvinceHighlight = null;
        int phasedHighlightColour = 127 + (int) (Math.abs(getProvinceSelectionPhasor()) * 6.4);
        int highlightColour = new Color(255, 255, 255, phasedHighlightColour).getRGB();
        int wAC, hAC, xSC, ySC, wSC, hSC;
        int provinceNo = getProvinceSelected();

        if (provinceNo != -1) {
            wAC = world.getProvince(provinceNo).getRectW();
            hAC = world.getProvince(provinceNo).getRectH();
            selectedProvinceHighlight = new BufferedImage(wAC, hAC, BufferedImage.TYPE_INT_ARGB);
            for (int xCP = 0; xCP < world.getProvince(provinceNo).getRectW(); xCP = xCP + 1) {
                    for (int yCP = 0; yCP < world.getProvince(provinceNo).getRectH(); yCP = yCP + 1) {
                        if (world.getProvince(provinceNo).getMapExtentAtPoint(xCP, yCP) == true && world.getProvince(provinceNo).getMapBordersAtPoint(xCP, yCP) == false) {
                            selectedProvinceHighlight.setRGB(xCP, yCP, highlightColour);
                        }
                    }
                }
        }

        if (selectedProvinceHighlight != null) {
            xSC = xConvertACtoSC(world.getProvince(provinceNo).getRectX());
            ySC = yConvertACtoSC(world.getProvince(provinceNo).getRectY());
            wSC = convertADtoSD(world.getProvince(provinceNo).getRectW());
            hSC = convertADtoSD(world.getProvince(provinceNo).getRectH());

            g.drawImage(selectedProvinceHighlight, xSC, ySC, wSC, hSC, null);
            cycleProvinceSelectionPhasor();
        }
    }
    
    public void drawProvinceNamesToScreen(Graphics g, World world) {
        int xSC, ySC, wSC, hSC, txSC, tySC;
        String provinceName;
        FontMetrics metrics = g.getFontMetrics(g.getFont());

        for (int provinceNo = 0; provinceNo < world.getNoOfProvinces(); provinceNo++) {
            if (provinceOnScreen(world, provinceNo)) {
                provinceName = world.getProvince(provinceNo).getName();
                xSC = xConvertACtoSC(world.getProvince(provinceNo).getRectX());
                ySC = yConvertACtoSC(world.getProvince(provinceNo).getRectY());
                wSC = convertADtoSD(world.getProvince(provinceNo).getRectW());
                hSC = convertADtoSD(world.getProvince(provinceNo).getRectH());
                txSC = xSC + ((wSC - metrics.stringWidth(provinceName))/2);
                tySC = ySC + ((hSC - metrics.getHeight())/2);

                g.drawString(provinceName, txSC, tySC);
            }
        }
    }

    public void update(Graphics g, World world) {
        processCursorPosition();
        processViewMovement();
        drawBackgroundToScreen(g, world);
        drawProvincesToScreen(g, world);
        drawProvinceBordersToScreen(g, world);
        drawProvinceHighlightToScreen(g, world);
        drawProvinceNamesToScreen(g, world);
    }
}