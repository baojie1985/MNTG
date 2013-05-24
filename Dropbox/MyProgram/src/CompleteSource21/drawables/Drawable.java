package drawables;

import java.awt.*;
import java.io.*;
import util.*;

/**
 * Basic class for drawable primitives.
 *
 * @version	3.41	20.08.2003	layers become byte, use of MBR improved, Rect removed
 * @version	3.40	05.10.2002	interface SpatialSearchTreeEntry removed
 * @version	3.30	20.02.2002	data output streams, toString added
 * @version	3.20	03.07.2001	getContainer added
 * @version	3.10	25.08.2000	writeGML added
 * @version	3.00	27.06.2000	implementation of spatial search tree objects, support of presenation modes, linked primitives
 * @version	2.50	09.04.2000	intersects for drawable primitives added
 * @version	2.40	26.03.2000	contains, isContainedBy added; intersects renamed
 * @version	2.30	31.01.2000	getObject, isVisible, computeDistance, setMBR added
 * @version	2.20	10.10.1999	write, writeCoord added
 * @version	2.10	14.08.1999	lazy loading of geometry
 * @version	2.00	14.03.1999	changed from interface to class
 * @author Thomas Brinkhoff
 */

public abstract class Drawable implements Serializable, EntryReadable {

	/**
	 * Standard layer for areal primitives.
	 */
	public static final byte AREALAYER = 0;
	/**
	 * Standard layer for line primitives.
	 */
	public static final byte LINELAYER = 1;
	/**
	 * Standard layer for point primitives.
	 */
	public static final byte POINTLAYER = 2;
	/**
	 * Standard layer for text primitives.
	 */
	public static final byte TEXTLAYER = 3;
	/**
	 * Standard layer for (not scalable) bitmaps.
	 */
	public static final byte BITMAPLAYER = 4;
	/**
	 * Default for the least detailed scale.
	 */
	public static final int DEFMINSCALE = Integer.MAX_VALUE; 
	/**
	 * Default for the most detailed scale.
	 */
	public static final int DEFMAXSCALE = 0; 

	/**
	 * The minimum bounding rectangle.
	 */
	protected Rectangle mbr = null;
	/**
	 * The least detailed scale for drawing.
	 */
	protected int minScale = DEFMINSCALE; 
	/**
	 * The most detailed scale for drawing.
	 */
	protected int maxScale = DEFMAXSCALE; 
	/**
	 * Layer.
	 */
	protected byte layer = LINELAYER; 
	/**
	 * Is the primitive selected?
	 */
	protected boolean selected = false;
	/**
	 * The corresponding presentation object.
	 */
	protected DrawablePresentation pres = null;
	/**
	 * The corresponding drawable object.
	 */
	protected DrawableObject obj = null;
	/**
	 * Container of the primitive.
	 */
	protected DrawableObjects container = null;
	/**
	 * Next primitive of the same layer.
	 */
	protected Drawable nextOfLayer = null;

/**
 * Computes the distance between two points.
 * @return distance
 * @param x1 x-coordinate of the 1st point
 * @param y1 y-coordinate of the 1st point
 * @param x2 x-coordinate of the 2nd point
 * @param y2 y-coordinate of the 2nd point
 */
public static double computeDistance (int x1, int y1, int x2, int y2) {
	long xDist = Math.abs(x1-x2);
	long yDist = Math.abs(y1-y2);
	return Math.sqrt(xDist*xDist + yDist*yDist);
}
/**
 * Computes the distance of the drawing primitive to a point.
 * @return distance
 * @param x x-coordinate of the point
 * @param y x-coordinate of the point
 */
public double computeDistanceTo (int x, int y) {
	Rectangle mbr = getMBR();
	return computeDistance (mbr.x+mbr.width/2,mbr.y+mbr.height/2, x,y);
}
/**
 * Tests whether the drawable primitive contains a given rectangle.
 * The default implementation works with the mbr of the primitive.
 * @return contains?
 * @param rect the rectangle
 */
public boolean contains (Rectangle rect) {
	if (rect == null)
		return false;
	else
		return getMBR().contains (rect);
}
/**
 * Draws the primitive and the linked primitives.
 * @param g graphical context
 */
public void draw (Graphics g) {
	draw(g,null,1);
}
/**
 * Draws the primitive and the linked primitives.
 * @param g graphical context
 * @param scale actual scale
 */
public void draw (Graphics g, int scale) {
	draw(g,null,scale);
}
/**
 * Draws the primitive and the linked primitives.
 * @param g graphical context
 * @param r clipping rectangle
 */
public void draw (Graphics g, Rectangle r) {
	draw(g,r,1);
}
/**
 * Draws the primitive.
 * @param g graphical context
 * @param r clipping rectangle
 * @param scale actual scale
 */
public void draw (Graphics g, Rectangle r, int scale) {
	if (scale == 0)
		scale = 1;
	// Draw the primitive
	if ((scale <= minScale) && (scale >= maxScale) && ((r == null) || intersects(r))) {
		int actMode = (container==null? DrawableObjects.STDMODE:container.getMode());
		int value = (obj==null? DrawablePresentation.NOVALUE:obj.getPresValue());
 		DrawablePresentation p = pres.get(scale,actMode,value);
		if ((p != null) && p.visible)
			drawProtected (g,scale,actMode,value);
	}
}
/**
 * Implements the draw method of a primitive.
 * @param g graphical context
 * @param sacle actual scale
 * @param mode actual presentation mode
 * @param value presentation value
 */
protected abstract void drawProtected (Graphics g, int scale, int mode, int value);
/**
 * Returns the container.
 * @return the container
 */
public DrawableObjects getContainer() {
	return container;
}
/**
 * Returns the identifier of the corresponding object (or 0).
 * @return the ID
 */
public long getId () {
	if (obj == null)
		return 0;
	else
		return obj.getId();
}
/**
 * Returns the layer of the primitive.
 * @return the layer
 */
public int getLayer () {
	return layer;
}
/**
 * Returns the most detailed scale for drawing.
 * @return the scale
 */
public int getMaxScale () {
	return maxScale;
}
/**
 * Returns the minimum bounding rectangle of the primitive.
 * @return the MBR
 */
public Rectangle getMBR () {
	return mbr;
}
/**
 * Returns the least detailed scale for drawing.
 * @return the scale
 */
public int getMinScale () {
	return minScale;
}
/**
 * Returns the next primitive of the same layer.
 * @return linked primitive
 */
public Drawable getNextOfLayer () {
	return nextOfLayer;
}
/**
 * Returns the corresponding drawable object.
 * @return drawable object
 */
public DrawableObject getObject () {
	return obj;
}
/**
 * Returns the basic presentation object.
 * @return basic presentation object
 */
public DrawablePresentation getPresentation () {
	return pres;
}
/**
 * Returns the presentation object for a given scale, mode and value.
 * @return presentation object
 * @param scale the scale
 * @param mode the presentation mode
 * @param value the presentation value
 */
public DrawablePresentation getPresentation (int scale, int mode, int value) {
	return pres.get (scale,mode,value);
}
/**
 * Returns the class of the primitive as Object.
 * @return the type
 */
public Object getType () {
	return (Object)getClass();
}
/**
 * Tests whether the primitive is selected by a given point or not.
 * The default implementation is based on the mbr of the primitive.
 * @return selected?
 * @param x x-coordinate of the point
 * @param y y-coordinate of the point
 * @param scale actuals scale
 */
public boolean interacts (int x, int y, int scale) {
	return getMBR().contains(x,y);
}
/**
 * Tests whether the drawable primitive intersects another primitive.
 * The default implementation is based on the mbr of the primitive.
 * @return intersects?
 * @param r the rectangle
 */
public boolean intersects (Drawable prim) {
	if (prim == null)
		return false;
	else
		return intersects (prim.getMBR());
}
/**
 * Tests whether the drawable primitive intersects a given rectangle.
 * The default implementation is based on the mbr of the primitive.
 * @return intersects?
 * @param r the rectangle
 */
public boolean intersects (Rectangle r) {
	return getMBR().intersects (r);
}
/**
 * Tests whether the drawable primitive intersects another primitive which is passed as Object.
 * @return intersect?
 * @param prim the other drawable primitive
 */
public boolean intersects (Object prim) {
	if ((prim == null) || !getClass().isInstance(prim))
		return false;
	return intersects ((Drawable)prim);
}
/**
 * Tests whether the drawable primitive is contained by a given rectangle.
 * The default implementation is based on the mbr of the primitive.
 * @return is contained?
 * @param rect the rectangle
 */
public boolean isContainedBy (Rectangle rect) {
	if (rect == null)
		return false;
	else
		return rect.contains (getMBR());
}
/**
 * Test whether the drawing primitive is visible or not.
 * @return is visible?
 * @param scale actual scale (negative value => returns true)
 */
public boolean isVisible (int scale) {
	if (scale < 0)
		return true;
	if ((scale > minScale) || (scale < maxScale))
		return false;
	int actMode = (container==null? DrawableObjects.STDMODE:container.getMode());
	int value = (obj==null? DrawablePresentation.NOVALUE:obj.getPresValue());
 	DrawablePresentation p = pres.get(scale,actMode,value);
 	if ((p == null) || !p.visible)
		return false;
	return true;
}
/**
 * Reads a coordinate.
 * @return  the coordinate
 * @param  in  data input stream
 */
protected static int readCoord (DataInputStream in) throws IOException {
	byte code = in.readByte();
	if (code == -127)
		return (int)in.readShort();
	else if (code == -128)
		return in.readInt();
	else
		return (int)code;
}
/**
 * Reads a coordinate.
 * @return the coordinate
 * @param in entry input
 */
protected static int readCoord (EntryInput in) {
	int coord = (int)in.readChar();
	if (coord < 128)
		return coord;
	coord = coord-256;
	if (coord > -127)
		return coord;
	else if (coord == -127)
		return in.readWord2();
	else
		return in.readWord4();
}
/**
 * Reads a new primitive from an entry input.
 * @return the primitive or null
 * @param  r  data input stream
 * @param  primType  the type of the primitive
 */
public static Drawable readDrawable (DataInputStream r, char primType) throws IOException {
	if (primType == 'L')
		return (DrawablePolyline) new DrawablePolyline().read(r);
	else
		throw new IOException ("unknown drawable");
}
/**
 * Reads a new primitive from an entry input.
 * @return the primitive or null
 * @param r entry input
 * @param primType the type of the primitive
 */
public static Drawable readDrawable (EntryInput r, String primType) {
	try {
		if (primType.compareTo("S")==0)
			primType = "Symbol";
		else if (primType.compareTo("T")==0)
			primType = "Text";
		else if (primType.compareTo("P")==0)
			primType = "Polygon";
		else if (primType.compareTo("L")==0)
			primType = "Polyline";
		Drawable prim = (Drawable)Class.forName("drawables.Drawable"+primType).newInstance();
		return (Drawable)prim.read(r);
	}
	catch (Exception e) {
		return null;
	}		
}
/**
 * Sets the containe of the primitive.
 * @param container the container
 */
public void setContainer (DrawableObjects container) {
	this.container = container;
}
/**
 * Sets the layer.
 * @param layer new layer
 */
public void setLayer (int layer) {
	this.layer = (byte)layer;
}
/**
 * Sets the most detailed scale for drawing.
 * @param scale the scale
 */
public void setMaxScale (int scale) {
	maxScale = scale;
}
/**
 * Sets the minimum bounding rectangle of the primitive.
 * @param newMBR the MBR
 */
public void setMBR (Rectangle newMBR) {
	if (newMBR != null)
		mbr = newMBR;
}
/**
 * Returns the least detailed scale for drawing.
 * @param scale the scale
 */
public void setMinScale (int scale) {
	minScale = scale;
}
/**
 * Sets the next primitive of the same layer.
 * @param prim the linked primitive
 */
public void setNextOfLayer (Drawable next) {
	nextOfLayer = next;
}
/**
 * Sets the corresponding object.
 * @param obj the new object
 */
public void setObject (DrawableObject obj) {
	this.obj = obj;
}
/**
 * Sets the corresponding presentation object.
 * @param pres the new presentation object
 */
public void setPresentation (DrawablePresentation pres) {
	this.pres = pres;
}
/**
 * Selects or deselect the primitive.
 * @param on selected?
 */
public void setSelection (boolean on) {
	selected = on;
}
/**
 * Returns a string representation.
 * @return  the string
 */
public String toString() {
	return "layer: "+layer+" minScale: "+minScale+" maxScale: "+maxScale+" mbr: "+getMBR();
}
/**
 * Writes the primitive.
 * @param out entry writer
 * @param type type of the output (meaning depends on the concrete subclass)
 */
public void write (EntryWriter out, int type) {
	writeProtected (out,type);
	if (obj != null) {
		long id = obj.getId();
		if (id != 0) {
			out.print('\t'); out.print(Math.abs(id));
		}
	}
	out.println();
}
/**
 * Writes a coordinate.
 * @param out  data output stream
 * @param coord the x- or y-coordinate
 */
protected static void writeCoord (DataOutputStream out, int coord) {
	try {
		// case: 1-byte presentation
		if ((coord > -127) && (coord < 128))
			out.writeByte(coord & 255);
		// case: 2-byte presentation
		else if ((coord > -32768) && (coord < 32767)) {
			out.writeByte(-127);
			out.writeShort((short)coord);
		}
		// case: 4-byte presentation
		else {
			out.writeByte(-128);
			out.writeInt(coord);
		}
	}
	catch (IOException ex) {}
}
/**
 * Writes a coordinate.
 * @param out entry writer
 * @param coord the x- or y-coordinate
 */
protected static void writeCoord (EntryWriter out, int coord) {
	// case: 1-byte presentation
	if ((coord > -127) && (coord < 128))
		out.writeByte(coord & 255);
	// case: 2-byte presentation
	else if ((coord > -32768) && (coord < 32767)) {
		out.writeByte(129);
		out.writeWord2(coord);
	}
	// case: 4-byte presentation
	else {
		out.writeByte(128);
		out.writeWord4(coord);
	}
}
/**
 * Writes the primitive as GML tag.
 * @param out entry writer
 */
public void writeGML (EntryWriter out) {
	String name = "box_";
	if (obj != null)
		name += obj.getName();
	out.println("<Polygon name=\"p"+name+"\" srsName=\"br\">");
	out.println(" <LineString name=\"l"+name+"\" srsName=\"br\">");
	out.print  ("  <CList>");
	Rectangle mbr = getMBR();
	out.print  (mbr.x+","+mbr.y+" ");
	out.print  ((mbr.x+mbr.width-1)+","+mbr.y+" ");
	out.print  ((mbr.x+mbr.width-1)+","+(mbr.y+mbr.height-1)+" ");
	out.print  (mbr.x+","+(mbr.y+mbr.height-1)+" ");
	out.println(mbr.x+","+mbr.y+"</CList>");
	out.println(" </LineString>");
	out.println("</Polygon>");
}
/**
 * Writes the primitive.
 * @param out entry writer
 * @param type type of the output (meaning depends on the concrete subclass)
 */
protected abstract void writeProtected (EntryWriter out, int type);
}
