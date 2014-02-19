package heptarchy;

/**
 *
 * @author Jack Durrant
 */
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.FileNotFoundException;

import javax.imageio.ImageIO;

import java.awt.Color;
//import java.awt.Font;
//import java.util.logging.Logger;
//import java.util.logging.Level;

public class World implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private List<Province> provinces = new ArrayList<>();
	private List<Nation> nations = new ArrayList<>();
	private int[][] provinceMap;
	private transient BufferedImage background;

	public World(BufferedImage provincesMap, File provincesTxt, File nationsTxt) {
		try {
			attemptToLoadWorld();
		} catch (RuntimeException e) {
			deleteOldWorld();
			generateWorld(provincesMap, provincesTxt, nationsTxt);
		}
	}

	public void addProvince(String name, String owner) {
		Province Province = new Province(name, owner);
		provinces.add(Province);
	}

	public void addNation(String name, int[] colourID) {
		Nation Nation = new Nation(name, colourID);
		nations.add(Nation);
	}

	public int getNoOfProvinces() {
		return provinces.size();
	}

	public int getNoOfNations() {
		return nations.size();
	}

	public Province getProvince(int provinceNo) {
		return provinces.get(provinceNo);
	}

	public Nation getNation(int nationNo) {
		return nations.get(nationNo);
	}

	public int getProvinceAtPoint(int[] coordinates) {
		return provinceMap[coordinates[0]][coordinates[1]];
	}

	public BufferedImage getBackgroundImage() {
		return background;
	}

	public int[] getWorldDimensions() {
		int[] coordinates = new int[2];
		coordinates[0] = background.getWidth();
		coordinates[1] = background.getHeight();
		return coordinates;
	}

	public void readProvinceMap(BufferedImage provincesMap,
			HashMap<Integer, Integer> provinceColourIDIndex) {
		provinceMap = new int[provincesMap.getWidth()][provincesMap.getHeight()];
		int pixel;
		Integer pixelProvinceNo;
		for (int x = 0; x < provinceMap.length; x = x + 1) {
			for (int y = 0; y < provinceMap[0].length; y = y + 1) {
				pixel = provincesMap.getRGB(x, y);
				pixelProvinceNo = provinceColourIDIndex.get(pixel);
				if (pixelProvinceNo == null) {
					throw new IllegalStateException(
							"No province with detected pixel colourID");
				} else {
					provinceMap[x][y] = pixelProvinceNo;
				}
			}
		}
		System.out.println("Finished reading in province map.");
	}

	public void getProvinceRects() {
		int[] empty = { 0, 0, 0, 0 };
		int currentProvince;
		for (int provinceNo = 0; provinceNo < getNoOfProvinces(); provinceNo++) {
			// Finds position components
			for (int x = 0; x < provinceMap.length; x = x + 1) {
				for (int y = 0; y < provinceMap[0].length; y = y + 1) {
					currentProvince = provinceMap[x][y];
					if (provinceNo == currentProvince) {
						if (Arrays.equals(provinces.get(provinceNo).getRect(),
								empty)) {
							provinces.get(provinceNo).setRect(x, y, x, y);
						} else {
							if (provinces.get(provinceNo).getRectX() > x) {
								provinces.get(provinceNo).setRectX(x);
							}
							if (provinces.get(provinceNo).getRectY() > y) {
								provinces.get(provinceNo).setRectY(y);
							}
							if (provinces.get(provinceNo).getRectW() < x) {
								provinces.get(provinceNo).setRectW(x);
							}
							if (provinces.get(provinceNo).getRectH() < y) {
								provinces.get(provinceNo).setRectH(y);
							}
						}
					}
				}
			}
			provinces.get(provinceNo).setRectW(
					provinces.get(provinceNo).getRectW()
							- provinces.get(provinceNo).getRectX() + 1);
			provinces.get(provinceNo).setRectH(
					provinces.get(provinceNo).getRectH()
							- provinces.get(provinceNo).getRectY() + 1);
		}
		System.out.println("Finished calculating province Rects.");
	}

	public void getProvinceImages(BufferedImage provincesMap,
			HashMap<Integer, Integer> provinceColourIDIndex) {
		int[] provinceRect;
		int pixel;
		for (int provinceNo = 0; provinceNo < getNoOfProvinces(); provinceNo++) {
			provinceRect = provinces.get(provinceNo).getRect();
			provinces.get(provinceNo).setImage(
					provincesMap.getSubimage(provinceRect[0], provinceRect[1],
							provinceRect[2], provinceRect[3]));
			provinces.get(provinceNo).setMapExtent(
					new boolean[provinceRect[2]][provinceRect[3]]);
			for (int x = 0; x < provinceRect[2]; x = x + 1) {
				for (int y = 0; y < provinceRect[3]; y = y + 1) {
					pixel = provinces.get(provinceNo).getImage().getRGB(x, y);
					if (provinceColourIDIndex.get(pixel) == provinceNo) {
						provinces.get(provinceNo).setMapExtentAtPoint(x, y,
								true);
					}
				}
			}
			provinces.get(provinceNo).setImage(null);
			provinces.get(provinceNo).imageToUpdate();
		}
		System.out.println("Finished calculating province images.");
	}

	public void giveProvinceImagesBorders() {
		int[] provinceRect;
		boolean[][] mapExtentTemp;
		for (int provinceNo = 0; provinceNo < getNoOfProvinces(); provinceNo++) {
			provinceRect = provinces.get(provinceNo).getRect();
			mapExtentTemp = new boolean[provinceRect[2] + 2][provinceRect[3] + 2];
			provinces.get(provinceNo).setMapBorders(
					new boolean[provinceRect[2]][provinceRect[3]]);
			for (int x = 0; x < provinceRect[2]; x = x + 1) {
				for (int y = 0; y < provinceRect[3]; y = y + 1) {
					if (provinces.get(provinceNo).getMapExtentAtPoint(x, y) == true) {
						mapExtentTemp[x + 1][y + 1] = true;
					}
				}
			}
			for (int x = 0; x < provinceRect[2]; x = x + 1) {
				for (int y = 0; y < provinceRect[3]; y = y + 1) {
					if (mapExtentTemp[x + 1][y] == false
							|| mapExtentTemp[x + 2][y + 1] == false
							|| mapExtentTemp[x + 1][y + 2] == false
							|| mapExtentTemp[x][y + 1] == false) {
						if (provinces.get(provinceNo).getMapExtentAtPoint(x, y) == true) {
							provinces.get(provinceNo).setMapBordersAtPoint(x,
									y, true);
						}
					}
				}
			}
		}
		System.out.println("Finished calculating province borders.");
	}

	public void getProvinceConnectivityStates() {
		int[] provinceRect;
		int[] pixel = new int[2];
		int[] testPixel = new int[2];
		int testProvince;
		for (int provinceNo = 0; provinceNo < getNoOfProvinces(); provinceNo++) {
			provinceRect = provinces.get(provinceNo).getRect();
			for (int x = 0; x < provinceRect[2]; x = x + 1) {
				for (int y = 0; y < provinceRect[3]; y = y + 1) {
					if (provinces.get(provinceNo).getMapBordersAtPoint(x, y) == true) {
						pixel[0] = provinceRect[0] + x;
						pixel[1] = provinceRect[1] + y;
						if (pixel[1] != 0) {
							testPixel[0] = pixel[0];
							testPixel[1] = pixel[1] - 1;
							testProvince = getProvinceAtPoint(testPixel);
							provinces.get(provinceNo).setProvinceIsConnected(
									testProvince);
						}
						if (pixel[0] != getWorldDimensions()[0]) {
							testPixel[0] = pixel[0] + 1;
							testPixel[1] = pixel[1];
							testProvince = getProvinceAtPoint(testPixel);
							provinces.get(provinceNo).setProvinceIsConnected(
									testProvince);
						}
						if (pixel[1] != getWorldDimensions()[1]) {
							testPixel[0] = pixel[0];
							testPixel[1] = pixel[1] + 1;
							testProvince = getProvinceAtPoint(testPixel);
							provinces.get(provinceNo).setProvinceIsConnected(
									testProvince);
						}
						if (pixel[1] != 0) {
							testPixel[0] = pixel[0] - 1;
							testPixel[1] = pixel[1];
							testProvince = getProvinceAtPoint(testPixel);
							provinces.get(provinceNo).setProvinceIsConnected(
									testProvince);
						}
					}
				}
			}
			provinces.get(provinceNo).disconnectProvince(provinceNo);
			provinces.get(provinceNo).disconnectProvince(-1);
		}
	}

	public void readProvinces(File provincesTxt,
			HashMap<Integer, Integer> provinceColourIDIndex) {
		try {
			Scanner text = new Scanner(provincesTxt);
			String line;
			String provinceName = "";
			int[] provinceColourID = new int[3];
			int provinceColourID_SI;
			String provinceOwner;
			int[] colourPos = new int[2];
			int provinceNo = 0;

			while (text.hasNextLine()) {
				line = text.nextLine();
				if (line.equals("")) {
					// Blank line, ignore
				} else if (line.substring(0, 9).equals("Province:")) {
					provinceName = line.substring(10, line.length());
				} else if (line.substring(0, 7).equals("Colour:")) {
					colourPos[0] = 0;
					colourPos[1] = 0;
					line = line.substring(8, line.length());
					for (int x = 0; x < line.length(); x = x + 1) {
						if (line.charAt(x) == ',') {
							if (colourPos[0] == 0) {
								colourPos[0] = x;
							} else {
								colourPos[1] = x;
							}
						}
					}
					provinceColourID[0] = Integer.valueOf(line.substring(0,
							colourPos[0]));
					provinceColourID[1] = Integer.valueOf(line.substring(
							colourPos[0] + 2, colourPos[1]));
					provinceColourID[2] = Integer.valueOf(line.substring(
							colourPos[1] + 2, line.length()));
				} else if (line.substring(0, 6).equals("Owner:")) {
					provinceOwner = line.substring(7, line.length());
					provinceColourID_SI = new Color(provinceColourID[0],
							provinceColourID[1], provinceColourID[2]).getRGB();
					provinceColourIDIndex.put(provinceColourID_SI, provinceNo);
					addProvince(provinceName, provinceOwner);
					provinceNo++;
				}
			}

			// Set black no province pixel to -1 provinceNo
			provinceColourID_SI = new Color(0, 0, 0).getRGB();
			provinceColourIDIndex.put(provinceColourID_SI, -1);

			text.close();
			System.out.println("Finished reading province file.");
		} catch (Exception e) {
			System.out.println("Provinces File not found.");
		}
	}

	public void readNations(File nationsTxt) {
		try {
			Scanner text = new Scanner(nationsTxt);
			String line;
			String nationName = "";
			int[] nationColour = new int[3];
			int[] colourPos = new int[2];
			while (text.hasNextLine()) {
				line = text.nextLine();
				if (line.equals("")) {
					// Blank line, ignore
				} else if (line.substring(0, 7).equals("Nation:")) {
					nationName = line.substring(8, line.length());
				} else if (line.substring(0, 7).equals("Colour:")) {
					colourPos[0] = 0;
					colourPos[1] = 0;
					line = line.substring(8, line.length());
					for (int x = 0; x < line.length(); x = x + 1) {
						if (line.charAt(x) == ',') {
							if (colourPos[0] == 0) {
								colourPos[0] = x;
							} else {
								colourPos[1] = x;
							}
						}
					}
					nationColour[0] = Integer.valueOf(line.substring(0,
							colourPos[0]));
					nationColour[1] = Integer.valueOf(line.substring(
							colourPos[0] + 2, colourPos[1]));
					nationColour[2] = Integer.valueOf(line.substring(
							colourPos[1] + 2, line.length()));
					addNation(nationName, nationColour);
				}
			}
			text.close();
			System.out.println("Finished reading nations file.");
		} catch (Exception e) {
			System.out.println("Nations File not found.");
		}
	}

	public void loadBackgroundImage() {
		try {
			this.background = ImageIO.read(new File("resources/Terrain.png"));
		} catch (IOException e) {
		}
	}

	public void deleteOldWorld() {
		try {
			File file = new File("resources/world.ser");
			file.delete();
		} finally {
			System.out.println("Deleting obsolete world.ser");
		}
	}

	public void attemptToLoadWorld() {
		try {
			System.out.println("Loading the world.");
			World loadedWorld = null;
			FileInputStream fileIn = new FileInputStream("resources/world.ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			loadedWorld = (World) in.readObject();
			in.close();
			fileIn.close();
			this.provinces = loadedWorld.provinces;
			this.nations = loadedWorld.nations;
			this.provinceMap = loadedWorld.provinceMap;
			for (int provinceNo = 0; provinceNo < getNoOfProvinces(); provinceNo++) {
				getProvince(provinceNo).imageToUpdate();
			}
			loadBackgroundImage();
		} catch (InvalidClassException e) {
			throw new RuntimeException("Invalid World.ser");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("World class not found");
		} catch (FileNotFoundException e) {
			throw new RuntimeException("No world.ser present");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			System.gc();
		}
	}

	public void generateWorld(BufferedImage provincesMap, File provincesTxt,
			File nationsTxt) {
		HashMap<Integer, Integer> provinceColourIDIndex = new HashMap<Integer, Integer>();

		System.out.println("Generating the world.");
		loadBackgroundImage();
		readProvinces(provincesTxt, provinceColourIDIndex);
		readNations(nationsTxt);
		readProvinceMap(provincesMap, provinceColourIDIndex);
		getProvinceRects();
		getProvinceImages(provincesMap, provinceColourIDIndex);
		giveProvinceImagesBorders();
		getProvinceConnectivityStates();

		try {
			FileOutputStream fileOut = new FileOutputStream(
					"resources/world.ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(this);
			out.close();
			fileOut.close();
			System.out.println("Finished generating world.ser");
		} catch (IOException i) {
			i.printStackTrace();
		}
	}

	public void updateProvinceImages() {
		BufferedImage provinceImage;
		BufferedImage bordersImage;
		String provinceNation;
		int provinceColourID;
		int pixel;
		// String writeLocation;

		for (int provinceNo = 0; provinceNo < getNoOfProvinces(); provinceNo++) {
			provinceColourID = 0;
			if (getProvince(provinceNo).getImageToUpdate() == true) {
				if (getProvince(provinceNo).getImage() == null) {
					getProvince(provinceNo).setImage(
							new BufferedImage(getProvince(provinceNo)
									.getRectW(), getProvince(provinceNo)
									.getRectH(), BufferedImage.TYPE_INT_ARGB));
					getProvince(provinceNo).setBordersImage(
							new BufferedImage(getProvince(provinceNo)
									.getRectW(), getProvince(provinceNo)
									.getRectH(), BufferedImage.TYPE_INT_ARGB));
				}
				provinceImage = getProvince(provinceNo).getImage();
				bordersImage = getProvince(provinceNo).getBordersImage();
				provinceNation = getProvince(provinceNo).getOwner();
				for (int nationNo = 0; nationNo < getNoOfNations(); nationNo++) {
					if (provinceNation.equals(getNation(nationNo).getName())) {
						provinceColourID = getNation(nationNo).getColourID();
					}
					for (int x = 0; x < getProvince(provinceNo).getRectW(); x = x + 1) {
						for (int y = 0; y < getProvince(provinceNo).getRectH(); y = y + 1) {
							if (getProvince(provinceNo).getMapExtentAtPoint(x,
									y) == true) {
								pixel = provinceColourID;
							} else {
								pixel = new Color(0, 0, 0, 0).getRGB();
							}
							provinceImage.setRGB(x, y, pixel);

							if (getProvince(provinceNo).getMapBordersAtPoint(x,
									y) == true) {
								pixel = new Color(0, 0, 0, 255).getRGB();
							} else {
								pixel = new Color(0, 0, 0, 0).getRGB();
							}
							bordersImage.setRGB(x, y, pixel);
						}
					}
					getProvince(provinceNo).imageUpdated();
					// writeLocation = provinceNation + ".png";
					// try {
					// ImageIO.write(provinceImage, "png", new
					// File(writeLocation));
					// } catch (IOException ex) {
					// Logger.getLogger(world.class.getName()).log(Level.SEVERE,
					// null, ex);
					// }
				}
			}
		}
	}
}