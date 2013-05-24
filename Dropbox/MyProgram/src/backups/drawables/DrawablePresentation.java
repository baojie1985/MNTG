package drawables;

import java.awt.*;

/**
 * Class for defining the graphical presentation of drawable primitives.
 *
 * @version	2.00	08.06.00	support of presentation modes
 * @version	1.30	09.12.99	set-operations return the object
 * @version	1.20	09.10.99	getName added
 * @version	1.10	22.06.99	getNext added
 * @version	1.00	16.03.99	first version
 * @author Thomas Brinkhoff
 */

public class DrawablePresentation {

	/**
	 * The "first" objects; it is the starting point for seraching.
	 */
	private static DrawablePresentation first = null;

	/**
	 * Name of the presentation.
	 */
	private String name = "default";

	/**
	 * The at least detailed scale where the object is visible.
	 */
	protected int minScale = 0x7fffffff; 
	/**
	 * The most detailed scale where the object is visible.
	 */
	protected int maxScale = 0; 
	/**
	 * All modes.
	 */
	public static final int ALLMODES = -1; 
	/**
	 * The mode.
	 */
	protected int mode = ALLMODES; 
	/**
	 * Flag for the case where no value exists.
	 */
	public static final int NOVALUE = 0x80000000; 
	/**
	 * The value.
	 */
	protected int value = NOVALUE; 

	/**
	 * Is the object selectable?
	 */
	protected boolean selectable = false;
	/**
	 * Is the object visible?
	 */
	protected boolean visible = true;

	/**
	 * Color of the border.
	 */
	protected Color color = Color.black;
	/**
	 * Fill color.
	 */
	protected Color fillColor = Color.white;
	/**
	 * Color of the border of selected primitives.
	 */
	protected Color selectionColor = Color.red;
	/**
	 * Fill color of selected primitives.
	 */
	protected Color selectionFillColor = Color.red;

	/**
	 * The type of the primitive.
	 */
	protected int type = 0;
	/**
	 * Size in pixel or point.
	 */
	protected int size = 1;
	/**
	 * Offset in pixel or point.
	 */
	protected int offset = 0;

	/**
	 * The next presentation object with the same name.
	 */
	private DrawablePresentation nextOfSame = null;
	/**
	 * The next presentation object with a different name.
	 */
	private DrawablePresentation nextOfOther = null;

/**
 * Gibt das Darstellungsobjekt zurück, das den gleichen Namen hat und für den angegebenen Maßstab gilt.
 * @return gefundenes DrawablePresentation-Objekt
 * @param scale akt. Maßstab
 */
private DrawablePresentation get (int scale) {
	DrawablePresentation actObj = this;
	while (actObj.nextOfSame != null)
		if ((scale <= actObj.minScale) && (scale > actObj.maxScale))
			return actObj;
		else
			actObj = actObj.nextOfSame;
	return actObj;
}
/**
 * Returns the presentation object, which has the same name as this and which
 * fits for the given scale, the given mode and the given value.
 * @return the presentation object
 * @param scale actual scale
 * @param mode actual mode
 * @param value required value
 */
public DrawablePresentation get (int scale, int mode, int value) {
	DrawablePresentation actObj = this;
	while (actObj != null)
		if ((scale <= actObj.minScale) && (scale >= actObj.maxScale) &&
			((mode == ALLMODES) || (actObj.mode == ALLMODES) || (actObj.mode == mode)) &&
			((value == NOVALUE) || (actObj.value == NOVALUE) || (actObj.value == value)))
			return actObj;
		else
			actObj = actObj.nextOfSame;
	return first;
}
/**
 * Returns the first presentation object with the given name.
 * @return the object found (or the default object)
 * @param name the name
 */
public static DrawablePresentation get (String name) {
	DrawablePresentation actObj = first;
	while (actObj != null)
		if (actObj.name.compareTo(name) == 0)
			return actObj;
		else
			actObj = actObj.nextOfOther;
	return first;
}
/**
 * Gibt die Randfarbe zurück.
 * @return Farbe
 */
public Color getColor () {
	return color;
}
/**
 * Gibt die Füllfarbe zurück.
 * @return Farbe
 */
public Color getFillColor () {
	return fillColor;
}
/**
 * Gibt den Namen zurück.
 * @return Name
 */
public String getName () {
	return name;
}
/**
 * Gibt das nächste Darstellungsobjekt zurück, das den gleichen Namen hat.
 * @return gefundenes DrawablePresentation-Objekt (oder null)
 */
public DrawablePresentation getNext () {
	return nextOfSame;
}
/**
 * Gibt den Offset zurück.
 * @return Offset
 */
public int getOffset () {
	return offset;
}
/**
 * Gibt die Selektierbarkeit zurück.
 * @return selektierbar?
 */
public boolean getSelectability () {
	return selectable;
}
/**
 * Gibt die Selektionsrandfarbe zurück.
 * @return Farbe
 */
public Color getSelectionColor () {
	return selectionColor;
}
/**
 * Gibt die Selektionsfüllfarbe zurück.
 * @return Farbe
 */
public Color getSelectionFillColor () {
	return selectionFillColor;
}
/**
 * Gibt die Größe zurück.
 * @return Größe
 */
public int getSize () {
	return size;
}
/**
 * Gibt den Typ zurück.
 * @return Typ
 */
public int getType () {
	return type;
}
/**
 * Gibt die grundsätzliche Sichtbarkeit zurück.
 * @return sichtbar?
 */
public boolean getVisibility () {
	return visible;
}
/**
 * Inits the object: all presentations are removed.
 */
public static void init () {
	if (first != null) {
		first.nextOfSame = null;
		first.nextOfOther = null;
	}	
}
/**
 * Creates a new presentation object.
 * @return the object
 * @param name Name
 */
public static DrawablePresentation newDrawablePresentation (String name) {
	// create first presentation if necessary
	if (first == null)
		first = new DrawablePresentation().setVisibility(false);
	// create object
	DrawablePresentation newObj = new DrawablePresentation();
	newObj.name = name;
	// und anhängen
	DrawablePresentation father = get(name);
	if (father.name.equals(name)) {
		father = father.get(-1);
		newObj.nextOfSame = father.nextOfSame;
		father.nextOfSame = newObj;
	}	
	else {
		newObj.nextOfOther = father.nextOfOther;
		father.nextOfOther = newObj;
	}
	return newObj;
}
/**
 * Erzeugt neues Darstellungsobjekt.
 * @return erzeugtes DrawablePresentation-Objekt
 * @param name Name
 * @param mode Darstellungsmodus
 * @param value Wert
 * @param selectable selektierbar?
 * @param color Farbe
 * @param selectionColor Selektionsfarbe
 */
public static DrawablePresentation newDrawablePresentation (String name, int mode, int value, boolean selectable, Color color, Color selectionColor) {
	return newDrawablePresentation (name,mode,value,selectable,color,color,selectionColor,selectionColor);
}
/**
 * Erzeugt neues Darstellungsobjekt.
 * @return erzeugtes DrawablePresentation-Objekt
 * @param name Name
 * @param mode Darstellungsmodus
 * @param value Wert
 * @param selectable selektierbar?
 * @param color Randfarbe
 * @param selectionColor Selektionsrandfarbe
 * @param type Typ
 * @param size Größe in Pixel/Punkt
 */
public static DrawablePresentation newDrawablePresentation (String name, int mode, int value, boolean selectable, Color color, Color selectionColor, int type, int size) {
	return newDrawablePresentation (name,mode,value, selectable,color,color,selectionColor,selectionColor, type,size);
}
/**
 * Erzeugt neues Darstellungsobjekt.
 * @return erzeugtes DrawablePresentation-Objekt
 * @param name Name
 * @param mode Darstellungsmodus
 * @param value Wert
 * @param selectable selektierbar?
 * @param color Randfarbe
 * @param selectionColor Selektionsrandfarbe
 * @param type Typ
 * @param size Größe in Pixel/Punkt
 * @param offset Offset in Pixel
 * @param minScale (eingeschlossener) Minimal-Maßstab
 * @param maxScale (nicht mehr eingeschlossener) Maximal-Maßstab
 */
public static DrawablePresentation newDrawablePresentation (String name, int mode, int value, boolean selectable, Color color, Color selectionColor, int type, int size, int offset, int minScale, int maxScale) {
	return newDrawablePresentation (name,mode,value, selectable,color,color,selectionColor,selectionColor, type,size,offset, minScale,maxScale);
}
/**
 * Erzeugt neues Darstellungsobjekt.
 * @return erzeugtes DrawablePresentation-Objekt
 * @param name Name
 * @param mode Darstellungsmodus
 * @param value Wert
 * @param selectable selektierbar?
 * @param color Randfarbe
 * @param fillColor Füllfarbe
 * @param selectionColor Selektionsrandfarbe
 * @param selectionFillColor Selektionsfüllfarbe
 */
public static DrawablePresentation newDrawablePresentation (String name, int mode, int value, boolean selectable, Color color, Color fillColor, Color selectionColor, Color selectionFillColor) {
	DrawablePresentation newObj = newDrawablePresentation (name);
	newObj.mode = mode;
	newObj.value = value;
	newObj.selectable = selectable;
	newObj.color = color;
	newObj.fillColor = fillColor;
	newObj.selectionColor = selectionColor;
	newObj.selectionFillColor = selectionFillColor;
	return newObj;
}
/**
 * Erzeugt neues Darstellungsobjekt.
 * @return erzeugtes DrawablePresentation-Objekt
 * @param name Name
 * @param mode Darstellungsmodus
 * @param value Wert
 * @param selectable selektierbar?
 * @param color Randfarbe
 * @param fillColor Füllfarbe
 * @param selectionColor Selektionsrandfarbe
 * @param selectionFillColor Selektionsfüllfarbe
 * @param type Typ
 * @param size Größe in Pixel/Punkt
 */
public static DrawablePresentation newDrawablePresentation (String name, int mode, int value, boolean selectable, Color color, Color fillColor, Color selectionColor, Color selectionFillColor, int type, int size) {
	DrawablePresentation newObj = newDrawablePresentation (name,mode,value, selectable,color,fillColor,selectionColor,selectionFillColor);
	newObj.type = type;
	newObj.size = size;
	return newObj;
}
/**
 * Erzeugt neues Darstellungsobjekt.
 * @return erzeugtes DrawablePresentation-Objekt
 * @param name Name
 * @param mode Darstellungsmodus
 * @param value Wert
 * @param selectable selektierbar?
 * @param color Randfarbe
 * @param fillColor Füllfarbe
 * @param selectionColor Selektionsrandfarbe
 * @param selectionFillColor Selektionsfüllfarbe
 * @param type Typ
 * @param size Größe in Pixel/Punkt
 * @param offset Offset in Pixel
 * @param minScale (eingeschlossener) Minimal-Maßstab
 * @param maxScale (nicht mehr eingeschlossener) Maximal-Maßstab
 */
public static DrawablePresentation newDrawablePresentation (String name, int mode, int value, boolean selectable, Color color, Color fillColor, Color selectionColor, Color selectionFillColor, int type, int size, int offset, int minScale, int maxScale) {
	DrawablePresentation newObj = newDrawablePresentation (name,mode,value, selectable,color,fillColor,selectionColor,selectionFillColor, type,size);
	newObj.offset = offset;
	newObj.minScale = minScale;
	newObj.maxScale = maxScale;
	return newObj;
}
/**
 * Erzeugt neues Darstellungsobjekt.
 * @return erzeugtes DrawablePresentation-Objekt
 * @param name Name
 * @param selectable selektierbar?
 * @param color Farbe
 * @param selectionColor Selektionsfarbe
 */
public static DrawablePresentation newDrawablePresentation (String name, boolean selectable, Color color, Color selectionColor) {
	return newDrawablePresentation (name, selectable,color,color,selectionColor,selectionColor);
}
/**
 * Erzeugt neues Darstellungsobjekt.
 * @return erzeugtes DrawablePresentation-Objekt
 * @param name Name
 * @param selectable selektierbar?
 * @param color Randfarbe
 * @param selectionColor Selektionsrandfarbe
 * @param type Typ
 * @param size Größe in Pixel/Punkt
 */
public static DrawablePresentation newDrawablePresentation (String name, boolean selectable, Color color, Color selectionColor, int type, int size) {
	return newDrawablePresentation (name, selectable,color,color,selectionColor,selectionColor, type,size);
}
/**
 * Erzeugt neues Darstellungsobjekt.
 * @return erzeugtes DrawablePresentation-Objekt
 * @param name Name
 * @param selectable selektierbar?
 * @param color Randfarbe
 * @param selectionColor Selektionsrandfarbe
 * @param type Typ
 * @param size Größe in Pixel/Punkt
 * @param offset Offset in Pixel
 * @param minScale (eingeschlossener) Minimal-Maßstab
 * @param maxScale (nicht mehr eingeschlossener) Maximal-Maßstab
 */
public static DrawablePresentation newDrawablePresentation (String name, boolean selectable, Color color, Color selectionColor, int type, int size, int offset, int minScale, int maxScale) {
	return newDrawablePresentation (name, selectable,color,color,selectionColor,selectionColor, type,size,offset, minScale,maxScale);
}
/**
 * Erzeugt neues Darstellungsobjekt.
 * @return erzeugtes DrawablePresentation-Objekt
 * @param name Name
 * @param selectable selektierbar?
 * @param color Randfarbe
 * @param fillColor Füllfarbe
 * @param selectionColor Selektionsrandfarbe
 * @param selectionFillColor Selektionsfüllfarbe
 */
public static DrawablePresentation newDrawablePresentation (String name, boolean selectable, Color color, Color fillColor, Color selectionColor, Color selectionFillColor) {
	return newDrawablePresentation (name,ALLMODES,NOVALUE, selectable,color,fillColor,selectionColor,selectionFillColor);
}
/**
 * Erzeugt neues Darstellungsobjekt.
 * @return erzeugtes DrawablePresentation-Objekt
 * @param name Name
 * @param selectable selektierbar?
 * @param color Randfarbe
 * @param fillColor Füllfarbe
 * @param selectionColor Selektionsrandfarbe
 * @param selectionFillColor Selektionsfüllfarbe
 * @param type Typ
 * @param size Größe in Pixel/Punkt
 */
public static DrawablePresentation newDrawablePresentation (String name, boolean selectable, Color color, Color fillColor, Color selectionColor, Color selectionFillColor, int type, int size) {
	return newDrawablePresentation (name,ALLMODES,NOVALUE, selectable,color,fillColor,selectionColor,selectionFillColor,type,size);
}
/**
 * Erzeugt neues Darstellungsobjekt.
 * @return erzeugtes DrawablePresentation-Objekt
 * @param name Name
 * @param selectable selektierbar?
 * @param color Randfarbe
 * @param fillColor Füllfarbe
 * @param selectionColor Selektionsrandfarbe
 * @param selectionFillColor Selektionsfüllfarbe
 * @param type Typ
 * @param size Größe in Pixel/Punkt
 * @param offset Offset in Pixel
 * @param minScale (eingeschlossener) Minimal-Maßstab
 * @param maxScale (nicht mehr eingeschlossener) Maximal-Maßstab
 */
public static DrawablePresentation newDrawablePresentation (String name, boolean selectable, Color color, Color fillColor, Color selectionColor, Color selectionFillColor, int type, int size, int offset, int minScale, int maxScale) {
	return newDrawablePresentation (name,ALLMODES,NOVALUE, selectable,color,fillColor,selectionColor,selectionFillColor, type,size,offset, minScale,maxScale);
}
/**
 * Setzt die Randfarbe.
 * @return this
 * @param pColor Farbe
 */
public DrawablePresentation setColor (Color pColor) {
	color = pColor;
	return this;
}
/**
 * Setzt die Füllfarbe.
 * @return this
 * @param pColor Farbe
 */
public DrawablePresentation setFillColor (Color pColor) {
	fillColor = pColor;
	return this;
}
/**
 * Setzt den Offset.
 * @return this
 * @param pOffset Offset
 */
public DrawablePresentation setOffset (int pOffset) {
	offset = pOffset;
	return this;
}
/**
 * Schaltet die Selektierbarkeit.
 * @return this
 * @param s Selektivität
 */
public DrawablePresentation setSelectability (boolean s) {
	selectable = s;
	return this;
}
/**
 * Setzt die Selektionsrandfarbe.
 * @return this
 * @param pColor Farbe
 */
public DrawablePresentation setSelectionColor (Color pColor) {
	selectionColor = pColor;
	return this;
}
/**
 * Setzt die Selektionsfüllfarbe.
 * @return this
 * @param pColor Farbe
 */
public DrawablePresentation setSelectionFillColor (Color pColor) {
	selectionFillColor = pColor;
	return this;
}
/**
 * Setzt die Größe.
 * @return this
 * @param pSize Größe
 */
public DrawablePresentation setSize (int pSize) {
	size = pSize;
	return this;
}
/**
 * Setzt den Typ.
 * @return this
 * @param pType Typ
 */
public DrawablePresentation setType (int pType) {
	type = pType;
	return this;
}
/**
 * Schaltet die grundsätzliche Sichtbarkeit an bzw. aus.
 * @return this
 * @param on sichtbar?
 */
public DrawablePresentation setVisibility (boolean on) {
	visible = on;
	return this;
}
}
