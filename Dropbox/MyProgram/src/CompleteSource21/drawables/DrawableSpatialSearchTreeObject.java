package drawables;

import java.awt.Graphics;
import spatial.*;

/**
 * Wrapper class for drawable primitives.
 * @version	1.20	16.08.2003	implements LayerSpatialSearchTreeObject, adapted to modified interface
 * @version	1.10	01.03.2003	adapted to split of SpatialSearchTreeObject
 * @version	1.00	05.10.2002	first version
 * @author Thomas Brinkhoff
 */
public class DrawableSpatialSearchTreeObject implements spatial.VisualizedSpatialSearchTreeObject, spatial.MovingSpatialSearchTreeObject, LayerSpatialSearchTreeObject {

	/**
	 * The drawable primitive.
	 */
	protected Drawable prim;
	/**
	 * Its minimum bounding rectangle.
	 */
	protected MBR mbr;

/**
 * DrawableSpatialSearchTreeObject constructor comment.
 * @param  prim  the drawable primitive
 */
public DrawableSpatialSearchTreeObject (Drawable prim) {
	this.prim = prim;
	this.mbr = new MBR(this.prim.getMBR());
}
/**
 * Computes the distance to a given point.
 * @return distance
 * @param  point  coordinates of the point
 */
public double computeDistanceTo (int[] point) {
	return prim.computeDistanceTo (point[MBR.X],point[MBR.Y]);
}
/**
 * Tests whether the object contains a given rectangle or not.
 * @return contains?
 * @param rect the rectangle
 */
public boolean contains (MBR rect) {
	return prim.contains(rect.extractRectangle(MBR.X,MBR.Y));
}
/**
 * Draws the object.
 * @param g graphic context
 * @param scale actual scale
 */
public void draw (Graphics g, int scale) {
	prim.draw(g,scale);
}
/**
 * Test whether the wrapped primitives are equal or not.
 * @return  equal?
 * @param  obj  the other object
 */
public boolean equals (Object obj) {
	if ((obj == null) || !(obj instanceof DrawableSpatialSearchTreeObject))
		return false;
	return prim.equals(((DrawableSpatialSearchTreeObject)obj).prim);
}
/**
 * Returns the drawable primitive.
 * @return  the drawable primitive
 */
public Drawable getDrawable() {
	return prim;
}
/**
 * Returns the identifier of the object.
 * @return the ID
 */
public long getId() {
	return prim.getId();
}
/**
 * Returns the layer of the object.
 * @return layer
 */
public int getLayer() {
	return prim.getLayer();
}
/**
 * Returns the minimum bounding rectangle.
 * @return  minimum bounding rectangle
 */
public MBR getMBR() {
	return mbr;
}
/**
 * Returns the number of dimensions.
 * @return  dimensions
 */
public int getNumOfDimensions() {
	return 2;
}
/**
 * Returns the type of the object.
 * @return type
 */
public Object getType() {
	return prim.getType();
}
/**
 * Tests whether an object is selected by a point or not.
 * @return selected?
 * @param x x-coordinate of the point
 * @param y y-coordinate of the point
 * @param scale scale
 */
public boolean interacts (int x, int y, int scale) {
	return prim.interacts (x,y,scale);
}
/**
 * Tests whether the object intersects another object.
 * @return intersects?
 * @param obj the object
 */
public boolean intersects (Object obj) {
	return prim.intersects(obj);
}
/**
 * Tests whether the object intersects a given rectangle.
 * @return intersects?
 * @param r the rectangle
 */
public boolean intersects (MBR r) {
	return prim.intersects(r.extractRectangle(MBR.X,MBR.Y));
}
/**
 * Tests whether the object is contained by a given rectangle or not.
 * @return is contained?
 * @param rect the rectangle
 */
public boolean isContainedBy (MBR rect) {
	return prim.isContainedBy(rect.extractRectangle(MBR.X,MBR.Y));
}
/**
 * Tests whether the object is contained by a given entry or not.
 * @return  is contained?
 * @param  entry  the entry
 */
public boolean isContainedBy (SpatialSearchTreeEntry entry) {
	if (entry == null)
		return false;
	else
		return isContainedBy (entry.getMBR());
}

/**
 * Tests whether the object is of the same type as the given object or not.
 * @return  is of same type?
 * @param  obj  the other object
 */
public boolean isOfSameType (SpatialSearchTreeObject obj) {
	try {
		DrawableSpatialSearchTreeObject o = (DrawableSpatialSearchTreeObject)obj;
		return getType() == o.getType();
	} catch (Exception ex) {
		return false;
	}
}
	
/**
 * Tests whether the object is visible at a given scale or not.
 * @return visible?
 * @param scale scale
 */
public boolean isVisible (int scale) {
	return prim.isVisible(scale);
}
/**
 * Sets the MBR of the object.
 * @param newMBR the new MNR
 */
public void setMBR (MBR newMBR) {
	mbr = newMBR;
	prim.setMBR(newMBR.extractRectangle(MBR.X,MBR.Y));
}
}
