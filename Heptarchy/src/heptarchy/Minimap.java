package heptarchy;

/**
 *
 * @author Jack Durrant
 */
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class Minimap extends ScreenElement {
	/**
	 * 
	 */
	private World world;
	private MapDisplay mapDisplay;
	private BufferedImage minimapImage;
	private BufferedImage minimapConstructionImage;
	private boolean minimapToUpdate;
	private double scalingFactor;
	private int[] cursorCoordinatesSC = null;

	public Minimap() {
		// Constructor
	}

	@Override
	public void processMouseClick(int[] clickCoordinatesSC) {
		int[] clickCoordinatesAC = new int[2];
		int[] newViewCoordinatesAC = new int[2];
		int maxLimitAC;

		// Convert clickCoordinatesSC to clickCoordinatesAC
		clickCoordinatesAC[0] = xConvertSCtoAC(clickCoordinatesSC[0]);
		clickCoordinatesAC[1] = yConvertSCtoAC(clickCoordinatesSC[1]);

		// Check if AC value is legal viewPosition, abort if not
		maxLimitAC = world.getWorldDimensions()[0];
		if ((clickCoordinatesAC[0] < 0) || (clickCoordinatesAC[0] > maxLimitAC)) {
			return;
		}
		maxLimitAC = world.getWorldDimensions()[1];
		if ((clickCoordinatesAC[1] < 0) || (clickCoordinatesAC[1] > maxLimitAC)) {
			return;
		}

		// Use clickCoordinatesAC to set where view should go
		newViewCoordinatesAC[0] = clickCoordinatesAC[0] - mapDisplay.getViewWidthAC()
				/ 2;
		newViewCoordinatesAC[1] = clickCoordinatesAC[1] - mapDisplay.getViewHeightAC()
				/ 2;
		mapDisplay.setViewPosition(newViewCoordinatesAC);

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
		updateMinimapImage();

		// Clear component panel to grey
		g.setColor(Color.gray);
		g.fillRect(getRectSC()[0], getRectSC()[1], getRectSC()[2], getRectSC()[3]);

		// Get rect coordinates for display of actual minimap
		int xSC, ySC, wSC, hSC;
		xSC = xConvertMCtoSC(0);
		ySC = yConvertMCtoSC(0);
		wSC = convertMDtoSD(minimapImage.getWidth());
		hSC = convertMDtoSD(minimapImage.getHeight());

		// Draw actual minimap
		g.setColor(Color.WHITE);
		g.fillRect(xSC, ySC, wSC, hSC);
		g.drawImage(minimapImage, xSC, ySC, wSC, hSC, null);
		int vPRect[] = mapDisplay.getViewPositionRectAC();
		g.setColor(Color.BLACK);

		// Draw current main view position rectangle
		xSC = xConvertACtoSC(vPRect[0]);
		ySC = yConvertACtoSC(vPRect[1]);
		wSC = convertADtoSD(vPRect[2]);
		hSC = convertADtoSD(vPRect[3]);
		g.drawRect(xSC, ySC, wSC, hSC);
	}

	public void initialize(World world, MapDisplay mapDisplay) {
		this.world = world;
		this.mapDisplay = mapDisplay;
		double xRatio = world.getWorldDimensions()[0] / getRectSC()[2];
		double yRatio = world.getWorldDimensions()[1] / getRectSC()[3];
		if (xRatio > yRatio) {
			scalingFactor = xRatio;
		} else {
			scalingFactor = yRatio;
		}

		int wAC = world.getWorldDimensions()[0];
		int hAC = world.getWorldDimensions()[1];
		int wMD = (int) (((double) wAC) / scalingFactor);
		int hMD = (int) (((double) hAC) / scalingFactor);
		minimapImage = new BufferedImage(wMD, hMD, BufferedImage.TYPE_INT_ARGB);
		minimapConstructionImage = new BufferedImage(wAC, hAC,
				BufferedImage.TYPE_INT_ARGB);
		minimapToUpdate = true;
	}

	private int convertACtoMC(int AC) {
		return (int) (((double) AC) / scalingFactor);
	}

	private int xConvertMCtoSC(int MC) {
		return MC + ((getRectSC()[2] - minimapImage.getWidth()) / 2)
				+ getRectSC()[0];
	}

	private int yConvertMCtoSC(int MC) {
		return MC + ((getRectSC()[3] - minimapImage.getHeight()) / 2)
				+ getRectSC()[1];
	}

	private int convertMDtoSD(int MD) {
		return MD;
	}

	private int xConvertACtoSC(int AC) {
		return (int) (((double) AC) / scalingFactor)
				+ ((getRectSC()[2] - minimapImage.getWidth()) / 2)
				+ getRectSC()[0];
	}

	private int yConvertACtoSC(int AC) {
		return (int) (((double) AC) / scalingFactor)
				+ ((getRectSC()[3] - minimapImage.getHeight()) / 2)
				+ getRectSC()[1];
	}

	private int convertADtoSD(int AD) {
		return (int) (((double) AD) / scalingFactor);
	}

	private int xConvertSCtoAC(int SC) {
		return (int) scalingFactor
				* ((SC - getRectSC()[0]) - ((getRectSC()[2] - minimapImage.getWidth()) / 2));
	}

	private int yConvertSCtoAC(int SC) {
		return (int) scalingFactor
				* ((SC - getRectSC()[1]) - ((getRectSC()[3] - minimapImage.getHeight()) / 2));
	}
	
	private void updateMinimapImage() {
		if (minimapToUpdate == false) {
			return;
		}

		Graphics2D gC = (Graphics2D) minimapConstructionImage.getGraphics();
		int xAC, yAC, wAC, hAC;
		for (int provinceNo = 0; provinceNo < world.getNoOfProvinces(); provinceNo++) {
			xAC = world.getProvince(provinceNo).getRect()[0];
			yAC = world.getProvince(provinceNo).getRect()[1];
			wAC = world.getProvince(provinceNo).getRect()[2];
			hAC = world.getProvince(provinceNo).getRect()[3];

			gC.drawImage(world.getProvince(provinceNo).getImage(), xAC, yAC,
					wAC, hAC, null);
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
}