package drawables;

import java.awt.*;
import util.*;

/**
 * Drawable class for circles.
 *
 * @version	2.20	03.06.00	support of presentation modes
 * @version	2.10	09.10.99	write added
 * @version	2.00	14.03.99	adapted to Drawable v2.0
 * @author Thomas Brinkhoff
 */

public class DrawableCircle extends DrawableRectangle {

/**
 * Konstruktor.
 */
protected DrawableCircle () {
}
/**
 * Konstruktor für nicht gefüllte Kreise.
 * @param x x-Koordinate des Mittelpunkts
 * @param y y-Koordinate des Mittelpunkts
 * @param rad Radius
 */
public DrawableCircle (int x, int y, int rad) {
	super (x-rad,y-rad,x+rad,y+rad);
}
/**
 * Konstruktor für Kreise.
 * @param x x-Koordinate des Mittelpunkts
 * @param y y-Koordinate des Mittelpunkts
 * @param rad Radius
 * @param pFilled gefüllt?
 * @param presName Name des Darstellungsobjekts
 */
public DrawableCircle (int x, int y, int rad, boolean pFilled, String presName) {
	super (x-rad,y-rad,x+rad,y+rad, pFilled,presName);
}
/**
 * Konstruktor für Kreise.
 * @param x x-Koordinate des Mittelpunkts
 * @param y y-Koordinate des Mittelpunkts
 * @param rad Radius
 * @param pFilled gefüllt?
 * @param presName Name des Darstellungsobjekts
 * @param pMinScale Mindest-Maßstab zum Zeichnen
 * @param pMaxScale Maximal-Maßstab zum Zeichnen
 */
public DrawableCircle (int x, int y, int rad, boolean pFilled, String presName, int pMinScale, int pMaxScale) {
	super (x-rad,y-rad,x+rad,y+rad, pFilled,presName, pMinScale,pMaxScale);
}
/**
 * Zeichnet den Kreis im Graphic Context g,
 * vorausgesetzt der aktuelle Maßstab scale wird vom Kreis eingehalten. 
 * @param g aktueller Graphic Context
 * @param scale aktueller Maßstab
 * @param mode aktueller Darstellungsmodus
 * @param value Darstellungswert
 */
protected void drawProtected (Graphics g, int scale, int mode, int value) {
	DrawablePresentation ap = pres.get(scale,mode,value);
	if (filled && selected && (ap.selectionFillColor != null)) {
		g.setColor (ap.selectionFillColor);
		g.fillOval (mbr.x/scale,mbr.y/scale, mbr.width/scale,mbr.height/scale);
	}
	if (filled && !selected && (ap.fillColor != null)) {
		g.setColor (ap.fillColor);
		g.fillOval (mbr.x/scale,mbr.y/scale, mbr.width/scale,mbr.height/scale);
	}	
	if (selected && (ap.selectionColor != null)) {
		g.setColor (ap.selectionColor);
		g.drawOval (mbr.x/scale,mbr.y/scale, mbr.width/scale,mbr.height/scale);
	}
	else if (ap.color != null) {
		g.setColor (ap.color);
		g.drawOval (mbr.x/scale,mbr.y/scale, mbr.width/scale,mbr.height/scale);
	}
}
/**
 * Liest den Kreis vom Entry-Input ein. Erster Eintrag "type". <br>
 * type == 0: x,y,rad <br>
 * type == 1: filled,presName <br>
 * type == 2: minScale,maxScale
 * @return this
 * @param r Entry-Input
 */
public EntryReadable read (EntryInput r) {
	int type = r.readInt();
	int x = r.readInt();
	int y = r.readInt();
	int rad = r.readInt();
	mbr = new Rectangle (x-rad,y-rad, x+rad,y+rad);
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
 * Writes the circle.
 * @param out entry writer
 * @param type type of the output; meaning see read
 */
protected void writeProtected (EntryWriter out, int type) {
	out.print("Circle\t"+type+'\t');
	// Koordinaten schreiben
	int x = mbr.x+mbr.width/2;
	int y = mbr.y+mbr.height/2;
	int rad = mbr.width/2;
	out.print(x); out.print('\t');
	out.print(y); out.print('\t');
	out.print(rad); out.print('\t');
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
