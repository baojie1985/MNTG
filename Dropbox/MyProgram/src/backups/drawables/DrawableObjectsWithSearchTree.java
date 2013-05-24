package drawables;

import java.awt.*;
import spatial.*;

/**
 * Container class for drawable objects.
 *
 * @version 2.01	31.10.2002	not-implemented funtionality added
 * @version 2.00	05.10.2002	adapted to package spatial v2.0
 * @version 1.00	08.06.2000	separation from the class "DrawableObjects"
 * @author Thomas Brinkhoff
 */
public class DrawableObjectsWithSearchTree extends DrawableObjects {

	/**
	 * The spatial search tree.
	 */
	private SpatialSearchTree tree = null;
	/**
	 * Prepared point query.
	 */
	private RegionQuery pQuery = null;
	/**
	 * Prepared window query.
	 */
	private RegionQuery wQuery = null;
	/**
	 * Prepared nearest neighbor query.
	 */
	private NearestNeighborQuery nnQuery = null;
	
/**
 * Constructor.
 * @param numOfLayers number of layers
 * @param tree spatial searchtree used (may be null)
 */
public DrawableObjectsWithSearchTree (int numOfLayers, SpatialSearchTree tree) {
	super(numOfLayers);
	this.tree = tree;
	if (tree != null) {
		nnQuery = new NearestNeighborQuery();
	}
}
/**
 * Add a drawable into the container.
 * @param prim the drawable
 */
public void addDrawable (Drawable prim) {
	super.addDrawable(prim);
	if (tree != null)
		tree.insert (new DrawableSpatialSearchTreeObject(prim));
}
/**
 * Zeichnet alle Drawable-Objekte im angegebenen Graphic Context, vorausgesetzt der aktuelle Maßstab
 * wird vom Drawable-Objekt überschritten und es liegt im übergebenen Rechteck.
 * Es werden nur die Objekte des angegebenen Layers gezeichnet.
 * @param  l  Layer
 * @param  g  aktueller Graphic Context
 * @param  r  Clipping-Rechteck
 * @param  scale  aktueller Maßstab
 */
public void drawAllObjectsOfLayer (int l, Graphics g, Rectangle r, int scale) {
	if (l >= numOfLayers)
		return;
	if ((l == Drawable.AREALAYER) || (tree == null))
		super.drawAllObjectsOfLayer(l,g,r,scale);
	else {
		wQuery = new RegionQuery(new LayerScaleWindowQuery(l,scale));
		wQuery.initWithRect (tree,new MBR(r));
		DrawableSpatialSearchTreeObject obj = (DrawableSpatialSearchTreeObject)wQuery.getNextObject();
		while (obj != null) {
			Drawable prim = obj.getDrawable();
			prim.draw (g,r,scale);
			obj = (DrawableSpatialSearchTreeObject)wQuery.getNextObject();
		}
	}
}
/**
 * Finds the nearest drawable to a given position.
 * @return  the nearest drawable
 * @param  x  x-coordinate of the position
 * @param  y  y-coordinate of the position
 * @param  prim  the excluded drawable (may be null)
 * @param typeObject  an object specifying the type that is searched (may be null)
 */
public Drawable findNearestDrawable (int x, int y, Drawable prim, SpatialSearchTreeObject typeObject) {
	if (tree != null) {
		int point[] = new int[2];
		point[MBR.X] = x;
		point[MBR.Y] = y;
		nnQuery.initWithPoint (tree,point,NearestNeighborQuery.ALLSCALES,NearestNeighborQuery.ALLLAYERS,typeObject,NearestNeighborQuery.QUERY,prim==null?null:new DrawableSpatialSearchTreeObject(prim));
		DrawableSpatialSearchTreeObject obj = (DrawableSpatialSearchTreeObject)nnQuery.getNearestObject();
		if (obj == null)
			return null;
		else
			return obj.getDrawable();
	}
	else
		return super.findNearestDrawable(x,y);
}
/**
 * Returns the data space of all objects.
 * @return mbr of the data space
 */
public Rectangle getDataspace() {
	if (tree != null)
		return tree.getRoot().getMBR().extractRectangle(MBR.X,MBR.Y);
	else
		return super.getDataspace();
}
/**
 * Returns the next drawable primitive.
 * @return the primitive (or null)
 * @param prevPrim previous drawable; null => look for first drawable
 */
public Drawable getNextDrawable (Drawable prevPrim) {
	// Fall 1: ohne Suchbaum
	if (tree == null)
		return super.getNextDrawable (prevPrim);
	// Fall 2: mit Suchbaum
	else {
		DrawableSpatialSearchTreeObject obj = (DrawableSpatialSearchTreeObject)pQuery.getNextObject();
		if (obj == null)
			return null;
		else
			return obj.getDrawable();
	}
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
	if (tree != null) {
		int[] point = new int[2];
		point[MBR.X] = px;
		point[MBR.Y] = py;
		SelectQuery sQuery = new SelectQuery(scale);
		sQuery.setTolerance(2*scale);
		pQuery = new RegionQuery(sQuery);
		pQuery.initWithPoint (tree,point);
	}
	visibleDrawable = getNextDrawable(visibleDrawable);
	boolean firstSearch = true;
	while (true) {
		while (visibleDrawable == null)
			if (firstSearch) {
				if (tree != null)
					pQuery.reset ();
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
 * Returns the number of all objects.
 * @return number of all objects
 */
public int getNumberOfObjects () {
	if ((tree != null) && tree.getClass().getName().equals("spatial.MemoryRTree"))
		return ((MemoryRTree)tree).getTotalNumberOfObjects();
	else
		return super.getNumberOfObjects();
}
/**
 * Löscht das Drawable-Objekt aus dem Container und löst die Beziehung
 * zum Grafik-Primitiv auf.
 * @param obj zu löschendes Drawable-Objekt
 */
public void remove (DrawableObject obj) {
	if (obj == null)
		return;
	super.remove(obj);
	if (tree != null)
		for (int i=0; i<obj.getNumberOfDrawables(); i++)
			tree.remove(new DrawableSpatialSearchTreeObject(obj.getDrawable(i)));
}
/**
 * Removes all objects from the container.
 */
public void removeAll () {
	super.removeAll();
	if (tree != null)
		tree = new MemoryRTree();
}
/**
 * Removes all objects of a given layer from the container.
 * @param  l  the layer
 */
public void removeAllObjectsOfLayer (int l) {
	super.removeAllObjectsOfLayer(l);
	if (tree != null) {
		RegionQuery rlQuery = new RegionQuery (new LayerAllQuery(l));
		rlQuery.init (tree,RegionQuery.REMOVEALL);
		rlQuery.getNextObject();
	}
}
}
