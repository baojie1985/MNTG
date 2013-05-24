package drawables;

import util.*;

/**
 * Class for handling object types.
 *
 * @version	2.10	26.05.00	functionality added
 * @version	2.00	31.01.00	re-design
 * @version	1.00	12.03.99	first version
 * @author Thomas Brinkhoff
 */

public class DrawableObjectType implements EntryReadable {

	/**
	 * name of the type.
	 */
	private String name = null;
	/**
	 * number of the attributes.
	 */
	private int numOfAttributes = 0;
	/**
	 * names of the attributes.
	 */
	private String attrName[] = null;
	/**
	 * types of the attributes.
	 */
	private String attrType[] = null;
	/**
	 * next element in the list.
	 */
	private DrawableObjectType next = null;

	/**
	 * terminating char.
	 */
	private static char termChar = '|';
	/**
	 * next element in the list.
	 */
	private static DrawableObjectType first = new DrawableObjectType("default");
/**
 * Constructor required for reading objects.
 */
public DrawableObjectType () {
	name = "unknown";
}
/**
 * Constructor.
 * @param name name of the type
 */
private DrawableObjectType (String name) {
	this.name = name;
}
/**
 * Constructor.
 * @param name name of the type
 * @param numOfAttributes the number of attributes
 * @param attrNames the names of the attributes
 * @param attrTypes the types of the attributes
 */
public DrawableObjectType (String name, int numOfAttributes, String[] attrNames, String[] attrTypes) {
	 this.name = name;
	 this.numOfAttributes = numOfAttributes;
	 this.attrName = attrNames;
	 this.attrName = attrTypes;
}
/**
 * Search for the object type with the given name.
 * @return the found object type (or null)
 * @param name name of the type
 */
public static DrawableObjectType findObjectType (String name) {
	return getObjectType (name,false);
}
/**
 * Returns a distinct attribute as an integer.
 * @throws NumberFormatException
 * @return the attribute
 * @param i index (0 = first)
 * @param attributes the complete attribute string
 */
public int getAttributeAsInteger (int i, String attributes) throws NumberFormatException {
	return (new Integer(getAttributeAsString(i,attributes))).intValue();
}
/**
 * Returns a distinct attribute as string.
 * @return the attribute
 * @param i index (0 = first)
 * @param attributes the complete attribute string
 */
public String getAttributeAsString (int i, String attributes) {
	if ((attributes == null) || (attributes.length()==0))
		return null;
	int first = 0;
	int last = attributes.indexOf(termChar);
	while ((i>0) && (last>0)) {
		first = last+1;
		last = attributes.indexOf(termChar,first);
		i--;
	}
	if (last < 0)
		last = attributes.length();
	return attributes.substring(first,last);
}
/**
 * returns the i-th attribute name.
 * @return attribut name (or "")
 * @param i index (start=0)
 */
public String getAttributeName (int i) {
	try {
		return attrName[i];
	}
	catch (Exception e) {	
		return "";
	}	
}
/**
 * returns the i-th attribute yype.
 * @return attribut type (or "")
 * @param i index (start=0)
 */
public String getAttributeType (int i) {
	try {
		return attrType[i];
	}
	catch (Exception e) {	
		return "";
	}	
}
/**
 * Returns a default type.
 * @return the default type
 */
public static DrawableObjectType getDefaultType () {
	return first;
}
/**
 * Returns the name of the type.
 * @return name
 */
public String getName () {
	return name;
}
/**
 * Returns the number of attributes.
 * @return number of attributes
 */
public int getNumOfAttributes () {
	return numOfAttributes;
}
/**
 * Search for the object type with the given name. If it does not exist, a new
 * type is created.
 * @return the found object yype (or null)
 * @param name name of the type
 */
public static DrawableObjectType getObjectType (String name) {
	return getObjectType (name,true);
}
/**
 * Search for the object type with the given name. If it does not exist, optionally a new
 * type is created.
 * @return the found object type (or null)
 * @param name name of the type
 * @param create flag for creating a new type
 */
private static DrawableObjectType getObjectType (String name, boolean create) {
	DrawableObjectType type = first;
	while (type != null) {
		if (type.name.compareTo(name)==0)
			return type;
		if ((type.next == null) && create) {
			type.next = new DrawableObjectType (name);
			return type.next;
		}
		type = type.next;
	}	
	return null;
}
/**
 * Reads the object type from an entry input.
 * @return new or found object type
 * @param r entry input
 */
public EntryReadable read(EntryInput r) {
	String newName = r.readString();
	DrawableObjectType foundType = findObjectType(newName);
	if (foundType == null)
		foundType = this;
	foundType.name = name;
	foundType.numOfAttributes = r.readInt();
	foundType.attrName = new String[foundType.numOfAttributes];
	foundType.attrType = new String[foundType.numOfAttributes];
	for (int i=0; i<numOfAttributes; i++) {
		foundType.attrName[i] = r.readString();
		foundType.attrType[i] = r.readString();
	}	
	return foundType;
}
/**
 * Sets the terminating char.
 * @param c char
 */
public static void setTerminatingChar (char c) {
	termChar = c;
}
}
