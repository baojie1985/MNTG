package drawables;

import java.awt.*;
import util.*;

/**
 * Class for drawable lines.
 *
 * @version	3.10	11.09.00	GML-support, draw improved
 * @version	3.00	03.06.00	subclass of DrawablePolygon
 * @version	2.30	13.02.00	interacts added
 * @version	2.20	09.10.99	write added
 * @version	2.10	22.06.99	support of databases
 * @version	2.00	15.03.99	adapted to Drawables v2.0
 * @author Thomas Brinkhoff
 */

public class DrawablePolyline extends DrawablePolygon {

/**
 * Konstruktor.
 */
protected DrawablePolyline () {
	pres = DrawablePresentation.get("default");
}
/**
 * Konstruktor; <b>VERALTET!</b>.
 */
public DrawablePolyline (int[] xpoints, int[] ypoints, int npoints, String presName) {
	this (npoints,xpoints,ypoints,presName);
}
/**
 * Konstruktor.
 * @param npoints Anzahl der Punkte
 * @param xpoints Array mit x-Koordinaten (wird nicht kopiert!)
 * @param ypoints Array mit y-Koordinaten (wird nicht kopiert!)
 */
public DrawablePolyline (int npoints, int[] xpoints, int[] ypoints) {
	super(npoints,xpoints,ypoints);
}
/**
 * Konstruktor.
 * @param npoints Anzahl der Punkte
 * @param xpoints Array mit x-Koordinaten (wird nicht kopiert!)
 * @param ypoints Array mit y-Koordinaten (wird nicht kopiert!)
 * @param presName Name des Darstellungsobjekts
 */
public DrawablePolyline (int npoints, int[] xpoints, int[] ypoints, String presName) {
	super (npoints,xpoints,ypoints,false,presName);
}
/**
 * Konstruktor.
 * @param npoints Anzahl der Punkte
 * @param xpoints Array mit x-Koordinaten (wird nicht kopiert!)
 * @param ypoints Array mit y-Koordinaten (wird nicht kopiert!)
 * @param presName Name des Darstellungsobjekts
 * @param minScale Mindest-Maßstab zum Zeichnen
 * @param maxScale Maximal-Maßstab zum Zeichnen
 */
public DrawablePolyline (int npoints, int[] xpoints, int[] ypoints, String presName, int minScale, int maxScale) {
	super (npoints,xpoints,ypoints,false,presName,minScale,maxScale);
}
/**
 * Zeichnet den Linienzug im Graphic Context g,
 * vorausgesetzt der aktuelle Maßstab wird eingehalten.
 * @param g aktueller Graphic Context
 * @param scale aktueller Maßstab
 * @param mode aktueller Darstellungsmodus
 * @param value Darstellungswert
 */
protected void drawProtected (Graphics g, int scale, int mode, int value) {
	if (num == 0)
		return;
	DrawablePresentation ap = pres.get(scale,mode,value);
	Color c = selected? ap.selectionColor : ap.color;
	if (c == null)
		return;
	if ((ap.size > 1) || (ap.type == DrawableLine.SPLITTED)) {
		int oldX = (xCoords[0]+scale-1)/scale;
		int oldY = (yCoords[0]+scale-1)/scale;
		for (int i=1; i<num; i++) {
			int actX = (xCoords[i]+scale-1)/scale;
			int actY = (yCoords[i]+scale-1)/scale;
			DrawableLine.drawProtected (g,ap,c,c,oldX,oldY,actX,actY);
			oldX = actX;
			oldY = actY;
		}
	}
	else {
		for (int i=0; i<num; i++) {
			outX[i] = (xCoords[i]+scale-1)/scale;
			outY[i] = (yCoords[i]+scale-1)/scale;
		}
		g.setColor (c);
		g.drawPolyline (outX,outY,num);
	}
}
/*
 * Testet, ob der Linienzug durch den übergebenen Punkt ausgewählt wird.
 * @return ausgewählt?
 * @param x x-Koordinate des zu testenden Punktes
 * @param y y-Koordinate des zu testenden Punktes
 * @param scale aktuelle Maßstab
 */
public boolean interacts (int x, int y, int scale) {
	for (int i=1; i<num; i++)
		if (DrawableLine.interacts (x,y,xCoords[i-1],yCoords[i-1],xCoords[i],yCoords[i],
			Math.min(xCoords[i-1],xCoords[i]),Math.min(yCoords[i-1],yCoords[i]),
			Math.max(xCoords[i-1],xCoords[i]),Math.max(yCoords[i-1],yCoords[i]),scale))
			return true;
	return false;
}
/**
 * Writes the primitive as GML tag.
 * @param out entry writer
 */
public void writeGML (EntryWriter out) {
	String name = "pline_";
	if (obj != null)
		name += obj.getName();
	out.println("<LineString name=\"l"+name+"\" srsName=\"br\">");
	out.print  (" <CList>");
	for (int i=0; i<num-1; i++)
		out.print(xCoords[i]+","+yCoords[i]+" ");
	out.println(xCoords[num-1]+","+yCoords[num-1]+"</CList>");
	out.println("</LineString>");
}
}
