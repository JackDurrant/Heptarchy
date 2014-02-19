package heptarchy;

/**
 *
 * @author Jack Durrant
 */

import java.awt.image.BufferedImage;
import java.util.Set;
import java.util.HashSet;

public class Province implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
    private String owner;
    private int[] rect;
    private boolean[][] mapExtent;
    private boolean[][] mapBorders;
    private Set<Integer> connectedProvinces;
    private transient BufferedImage image;
    private transient BufferedImage bordersImage;
    private transient boolean imageToUpdate;

    public Province(String name, String owner) {
        this.name = name;
        this.owner = owner;
        rect = new int[4];
        rect[0] = 0;
        rect[1] = 0;
        rect[2] = 0;
        rect[3] = 0;
        connectedProvinces = new HashSet<>();
        imageToUpdate = true;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public int[] getRect() {
        return rect;
    }

    public int getRectX() {
        return rect[0];
    }

    public int getRectY() {
        return rect[1];
    }

    public int getRectW() {
        return rect[2];
    }

    public int getRectH() {
        return rect[3];
    }

    public boolean getMapExtentAtPoint(int x, int y) {
        return mapExtent[x][y];
    }

    public boolean getMapBordersAtPoint(int x, int y) {
        return mapBorders[x][y];
    }

    public boolean getIfProvinceIsConnected(int provinceNo) {
        return connectedProvinces.contains(provinceNo);
    }

    public BufferedImage getImage() {
        return image;
    }
    
    public BufferedImage getBordersImage(){
        return bordersImage;
    }

    public boolean getImageToUpdate() {
        return imageToUpdate;
    }

    public void setRect(int x, int y, int w, int h) {
        int[] rect = {x, y, w, h};
        this.rect = rect;
    }

    public void setRectX(int x) {
        int[] rect = {x, this.rect[1], this.rect[2], this.rect[3]};
        this.rect = rect;
    }

    public void setRectY(int y) {
        int[] rect = {this.rect[0], y, this.rect[2], this.rect[3]};
        this.rect = rect;
    }

    public void setRectW(int w) {
        int[] rect = {this.rect[0], this.rect[1], w, this.rect[3]};
        this.rect = rect;
    }

    public void setRectH(int h) {
        int[] rect = {this.rect[0], this.rect[1], this.rect[2], h};
        this.rect = rect;
    }

    public void setMapExtent(boolean[][] mapExtent) {
        this.mapExtent = mapExtent;
    }

    public void setMapExtentAtPoint(int x, int y, boolean state) {
        mapExtent[x][y] = state;
    }

    public void setMapBorders(boolean[][] mapBorders) {
        this.mapBorders = mapBorders;
    }

    public void setMapBordersAtPoint(int x, int y, boolean state) {
        mapBorders[x][y] = state;
    }

    public void setProvinceIsConnected(int provinceNo) {
        connectedProvinces.add(provinceNo);
    }

    public void disconnectProvince(int provinceNo) {
        connectedProvinces.remove(provinceNo);
    }
    
    public void setImage(BufferedImage image) {
        this.image = image;
    }
    
    public void setBordersImage(BufferedImage image){
        bordersImage = image;
    }

    public void imageToUpdate() {
        imageToUpdate = true;
    }

    public void imageUpdated() {
        imageToUpdate = false;
    }
}