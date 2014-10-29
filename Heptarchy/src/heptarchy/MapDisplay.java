package heptarchy;

/**
 *
 * @author Jack Durrant
 */
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class MapDisplay extends ScreenElement {

	/**
	 * 
	 */
	private World world;
	private int[] screenDimensionsSC;
	private int[] mapBoundsAC;
	private int provinceSelected = -1;
	private int provinceSelectionPhasor;
	private int viewZoom = 2;
	private int[] viewCoordinatesAC = { 0, 0 };
	private int minimumPermittedZoom = 1;
	private int maximumPermittedZoom = 10;
	private int viewMovementSpeed = 50;
	private int[] cursorCoordinatesSC = null;
	private double cursorMovementAreaProportion = 0.01;
	private boolean viewMovingUpFromKeyboard;
	private boolean viewMovingDownFromKeyboard;
	private boolean viewMovingLeftFromKeyboard;
	private boolean viewMovingRightFromKeyboard;
	private boolean viewMovingUpFromMouse;
	private boolean viewMovingDownFromMouse;
	private boolean viewMovingLeftFromMouse;
	private boolean viewMovingRightFromMouse;

	public MapDisplay() {
		viewMovingUpFromKeyboard = false;
		viewMovingDownFromKeyboard = false;
		viewMovingLeftFromKeyboard = false;
		viewMovingRightFromKeyboard = false;
		viewMovingUpFromMouse = false;
		viewMovingDownFromMouse = false;
		viewMovingLeftFromMouse = false;
		viewMovingRightFromMouse = false;
	}

	@Override
	public void processMouseClick(int[] clickCoordinatesSC) {
		int[] clickCoordinatesAC = new int[2];

		// Convert clickCoordinatesSC to clickCoordinatesAC
		clickCoordinatesAC[0] = xConvertSCtoAC(clickCoordinatesSC[0]);
		clickCoordinatesAC[1] = yConvertSCtoAC(clickCoordinatesSC[1]);

		// Select province at mouse click
		provinceSelected = world.getProvinceAtPoint(clickCoordinatesAC);
		resetProvinceSelectionPhasor();
	}

	@Override
	public void processMouseMovement(int[] cursorCoordinatesSC) {
		this.cursorCoordinatesSC = cursorCoordinatesSC;
	}

	@Override
	public void processMouseWheelEvent(int notches, int[] cursorPositionSC) {
		if (notches < 0) {
			zoomViewIn(cursorPositionSC);
		} else if (notches > 0) {
			zoomViewOut();
		}
	}

	@Override
	public boolean attemptToPassKeyPressed(KeyEvent e) {
		// viewMovementFlags
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			viewMovingUpFromKeyboard = true;
			return true;
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			viewMovingDownFromKeyboard = true;
			return true;
		}
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			viewMovingLeftFromKeyboard = true;
			return true;
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			viewMovingRightFromKeyboard = true;
			return true;
		}

		return false;
	}

	@Override
	public boolean attemptToPassKeyReleased(KeyEvent e) {
		// deselect
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			provinceSelected = -1;
			return true;
		}

		// viewZoom
		if (e.getKeyCode() == KeyEvent.VK_EQUALS) {
			zoomViewIn();
			return true;
		}
		if (e.getKeyCode() == KeyEvent.VK_MINUS) {
			zoomViewOut();
			return true;
		}

		// viewMovementFlags
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			viewMovingUpFromKeyboard = false;
			return true;
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			viewMovingDownFromKeyboard = false;
			return true;
		}
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			viewMovingLeftFromKeyboard = false;
			return true;
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			viewMovingRightFromKeyboard = false;
			return true;
		}

		return false;
	}

	@Override
	public boolean attemptToPassKeyTyped(KeyEvent e) {
		return false;
	}

	@Override
	public void render(Graphics g) {
		processCursorPosition();
		world.updateProvinceImages();
		processViewMovement();
		drawBackgroundToScreen(g);
		drawProvincesToScreen(g);
		drawProvinceBordersToScreen(g);
		drawProvinceHighlightToScreen(g);
		drawProvinceNamesToScreen(g);

	}
	
	public void passScreenDimensions(int x, int y) {
		screenDimensionsSC = new int[2];
		screenDimensionsSC[0] = x;
		screenDimensionsSC[1] = y;
	}

	public void initialize(World world) {
		this.world = world;
		mapBoundsAC = world.getWorldDimensions();
	}

	public int getScreenRectWidth() {
		return getRectSC()[2];
	}

	public int getScreenRectHeight() {
		return getRectSC()[3];
	}

	public int getProvinceSelected() {
		return provinceSelected;
	}

	public int getProvinceSelectionPhasor() {
		return provinceSelectionPhasor;
	}

	public int getViewZoom() {
		return viewZoom;
	}

	public int[] getViewCoordinatesAC() {
		return viewCoordinatesAC;
	}

	public int[] getViewDisplacmentSC() {
		int[] viewDisplacmentSC = new int[2];
		if ((getScreenRectWidth() > getViewZoom() * mapBoundsAC[0])
				|| (getScreenRectHeight() > getViewZoom() * mapBoundsAC[1])) {
			viewDisplacmentSC[0] = ((getViewZoom() * getScreenRectWidth()) - mapBoundsAC[0])
					/ (2 * getViewZoom());
			viewDisplacmentSC[1] = ((getViewZoom() * getScreenRectHeight()) - mapBoundsAC[1])
					/ (2 * getViewZoom());
		} else {
			viewDisplacmentSC[0] = 0;
			viewDisplacmentSC[1] = 0;
		}
		return viewDisplacmentSC;
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

	public int xConvertACtoSC(int AC) {
		return getRectSC()[0]
				+ (AC - viewCoordinatesAC[0] + (getViewDisplacmentSC()[0] * viewZoom))
				* viewZoom;
	}

	public int yConvertACtoSC(int AC) {
		return getRectSC()[1]
				+ (AC - viewCoordinatesAC[1] + (getViewDisplacmentSC()[1] * viewZoom))
				* viewZoom;
	}

	// Maybe this bugs out far zoom
	public int convertADtoSD(int AD) {
		return AD * viewZoom;
	}

	public int xConvertSCtoAC(int SC) {
		return ((SC - getRectSC()[0]) / viewZoom) + viewCoordinatesAC[0]
				- (getViewDisplacmentSC()[0] * viewZoom);
	}

	public int yConvertSCtoAC(int SC) {
		return ((SC - getRectSC()[1]) / viewZoom) + viewCoordinatesAC[1]
				- (getViewDisplacmentSC()[1] * viewZoom);
	}

	public int convertSDtoAD(int SD) {
		return SD / viewZoom;
	}

	public int getViewXPositionAC() {
		return viewCoordinatesAC[0];
	}

	public int getViewYPositionAC() {
		return viewCoordinatesAC[1];
	}

	public int getViewWidthAC() {
		return convertSDtoAD(getRectSC()[2]);
	}

	public int getViewHeightAC() {
		return convertSDtoAD(getRectSC()[3]);
	}

	public int[] getViewPositionRectAC() {
		int[] rect = new int[4];
		rect[0] = viewCoordinatesAC[0];
		rect[1] = viewCoordinatesAC[1];
		rect[2] = convertSDtoAD(getRectSC()[2] - 2 * (getViewDisplacmentSC()[0]));
		rect[3] = convertSDtoAD(getRectSC()[3] - 2 * (getViewDisplacmentSC()[1]));
		return rect;
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
					+ convertSDtoAD(2 * (getViewDisplacmentSC()[0]));
		}
		if (getViewYPositionAC() + getViewHeightAC() > mapBoundsAC[1]) {
			viewCoordinatesAC[1] = mapBoundsAC[1] - getViewHeightAC()
					+ convertSDtoAD(2 * (getViewDisplacmentSC()[1]));
		}
	}

	public void moveViewPositionUp() {
		viewCoordinatesAC[1] = viewCoordinatesAC[1]
				- (viewMovementSpeed / viewZoom);
		validateCurrentViewPosition();
	}

	public void moveViewPositionDown() {
		viewCoordinatesAC[1] = viewCoordinatesAC[1]
				+ (viewMovementSpeed / viewZoom);
		validateCurrentViewPosition();
	}

	public void moveViewPositionLeft() {
		viewCoordinatesAC[0] = viewCoordinatesAC[0]
				- (viewMovementSpeed / viewZoom);
		validateCurrentViewPosition();
	}

	public void moveViewPositionRight() {
		viewCoordinatesAC[0] = viewCoordinatesAC[0]
				+ (viewMovementSpeed / viewZoom);
		validateCurrentViewPosition();
	}

	public void zoomViewIn() {
		int[] centeredViewPositionAC = new int[2];
		centeredViewPositionAC[0] = viewCoordinatesAC[0]
				+ (getScreenRectWidth() / (2 * viewZoom));
		centeredViewPositionAC[1] = viewCoordinatesAC[1]
				+ (getScreenRectHeight() / (2 * viewZoom));
		if (viewZoom != maximumPermittedZoom) {
			viewZoom++;
			viewCoordinatesAC[0] = centeredViewPositionAC[0]
					- (getScreenRectWidth() / (2 * viewZoom));
			viewCoordinatesAC[1] = centeredViewPositionAC[1]
					- (getScreenRectHeight() / (2 * viewZoom));
		}
	}

	public void zoomViewIn(int[] cursorPosition) {
		int[] cursorPositionAC = new int[2];
		cursorPositionAC[0] = viewCoordinatesAC[0]
				+ (cursorPosition[0] / viewZoom);
		cursorPositionAC[1] = viewCoordinatesAC[1]
				+ (cursorPosition[1] / viewZoom);
		if (viewZoom != maximumPermittedZoom) {
			viewZoom++;
			viewCoordinatesAC[0] = cursorPositionAC[0]
					- (cursorPosition[0] / viewZoom);
			viewCoordinatesAC[1] = cursorPositionAC[1]
					- (cursorPosition[1] / viewZoom);
			validateCurrentViewPosition();
		}
	}

	public void zoomViewOut() {
		int[] centeredViewPositionAC = new int[2];
		centeredViewPositionAC[0] = viewCoordinatesAC[0]
				+ (getScreenRectWidth() / (2 * viewZoom));
		centeredViewPositionAC[1] = viewCoordinatesAC[1]
				+ (getScreenRectHeight() / (2 * viewZoom));
		if (viewZoom != minimumPermittedZoom) {
			viewZoom--;
			viewCoordinatesAC[0] = centeredViewPositionAC[0]
					- (getScreenRectWidth() / (2 * viewZoom));
			viewCoordinatesAC[1] = centeredViewPositionAC[1]
					- (getScreenRectHeight() / (2 * viewZoom));
			validateCurrentViewPosition();
		}
	}

	public boolean provinceOnScreen(int provinceNo) {
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

	public void processCursorPosition() {
		int widthScreenMovingLimit;
		int minLimitScreenMovingArea;
		int maxLimitScreenMovingArea;

		if (cursorCoordinatesSC == null) {
			return;
		}

		// x dimension
		widthScreenMovingLimit = (int) (cursorMovementAreaProportion * screenDimensionsSC[0]);
		minLimitScreenMovingArea = 0 + widthScreenMovingLimit;
		maxLimitScreenMovingArea = screenDimensionsSC[0] - widthScreenMovingLimit;
		if (cursorCoordinatesSC[0] < minLimitScreenMovingArea) {
			viewMovingLeftFromMouse = true;
			viewMovingRightFromMouse = false;
		} else if (cursorCoordinatesSC[0] > maxLimitScreenMovingArea) {
			viewMovingRightFromMouse = true;
			viewMovingLeftFromMouse = false;
		} else {
			viewMovingLeftFromMouse = false;
			viewMovingRightFromMouse = false;
		}

		// y dimension
		widthScreenMovingLimit = (int) (cursorMovementAreaProportion * screenDimensionsSC[1]);
		minLimitScreenMovingArea = 0 + widthScreenMovingLimit;
		maxLimitScreenMovingArea = screenDimensionsSC[1] - widthScreenMovingLimit;
		if (cursorCoordinatesSC[1] < minLimitScreenMovingArea) {
			viewMovingUpFromMouse = true;
			viewMovingDownFromMouse = false;
		} else if (cursorCoordinatesSC[1] > maxLimitScreenMovingArea) {
			viewMovingDownFromMouse = true;
			viewMovingUpFromMouse = false;
		} else {
			viewMovingUpFromMouse = false;
			viewMovingDownFromMouse = false;
		}
	}

	public void processViewMovement() {
		if (viewMovingUpFromKeyboard == true || viewMovingUpFromMouse == true) {
			moveViewPositionUp();
		}
		if (viewMovingDownFromKeyboard == true || viewMovingDownFromMouse == true) {
			moveViewPositionDown();
		}
		if (viewMovingLeftFromKeyboard == true || viewMovingLeftFromMouse == true) {
			moveViewPositionLeft();
		}
		if (viewMovingRightFromKeyboard == true || viewMovingRightFromMouse == true) {
			moveViewPositionRight();
		}
	}

	public void drawBackgroundToScreen(Graphics g) {
		BufferedImage backgroundSubsection;
		int[] viewDisplacmentSC = getViewDisplacmentSC();

		if ((viewDisplacmentSC[0] == 0) && (viewDisplacmentSC[1] == 0)) {
			backgroundSubsection = world.getBackgroundImage().getSubimage(
					viewCoordinatesAC[0], viewCoordinatesAC[1],
					getScreenRectWidth() / viewZoom,
					getScreenRectHeight() / viewZoom);
			g.drawImage(backgroundSubsection, getRectSC()[0], getRectSC()[1],
					getScreenRectWidth(), getScreenRectHeight(), null);
		} else {
			backgroundSubsection = world.getBackgroundImage();
			// This line should draw 'background to interface' image
			g.clearRect(getRectSC()[0], getRectSC()[1], getScreenRectWidth(),
					getScreenRectHeight());
			g.drawImage(backgroundSubsection, getRectSC()[0]
					+ getViewDisplacmentSC()[0], getRectSC()[1]
					+ getViewDisplacmentSC()[1], getScreenRectWidth()
					- (2 * getViewDisplacmentSC()[0]), getScreenRectHeight()
					- (2 * getViewDisplacmentSC()[1]), null);
		}
	}

	public void drawProvincesToScreen(Graphics g) {
		int xSC, ySC, wSC, hSC;

		for (int provinceNo = 0; provinceNo < world.getNoOfProvinces(); provinceNo++) {
			if (provinceOnScreen(provinceNo)) {
				xSC = xConvertACtoSC(world.getProvince(provinceNo).getRectX());
				ySC = yConvertACtoSC(world.getProvince(provinceNo).getRectY());
				wSC = convertADtoSD(world.getProvince(provinceNo).getRectW());
				hSC = convertADtoSD(world.getProvince(provinceNo).getRectH());

				g.drawImage(world.getProvince(provinceNo).getImage(), xSC, ySC,
						wSC, hSC, null);
			}
		}
	}

	public void drawProvinceBordersToScreen(Graphics g) {
		int xSC, ySC, wSC, hSC;

		for (int provinceNo = 0; provinceNo < world.getNoOfProvinces(); provinceNo++) {
			if (provinceOnScreen(provinceNo)) {
				xSC = xConvertACtoSC(world.getProvince(provinceNo).getRectX());
				ySC = yConvertACtoSC(world.getProvince(provinceNo).getRectY());
				wSC = convertADtoSD(world.getProvince(provinceNo).getRectW());
				hSC = convertADtoSD(world.getProvince(provinceNo).getRectH());

				g.drawImage(world.getProvince(provinceNo).getBordersImage(),
						xSC, ySC, wSC, hSC, null);
			}
		}
	}

	public void drawProvinceHighlightToScreen(Graphics g) {
		BufferedImage selectedProvinceHighlight = null;
		int phasedHighlightColour = 127 + (int) (Math
				.abs(getProvinceSelectionPhasor()) * 6.4);
		int highlightColour = new Color(255, 255, 255, phasedHighlightColour)
				.getRGB();
		int wAC, hAC, xSC, ySC, wSC, hSC;
		int provinceNo = getProvinceSelected();

		if (provinceNo != -1) {
			wAC = world.getProvince(provinceNo).getRectW();
			hAC = world.getProvince(provinceNo).getRectH();
			selectedProvinceHighlight = new BufferedImage(wAC, hAC,
					BufferedImage.TYPE_INT_ARGB);
			for (int xCP = 0; xCP < world.getProvince(provinceNo).getRectW(); xCP = xCP + 1) {
				for (int yCP = 0; yCP < world.getProvince(provinceNo)
						.getRectH(); yCP = yCP + 1) {
					if (world.getProvince(provinceNo).getMapExtentAtPoint(xCP,
							yCP) == true
							&& world.getProvince(provinceNo)
									.getMapBordersAtPoint(xCP, yCP) == false) {
						selectedProvinceHighlight.setRGB(xCP, yCP,
								highlightColour);
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

	public void drawProvinceNamesToScreen(Graphics g) {
		int xSC, ySC, wSC, hSC, txSC, tySC;
		String provinceName;
		FontMetrics metrics = g.getFontMetrics(g.getFont());

		for (int provinceNo = 0; provinceNo < world.getNoOfProvinces(); provinceNo++) {
			if (provinceOnScreen(provinceNo)) {
				provinceName = world.getProvince(provinceNo).getName();
				xSC = xConvertACtoSC(world.getProvince(provinceNo).getRectX());
				ySC = yConvertACtoSC(world.getProvince(provinceNo).getRectY());
				wSC = convertADtoSD(world.getProvince(provinceNo).getRectW());
				hSC = convertADtoSD(world.getProvince(provinceNo).getRectH());
				txSC = xSC + ((wSC - metrics.stringWidth(provinceName)) / 2);
				tySC = ySC + ((hSC - metrics.getHeight()) / 2);

				g.drawString(provinceName, txSC, tySC);
			}
		}
	}
}