package spatial;

/**
 * Interface describing a visualized object in a spatial search tree.
 * 
 * @version 1.00	01.03.2003	first version, split from SpatialFileSearchTreeObject
 * @author Thomas Brinkhoff
 */
public interface VisualizedSpatialSearchTreeObject extends LayerSpatialSearchTreeObject {
	
	/**
	 * Tests whether an object is selected by a point or not.
	 * @return selected?
	 * @param x x-coordinate of the point
	 * @param y y-coordinate of the point
	 * @param scale scale
	 */
	boolean interacts (int x, int y, int scale);
	/**
	 * Tests whether the object is visible at a given scale or not.
	 * @return visible?
	 * @param scale scale
	 */
	boolean isVisible (int scale);

}
