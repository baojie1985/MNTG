package drawables;

import java.awt.*;
import util.*;

/**
 * Drawable class for line segments.
 *
 * @version	2.51	15.08.03	MBR extension increased by 1 unit, selected becomes byte
 * @version	2.50	25.08.00	GML-support
 * @version	2.40	01.06.00	support of presentation modes
 * @version	2.30	09.03.00	interacts, liesOn, changePoint, intersects, direction corrected or added
 * @version	2.20	09.10.99	write added
 * @version	2.10	22.06.99	more line styles
 * @version	2.00	19.03.99	adapted to Drawable v2.0
 * @author Thomas Brinkhoff
 */
 
public class DrawableLine extends Drawable {
	
	/**
	 * ungerichtet.
	 */
	public static final int UNDIRECTED = 0;
	/**
	 * in Hinrichtung.
	 */
	public static final int FORWARDS = 1;
	/**
	 * in Rückrichtung.
	 */
	public static final int BACKWARDS = 2;
	/**
	 * in beide Richtungen.
	 */
	public static final int BOTHDIRECTIONS = 3;

	/**
	 * normaler Linientyp.
	 */
	public static final int NORMALTYPE = 0;
	/**
	 * gesplittete Linie.
	 */
	public static final int SPLITTED = 1;

	/**
	 * x-Koordinate 1.
	 */
	private int x1 = 0;
	/**
	 * y-Koordinate 1.
	 */
	private int y1 = 0;
	/**
	 * x-Koordinate 2.
	 */
	private int x2 = 0;
	/**
	 * y-Koordinate 1.
	 */
	private int y2 = 0;

	/**
	 * Selektion (richtungsweise).
	 */
	private byte selected = 0;

/**
 * Konstruktor.
 */
protected DrawableLine () {
}
/**
 * Konstruktor.
 * @param px1 x-Koordinate des Startpunkts
 * @param py1 y-Koordinate des Startpunkts
 * @param px2 x-Koordinate des Endpunkts
 * @param py2 y-Koordinate des Endpunkts
 */
public DrawableLine (int px1, int py1, int px2, int py2) {
	x1 = px1;
	y1 = py1;
	x2 = px2;
	y2 = py2;
	mbr = new Rectangle(Math.min(x1,x2), Math.min(y1,y2), Math.abs(x2-x1+1), Math.abs(y2-y1+1));
	pres = DrawablePresentation.get("default");
}
/**
 * Konstruktor.
 * @param px1 x-Koordinate des Startpunkts
 * @param py1 y-Koordinate des Startpunkts
 * @param px2 x-Koordinate des Endpunkts
 * @param py2 y-Koordinate des Endpunkts
 * @param presName Name des Darstellungsobjekts
 */
public DrawableLine (int px1, int py1, int px2, int py2, String presName) {
	this (px1,py1,px2,py2);
	pres = DrawablePresentation.get(presName);
}
/**
 * Konstruktor.
 * @param px1 x-Koordinate des Startpunkts
 * @param py1 y-Koordinate des Startpunkts
 * @param px2 x-Koordinate des Endpunkts
 * @param py2 y-Koordinate des Endpunkts
 * @param presName Name des Darstellungsobjekts
 * @param pMinScale Mindest-Maßstab zum Zeichnen
 * @param pMaxScale Maximal-Maßstab zum Zeichnen
 */
public DrawableLine (int px1, int py1, int px2, int py2, String presName, int pMinScale, int pMaxScale) {
	this (px1,py1,px2,py2, presName);
	minScale = pMinScale;
	maxScale = pMaxScale;
}
/**
 * Changes the first point of the line.
 * @param x x-coordinate
 * @param y y-coordinate
 */
public void changePoint1 (int x, int y) {
	x1 = x;
	y1 = y;
	mbr = new Rectangle(Math.min(x1,x2), Math.min(y1,y2), Math.abs(x2-x1), Math.abs(y2-y1));
}
/**
 * Changes the second point of the line.
 * @param x x-coordinate
 * @param y y-coordinate
 */
public void changePoint2 (int x, int y) {
	x2 = x;
	y2 = y;
	changePoint1 (x1,y1);
}
/**
 * Deselektiert die Linie richtungsbezogen.
 * @param dir zu deselektierende Richtung
 */
public void deselect (int direction) {
	selected = (byte)((int)(selected) & (~direction));
}
/**
 * Zeichnet die Linie im Graphic Context g,
 * @param g aktueller Graphic Context
 * @param scale aktueller Maßstab
 * @param mode aktueller Darstellungsmodus
 * @param value Darstellungswert
 */
protected void drawProtected (Graphics g, int scale, int mode, int value) {
	// Darstellung vorbereiten
	DrawablePresentation ap = pres.get(scale,mode,value);
	Color forwardsColor = ap.color;
	if (isSelected(FORWARDS))
		forwardsColor = ap.selectionColor;
	Color backwardsColor = ap.color;
	if (isSelected(BACKWARDS))
		backwardsColor = ap.selectionColor;
	// Koordinaten berechnen
	int cx1 = x1/scale;
	int cy1 = y1/scale;
	int cx2 = x2/scale;
	int cy2 = y2/scale;
	// Zeichnen
	drawProtected (g,ap,forwardsColor,backwardsColor,cx1,cy1,cx2,cy2);
}
/**
 * Zeichnet die Linie im Graphic Context g,
 * @param g aktueller Graphic Context
 * @param ap aktuelle Darstellungseigenschaften
 * @param forwardColor Farbe in Hinrichtung
 * @param forwardColor Farbe in Rückrichtung
 * @param cx1 1. x-Pixel-Koordinate
 * @param cy1 1. y-Pixel-Koordinate
 * @param cx2 2. x-Pixel-Koordinate
 * @param cy2 2. y-Pixel-Koordinate
 */
public static void drawProtected (Graphics g, DrawablePresentation ap, Color forwardsColor, Color backwardsColor, int cx1, int cy1, int cx2, int cy2) {
	int startIndex = 0;
	// Grundlinie
	if (ap.type == NORMALTYPE) {
		g.setColor (forwardsColor);
		g.drawLine (cx1,cy1, cx2,cy2);
		startIndex = 1;
	}
	// Zeichnen breiter / gesplitteter Linien
	if ((ap.size > 1) || (ap.type == SPLITTED)) {
		Color c1 = forwardsColor;
		Color c2 = forwardsColor;
		if (Math.abs(cy2-cy1) > Math.abs(cx2-cx1)) {
			if (cy2 < cy1)
				c2 = backwardsColor;
			else
				c1 = backwardsColor;
			for (int i=startIndex; i<ap.size; i++) {
				g.setColor(c1);
				g.drawLine (cx1+i+ap.offset,cy1, cx2+i+ap.offset,cy2);
				g.setColor(c2);
				g.drawLine (cx1-i-ap.offset,cy1, cx2-i-ap.offset,cy2);
			}
		}
		else {
			if (cx2 < cx1)
				c1 = backwardsColor;
			else
				c2 = backwardsColor;
			for (int i=startIndex; i<ap.size; i++) {
				g.setColor(c1);
				g.drawLine (cx1,cy1+i+ap.offset, cx2,cy2+i+ap.offset);
				g.setColor(c2);
				g.drawLine (cx1,cy1-i-ap.offset, cx2,cy2-i-ap.offset);
			}
		}
	}
}
/**
 * Returns the x-coordinate of the first point.
 * @return x-coordinate
 */
public int getX1() {
	return x1;
}
/**
 * Returns the x-coordinate of the second point.
 * @return x-coordinate
 */
public int getX2() {
	return x2;
}
/**
 * Returns the y-coordinate of the first point.
 * @return y-coordinate
 */
public int getY1() {
	return y1;
}
/**
 * Returns the y-coordinate of the second point.
 * @return y-coordinate
 */
public int getY2() {
	return y2;
}
/*
 * Testet, ob die Linie durch den übergebenen Punkt ausgewählt wird.
 * @return ausgewählt?
 * @param x x-Koordinate des zu testenden Punktes
 * @param y y-Koordinate des zu testenden Punktes
 * @param scale aktuelle Maßstab
 */
public boolean interacts (int x, int y, int scale) {
	return interacts (x,y,x1,y1,x2,y2,mbr.x,mbr.y,mbr.x+mbr.width-1,mbr.y+mbr.height-1,scale);
}
/*
 * Testet, ob die Linie durch den übergebenen Punkt ausgewählt wird.
 * @return ausgewählt?
 * @param x x-Koordinate des zu testenden Punktes
 * @param y y-Koordinate des zu testenden Punktes
 * @param x1 x1-Koordinate der Linie
 * @param y1 y1-Koordinate der Linie
 * @param x2 x2-Koordinate der Linie
 * @param y2 y2-Koordinate der Linie
 * @param mx1 x1-Koordinate des MUR
 * @param my1 y1-Koordinate des MUR
 * @param mx2 x2-Koordinate des MUR
 * @param my2 y2-Koordinate des MUR
 * @param scale aktuelle Maßstab
 */
public static boolean interacts (int x, int y, int x1, int y1, int x2, int y2, int mx1, int my1, int mx2, int my2, int scale) {
	if ( (x < mx1-2*scale) || (x > mx2+2*scale) ||
	     (y < my1-2*scale) || (y > my2+2*scale) )
		return false;
	if ((x1 == x2) || (y1 == y2))
		return true;
	else if ((mx2-mx1) >= (my2-my1)) {
		int ny = (x-x1)*(y2-y1)/(x2-x1)+y1;
		return Math.abs(ny-y) < 3*scale;
	}
	else {
		int nx = (y-y1)*(x2-x1)/(y2-y1)+x1;
		return Math.abs(nx-x) < 3*scale;
	}
}
/**
 * Tests if the lines intersect. If they intersect, the intersection point is
 * returned, otherwise null.
 * @return intersection point (or null)
 * @param line line to be tested
 */
public Point intersects (DrawableLine line) {
	int x21 = line.getX1();
	int x22 = line.getX2();
	int y21 = line.getY1();
	int y22 = line.getY2();
	long a = x1-x2;
  	long b = y21-y22;
	long c = y1-y2;
	long d = x21-x22;
	long denom = a*b - c*d;
	if (denom == 0)
		return null;	// parallel!
	long e = x1-x21;
	long f = y1-y21;
	long t1nom = e*b - f*d;
	if ( ((t1nom<0) && (denom>0)) ||
		((t1nom>0) && (denom<0)) )
		return null;	// t1 < 0
	if (Math.abs(denom) < Math.abs(t1nom))
		return null;	// t1 > 1
	long t2nom = e*c - f*a;
	if ( ((t2nom<0) && (denom>0)) ||
		((t2nom>0) && (denom<0)) )
		return null;	// t2 < 0
	if (Math.abs(denom) < Math.abs(t2nom))
		return null;	// t2 > 1
	int px = (int)(x1+(-t1nom*a)/denom);
	int py = (int)(y1+(-t1nom*c)/denom);
	return new Point(px,py);
}
/**
 * Gibt zurück, ob die Linie selektiert ist.
 * @return Linie selektiert?
 */
public boolean isSelected () {
	return selected > 0;
}
/**
 * Gibt zurück, ob die Linie richtungsbezogen selektiert ist.
 * @return Linie selektiert?
 */
public boolean isSelected (int direction) {
	return (selected & direction) > 0;
}
/**
 * Liest die Linie vom Entry-Input ein. Erster Eintrag "type". <br>
 * type == 0: x1,y1,x2,y2 <br>
 * type == 1: presName <br>
 * type == 2: minScale,maxScale
 * @return die Linie
 * @param r this
 */
public EntryReadable read (EntryInput r) {
	int type = r.readInt();
	x1 = r.readInt();
	y1 = r.readInt();
	x2 = r.readInt();
	y2 = r.readInt();
	mbr = new Rectangle (Math.min(x1,x2), Math.min(y1,y2), Math.abs(x2-x1), Math.abs(y2-y1));
	if (type == 0) {
		pres = DrawablePresentation.get("default");
		return (EntryReadable) this;
	}	
	pres = DrawablePresentation.get(r.readString());
	if (type == 1)
		return (EntryReadable) this;
	minScale = r.readInt();
	maxScale = r.readInt();
	return (EntryReadable) this;
}
/**
 * Selektiert die Linie richtungsbezogen.
 * @param dir zu selektierende Richtung
 */
public void select (int direction) {
	selected = (byte)((int)selected | direction);
}
/**
 * (De-)Selektiert die Linie.
 * @param on selektieren?
 */
public void setSelection (boolean on) {
	if (on)
		selected = BOTHDIRECTIONS;
	else
		selected = 0;
}
/**
 * Testet, ob der angegebene Punkt mit einer Toleranz auf der Linie liegt.
 * @return Berechneter Lagepunkt; null, falls keine Deckung
 * @param x x-Koordinate
 * @param y y-Koordinate
 * @param tolerance Toleranzwert
 */
public Point touches (int x, int y, int tolerance) {
	if ( (x < mbr.x-tolerance) || (x > mbr.x+mbr.width+tolerance) ||
	     (y < mbr.y-tolerance) || (y > mbr.y+mbr.height+tolerance) )
		return null;
	if (x1 == x2)
		return new Point(x1,y);
	else if (y1 == y2)
		return new Point(x,y1);
	else if (mbr.width >= mbr.height) {
		int ny = (x-x1)*(y2-y1)/(x2-x1)+y1;
		if (Math.abs(ny-y) <= tolerance)
			return new Point(x,ny);
		else
			return null;
	}
	else {
		int nx = (y-y1)*(x2-x1)/(y2-y1)+x1;
		if (Math.abs(nx-x) <= tolerance)
			return new Point(nx,y);
		else
			return null;
	}
}
/**
 * Writes the primitive as GML tag.
 * @param out entry writer
 */
public void writeGML (EntryWriter out) {
	String name = "line_";
	if (obj != null)
		name += obj.getName();
	out.println("<LineString name=\""+name+"\" srsName=\"br\">");
	out.print  (" <CList>"+x1+","+y1+" "+x2+","+y2+"</CList>");
	out.println("</LineString>");
}
/**
 * Writes the line.
 * @param out entry writer
 * @param type type of the output; meaning see read
 */
protected void writeProtected (EntryWriter out, int type) {
	out.print("Line\t"+type+'\t');
	// Koordinaten schreiben
	out.print(x1); out.print('\t');
	out.print(y1); out.print('\t');
	out.print(x2); out.print('\t');
	out.print(y2); out.print('\t');
	if (type == 0)
		return;
	// ggf. weitere Attribute ausgeben
	out.print(pres.getName()+'\t');
	if (type == 1)
		return;
	out.print(minScale); out.print('\t');
	out.print(maxScale); out.print('\t');
}
}
