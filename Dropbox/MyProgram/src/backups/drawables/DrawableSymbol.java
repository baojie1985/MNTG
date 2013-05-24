package drawables;

import java.awt.*;
import util.*;

/**
 * Class for drawable symbols.
 *
 * @version	2.40	19.08.2003	value removed
 * @version	2.30	25.08.2000	GML-support
 * @version	2.20	01.06.2000	support of presentation modes
 * @version	2.10	26.11.1999	write, UPDOWNARROW, value, moveTo
 * @version	2.00	15.03.1999	Anpassung an Drawable 2.0
 * @author Thomas Brinkhoff
 */

public class DrawableSymbol extends FilledDrawable {

	/**
	 * Square.
	 */
	public static final int SQUARE = 0;
	/**
	 * Circle.
	 */
	public static final int CIRCLE = 1;
	/**
	 * Square with point.
	 */
	public static final int SQUAREDOT = 2;
	/**
	 * Circle with point.
	 */
	public static final int CIRCLEDOT = 3;
	/**
	 * Ring.
	 */
	public static final int RING = 4;
	/**
	 * Triangle.
	 */
	public static final int TRIANGLE = 5;
	/**
	 * Up or down arrow.
	 */
	public static final int UPDOWNARROW = 6;

	/**
	 * x-coordinate.
	 */
	protected int x = 0;
	/**
	 * y-coordinate.
	 */
	protected int y = 0;

	/**
	 * Read drawable text. DEPRECATED!
	 */
	protected static DrawableText bufferedText = null;

/**
 * Konstruktor.
 */
protected DrawableSymbol () {
	layer = POINTLAYER;
}
/**
 * Konstruktor für Symbol.
 * @param px x-Koordinate
 * @param py y-Koordinate
 * @param presName Name des Darstellungsobjekts
 */
public DrawableSymbol (int px, int py, String presName) {
	this();
	x = px;
	y = py;
	mbr = new Rectangle (x-1,y-1,3,3);
	pres = DrawablePresentation.get(presName);
}
/**
 * Konstruktor für Symbol.
 * @param px x-Koordinate
 * @param py y-Koordinate
 * @param presName Name des Darstellungsobjekts
 * @param pMinScale Mindest-Maßstab zum Zeichnen
 * @param pMaxScale Maximal-Maßstab zum Zeichnen
 */
public DrawableSymbol (int x, int y, String presName, int pMinScale, int pMaxScale) {
	this (x,y,presName);
	minScale = pMinScale;
	maxScale = pMaxScale;
}
/**
 * Zeichnet das Symbol im Graphic Context g,
 * vorausgesetzt der aktuelle Maßstab wird eingehalten. 
 * @param g aktueller Graphic Context
 * @param scale aktueller Maßstab
 * @param mode aktueller Darstellungsmodus
 * @param value Darstellungswert
 */
protected void drawProtected (Graphics g, int scale, int mode, int pvalue) {
	DrawablePresentation ap = pres.get(scale,mode,pvalue);
	int size = ap.size;
	try {
		size = size*obj.getDataValue(0);
	}
	catch (Exception ex) {
	}
	// Symbol-Füllung zeichnen
	if (selected)
		g.setColor (ap.selectionFillColor);
	else
		g.setColor (ap.fillColor);
	int off = 0;			// Offset für Symbol mit eingelagerten Grafik-Primitiven
	if (size % 2 == 1)
		off = 1;
	Polygon pol = null;
	switch (ap.type) {
		case SQUAREDOT:	
		case SQUARE:		g.fillRect (x/scale-size/2,y/scale-size/2,size,size);
							break;
		case CIRCLEDOT:	
		case CIRCLE:		g.fillOval (x/scale-size/2,y/scale-size/2,size,size);
							break;
		case RING:			g.fillOval (x/scale-size/2,y/scale-size/2,size,size);	
							g.setColor (Color.white);
							g.fillOval (x/scale-size*3/10,y/scale-size*3/10,(size+off)*3/5,(size+off)*3/5);
							break;
		case TRIANGLE:		int pgxT[] = {x/scale-size/2,x/scale+size/2,x/scale};	
							int pgyT[] = {y/scale+size/2,y/scale+size/2,y/scale-size/2};
							pol = new Polygon(pgxT,pgyT,3);
							g.fillPolygon (pol);
							break;
		case UPDOWNARROW:	if (size == 0)
								g.fillRect (x/scale-3,y/scale,7,4);
							else
								g.fillRect (x/scale-3,y/scale-(Math.abs(size)+1)/2,6,Math.abs(size)+1);
							break;
	}
	// Symbol-Umriß zeichnen
	if (selected)
		g.setColor (ap.selectionColor);
	else
		g.setColor (ap.color);
	switch (ap.type) {
		case SQUAREDOT:		g.fillRect (x/scale-size/6,y/scale-size/6,(size+off)/3,(size+off)/3);
		case SQUARE:		g.drawRect (x/scale-size/2,y/scale-size/2,size,size); break;
		case RING:			g.drawOval (x/scale-size*3/10,y/scale-size*3/10,(size+off)*3/5,(size+off)*3/5);
							g.drawOval (x/scale-size/2,y/scale-size/2,size,size); break;
		case CIRCLEDOT:		g.drawOval (x/scale-size/6,y/scale-size/6,(size+off)/3,(size+off)/3);
		case CIRCLE:		g.drawOval (x/scale-size/2,y/scale-size/2,size,size); break;
		case TRIANGLE:		g.drawPolygon (pol); break;
		case UPDOWNARROW:	if (size > 0) {
								g.drawRect (x/scale-3,y/scale-(size+1)/2,6,size+1);
								g.drawLine (x/scale-5,y/scale+(size+2)/2,x/scale+5,y/scale+(size+2)/2);
							}
							else if (size < 0) {
								g.drawRect (x/scale-3,y/scale+(size-1)/2,6,-size+1);
								g.drawLine (x/scale-5,y/scale+(size-1)/2,x/scale+5,y/scale+(size-1)/2);
							}
							else
								g.drawRect (x/scale-3,y/scale,6,4);
							break;
	}
}
/**
 * Gibt die x-Koordinate des Symbols zurück.
 * @return x-Koordinate
 */
public int getX () {
	return x;
}
/**
 * Gibt die y-Koordinate des Symbols zurück.
 * @return y-Koordinate
 */
public int getY () {
	return y;
}
/**
 * Testet, ob das Symbol durch den übergebenen Punkt ausgewählt wird.
 * @return ausgewählt?
 * @param px x-Koordinate in Basis-Koordinaten
 * @param py y-Koordinate in Basis-Koordinaten
 * @param scale akt. Maßstab
 */
public boolean interacts (int px, int py, int scale) {
	int actMode = (container==null? DrawableObjects.STDMODE:container.getMode());
	int pvalue = (obj==null? DrawablePresentation.NOVALUE:obj.getPresValue());
	DrawablePresentation ap = pres.get(scale,actMode,pvalue);
	int size = Math.abs(ap.size*pvalue) / 10;
	if (size < 4)
		size = 4;
	return (px >= x-size/2*scale) && (px <= x+size/2*scale) &&
	       (py >= y-size/2*scale-1) && (py <= y+size/2*scale);
}
/**
 * Verschiebt Symbol auf die neue Position (x,y).
 * @param x neue x-Koordinate
 * @param y neue y-Koordinate
 */
public void moveTo (int x, int y) {
	this.x = x;
	this.y = y;
}
/**
 * Liest das Symbol vom Entry-Input ein. Erster Eintrag "type" (>1 veraltet!). <br>
 * type == 0: x,y,presName <br>
 * type == 1: minScale,maxScale <br>
 * type == 4: value (nicht bei Typ 2 und 3)<br>
 * type == 2: txt,textPres,hor,vert <br>
 * type == 3: txtMinScale,txtMaxScale
 * @return eingelesenes Symbol bzw. null
 * @param r Entry-Input
 */
public EntryReadable read (EntryInput r) {
	int type = r.readInt();
	x = r.readInt();
	y = r.readInt();
	//value = 10;
	pres = DrawablePresentation.get(r.readString());
	mbr = new Rectangle (x-1,y-1,3,3);
	if (type == 0)
		return (EntryReadable) this;
	minScale = r.readInt();
	maxScale = r.readInt();
 	if (type == 1)
		return (EntryReadable) this;
	// das nachfolgende ist veraltet
	if (type == 4) {
		r.readInt();	// value
		return (EntryReadable) this;
	}
	String txt = DrawableText.correctString(r.readString());
	String txtPres = r.readString();
	int h = r.readInt();
	int v = r.readInt();
	if (type == 2) {
	 	bufferedText = new DrawableText(x,y,txt,txtPres,h,v,minScale,maxScale);
		return (EntryReadable) this;
	}	
	int txtMinScale = r.readInt();
	int txtMaxScale = r.readInt();
 	bufferedText = new DrawableText(x,y,txt,txtPres,h,v,txtMinScale,txtMaxScale);
	return (EntryReadable) this;
}

/**
 * Writes the primitive as GML tag.
 * @param out entry writer
 */
public void writeGML (EntryWriter out) {
	String name = "pnt_";
	if (obj != null)
		name += obj.getName();
	out.println("<Point name=\""+name+"\" srsName=\"br\">");
	out.print  (" <CList>"+x+","+y+"</CList>");
	out.println("</Point>");
}
/**
 * Writes the symbol.
 * @param out entry writer
 * @param type type of the output (0-1); meaning see read
 */
protected void writeProtected (EntryWriter out, int type) {
	out.print("S\t"+type+'\t');
	// Koordinaten schreiben
	out.print(x); out.print('\t');
	out.print(y); out.print('\t');
	out.print(pres.getName());
	if (type == 0)
		return;
	// ggf. weitere Attribute ausgeben
	out.print("\t"+minScale+'\t'+maxScale);
}
}
