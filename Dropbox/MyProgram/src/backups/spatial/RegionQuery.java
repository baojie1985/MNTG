package spatial;

import index.*;

/**
 * A general region query for search trees
 * 
 * @version 3.00	01.03.2003  scale, layer, type, tolerence removed, queryObj added, implements new interface
 * @version 2.01	19.02.2003  adapted to modified SpatialSearchTreeNode
 * @version 2.00	13.10.2002	Rectangle becomes MBR
 * @version 1.30	31.03.2002	getQuery added
 * @version 1.20	16.01.2001	locking of nodes, resetLock added
 * @version 1.11	09.07.2000	changed imports, support of simulated updates
 * @version 1.10	25.04.2000	extension of Query, init corrected
 * @version 1.02	25.03.2000	getRoot, SpatialSearchTreeNode used
 * @version 1.01	01.03.2000	remove corrected
 * @version 1.00	31.01.2000	first version
 * @author Thomas Brinkhoff
 */
public class RegionQuery extends Query implements SpatialSearchTreeQuery {
	
	/**
	 * The current query.
	 */
	protected SpatialQuery query = null;
	/**
	 * The current query object, may be null.
	 */
	protected SpatialSearchTreeObject queryObj;
	/**
	 * The current query rectangle.
	 */
	protected MBR rect;
	/**
	 * The query mode.
	 */
	protected int operation = QUERY;

	/**
	 * The maximum height of the tree.
	 */
	protected static final int MAXHEIGHT = 40;
	/**
	 * The current path: nodes from different heights of the tree.
	 */
	protected SpatialSearchTreeNode node[] = new SpatialSearchTreeNode[MAXHEIGHT];
	/**
	 * The current path: search indices for the different heights of the tree.
	 */
	protected int index[] = new int[MAXHEIGHT];
	/**
	 * The length of the current path.
	 */
	protected int pathLength = 0;

/**
 * Region query constructor.
 * @param query definition of the query
 */
public RegionQuery (SpatialQuery query) {
	node[0] = null;
	this.query = query;
}
/**
 * Returns the next object fulfilling the query condition.
 * Will return null if no object is found.
 * @return the found object
 */
public SpatialSearchTreeObject getNextObject () {
	if (node[0] != null) {
		timer.start();
		SpatialSearchTreeObject obj = getNextObject(0);
		if (obj == null)
			reset();
		else
			resultNum++;
		timer.stop();
		return obj;
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
protected SpatialSearchTreeObject getNextObject (int actHeight) {
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
		  SpatialSearchTreeEntry entry = node[actHeight].getEntry(i);
		  if (query.conditionFulfilled(rect,entry)) {
			SpatialSearchTreeObject obj = (SpatialSearchTreeObject) node[actHeight].getObject(i);
			if (query.conditionFulfilled(rect,obj)) {
				if (operation == QUERY) {
					index[actHeight] = i+1;
					return obj;
				}
				else if (operation == REMOVEALL) {
					node[actHeight].removeObject(i);
					i--;
					num--;
				}
				else if (operation == REMOVEEXACT) {
					if (query.areEqual(queryObj,obj)) {
						node[actHeight].removeObject(i);
						index[actHeight] = i;
						return obj;
					}
				}
				else if (operation == UPDATEALL) {
					node[actHeight].updateObject(i);
				}
				else if (operation == UPDATEEXACT) {
					if (query.areEqual(queryObj,obj)) {
						node[actHeight].updateObject(i);
						return obj;
					}
				}
			}
		  }
		}
	// case 2: actHeight = non-leaf
	else
		for (int i=index[actHeight]; i<num; i++)
			if ((actHeight+1 < pathLength) || query.conditionFulfilled(rect,node[actHeight].getEntryMBR(i))) {
				index[actHeight] = i;
				SpatialSearchTreeObject res = getNextObject (actHeight+1);
				if (res != null)
					return res;
			}
	// shorten path
	pathLength--;
	if (! node[pathLength].isRoot())
		node[pathLength].resetLock();
	return null;
}
/**
 * Return the spatial query.
 * @return  the spatial query
 */
public SpatialQuery getQuery() {
	return query;
}
/**
 * Initializes a query without spatial conditions.
 * @param  tree  the searchtree
 */
public void init (SpatialSearchTree tree) {
	init (tree,QUERY);
}
/**
 * Initializes a query without spatial conditions.
 * @param  tree  the searchtree
 * @param  operation  the operation
 */
public void init (SpatialSearchTree tree, int operation) {
	initWithRect (tree,new MBR(2),operation);
}
/**
 * Initializes a new query.
 * @param  tree  the searchtree
 * @param  point  coordinates of the query point
 */
public void initWithPoint (SpatialSearchTree tree, int[] point) {
	initWithPoint (tree,point,QUERY);
}
/**
 * Initializes a new query.
 * @param  tree  the searchtree
 * @param  point  coordinates of the query point
 * @param  operation  the query operation
 */
public void initWithPoint (SpatialSearchTree tree, int[] point, int operation) {
	MBR r = new MBR(point.length);
	r.setLocation (point);
	initWithRect (tree,r,operation);
}
/**
 * Initializes a new query.
 * @param  tree  the searchtree
 * @param  window  the query window
 */
public void initWithRect (SpatialSearchTree tree, MBR window) {
	initWithRect (tree,window,QUERY);
}
/**
 * Initializes a new query.
 * @param  tree  the searchtree
 * @param  window  the query window
 * @param  operation  the query operation
 */
public void initWithRect (SpatialSearchTree tree, MBR window, int operation) {
	if (! window.equals(rect) || (operation != this.operation) || (tree.getRoot() != node[0])) {
		rect = window;
		this.operation = operation;
		node[0] = tree.getRoot();
		queryNum++;
		reset();
	}
}
/**
 * Initializes a new query.
 * @param  tree  the searchtree
 * @param  queryObj  the query object
 */
public void initWithObject (SpatialSearchTree tree, SpatialSearchTreeObject queryObj) {
	initWithObject (tree,queryObj,QUERY);
}
/**
 * Initializes a new query.
 * @param  tree  the searchtree
 * @param  queryObj  the query object
 * @param  operation  the query operation
 */
public void initWithObject (SpatialSearchTree tree, SpatialSearchTreeObject queryObj, int operation) {
	if (queryObj != null) {
		this.queryObj = queryObj;
		initWithRect (tree,queryObj.getMBR(),operation);
	}
}
/**
 * Resets the query.
 */
public void reset () {
	resetLocks();
	index[0] = 0;
	pathLength = 1;
}
/**
 * Resets all locks except of the root node.
 */
public void resetLocks() {
	int i=1;
	while ((i<MAXHEIGHT) && (node[i]!=null)) {
		if (! node[i].isRoot())
			node[i].resetLock();
		i++;
	}
}
}
