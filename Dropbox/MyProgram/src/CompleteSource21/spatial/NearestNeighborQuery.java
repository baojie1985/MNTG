package spatial;

import index.*;

/**
 * Nearest neighbor query for search trees
 * 
 * @version 2.02	16.08.2003  type replaced by typeObject
 * @version 2.01	01.03.2003  adapted to modified SpatialSearchTreeNode and ST_RegionQuery v3.0
 * @version 2.00	05.10.2002	Rectangle becomes MBR
 * @version 1.01	28.06.2000	changed imports
 * @version 1.10	12.04.2000	extension of Query
 * @version 1.01	26.03.2000	getRoot, SpatialSearchTreeNode used
 * @version 1.00	31.01.2000	first version
 * @author Thomas Brinkhoff
 */
public class NearestNeighborQuery extends Query {
	
	/**
	 * all layers.
	 */
	public static final int ALLLAYERS = -1;
	/**
	 * all scales.
	 */
	public static final int ALLSCALES = -1;
	/**
	 * normal query.
	 */
	public static final int QUERY = 0;

	/**
	 * coordinates of query point.
	 */
	protected int[] point;
	/**
	 * tolerance.
	 */
	protected double tolerance = 0;
	/**
	 * scale, required for testing the visibility
	 */
	protected int scale = 1;
	/**
	 * layer.
	 */
	protected int layer = ALLLAYERS;
	/**
	 * remove mode.
	 */
	protected int operation = QUERY;
	/**
	 * new rectangle for move.
	 */
	protected SpatialSearchTreeObject unequal = null;
	/**
	 * object for type selection.
	 */
	protected SpatialSearchTreeObject typeObject = null;

	/**
	 * nearest object until now.
	 */
	protected SpatialSearchTreeObject bestObject = null;
	/**
	 * the distance of the nearest object until now.
	 */
	protected double bestDist;
	
	/**
	 * maximum allowed height of the subtree.
	 */
	protected static final int MAXHEIGHT = 40;
	/**
	 * the actual nodes from the different heights of the tree.
	 */
	protected SpatialSearchTreeNode node[] = new SpatialSearchTreeNode[MAXHEIGHT];
	/**
	 * the actual serach indices for the different heights of the tree.
	 */
	protected int index[] = new int[MAXHEIGHT];
	/**
	 * length of the path.
	 */
	protected int pathLength = 0;

/**
 * Nearest Neighbor query constructor.
 */
public NearestNeighborQuery () {
	node[0] = null;
}
/**
 * Returns the next object fulfilling the query condition.
 * Will return null if no object is found.
 * @return the found object
 */
public SpatialSearchTreeObject getNearestObject () {
	if ((node[0] != null) && (node[0].getNumberOfEntries() > 0)) {
		timer.start();
		// 1st phase: window query with an increasing tolerance until an object is found
		if (unequal == null)
			tolerance = 100;
		else {
			tolerance = unequal.getMBR().extension(0);
			for (int i=1; i<unequal.getMBR().numOfDimensions(); i++)
				tolerance = Math.max(tolerance,unequal.getMBR().extension(i));
		}
		bestObject = null;
		while (bestObject == null) {
			getNextObject(0);
			if (bestObject == null) {
				tolerance += tolerance;
				reset();
			}
		}
		// distance of best object <= tolerance => finish
		if (bestDist <= tolerance) {
			timer.stop();
			resultNum++;
			return bestObject;
		}
		// otherwise: again with tolerance <= distance of best object
		tolerance = bestDist;
		reset();
		getNextObject(0);
		timer.stop();
		if (bestObject == null)
			System.err.println("NearestNeighborQuery - Error: bestObject null in second phase");
		else
			resultNum++;
		return bestObject;
	}
	else
		return null;
}
/**
 * Returns (or removes or moves) the next object(s) fulfilling the query condition.
 * Will return null if no object is found.
 * @return the found object
 * @param actHeight actual height
 */
protected void getNextObject (int actHeight) {
	// enlarge path
	if (actHeight == pathLength) {
		 node[pathLength] = node[pathLength-1].getChildNode(index[pathLength-1]);
		 index[pathLength] = 0;
		 pathLength++;
	}
	int num = node[actHeight].getNumberOfEntries();
	// case 1: actHeight = leaf
	if (node[actHeight].isLeaf())
		for (int i=index[actHeight]; i<num; i++) {
			SpatialSearchTreeObject obj = (SpatialSearchTreeObject) node[actHeight].getObject(i);
			if ((unequal != obj) &&
			   ((typeObject == null) || (obj.isOfSameType(typeObject)))) {
					double objDist = obj.computeDistanceTo (point);
					if ((bestObject == null) || (objDist < bestDist)) {
						bestObject = obj;
						bestDist = objDist;
					}
			}
		}
	// case 2: actHeight = non-leaf
	else
		for (int i=index[actHeight]; i<num; i++)
			if ((actHeight+1 < pathLength) || node[actHeight].getEntryMBR(i).contains(point,(int)Math.ceil(tolerance))) {
				index[actHeight] = i;
				getNextObject (actHeight+1);
			}
	// shorten path
	pathLength--;
	if (! node[pathLength].isRoot())
		node[pathLength].resetLock();
}
/**
 * Initializes a new query point.
 * @param  tree  the searchtree
 * @param  point  coordinates of the query point
 */
public void initWithPoint (SpatialSearchTree tree, int[] point) {
	initWithPoint (tree,point,ALLSCALES,0,ALLLAYERS,null);
}
/**
 * Initializes a new query point.
 * @param  tree  the searchtree
 * @param  point  coordinates of the query point
 * @param  scale  scale
 * @param  tolerance  tolerance
 * @param  layer  layer
 * @param  type  type
 */
public void initWithPoint (SpatialSearchTree tree, int[] point, int scale, int tolerance, int layer, SpatialSearchTreeObject typeObject) {
	initWithPoint (tree,point,scale,layer,typeObject,QUERY,null);
}
/**
 * Initializes a new query point.
 * @param  tree  the searchtree
 * @param  point  coordinates of the query point
 * @param  scale  scale
 * @param  tolerance  tolerance
 * @param  layer  layer
 * @param  type  type
 * @param  operation  operation
 * @param  unequal  unequal object
 */
public void initWithPoint (SpatialSearchTree tree, int[] point, int scale, int layer, SpatialSearchTreeObject typeObject, int operation, SpatialSearchTreeObject unequal) {
	this.point = point;
	this.scale = scale;
	this.layer = layer;
	this.typeObject = typeObject;
	this.operation = operation;
	this.unequal = unequal;
	node[0] = tree.getRoot();
	queryNum++;
	reset();
}
/**
 * Resets the query.
 */
protected void reset () {
	index[0] = 0;
	pathLength = 1;
	bestObject = null;
}
}
