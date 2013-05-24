package spatial;

/**
 * Interface describing a spatial search tree node
 * 
 * @version 2.30	21.08.2003	getTree,setEntry added
 * @version 2.20	01.03.2003	getEntry renamed to getChildNode, getEntry added
 * @version 2.10	13.12.2002	getHeight added
 * @version 2.00	13.10.2002	Rectangle becomes MBR
 * @version 1.30	16.01.2001	setLock, resetLock added
 * @version 1.20	09.07.2000	updateObject added
 * @version 1.10	15.04.2000	getEntryMBR, pointsToLeaf, getMaxNumberOfEntries added; renamed
 * @version 1.00	28.01.2000	first version
 * @author Thomas Brinkhoff
 */
public interface SpatialSearchTreeNode extends SpatialSearchTreeEntry {

	/**
	 * Returns the child node of a non-leaf node (with locking).
	 * @return  the child node
	 * @param  index  index of the child
	 */
	SpatialSearchTreeNode getChildNode (int index);
	/**
	 * Returns the entry in a node.
	 * @return  the entry
	 * @param  index  index of the entry
	 */
	SpatialSearchTreeEntry getEntry (int index);
	/**
	 * Returns the minimum bounding box of an entry in a node.
	 * @return  the rectangle
	 * @param  index  index of the entry
	 */
	MBR getEntryMBR (int index);
	/**
	 * Returns the height of the block.
	 * @return  the height
	 */
	int getHeight ();
	/**
	 * Returns the maximum number of entries in a non-leaf node.
	 * @return  the maximum number of entries
	 */
	int getMaxNumberOfEntries();
	/**
	 * Returns the number of entries in the node.
	 * @return  the number of entries
	 */
	int getNumberOfEntries ();
	/**
	 * Returns the object of a leaf node.
	 * @return  the object
	 * @param  index  index of the object
	 */
	SpatialSearchTreeObject getObject (int index);
	/**
	 * Returns the tree the node belongs to.
	 * @return  the tree
	 */
	SpatialSearchTree getTree ();
	/**
	 * Returns whether the node is a leaf or not.
	 * @return  is it a leaf?
	 */
	boolean isLeaf ();
	/**
	 * Returns whether the node is a root node or not.
	 * @return  true if it is the root node
	 */
	boolean isRoot();
	/**
	 * Removes the object with a given index.
	 * @param  index  index of the object
	 */
	void removeObject (int index);
	/**
	 * Resets the lock.
	 */
	void resetLock();
	/**
	 * Sets the lock.
	 */
	void setLock();
	/**
	 * Sets the i-th entry of the node
	 * @param  entry  the new entry
	 * @param  i  the index
	 */
	void setEntry (SpatialSearchTreeEntry entry, int i);
	/**
	 * Simulates an update of the object with a given index.
	 * @param  index  index of the object
	 */
	void updateObject (int index);

}
