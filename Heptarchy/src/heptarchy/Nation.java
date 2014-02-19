package heptarchy;

/**
 *
 * @author Jack Durrant
 */
import java.awt.Color;

public class Nation implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private int colourID;

	public Nation(String name, int[] colourID) {
		this.name = name;
		this.colourID = new Color(colourID[0], colourID[1], colourID[2])
				.getRGB();
	}

	public String getName() {
		return name;
	}

	public int getColourID() {
		return colourID;
	}
}