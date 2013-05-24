package spatial;

import java.awt.Graphics;

/**
 * Interface describing an object in a spatial search tree.
 * 
 * @version 3.10	16.08.2003	isContainedBy(SpatialSearchTreeEntry), isOfSameType added
 * @version 3.00	01.03.2003	several methods removed
 * @version 2.00	05.10.2002	Rectangle becomes MBR
 * @version 1.30	26.06.2000	getId added
 * @version 1.20	15.04.2000	intersects on objects added
 * @version 1.10	26.03.2000	contains, isContainedBy added
 * @version 1.00	31.01.2000	first version
 * @author Thomas Brinkhoff
 */
public interface SpatialSearchTreeObject extends SpatialSearchTreeEntry {
	
	/**
	 * Computes the distance to a given point.
	 * @return  the distance
	 * @param  point  coordinates of the point
	 */
	double computeDistanceTo (int[] point);
	/**
	 * Tests whether the object contains a given rectangle or not.
	 * @return  contains?
	 * @param  rect  the rectangle
	 */
	boolean contains (MBR rect);
	/**
	 * Draws the object.
	 * @param  g      graphic context
	 * @param  scale  the current scale
	 */
	void draw (Graphics g, int scale);
	/**
	 * Returns the identifier of the object.
	 * @return  the ID
	 */
	long getId ();
	/**
	 * Tests whether the object intersects another object.
	 * @return  intersects?
	 * @param  obj  the object
	 */
	boolean intersects (Object obj);
	/**
	 * Tests whether the object intersects a given rectangle.
	 * @return  intersects?
	 * @param  r  the rectangle
	 */
	boolean intersects (MBR r);
	/**
	 * Tests whether the object is contained by a given rectangle or not.
	 * @return  is contained?
	 * @param  rect  the rectangle
	 */
	boolean isContainedBy (MBR rect);
	/**
	 * Tests whether the object is contained by a given entry or not.
	 * @return  is contained?
	 * @param  entry  the entry
	 */
	boolean isContainedBy (SpatialSearchTreeEntry entry);
	/**
	 * Tests whether the object is of the same type as the given object or not.
	 * @return  is of same type?
	 * @param  obj  the other object
	 */
	boolean isOfSameType (SpatialSearchTreeObject obj);
	
}
