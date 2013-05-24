package drawables;

import java.awt.*;
import java.io.*;
import util.*;

/**
 * Object class for objects with a drawable primitive.
 *
 * @version	4.20	07.02.02	data output streams added
 * @version 4.10	06.02.02	support of DataOutputStream
 * @version	4.00	02.06.00	support of sets of drawables and presentation modes
 * @version	3.40	24.04.00	intersects of objects added, getName corrected
 * @version	3.30	26.03.00	contains, isContainedBy added; intersects renamed
 * @version	3.20	16.02.00	setName, hashcode, equals, move added; DrawableObjectType used; implements SpatialSearchTreeObject
 * @version	3.10	09.10.99	write added
 * @version	3.00	19.08.99	container functionality in extra class
 * @version	2.00	05.04.99	adapted to version 2.0 of Drawables
 * @author Thomas Brinkhoff
 */

public class DrawableObject {

	/**
	 * Array with corresponding drawable primitives.
	 */
	private Drawable prim[] = null; 
	/**
	 * Number of primitives.
	 */
	private int primNum = 0; 
	/**
	 * The identifier.
	 */
	private long id = 0;
	/**
	 * The type.
	 */
	private DrawableObjectType type = null;	
	/**
	 * The name.
	 */
	private String name = null;	
	/**
	 * Attributes of the object.
	 */
	private String info = null;
	/**
	 * Value for no default scale.
	 */
	protected static final int NODEFSCALE = -1;
	/**
	 * Default scale.
	 */
	private int defaultScale = NODEFSCALE;
	/**
	 * Container of the object.
	 */
	private DrawableObjects container = null;
	/**
	 * Is the object selected.
	 */
	private boolean selected = false;

/**
 * Constructor of an object.
 * @param id the identifier
 * @param type the type of the object
 * @param name the name of the object
 * @param info attributes of the object
 * @param container container storing the object (the constructor does not put the object into the container)
 */
public DrawableObject (long id, DrawableObjectType type, String name, String info, DrawableObjects container) {
	this.id = id;
	this.type = type;
	this.name = name;
	this.info = info;
	this.container = container;
}
/**
 * Adapts the text of a corresponding drawable primitive.
 * @param d new drawable
 * @param name name of the object
 */
private static void adaptText (Drawable d, String name) {
	if ((d != null) && (name != null) && (name.length()>0) && (d.getClass().getName().endsWith("Text")))
		if (((DrawableText)d).getString().equals("="))
			((DrawableText)d).setText(name);
}
/**
 * Adds a primitive to the object.
 * @param addPrim the primitive
 */
public void addDrawable (Drawable addPrim) {
	// store drawable
	if (prim == null)
		prim = new Drawable[3];
	else if (primNum+1 >= prim.length) {
		Drawable newPrim[] = new Drawable[prim.length*2];
		for (int i=0; i<primNum; i++)
			newPrim[i] = prim[i];
		prim = newPrim;
	}
	prim[primNum++] = addPrim;
	// adapt drawable
	addPrim.setObject(this);
	adaptText(addPrim,name);
}
/**
 * Draws the primitives of the object.
 * @param g graphic context
 */
public void draw (Graphics g) {
	for (int i=0; i<primNum; i++)
		prim[i].draw(g);
}
/**
 * Draws the primitives of the object.
 * @param g graphic context
 * @param scale actual scale
 */
public void draw (Graphics g, int scale) {
	for (int i=0; i<primNum; i++)
		prim[i].draw(g,scale);
}
/**
 * Draws the primitives of the object.
 * @param g graphic context
 * @param r clipping rectangle
 */
public void draw (Graphics g, Rectangle r) {
	for (int i=0; i<primNum; i++)
		prim[i].draw(g,r);
}
/**
 * Draws the primitives of the object.
 * @param g graphic context
 * @param r clipping rectangle
 * @param scale actual scale
 */
public void draw (Graphics g, Rectangle r, int scale) {
	for (int i=0; i<primNum; i++)
		prim[i].draw(g,r,scale);
}
/**
 * Compares to objects using their identifier.
 * @return equal?
 * @param obj object to be compared
 */
public boolean equals (Object obj) {
	if ((obj == null) || !obj.getClass().equals(getClass()))
		return false;
	return (id == ((DrawableObject)obj).id); 
}
/**
 * Returns a data value depending on the actual mode.
 * @throws NumberFormatException
 * @param index the index of the data value
 * @return the value
 */
public int getDataValue (int index) throws NumberFormatException {
	if ((info.length() > 0) && (container != null))
		return type.getAttributeAsInteger(container.getMode()+container.getNumberOfModes()*(index+1),info);
	throw new NumberFormatException();
}
/**
 * Returns the default scale of the object. If it is not set, the minimum scale of
 * the first corresponding primitive is returned.
 * @return the default scale
 */
public int getDefaultScale () {
	if ((defaultScale != NODEFSCALE) || (primNum == 0))
		return defaultScale;
	else 
		return prim[0].getMinScale();
}
/**
 * Returns the a corresponding primitive.
 * @return the primitive
 * @param index of the primitive
 */
public Drawable getDrawable (int index) {
	if ((index >= 0) && (index < primNum))
		return prim[index];
	else
		return null;
}
/**
 * Returns the identifier.
 * @return the ID
 */
public long getId () {
	return id;
}
/**
 * Returns the string with the attributes.
 * @return the attributes
 */
public String getInfo () {
	return DrawableText.toOutputString(info);
}
/**
 * Computes the minimum bounding rectangle.
 * @return  the rectangle
 */
public Rectangle getMBR() {
	if (primNum == 0)
		return null;
	Rectangle mbr = new Rectangle(prim[0].getMBR());
	for (int i=1; i<primNum; i++)
		mbr.add(prim[i].getMBR());
	return mbr;
}
/**
 * Returns the name of the object.
 * @return the name (or an empty string)
 */
public String getName () {
	if (name == null)
		return "";
	else if (name.equals("="))
		// veraltet
		if ((primNum > 1) && (prim[1] != null) && prim[1].getClass().getName().endsWith("Text"))
			return ((DrawableText)prim[1]).getString();
		else if ((primNum > 0) && prim[0].getClass().getName().endsWith("Text"))
			return ((DrawableText)prim[0]).getString();
		else
			return name;
	else
		return DrawableText.toOutputString(name);
}
/**
 * Returns the number of drawable primitives.
 * @return number
 */
public int getNumberOfDrawables () {
	return primNum;
}
/**
 * Returns the type of the object.
 * @return the type
 */
public DrawableObjectType getObjectType () {
	return type;
}
/**
 * Returns the value which influeces the presentation depending on the actual mode.
 * @return the value
 */
public int getPresValue() {
	if ((info != null) && (info.length() > 0) && (container != null))
		try {return type.getAttributeAsInteger(container.getMode(),info);}
		catch (NumberFormatException e) {}
	return DrawablePresentation.NOVALUE;
}
/**
 * Returns the hashcode (=id).
 * @return the hashcode
 */
public int hashCode () {
	return (int)id;
}
/**
 * Returns whether the object is selected or not.
 * @return selected?
 */
public boolean isSelected () {
	return isSelected();
}
/**
 * Test whether one of the corresponding primitives is visible.
 * @return visible?
 * @param scale scale (-1 => return true)
 */
public boolean isVisible (int scale) {
	for (int i=0; i<primNum; i++)
		if (prim[i].isVisible (scale))
			return true;
	return false;
}
/**
 * Sets the default scale.
 * @param scale new default scale
 */
public void setDefaultScale (int scale) {
	defaultScale = scale;
}
/**
 * Sets the id.
 * @param id new id
 */
public void setId (long id) {
	this.id = id;
}
/**
 * Sets the name of the object.
 * @param name new name
 */
public void setName (String name) {
	this.name = name;
	for (int i=0; i<primNum; i++)
		adaptText(prim[i],name);
}
/**
 * Selects or deselect the object and its primitives.
 * @param on selected?
 */
public void setSelection (boolean on) {
	selected = on;
	for (int i=0; i<primNum; i++)
		prim[i].setSelection(on);
}
/**
 * Writes the object without its primitives.
 * @param  out  the data output stream
 */
public void write (DataOutputStream out) {
	if ((id != 0) || (info.length()>0)) {
		try {
			out.writeByte((int)'O');
			out.writeLong(id);
			out.writeUTF(type.getName());
			out.writeUTF(name);
			out.writeUTF(info);
			out.writeShort((short)defaultScale);
		}
		catch (IOException ex) {
		}
	}
}
/**
 * Writes the object without its primitives.
 * @param out entry writer
 */
public void write (EntryWriter out) {
	if ((id != 0) || (info.length()>0)) {
		out.print("O\t");
		out.print(id);
		String typeName = type.getName();
		out.print('\t'+typeName+'\t'+name+'\t'+info);
		if (defaultScale != NODEFSCALE)
			out.print("\t"+defaultScale);
		out.println();
	}
}
}
