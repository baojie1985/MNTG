package drawables;

import java.awt.*;
import util.*;

/**
 * Class for drawable rectangles.
 *
 * @version	2.40	05.10.2002	all constructors public
 * @version	2.30	03.06.2000	support of presentation modes
 * @version	2.20	25.03.2000	new constructor
 * @version	2.10	09.10.1999	write added
 * @version	2.00	15.03.1999	adapted to Drawable v2.0
 * @author Thomas Brinkhoff
 */
 
public class DrawableRectangle extends FilledDrawable {


/**
 * Konstruktor.
 */
public DrawableRectangle () {
}
/**
 * Konstruktor für nicht-gefülltes Rechteck.
 * @param x1 x-Koordinate eines Eckpunktes
 * @param y1 y-Koordinate eines Eckpunktes
 * @param x2 x-Koordinate des gegenüberliegenden Eckpunktes
 * @param y2 y-Koordinate des gegenüberliegenden Eckpunktes
 */
public DrawableRectangle (int x1, int y1, int x2, int y2) {
	mbr = new Rectangle (Math.min(x1,x2), Math.min(y1,y2), Math.abs(x2-x1), Math.abs(y2-y1));
	pres = DrawablePresentation.get("default");
}
/**
 * Konstruktor für Rechtecke.
 * @param x1 x-Koordinate eines Eckpunktes
 * @param y1 y-Koordinate eines Eckpunktes
 * @param x2 x-Koordinate des gegenüberliegenden Eckpunktes
 * @param y2 y-Koordinate des gegenüberliegenden Eckpunktes
 * @param pFilled gefüllt?
 * @param presName Name des Darstellungsobjekts
 */
public DrawableRectangle (int x1, int y1, int x2, int y2, boolean pFilled, String presName) {
	this (x1,y1,x2,y2);
	filled = pFilled;
	if (filled)
		layer = AREALAYER;
	pres = DrawablePresentation.get(presName);
}
/**
 * Konstruktor für Rechtecke.
 * @param x1 x-Koordinate eines Eckpunktes
 * @param y1 y-Koordinate eines Eckpunktes
 * @param x2 x-Koordinate des gegenüberliegenden Eckpunktes
 * @param y2 y-Koordinate des gegenüberliegenden Eckpunktes
 * @param pFilled gefüllt?
 * @param presName Name des Darstellungsobjekts
 * @param pMinScale Mindest-Maßstab zum Zeichnen
 * @param pMaxScale Maximal-Maßstab zum Zeichnen
 */
public DrawableRectangle (int x1, int y1, int x2, int y2, boolean pFilled, String presName, int pMinScale, int pMaxScale) {
	this (x1,y1,x2,y2, pFilled,presName);
	minScale = pMinScale;
	maxScale = pMaxScale;
}
/**
 * Konstruktor für nicht-gefülltes Rechteck.
 * @param rect Rechteck
 */
public DrawableRectangle (Rectangle rect) {
	mbr = new Rectangle (rect);
	pres = DrawablePresentation.get("default");
}
/**
 * Zeichnet das Rechteck im Graphic Context g.
 * @param g aktueller Graphic Context
 * @param scale aktueller Maßstab
 * @param mode aktueller Darstellungsmodus
 * @param value Darstellungswert
 */
protected void drawProtected (Graphics g, int scale, int mode, int value) {
	DrawablePresentation ap = pres.get(scale,mode,value);
	if (filled && selected && (ap.selectionFillColor != null)) {
		g.setColor (ap.selectionFillColor);
		g.fillRect (mbr.x/scale,mbr.y/scale, mbr.width/scale,mbr.height/scale);
	}
	if (filled && !selected && (ap.fillColor != null)) {
		g.setColor (ap.fillColor);
		g.fillRect (mbr.x/scale,mbr.y/scale, mbr.width/scale,mbr.height/scale);
	}	
	if (selected && (ap.selectionColor != null)) {
		g.setColor (ap.selectionColor);
		g.drawRect (mbr.x/scale,mbr.y/scale, mbr.width/scale,mbr.height/scale);
	}
	else if (ap.color != null) {
		g.setColor (ap.color);
		g.drawRect (mbr.x/scale,mbr.y/scale, mbr.width/scale,mbr.height/scale);
	}
}
/**
 * Liest das Rechteck vom Entry-Input ein. Erster Eintrag "type". <br>
 * type == 0: x1,y1,x2,y2 <br>
 * type == 1: filled,presName <br>
 * type == 2: minScale,maxScale
 * @return this
 * @param r Entry-Input
 */
public EntryReadable read (EntryInput r) {
	int type = r.readInt();
	int x1 = r.readInt();
	int y1 = r.readInt();
	int x2 = r.readInt();
	int y2 = r.readInt();
	mbr = new Rectangle (Math.min(x1,x2), Math.min(y1,y2), Math.abs(x2-x1), Math.abs(y2-y1));
	if (type == 0) {
		pres = DrawablePresentation.get("default");
		return (EntryReadable) this;
	}	
	int f = r.readInt();
	filled = (f!=0);
	pres = DrawablePresentation.get(r.readString());
	if (filled)
		layer = AREALAYER;
	if (type == 1)
		return (EntryReadable) this;
	minScale = r.readInt();
	maxScale = r.readInt();
	return (EntryReadable) this;
}
/**
 * Writes the rectangle.
 * @param out entry writer
 * @param type type of the output; meaning see read
 */
protected void writeProtected (EntryWriter out, int type) {
	out.print("Rectangle\t"+type+'\t');
	// Koordinaten schreiben
	int x2 = mbr.x+mbr.width/2;
	int y2 = mbr.y+mbr.height/2;
	out.print(mbr.x); out.print('\t');
	out.print(mbr.y); out.print('\t');
	out.print(x2); out.print('\t');
	out.print(y2); out.print('\t');
	if (type == 0)
		return;
	// ggf. weitere Attribute ausgeben
	if (filled)
		out.print("1\t");
	else
		out.print("0\t");
	out.print(pres.getName()+'\t');
	if (type == 1)
		return;
	out.print(minScale); out.print('\t');
	out.print(maxScale); out.print('\t');
}
}
