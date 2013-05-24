package spatial;

import java.awt.*;

/**
 * A memory-based R-tree.
 * 
 * @version 3.01	01.03.2003	adpated to ST_RegionQuery v3.0
 * @version 3.00	09.10.2002	Rectangle becomes MBR
 * @version 2.20	01.04.2002	setSplitStrategy added
 * @version 2.10	25.06.2001	a constructor added
 * @version 2.00	25.04.2000	separated into tree and node
 * @version 1.10	14.04.2000	getRoot, getEntryMBR, pointsToLeaf, getMaxNumberOfEntries added; RTreeStrategy used; separation of SpatialSearchTree and SpatialSearchTreeNode
 * @version 1.00	29.01.2000	first version
 * @author Thomas Brinkhoff
 */
public class MemoryRTree implements SpatialSearchTree {
	
	/**
	 * number of dimensions.
	 */
	protected int dim = 2;
	/**
	 * strategy used by the memory r-tree.
	 */
 	protected RTreeStrategy strategy = new SimpleRTreeStrategy();
	/**
	 * the root node.
	 */
	protected MemoryRTreeNode root = null;
	/**
	 * number of objects.
	 */
	protected int totalnum = 0;
	/**
	 * query to remove objects.
	 */
	protected RegionQuery removeQuery;
	/**
	 * query to move objects.
	 */
	protected RegionQuery moveQuery;

/**
 * Constructor.
 */
public MemoryRTree () {
	removeQuery = new RegionQuery(new IsContainedQuery());
	moveQuery = new RegionQuery(new IsContainedQuery());
	root = new MemoryRTreeNode(this);
}
/**
 * Constructor.
 * @param  dim  number of dimensions
 * @param  nodecapacity  capacity of the nodes
 */
public MemoryRTree (int dim, int nodecapacity) {
	this.dim = dim;
	MemoryRTreeNode.setMaximumCapacity(nodecapacity);
	removeQuery = new RegionQuery(new IsContainedQuery());
	moveQuery = new RegionQuery(new IsContainedQuery());
	root = new MemoryRTreeNode(this);
}
/**
 * Dummy operation.
 */
public void close() {
}
/**
 * Returns the number of entries of the tree.
 * @return number of entries
 */
public int computeNumberOfEntries () {
	return root.computeNumberOfEntries();
}
/**
 * Returns the number of nodes of the tree.
 * @return number of nodes
 */
public int computeNumberOfNodes () {
	return root.computeNumberOfNodes();
}
/**
 * Debug print of the tree.
 */
public void debugPrint () {
	root.debugPrint(1,true);
}
/**
 * Draws the tree.
 * @param g graphic context
 * @param clip clipping rectangle
 * @param scale scale
 * @param minHeight minimum height (root = 0)
 * @param maxHeight maximum height
 */
public void draw (Graphics g, Rectangle clip, int scale, int minHeight, int maxHeight) {
	root.draw (0, g,scale, minHeight,maxHeight);
	System.out.println("Info: "+getHeight()+"/"+computeNumberOfNodes()+"/"+
		computeNumberOfEntries()+"/"+totalnum);
}
/**
 * Returns the height of the tree.
 * @return height
 */
public int getHeight () {
	return root.getHeight();
}
/**
 * Returns the number of dimensions.
 * @return  the number of dimensions
 */
public int getNumOfDimensions() {
	return dim;
}
/**
 * Returns the root.
 * @return the root
 */
public SpatialSearchTreeNode getRoot () {
	return root;
}
/**
 * Returns the total number of entries in the tree.
 * @return total number of entries
 */
public int getTotalNumberOfObjects () {
	return totalnum;
}
/**
 * Inserts the object into the r-tree.
 * @return new r-tree node
 * @param obj spatial searchtree object
 */
public void insert (SpatialSearchTreeObject obj) {
	root = root.insert(obj);
}
/**
 * Removes an object.
 * @return was an object removed?
 * @param obj the object
 */
public boolean remove (SpatialSearchTreeObject obj) {
	if (obj == null)
		return false;
	removeQuery.initWithObject (this,obj,RegionQuery.REMOVEEXACT);
	SpatialSearchTreeObject found = removeQuery.getNextObject();
	return (found != null);
}
/**
 * Sets the split strategy.
 * @param  str  the new split strategy
 */
public void setSplitStrategy (RTreeStrategy str) {
	strategy = str;
}
}
