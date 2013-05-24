package drawables;

import java.awt.*;
import java.awt.image.*;
import java.net.*;
import util.*;

/**
 * Drawable class for bitmaps.
 *
 * @version	1.20	08.06.00	support of presentation modes
 * @version	1.10	09.10.99	write added
 * @version	1.00	19.03.99	first version
 * @author Thomas Brinkhoff
 */
 
public class DrawableBitmap extends Drawable {

	/**
	 * Image observer.
	 */
	private static ImageObserver observer = null;
	
	/**
	 * The bitmap.
	 */
	private Image image = null;
	/**
	 * Is the bitmap scalable?
	 */
	private boolean scalable = false;


/**
 * Constructor.
 */
protected DrawableBitmap () {
	layer = Drawable.BITMAPLAYER;
	pres = DrawablePresentation.get("default");
}
/**
 * Constructor.
 * @param x left x-coordinate
 * @param y upper y-coordinate
 * @param width width
 * @param height height
 * @param url complete URL
 * @param presName name of the presentation
 * @param scalable is the bitmap scalable?
 */
public DrawableBitmap (int x, int y, int width, int height, URL url, String presName, boolean scalable) {
	this();
	mbr = new Rectangle (x, y, width, height);
	image = Toolkit.getDefaultToolkit().getImage (url);
	pres = DrawablePresentation.get(presName);
	this.scalable = scalable;
	if (scalable)
		layer = Drawable.AREALAYER;
}
/**
 * Constructor.
 * @param x left x-coordinate
 * @param y upper y-coordinate
 * @param width width
 * @param height height
 * @param url complete URL
 * @param presName name of the presentation
 * @param scalable is the bitmap scalable?
 * @param minScale least detailed scale where the drawable is visible
 * @param maxScale most detailed scale where the drawable is visible
 */
public DrawableBitmap (int x, int y, int width, int height, URL url, String presName, boolean scalable, int minScale, int maxScale) {
	this (x,y, width,height, url, presName,scalable);
	this.minScale = minScale;
	this.maxScale = maxScale;
}
/**
 * Constructor.
 * @param x left x-coordinate
 * @param y upper y-coordinate
 * @param master bitmap which delivers the required properties.
 */
public DrawableBitmap (int x, int y, DrawableBitmap master) {
	this();
	mbr = new Rectangle (x,y, master.mbr.width, master.mbr.height);
	image = master.image;
	scalable = master.scalable;
	layer = master.layer;
	minScale = master.minScale;
	maxScale = master.maxScale;
	pres = master.pres;
}
/**
 * Constructor of a non-scalable bitmap.
 * @param x left x-coordinate
 * @param y upper y-coordinate
 * @param url complete URL
 */
public DrawableBitmap (int x, int y, URL url) {
	this(x,y,1,1,url,"default",false);
}
/**
 * Draws the bitmap.
 * @param g graphical context
 * @param scale actual scale
 * @param mode actual mode (has no influene)
 * @param value presentation value (has no influence)
 */
protected void drawProtected (Graphics g, int scale, int mode, int value) {
	if (scalable)
		g.drawImage (image, mbr.x/scale,mbr.y/scale, mbr.width/scale,mbr.height/scale, observer);
	else
		g.drawImage (image, mbr.x/scale,mbr.y/scale, observer);
}
/**
 * Announces the image observer to the class.
 * @param newObserver the image observer
 */
public static void init (ImageObserver newObserver) {
	observer = newObserver;
}
/**
 * Read a bitmap from file. The first entry ist the type. <br>
 * type == 0: x,y,URL <br>
 * type == 1: width,height,presName,scale <br>
 * type == 2: minScale,maxScale
 * @return the bitmap or null
 * @param r entry input
 */
public EntryReadable read (EntryInput r) {
	int type = r.readInt();
	int x = r.readInt();
	int y = r.readInt();
	String urlName = r.readString();
	if (type == 0) {
		try  {
			return (EntryReadable) new DrawableBitmap (x,y,new URL(urlName));
		}
		catch (Exception e) {
			return null;
		}		
	}	
	int width = r.readInt();
	int height = r.readInt();
	String presName = r.readString();
	int scale = r.readInt();
	scalable = (scale!=0);
	if (type == 1)
		try  {
			return (EntryReadable) new DrawableBitmap (x,y,width,height,new URL(urlName),presName,scalable);
		}
		catch (Exception e) {
			return null;
		}		
	minScale = r.readInt();
	maxScale = r.readInt();
	try  {
		return (EntryReadable) new DrawableBitmap (x,y,width,height,new URL(urlName),presName,scalable,minScale,maxScale);
	}
	catch (Exception e) {
		return null;
	}		
}
/**
 * Writes the bitmap; not yet implemented.
 * @param out entry writer
 * @param type type of the output
 */
protected void writeProtected (EntryWriter out, int type) {
	System.err.println("DrawableBitmap: write not implemented yet");
}
}
