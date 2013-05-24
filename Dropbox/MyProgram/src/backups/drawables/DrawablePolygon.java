package drawables;

import java.awt.*;
import java.io.*;
import util.*;

/**
 * Class for drawable polygons.
 *
 * @version	2.40	07.02.02	data output streams added
 * @version	2.30	25.08.00	GML-support
 * @version	2.20	03.06.00	drawProtected corrected, getNumberOfPoints added, support of presentation modes
 * @version	2.10	18.06.99	support of databases
 * @version	2.00	15.03.99	adapted to Drawable v2.0
 * @author Thomas Brinkhoff
 */

public class DrawablePolygon extends FilledDrawable {

	/**
	 * x-coordinates.
	 */
	protected int[] xCoords = null;
	/**
	 * y-coordinates.
	 */
	protected int[] yCoords = null;
	/**
	 * Number of coordinates.
	 */
	protected int num = 0;
	/**
	 * Output buffer for x-coordinates.
	 */
	protected int[] outX = null;
	/**
	 * Output buffer for y-coordinates.
	 */
	protected int[] outY = null;

/**
 * Constructor.
 */
public DrawablePolygon () {
	pres = DrawablePresentation.get("default");
}
/**
 * Constructor for non-filled polygons with holes.
 * @param npoints number of points
 * @param xpoints x-coordinates (will not be duplicated!)
 * @param ypoints y-coordinaten (will not be duplicated!)
 */
public DrawablePolygon (int npoints, int[] xpoints, int[] ypoints) {
	this();
	xCoords = xpoints;
	yCoords = ypoints;
	num = npoints;
	if (num > 0)  {
		outX = new int[num];
		outY = new int[num];
		mbr = computeMBR(num,xCoords,yCoords);
	}
}
/**
 * Constructor for polygons.
 * @param npoints number of points
 * @param xpoints x-coordinates (will not be duplicated!)
 * @param ypoints y-coordinaten (will not be duplicated!)
 * @param filled polygon filled?
 * @param presName name of the presentation object
 */
public DrawablePolygon (int npoints, int[] xpoints, int[] ypoints, boolean filled, String presName) {
	this (npoints,xpoints,ypoints);
	this.filled = filled;
	if (filled)
		layer = AREALAYER;
	pres = DrawablePresentation.get(presName);
}
/**
 * Constructor for polygons.
 * @param npoints number of points
 * @param xpoints x-coordinates (will not be duplicated!)
 * @param ypoints y-coordinaten (will not be duplicated!)
 * @param filled polygon filled?
 * @param presName name of the presentation object
 * @param minScale least detailed scale where the drawable is visible
 * @param maxScale most detailed scale where the drawable is visible
 */
public DrawablePolygon (int npoints, int[] xpoints, int[] ypoints, boolean filled, String presName, int minScale, int maxScale) {
	this (npoints,xpoints,ypoints,filled,presName);
	this.minScale = minScale;
	this.maxScale = maxScale;
}
/**
 * Computes the minimum bounding box for coordinate arrays.
 * @return the mbr
 * @param num number of points
 * @param xCoords x-coordinates
 * @param yCoords y-coordinates
 */
static public Rectangle computeMBR (int num, int xCoords[], int yCoords[]) {
	if (num <= 0)
		return null;
	int xmin = xCoords[0];
	int xmax = xmin;
	int ymin = yCoords[0];
	int ymax = ymin;
	for (int i=1; i<num; i++) {
		if (xCoords[i] < xmin)
			xmin = xCoords[i];
		if (xCoords[i] > xmax)
			xmax = xCoords[i];
		if (yCoords[i] < ymin)
			ymin = yCoords[i];
		if (yCoords[i] > ymax)
			ymax = yCoords[i];
	}
	return new Rectangle (xmin,ymin, xmax-xmin,ymax-ymin);	
}
/**
 * Draws the polygon using a given GraphicContext, if the scale is observed.
 * @param g graphical context
 * @param scale actual scale
 * @param mode actual mode
 * @param value presentation value
 */
protected void drawProtected (Graphics g, int scale, int mode, int value) {
	if (num == 0)
		return;
	DrawablePresentation ap = pres.get(scale,mode,value);
	for (int i = 0; i < num; i++) {
		outX[i] = (xCoords[i]+scale-1)/scale;
		outY[i] = (yCoords[i]+scale-1)/scale;
	}	
	if (filled && selected && (ap.selectionFillColor != null)) {
		g.setColor (ap.selectionFillColor);
		g.fillPolygon (outX,outY,num);
	}
	if (filled && !selected && (ap.fillColor != null)) {
		g.setColor (ap.fillColor);
		g.fillPolygon (outX,outY,num);
	}	
	if (selected && (ap.selectionColor != null)) {
		g.setColor (ap.selectionColor);
		g.drawPolygon (outX,outY,num);
	}
	else if (ap.color != null) {
		g.setColor (ap.color);
		g.drawPolygon (outX,outY,num);
	}
}
/**
 * Returns the number of points.
 * @return number of points
 */
public int getNumberOfPoints () {
	return num;
}
/**
 * Reads the polygon from the data input stream. The first entry is the "type". <br>
 * type == 0: num,xCoords/yCoords <br>
 * type == 1: filled,presName <br>
 * type == 2: minScale,maxScale
 * type == 3: layer
 * @return  this
 * @param  r  data input stream
 */
public EntryReadable read (DataInputStream r) throws IOException {
	int type = r.readByte();
	// read coordinates
	num = r.readShort();
	xCoords = new int[num];
	yCoords = new int[num];
	outX = new int[num];
	outY = new int[num];
	for (int i=0; i<num; i++) {
		if (i>0) {
			xCoords[i] = readCoord(r)+xCoords[i-1];
			yCoords[i] = readCoord(r)+yCoords[i-1];
		}
		else {
			xCoords[i] = readCoord(r);
			yCoords[i] = readCoord(r);
		}
	}
	mbr = computeMBR(num,xCoords,yCoords);
	// read additional attributes
	if (type > 0) {
		if (getClass().getName().endsWith("gon")) {
			filled = r.readBoolean();
			if (filled)
				layer = AREALAYER;
		}
		String presName = r.readUTF();
		pres = DrawablePresentation.get(presName);
		if (type > 1) {
			minScale = r.readShort();
			maxScale = r.readShort();
			if (type > 2)
				layer = r.readByte();
		}
	}
	return this;
}
/**
 * Reads the polygon from the EntryInput. First entry is the "type". <br>
 * type == 0: num,xCoords/yCoords <br>
 * type == 1: filled,presName <br>
 * type == 2: minScale,maxScale
 * type == 3: layer
 * @return this
 * @param r EntryInput
 */
public EntryReadable read (EntryInput r) {
	int type = r.readInt();
	// read coordinates
	num = r.readInt();
	boolean relative = (num<0);
	if (relative)
		num = -num;
	xCoords = new int[num];
	yCoords = new int[num];
	outX = new int[num];
	outY = new int[num];
	for (int i=0; i<num; i++) {
		if (relative && (i>0)) {
			xCoords[i] = readCoord(r)+xCoords[i-1];
			yCoords[i] = readCoord(r)+yCoords[i-1];
		}
		else if (relative) {
			xCoords[i] = readCoord(r);
			yCoords[i] = readCoord(r);
		}
		else {
			xCoords[i] = r.readInt();
			yCoords[i] = r.readInt();
		}
	}
	mbr = computeMBR(num,xCoords,yCoords);
	// read additional attributes
	if (type > 0) {
		if (getClass().getName().endsWith("gon")) {
			int f = r.readInt();
			filled = (f!=0);
			if (filled)
				layer = AREALAYER;
		}
		String presName = r.readString();
		pres = DrawablePresentation.get(presName);
		if (type > 1) {
			minScale = r.readInt();
			maxScale = r.readInt();
			if (type > 2)
				layer = (byte)r.readInt();
		}
	}
	return this;
}
/**
 * Writes the polygon to the data output stream.
 * @param  out  data output stream
 * @param  type  type of the output; meaning see read-operation
 */
public void write (DataOutputStream out, int type) {
	try {
		boolean isPolygon = !getClass().getName().endsWith("line");
		if (isPolygon)
			out.writeByte((int)'P');
		else
			out.writeByte((int)'L');
		out.writeByte(type);
		// coordinates
		out.writeShort((short)num);
		writeCoord(out,xCoords[0]);
		writeCoord(out,yCoords[0]);
		for (int i=1; i<num; i++) {
			writeCoord(out,xCoords[i]-xCoords[i-1]);
			writeCoord(out,yCoords[i]-yCoords[i-1]);
		}
		// further attributes
		if (type > 0) {
			if (isPolygon)
				out.writeBoolean(filled);
			out.writeUTF(pres.getName());
			if (type > 1) {
				out.writeShort((short)minScale);
				out.writeShort((short)maxScale);
				if (type > 2)
					out.writeByte((byte)layer);
			}
		}
		out.writeLong((obj==null)? 0 : Math.abs(obj.getId()));
	}
	catch (IOException ex) {}
}
/**
 * Writes the primitive as GML tag.
 * @param out entry writer
 */
public void writeGML (EntryWriter out) {
	String name = "gon_";
	if (obj != null)
		name += obj.getName();
	out.println("<Polygon name=\"p"+name+"\" srsName=\"br\">");
	out.println(" <LineString name=\"l"+name+"\" srsName=\"br\">");
	out.print  ("  <CList>");
	for (int i=0; i<num; i++)
		out.print(xCoords[i]+","+yCoords[i]+" ");
	out.println(xCoords[0]+","+yCoords[0]+"</CList>");
	out.println(" </LineString>");
	out.println("</Polygon>");
}
/**
 * Writes the polygon to the EntryWriter.
 * @param out entry writer
 * @param type type of the output; meaning see read-operation
 */
protected void writeProtected (EntryWriter out, int type) {
	boolean isPolygon = !getClass().getName().endsWith("line");
	if (isPolygon)
		out.print('P');
	else
		out.print('L');
	out.print("\t"+type+'\t');
	// Koordinaten schreiben
	out.print(-num); out.print('\t');
	writeCoord(out,xCoords[0]);
	writeCoord(out,yCoords[0]);
	for (int i=1; i<num; i++) {
		writeCoord(out,xCoords[i]-xCoords[i-1]);
		writeCoord(out,yCoords[i]-yCoords[i-1]);
	}
	if (type == 0)
		return;
	// ggf. weitere Attribute ausgeben
	if (isPolygon)
		if (filled)
			out.print("1\t");
		else
			out.print("0");
	out.print(pres.getName());
	if (type == 1)
		return;
	out.print("\t"+minScale+'\t'+maxScale);
	if (type == 2)
		return;
	out.print("\t"+layer);
}
}
