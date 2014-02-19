package heptarchy;
//Bugs:
//Province name rendering quite basic
//Flash of green land background, check if rendering engine can be clarified
//Zoom in on mouse cursor from zoomed out past max position does not zoom to cursor

/**
 *
 * @author Jack Durrant
 */
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import java.io.IOException;
import java.io.File;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Graphics2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.ConsoleHandler;

public class Heptarchy {
    /**
	 * 
	 */
	//Zoom=10 is to be investigated as default zoom for game
    //Background image needed when view is zoomed back past world dimensions
    //
    //Add pan view by moving cursor to screen edges
    //Done - but not really compatible with minimap unless modified in some way
    //set mapDisplay.cursorMovementAreaProportion to non-zero to renable
    //Try adding set movement flags to zero when cursor leaves map
    //or make right movement area different.
    //IDEA: Make mapMovemnt about removing cursor from actual window by a side!

	private static Logger LOGGER = Logger.getLogger(Heptarchy.class.getName());
	private static ConsoleHandler loggerConsoleHandler = new ConsoleHandler();
    private static World world;
    private static Frame window;
    private static ExternalScreen screen;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        window = new Frame();   
        setupWindow(window);
        window.pack();
        screen = new ExternalScreen(window, world);
        window.pack();
        //window.setSize(testScreen.getWidth()+testWindow.getInsets().left+testWindow.getInsets().right,
        //		testScreen.getHeight()+testWindow.getInsets().bottom+testWindow.getInsets().top);
        window.setSize(screen.getWidth()+window.getInsets().left+window.getInsets().right,
        		screen.getHeight()+window.getInsets().bottom+window.getInsets().top);
        screen.setBackground(Color.RED);
        window.setVisible(true);
        window.setResizable(false);
        makeWindowFullscreen(window);

        window.addWindowListener(new WindowAdapter(){
        	public void windowClosing(WindowEvent we){
        	   System.exit(0);
        	}});
        
        gameLoop();

        System.exit(0);
    }

	public static void setupWindow(Frame window) throws IOException {
    	LOGGER.setUseParentHandlers(false);
    	LOGGER.addHandler(loggerConsoleHandler);
    	//loggerConsoleHandler.setLevel(Level.CONFIG);
    	loggerConsoleHandler.setLevel(Level.INFO);
    	LOGGER.setLevel(Level.ALL);

        BufferedImage provincesMap = null;
        try {
            provincesMap = ImageIO.read(new File("resources/Provinces.png"));
        } catch (IOException e) {
        	e.printStackTrace();
        }

        File provincesTxt = new File("resources/Provinces.txt");
        File nationsTxt = new File("resources/Nations.txt");

        Heptarchy.world = new World(provincesMap, provincesTxt, nationsTxt);
        LOGGER.info("Done initialising world.");
    }
	
	private static void makeWindowFullscreen(Frame window) {
		GraphicsEnvironment graphicsEnvironment = 
		         GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice graphicsDevice = 
		                    graphicsEnvironment.getScreenDevices()[0];
		
		if (graphicsDevice.isFullScreenSupported()){
			window.dispose();
		    window.setUndecorated(true);
		    window.setResizable(false);
		    graphicsDevice.setFullScreenWindow(window);
		    window.validate();
		    
		    int screenWidth = graphicsDevice.getDisplayMode().getWidth();
		    int screenHeight = graphicsDevice.getDisplayMode().getHeight();
		    screen.resizeScreen(screenWidth, screenHeight);
		    screen.requestFocus();
		    }else{
		      System.out.println("Full-screen mode not supported");
		    }
		
	}
    
    public static void gameLoop() {
    	boolean running = true;
    	long tickCount;
        long lastTickCount = 0;
        int framesPerSecond = 25;
        
        while (running) {
            tickCount = System.currentTimeMillis();

            if (lastTickCount == 0 || tickCount - lastTickCount >= (1000 / framesPerSecond)) {
            	Graphics2D gT = (Graphics2D) screen.getStrategy().getDrawGraphics();
            	
//                mapDisplay.processClick(world);
//                minimap.processClick(world, mapDisplay);
            	
                screen.paint(gT);
                
                gT.dispose();
                
                screen.getStrategy().show();

                LOGGER.config("Ms between frame: "+(tickCount-lastTickCount));
                lastTickCount = System.currentTimeMillis();
            }
        }
    }
}