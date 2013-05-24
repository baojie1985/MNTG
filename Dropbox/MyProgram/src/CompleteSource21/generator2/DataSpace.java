package generator2;

import java.awt.*;
import drawables.*;

/**
 * Description of the dataspace.
 * 
 * @version 1.10	01.02.00	use of DrawableObjects
 * @version 1.00	29.12.99	first version
 * @author FH Oldenburg
 */
public class DataSpace {
	
	/**
	 * minimum x-coordinate of the network
	 */
	private int minX = 0;
	/**
	 * maximum x-coordinate of the network
	 */
	private int maxX = 0;
	/**
	 * minimum y-coordinate of the network
	 */
	private int minY = 0;
	/**
	 * maximum y-coordinate of the network
	 */
	private int maxY = 0;

/**
 * DataSpace constructor.
 * @param objects container of all spatial objects
 */
public DataSpace (DrawableObjects objects) {
	// determine extrema
	if (objects.getNumberOfObjects() <= 0)
		return;
	Rectangle mbr = objects.getDataspace();
	minX = mbr.x;
	maxX = mbr.x+mbr.width-1;
	minY = mbr.y;
	maxY = mbr.y+mbr.height-1;
}
/**
 * Returns the maximum x-coordinate.
 * @return x-coordinate
 */
public int getMaxX() {
	return maxX;
}
/**
 * Returns the maximum y-coordinate.
 * @return y-coordinate
 */
public int getMaxY() {
	return maxY;
}
/**
 * Returns the minimum x-coordinate.
 * @return x-coordinate
 */
public int getMinX() {
	return minX;
}
/**
 * Returns the minimum y-coordinate.
 * @return y-coordinate
 */
public int getMinY() {
	return minY;
}
}