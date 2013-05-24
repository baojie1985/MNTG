package generator2;

import java.awt.*;
import spatial.*;

/**
 * Class representing an external object.
 * 
 * @version 3.10	08.08.2003	getNumOfDimensions, isContainedBy(obj) added
 * @version 3.00	06.10.2002	Rectangle replaced by MBR
 * @version 2.10	17.03.2002	scale in isVisible now double
 * @version 2.00	04.09.2001	adapted to revision 2.0
 * @version 1.10	15.06.2001	reporting added
 * @version 1.01	28.06.2000	getId added
 * @version 1.00	28.04.2000	first version
 * @author FH Oldenburg
 */
public class ExternalObject implements LayerSpatialSearchTreeObject {
	
	/**
	 * The identifier of the external object.
	 */
	private int id;
	/**
	 * The area represented by a rectangle.
	 */
	private MBR area;
	/**
	 * The object class.
	 */
	private int objClass = 0;
	/**
	 * The starting time.
	 */
	private int startTime = 0;
	/**
	 * The time of death.
	 */
	private int deathTime = 0;

	/**
	 * The container of the external object.
	 */
	private ExternalObjects container = null;


/**
 * Constructor.
 * @param id the identifier
 * @param time current time
 * @param lifetime lifetime
 * @param objClass id of the object class
 * @param x x-coordinate
 * @param y y-coordinate
 * @param width width
 * @param height height
 */
public ExternalObject (int id, int time, int lifetime, int objClass, int x, int y, int width, int height) {
	area = new MBR(x,y,width,height);
	this.id = id;
	this.objClass = objClass;
	this.startTime = time;
	this.deathTime = time+lifetime;
}
/**
 * Adds the object to a container.
 * @param container the container
 */
public void addToContainer (ExternalObjects container) {
	if (container != null) {
		this.container = container;
		container.add(this);
	}
}
/**
 * Computes the distance to a given point.
 * @return distance
 * @param px x-coordinate of the point
 * @param py y-coordinate of the point
 */
public double computeDistanceTo (int p[]) {
	return area.quadDistanceFromCenter(p);
}
/**
 * Tests whether the object contains a given rectangle or not.
 * @return contains?
 * @param rect the rectangle
 */
public boolean contains (MBR rect) {
	return area.contains(rect);
}
/**
 * Draws the MBR of the object if it is exists at the actual time.
 * @param g graphic context
 * @param scale actual scale
 */
public void draw (Graphics g, int scale) {
	if (!isVisible(scale))
		return;
	g.setColor (container.getObjectClasses().getColor(objClass));
	Rectangle r = area.extractRectangle(MBR.X,MBR.Y);
	g.drawRect (r.x/scale,r.y/scale,r.width/scale+1,r.height/scale+1);
}
/**
 * Returns the identifier of the external object.
 * @return the id
 */
public long getId () {
	return id;
}
/**
 * Returns the layer of the object.
 * @return the layer
 */
public int getLayer() {
	return Reporter.SYMBOLLAYER;
}
/**
 * Returns the area.
 * @return the area
 */
public MBR getMBR() {
	return area;
}
/**
 * Returns the number of dimensions.
 * @return  dimensions
 */
public int getNumOfDimensions() {
	return 2;
}
/**
 * Returns the object class of the object.
 * @return id of the object class
 */
public int getObjectClass () {
	return objClass;
}
/**
 * Returns the class of the object as Integer.
 * @return class
 */
public Object getType() {
	return new Integer(objClass);
}
/**
 * Tests whether an object is selected by a point or not.
 * @return selected?
 * @param x x-coordinate of the point
 * @param y y-coordinate of the point
 * @param scale scale
 */
public boolean interacts (int x, int y, int scale) {
	int[] point = new int[2];
	point[MBR.X] = x;
	point[MBR.Y] = y;
	return area.contains(point,1);
}
/**
 * Tests whether the object intersects another object.
 * @return intersects?
 * @param obj the object
 */
public boolean intersects (Object obj) {
	return getMBR().intersects(((ExternalObject)obj).getMBR());
}
/**
 * Tests whether the object intersects a given rectangle.
 * @return intersects?
 * @param r the rectangle
 */
public boolean intersects (MBR r) {
	return r.intersects(getMBR());
}
/**
 * Returns whether the object is alive or not.
 * @return  object alive?
 * @param  currTime  the current time
 */
public boolean isAlive (int currTime) {
	return currTime < deathTime;
}
/**
 * Tests whether the object is contained by a given rectangle or not.
 * @return is contained?
 * @param rect the rectangle
 */
public boolean isContainedBy (MBR rect) {
	return rect.contains(getMBR());
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
		ExternalObject o = (ExternalObject)obj;
		return getType() == o.getType();
	} catch (Exception ex) {
		return false;
	}
}
/**
 * Test whether the external object is visible or not.
 * @return is visible?
 * @param  scale  the current actual scale (negative value => returns true)
 */
public boolean isVisible (double scale) {
	int actTime = container.getTime().getCurrTime();
	if ((actTime < startTime) || (actTime >= deathTime))
		return false;
	return true;
}
/**
 * Tests whether the object is visible at a given scale or not.
 * @return visible?
 * @param scale scale
 */
public boolean isVisible (int scale) {
	return isVisible ((double)scale);
}
/**
 * Reports a deleted external object.
 * @param reporter the reporter
 * @param currTime the current time
 */
public void reportDeletedObject (Reporter reporter, int currTime) {
	util.Timer.stop(1);
	if (reporter != null)
		reporter.reportDisappearingExternalObject (currTime,id,currTime-startTime+1,objClass,area.extractRectangle(MBR.X,MBR.Y));
	util.Timer.start(1);
}
/**
 * Reports a moving external object.
 * @param reporter the reporter
 * @param currTime the current time
 */
public void reportMovingObject (Reporter reporter, int currTime) {
	util.Timer.stop(1);
	if (reporter != null)
		reporter.reportMovingExternalObject (currTime,id,currTime-startTime+1,objClass,area.extractRectangle(MBR.X,MBR.Y));
	util.Timer.start(1);
}
/**
 * Reports a new external object.
 * @param reporter the reporter
 */
public void reportNewObject (Reporter reporter) {
	util.Timer.stop(1);
	if (reporter != null)
 		reporter.reportNewExternalObject (startTime,id,objClass,area.extractRectangle(MBR.X,MBR.Y));
	util.Timer.start(1);
}
/**
 * Sets the MBR of the object.
 * @param newMBR the new MNR
 */
public void setMBR (MBR newMBR) {
	area = newMBR;
}
}
