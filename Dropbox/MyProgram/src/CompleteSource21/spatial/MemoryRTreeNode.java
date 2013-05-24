package spatial;

import java.awt.Color;
import java.awt.Graphics;
import java.util.BitSet;

/**
 * A memory-based R-tree node.
 * 
 * @version 2.20	21.08.2003	getNumOfDimensions, getTree, setEntry added, adapted to changed strategy interface
 * @version 2.10	01.03.2003	getEntry becomes getChildNode, getEntry added
 * @version 2.00	13.10.2002	Rectangle becomes MBR
 * @version 1.31	29.08.2001	inserting of subtrees modified
 * @version 1.30	25.06.2001	setMaximumCapacityAdded
 * @version 1.20	16.01.2001	setLock, resetLock added
 * @version 1.10	09.07.2000	updateObject added
 * @version 1.00	25.04.2000	first version
 * @author Thomas Brinkhoff
 */
public class MemoryRTreeNode implements SpatialSearchTreeNode {
	
	/**
	 * maximum number of entries per node.
	 */
 	protected static int MAXENTRYNUM = 16;
	/**
	 * the tree of the node.
	 */
 	protected MemoryRTree tree;
	/**
	 * minimum bounding box.
	 */
 	protected MBR mbr = null;
	/**
	 * the entries.
	 */
 	protected SpatialSearchTreeEntry[] entry = new SpatialSearchTreeEntry[MAXENTRYNUM+1];
	/**
	 * number of entries.
	 */
	protected int num = 0;
	/**
	 * leaf flag.
	 */
	protected boolean isLeaf = false;
	/**
	 * root flag.
	 */
	protected boolean isRoot = false;
	
	/**
	 * Class representing an entry - required for calling strategy.distribute
	 */
	protected class MemoryRTreeEntry implements SpatialSearchTreeEntry {
		protected MBR mbr;
		protected MemoryRTreeEntry (MBR mbr) {
			this.mbr = mbr;
		}
		public MBR getMBR () {
			return mbr;
		}
		public int getNumOfDimensions() {
			return mbr.numOfDimensions();
		}
	}
	
/**
 * Creates a new root which is also a leaf.
 * @param tree the memory r-tree
 */
protected MemoryRTreeNode (MemoryRTree tree) {
	mbr = new MBR(tree.dim);
	isLeaf = true;
	isRoot = true;
	this.tree = tree;
}
/**
 * Internal constructor for a new root.
 * The mbr is computed.
 * @param subtree1 first subtree
 * @param subtree2 second subtree
 */
protected MemoryRTreeNode (MemoryRTreeNode subtree1, MemoryRTreeNode subtree2) {
//System.out.println("new root: "+subtree1.getMBR().toString()+" + "+subtree2.getMBR().toString());
	entry[0] = subtree1;
	entry[1] = subtree2;
	num = 2;
	this.mbr = subtree1.getMBR().union(subtree2.getMBR());
	tree = subtree1.tree;
	tree.root = this;
}
/**
 * Internal constructor for new nodes which are not the root.
 * The mbr is unset.
 * @param ifLeaf is the new node a leaf?
 * @param tree the memory r-tree
 */
protected MemoryRTreeNode (boolean isLeaf, MemoryRTree tree) {
	mbr = new MBR(tree.dim);
	this.isLeaf = isLeaf;
	isRoot = false;
	this.tree = tree;
}
/**
 * Re-computes the mbr.
 */
protected void adaptMBR () {
	if (num > 0) {
		mbr.setBounds(entry[0].getMBR());
		for (int i=1; i<num; i++)
			mbr.add(entry[i].getMBR());
	}
}
/**
 * Returns the number of entries of the tree.
 * @return number of entries
 */
public int computeNumberOfEntries () {
	if (isLeaf)
		return num;
	int n = num;
	for (int i=0; i<num; i++)
		n += ((MemoryRTreeNode)entry[i]).computeNumberOfEntries();
	return n;
}
/**
 * Returns the number of nodes of the tree.
 * @return number of nodes
 */
public int computeNumberOfNodes () {
	if (isLeaf)
		return 1;
	int n = 1;
	for (int i=0; i<num; i++)
		n += ((MemoryRTreeNode)entry[i]).computeNumberOfNodes();
	return n;
}
/**
 * Debug print of the tree.
 * @return number of nodes
 * @param height height of the node
 * @param traverse traverse tree?
 */
protected void debugPrint (int height, boolean traverse) {
	//if (isLeaf())
	//	return;
	System.out.println("============ "+height);
	for (int i=0; i<num; i++) {
		if (entry[i].getMBR() == null)
			System.out.println("null");
		else
			System.out.println(entry[i].getMBR().toString());
		if (!isLeaf() && traverse)
			((MemoryRTreeNode)entry[i]).debugPrint(height+1,true);
	}
	System.out.println("============ "+(height+1));
}
/**
 * Draws the subtree.
 * @param actHeight actual height
 * @param g graphic context
 * @param scale scale
 * @param minHeight minimum height
 * @param maxHeight maximum height
 */
protected void draw (int actHeight, Graphics g, int scale, int minHeight, int maxHeight) {
	if (actHeight == 0)
		g.setColor(Color.black);
	else if (actHeight == 1)
		g.setColor(Color.blue);
	else if (actHeight == 2)
		g.setColor(Color.red);
	else
		g.setColor(Color.orange);
	if ((mbr != null) && (actHeight >= minHeight))		
		g.drawRect (mbr.min(MBR.X)/scale,mbr.min(MBR.Y)/scale, mbr.extension(MBR.WIDTH)/scale,mbr.extension(MBR.HEIGHT)/scale);
	if (!isLeaf && (actHeight < maxHeight))
		for (int i=0; i<num; i++) {
			MemoryRTreeNode subtree = (MemoryRTreeNode) entry[i];
			subtree.draw (actHeight+1,g,scale,minHeight,maxHeight);
		}
}
/**
 * Returns the child node of a non-leaf entry.
 * @return  the child node
 * @param index index of entry
 */
public SpatialSearchTreeNode getChildNode (int index) {
	if ((!isLeaf) && (index>=0) && (index<num))
		return (SpatialSearchTreeNode)entry[index];
	else
		return null;
}
/**
 * Returns an entry in a node.
 * @return  the entry
 * @param  index  index of the entry
 */
public SpatialSearchTreeEntry getEntry (int index) {
	if ((index>=0) && (index<num))
		return entry[index];
	else
		return null;
}
/**
 * Returns the minimum bounding box of an entry in a node.
 * @return rectangle
 * @param index index of entry
 */
public MBR getEntryMBR (int index) {
	if ((index>=0) && (index<num))
		return entry[index].getMBR();
	else
		return null;
}
/**
 * Returns the height of the tree.
 * @return height
 */
public int getHeight () {
	if (isLeaf)
		return 1;
	else
		return ((MemoryRTreeNode)entry[0]).getHeight()+1;
}
/**
 * Returns the maximum number of entries.
 * @return maximum number
 */
public int getMaxNumberOfEntries() {
	return MAXENTRYNUM;
}
/**
 * Returns the minimum bounding box.
 * @return minimum bounding box
 */
public MBR getMBR() {
	return mbr;
}
/**
 * Returns the number of spatial dimensions.
 * @return  the dimensions 
 */
public int getNumOfDimensions() {
	return mbr.numOfDimensions();
}
/**
 * Returns the number of entries in the node.
 * @return number of entries
 */
public int getNumberOfEntries () {
	return num;
}
/**
 * Returns the object of a leaf node.
 * @return object
 * @param index index of object
 */
public SpatialSearchTreeObject getObject (int index) {
	if (isLeaf && (index>=0) && (index<num))
		return (SpatialSearchTreeObject)entry[index];
	else
		return null;
}

/**
 * Returns the tree the node belongs to.
 * @return  the tree
 */
public SpatialSearchTree getTree () {
	return tree;
}

/**
 * Inserts the object into the r-tree.
 * @return new r-tree node
 * @param obj spatial searchtree object
 */
public MemoryRTreeNode insert (SpatialSearchTreeObject obj) {
	// adapt mbr and root
	if (num == 0)
		mbr.setBounds(obj.getMBR());
	else
		mbr.add(obj.getMBR());
	// insert object
	if (isLeaf) {
		if ((num < 0) || (num > MAXENTRYNUM))
			System.err.println ("num falsch (1) !!!"); 
		entry[num] = obj;
		num++;
		tree.totalnum++;
	}
	else {
		int index = tree.strategy.chooseSubtree (this,obj);
		MemoryRTreeNode subtree = (MemoryRTreeNode)entry[index];
		MemoryRTreeNode newEntry = subtree.insert (obj);
		if (newEntry != null) {
			if ((num < 0) || (num > MAXENTRYNUM))
				System.err.println ("num falsch (2) !!!"); 
			if (tree.strategy.isOrdering()) {
				for (int i=num-1; i>index; i--)
					entry[i+1] = entry[i];
				entry[index+1] = newEntry;
			}
			else
				entry[num] = newEntry;
			num++;
		}
	}
	if (num <= MAXENTRYNUM)
		if (isRoot)
			return this;
		else
			return null;
	// the node is too full => split
	MemoryRTreeNode newSubtree = split();
	if (isRoot) {
		MemoryRTreeNode newRoot = new MemoryRTreeNode (this,newSubtree);
		newRoot.isRoot = true;
		isRoot = false;
		return newRoot;
	}
	return newSubtree;		
}
/**
 * Inserts the object into the node without handling of any special cases.
 * @param obj new spatial searchtree object
 */
protected void insertSimple (SpatialSearchTreeEntry obj) {
	// adapt mbr
	if (num == 0)
		mbr.setBounds(obj.getMBR());
	else
		mbr.add(obj.getMBR());
	// insert object
	entry[num] = obj;
	num++;
}
/**
 * Returns whether the node is a leaf or not.
 * @return is leaf?
 */
public boolean isLeaf () {
	return isLeaf;
}
/**
 * Returns whether the node is a root node or not.
 * @return  true if it is a node
 */
public boolean isRoot() {
	return isRoot;
}
/**
 * Moves an object.
 * @param index the index of the object
 * @param new MBR the new MBR of the object
 */
public boolean move (int index, MBR newMBR) {
	if (isLeaf && (index>=0) && (index<num)) {
		if (mbr.contains(newMBR))
			return false;
		else {
			SpatialSearchTreeObject obj = (SpatialSearchTreeObject) entry[index];
			removeObject(index);
			((MovingSpatialSearchTreeObject)obj).setMBR(newMBR);
			tree.root.insert(obj);
			return true;
		}
	}
	else
		return false;
}
/**
 * Returns whether the node points to a leaf or not.
 * @return points to a leaf?
 */
public boolean pointsToLeaf () {
	if ((isLeaf) || (num == 0))
		return false;
	MemoryRTreeNode subtree = (MemoryRTreeNode)entry[0];
	return subtree.isLeaf();
}
/**
 * Removes the object with a given index.
 * @param index index of the object
 */
public void removeObject (int index) {
	if (isLeaf && (index>=0) && (index<num)) {
		num--;
		entry[index] = entry[num];
		entry[num] = null;
		tree.totalnum--;
		adaptMBR();
	}
}
/**
 * Pseudo operation.
 */
public void resetLock() {
}

/**
 * Sets the i-th entry of the node
 * @param  entry  the new entry
 * @param  i  the index
 */
public void setEntry (SpatialSearchTreeEntry entry, int i) {
	if ((i >= 0) && (i < num))
		this.entry[i] = entry;
}

/**
 * Pseudo operation.
 */
public void setLock() {
}
/**
 * Sets the maximum capacity of the nodes. Must be called before a node is created.
 * @param capacity the new capaciy
 */
public static void setMaximumCapacity (int capacity) {
	MAXENTRYNUM = capacity;
}
/**
 * Splits this node.
 * @return  the new node
 * @param  the parent entry
 */
protected MemoryRTreeNode split () {
	MemoryRTreeEntry parentEntry = new MemoryRTreeEntry(mbr);
	BitSet indicator = tree.strategy.distribute (this,parentEntry);
	// move objects
	MemoryRTreeNode newNode = new MemoryRTreeNode(isLeaf,tree);
	for (int i=0; i<num; i++)
		if (indicator.get(i))
			newNode.insertSimple (entry[i]);
	// adapt this node
	int oldNum = num;
	num = 0;
	for (int i=0; i<oldNum; i++)
		if (!indicator.get(i))
			this.insertSimple (entry[i]);
	if ((num <= 0) || (num > MAXENTRYNUM))
		System.err.println("split - Fehler: "+num+" + "+newNode.getNumberOfEntries());
	return newNode;
}
/**
 * Simulates an update of the object with a given index.
 * @param index index of the object
 */
public void updateObject (int index) {
}
}
