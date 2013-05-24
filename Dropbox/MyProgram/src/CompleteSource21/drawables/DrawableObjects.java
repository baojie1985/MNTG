package drawables;

import java.awt.*;
import java.io.*;
import java.util.*;
import util.*;

/**
 * Container class for drawable objects.
 *
 * @version	1.50	07.02.02	data output streams added
 * @version 1.40	06.02.02	support of DataInputStream
 * @version 1.30	31.05.00	support of presentation modes, spatial search trees separated
 * @version 1.20	01.03.00	support of searh trees, hash-tables, and the class DrawableObjectType
 * @version	1.10	30.10.99	getObject(OfLayer)ById supports visibility, textes added while loading
 * @version	1.00	22.08.99	separation from the class "DrawableObject"
 * @author Thomas Brinkhoff
 */
public class DrawableObjects {

	/**
	 * Number of layers.
	 */
	protected int numOfLayers = 0;
	/**
	 * The layers.
	 */
	protected Drawable layer[] = null;
	/**
	 * The Last object in a layer.
	 */
	protected Drawable lastInLayer[] = null;
	/**
	 * Hashtable for ID search.
	 */
	protected DupHashtable hashtable = null;
	
	/**
	 * Standard presentation mode
	 */
	public static final int STDMODE = 0;
	/**
	 * Presentation mode
	 */
	protected int mode = STDMODE;
	/**
	 * Number of presentation modes
	 */
	protected int numOfModes = 1;

	/**
	 * Highest id of an object
	 */
	protected long maxId = 0;
	/**
	 * Selected object.
	 */
	protected DrawableObject selectedObject = null;

	/**
	 * Actual drawable used by "getNextVisibleIntersectingObject".
	 */
	protected Drawable visibleDrawable = null;
	/**
	 * Prepared search object used for hashtable search.
	 */
	protected DrawableObject searchObject = new DrawableObject (0,null,null,null,this);

/**
 * Constructor.
 * @param numOfLayers number of layers
 */
public DrawableObjects (int numOfLayers) {
	this.numOfLayers = numOfLayers;
	layer = new Drawable[numOfLayers];
	lastInLayer = new Drawable[numOfLayers];
	hashtable = new DupHashtable(5000);
}
/**
 * Add a drawable into the container.
 * @param prim the drawable
 */
public void addDrawable (Drawable prim) {
	if (prim == null)
		return;
	// put into layer
	int l = prim.getLayer();
	if ((l<0) || (l>=numOfLayers)) {
		System.err.println("wrong layer number!");
		return;
	}
	if (layer[l] == null)
		layer[l] = prim;
	else
		lastInLayer[l].setNextOfLayer (prim);
	lastInLayer[l] = prim;
	prim.setContainer(this);
}
/**
 * Deselects the selected object.
 */
public void deselect () {
	if (selectedObject != null)
		selectedObject.setSelection(false);
	selectedObject = null;
}
/**
 * Zeichnet alle Drawable-Objekte im angegebenen Graphic Context, vorausgesetzt der aktuelle Maßstab
 * wird vom Drawable-Objekt überschritten und es liegt im übergebenen Rechteck.
 * Die Zeichenreihenfolge richtet sich nach den Layern.
 * @param g aktueller Graphic Context
 * @param r Clipping-Rechteck
 * @param scale aktueller Maßstab
 */
public void drawAllObjects (Graphics g, Rectangle r, int scale) {
	for (int l=0; l<numOfLayers; l++)
		drawAllObjectsOfLayer (l, g,r,scale);
}
/**
 * Zeichnet alle Drawable-Objekte im angegebenen Graphic Context, vorausgesetzt der aktuelle Maßstab
 * wird vom Drawable-Objekt überschritten und es liegt im übergebenen Rechteck.
 * Es werden nur die Objekte des angegebenen Layers gezeichnet.
 * @param l Layer
 * @param g aktueller Graphic Context
 * @param r Clipping-Rechteck
 * @param scale aktueller Maßstab
 */
public void drawAllObjectsOfLayer (int l, Graphics g, Rectangle r, int scale) {
	if (l >= numOfLayers)
		return;
	Drawable prim = layer[l];
	while (prim != null) {
		prim.draw (g,r,scale);
		prim = prim.getNextOfLayer();
	}
}
/**
 * Returns enumeration of all drawable objects.
 * @return enumeration
 */
public Enumeration elements () {
	return hashtable.elements();
}
/**
 * Finds the nearest derawable to a given position.
 * @return the nearest drawable
 * @param x x-coordinate of the position
 * @param y y-coordinate of the position
 */
public Drawable findNearestDrawable (int x, int y) {
	System.err.println("DrawableObjects.findNearestDrawable not implemented without search tree");
	return null;
}
/**
 * Returns the data space of all objects. Not implemented without search tree.
 * @return mbr of the data space
 */
public Rectangle getDataspace() {
	System.err.println("DrawableObjects.getDataspace not implemented without search tree");
	return null;
}
/**
 * Returns the presentation mode.
 * @return presentation mode
 */
public int getMode () {
	return mode;
}
/**
 * Returns the next drawable primitive.
 * @return the primitive (or null)
 * @param prevPrim previous drawable; null => look for first drawable
 */
public Drawable getNextDrawable (Drawable prevPrim) {
	int l = 0;
	Drawable next = null;
	// ggf. nächstes im akt. Layer bestimmen
	if (prevPrim != null) {
		next = prevPrim.getNextOfLayer();
		if (next != null)
			return next;
		l = prevPrim.getLayer()+1;
	}
	// ggf. nächstes in den folgenden Layern bestimmen
	while (l < numOfLayers) {
		next = getNextDrawableOfLayer (null,l);
		if (next != null)
			return next;
		l++;
	}
	return null;
}
/**
 * Returns the next drawable primitive of the layer.
 * @return the primitive (or null)
 * @param prim previous drawable; null => look for first drawable
 * @param l the layer (only necessary if prevPrim null)
 */
private Drawable getNextDrawableOfLayer (Drawable prim, int l) {
	if (prim == null)
		if (l >= numOfLayers)
			return null;
		else
			return layer[l];
	else
		return prim.getNextOfLayer();
}
/**
 * Returns the next free identifier.
 * @return free identifier
 */
public long getNextFreeId () {
	return maxId+1;
}
/**
 * Gibt das nächste benannte, sichtbare und ggf. selektierbare Drawable-Objekt zurück,
 * welches sich an der angegebenen Position befindet und den Maßstab einhält.
 * Falls es kein solches Objekt gibt, wird null zurückgegeben.
 * @return gefundenes Drawable-Objekt
 * @param px x-Koordinate der gesuchten Position (in Basis-Koordinaten)
 * @param py y-Koordinate der gesuchten Position (in Basis-Koordinaten)
 * @param s Mindest-Maßstab
 * @param selectable muß das Objekt selektierbar sein?
 */
public DrawableObject getNextVisibleIntersectingObject (int px, int py, int scale, boolean selectable) {
	visibleDrawable = getNextDrawable(visibleDrawable);
	boolean firstSearch = true;
	while (true) {
		while (visibleDrawable == null)
			if (firstSearch) {
				visibleDrawable = getNextDrawable(null);
				firstSearch = false;
			}	
			else
				return null;
		if (visibleDrawable.isVisible(scale) && (visibleDrawable.getObject()!=null) && (visibleDrawable.getObject().getName()!=null) && (visibleDrawable.getObject().getName().length() > 0) &&
			(!selectable || visibleDrawable.getPresentation(scale,mode,visibleDrawable.getObject().getPresValue()).getSelectability()) && (visibleDrawable.interacts (px,py, scale)))
			return visibleDrawable.getObject();
		visibleDrawable = getNextDrawable(visibleDrawable);
	}
}
/**
 * Returns the number of presentation modes.
 * @return the number
 */
public int getNumberOfModes () {
	return numOfModes;
}
/**
 * Returns the number of all objects. Not implemented without search tree.
 * @return number of all objects
 */
public int getNumberOfObjects () {
	System.err.println("DrawableObjects.getNumberOfObjects not implemented");
	return 0;
}
/**
 * Gibt das Drawable-Objekt zurück, welches die übergebende ID besitzt.
 * Falls es kein solches Objekt gibt, wird null zurückgegeben.
 * @return gefundenes Drawable-Objekt
 * @param id gesuchte ID
 */
public DrawableObject getObjectById (long id) {
	searchObject.setId(id);
	return (DrawableObject) hashtable.get(searchObject);
}
/**
 * Returns the selected object.
 * @return select object (or null)
 */
public DrawableObject getSelectedObject ()  {
	return selectedObject;
}
/**
 * Gibt das Drawable-Objekt zurück, welches die übergebende ID besitzt und sichtbar ist.
 * Falls es kein solches Objekt gibt, wird null zurückgegeben.
 * @return gefundenes Drawable-Objekt
 * @param id gesuchte ID
 * @param scale Maßstab
 */
public DrawableObject getVisibleObjectById (long id, int scale) {
	searchObject.setId(id);
	DrawableObject obj = (DrawableObject) hashtable.get(searchObject);
	while ((obj != null) && !obj.isVisible(scale))
		obj = (DrawableObject) hashtable.getNext(searchObject);
	return obj;
}
/**
 * Public "conctructor" of a drawable object; it will also be inserted into the container.
 * @param id id of the object
 * @param typeName the name of the object type
 * @param name the name of the object
 * @param info attributes
 * @param defScale the default scale
 * @return the object
 */
public DrawableObject newDrawableObject (long id, DrawableObjectType type, String name, String info) {
	DrawableObject obj = new DrawableObject (id, type, name,info, this);
	hashtable.put (obj,obj);
	if (id > maxId)
		maxId = id;
	return obj;
}
/**
 * Public "conctructor" of a drawable object; it will also be inserted into the container.
 * @param id id of the object
 * @param typeName the name of the object type
 * @param name the name of the object
 * @param info attributes
 * @return the object
 */
public DrawableObject newDrawableObject (long id, String typeName, String name, String info) {
	return newDrawableObject (id, DrawableObjectType.getObjectType(typeName), name,info);
}
/**
 * Public "conctructor" of a drawable object; it will also be inserted into the container.
 * @param id id of the object
 * @param typeName the name of the object type
 * @param name the name of the object
 * @param info attributes
 * @param defScale the default scale
 * @return the object
 */
public DrawableObject newDrawableObject (long id, String typeName, String name, String info, int defScale) {
	DrawableObject obj = newDrawableObject (id, DrawableObjectType.getObjectType(typeName), name,info);
	obj.setDefaultScale(defScale);
	return obj;
}
/**
 * Reads a drawable object from the data input stream.
 * @return the object
 * @param  r  data input stream
 * @param  version  the file version
 */
public DrawableObject readDrawableObject (DataInputStream r, int version) throws IOException {
	return newDrawableObject (r.readLong(),r.readUTF(),r.readUTF(),r.readUTF(),(int)r.readShort());
}
/**
 * Reads a drawable object from the entry input.
 * @return the object
 * @param r entry input
 */
public DrawableObject readDrawableObject (EntryInput r, int version) {
	return newDrawableObject (r.readLong(), r.readString(),DrawableText.correctString(r.readString()),
		DrawableText.correctString(r.readString()),r.eol()?DrawableObject.NODEFSCALE:r.readInt());
}
/**
 * Liest das Drawable-Objekt vom Entry-Input ein. DEPRECATED!
 * @param r Entry-Input
 * @param objType vom Entry-Input eingelesener Drawable-Typ
 */
public void readDrawableObject (EntryInput r, String objType) {
	if (objType == null)
		return;
	// Primitiv einlesen
	DrawableSymbol.bufferedText = null;
	Drawable prim = Drawable.readDrawable (r,objType);
	if (prim == null)
		return;
	// Objekt-Daten einlesen
	long id = r.readLong();
	if (r.eol()) {
		addDrawable (prim);
		if (DrawableSymbol.bufferedText != null)
			addDrawable(DrawableSymbol.bufferedText);
		DrawableSymbol.bufferedText = null;
	}
	else {
		DrawableObject obj = newDrawableObject (id, r.readString(),DrawableText.correctString(r.readString()),DrawableText.correctString(r.readString()));
		obj.addDrawable(prim);
		addDrawable(prim);
		if (DrawableSymbol.bufferedText != null) {
			obj.addDrawable(DrawableSymbol.bufferedText);
			addDrawable(DrawableSymbol.bufferedText);
			DrawableSymbol.bufferedText = null;
		}
		// ggf. zugehörigen Text suchen
		// kann entfallen, wenn alle Länderdateien umgestellt
		else if (objType.startsWith("S")) {
			DrawableObject baseObj = getObjectById(obj.getId());
			if ((baseObj != null) && (baseObj.getNumberOfDrawables() > 1))
				obj.addDrawable(baseObj.getDrawable(1));
		}
	}	
}
/**
 * Löscht das Drawable-Objekt aus dem Container und löst die Beziehung
 * zum Grafik-Primitiv auf.
 * @param obj zu löschendes Drawable-Objekt
 */
public void remove (DrawableObject obj) {
	if (obj == null)
		return;
	// delete primitives
	for (int i=0; i<obj.getNumberOfDrawables(); i++) {
		int l = obj.getDrawable(i).getLayer();
		if (layer[l] == obj.getDrawable(i))
			layer[l] = obj.getDrawable(i).getNextOfLayer();
		else {
			Drawable prev = layer[l];
			while ((prev != null) && (prev.getNextOfLayer() != obj.getDrawable(i)))
				prev = prev.getNextOfLayer();
			if (prev != null)
				prev.setNextOfLayer (obj.getDrawable(i).getNextOfLayer());
		}
	}
	// delete from hashtable
	hashtable.remove(obj);
	// delete links
	if (selectedObject == obj)
		deselect();
	visibleDrawable = null;
}
/**
 * Removes all objects from the container.
 */
public void removeAll () {
	deselect ();
	for (int i=0; i<numOfLayers; i++)
		layer[i] = null;
	hashtable = new DupHashtable(5000);
	visibleDrawable = null;
}
/**
 * Removes all objects of a given layer from the container.
 * @param l the layer
 */
public void removeAllObjectsOfLayer (int l) {
	if ((l<0) || (l >= numOfLayers))
		return;
	// delete from hashtable
	Drawable prim = layer[l];
	while (prim != null) {
		DrawableObject obj = prim.getObject();
		if (obj != null)
			hashtable.remove(obj);
		prim = prim.getNextOfLayer();
	}
	// delete layer
	layer[l] = null;
	// adapt the stored objects
	deselect();
	visibleDrawable = null;
}
/**
 * Deselektiert das bislang selektierte Drawable-Objekt und selektiert das übergebene Objekt.
 * @param obj zu löschendes Drawable-Objekt
 */
public void select (DrawableObject obj) {
	if (obj == null)
		return;
	deselect ();
	obj.setSelection (true);
	selectedObject = obj;
}
/**
 * Sets the presentation mode.
 * @param mode the presentation mode
 */
public void setMode (int mode) {
	this.mode = mode;
}
/**
 * Sets the number of presentation modes.
 * @param mode the number
 */
public void setNumberOfModes (int num) {
	this.numOfModes = num;
}
}
