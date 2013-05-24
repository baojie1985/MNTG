package spatial;

import java.io.*;
import java.util.BitSet;

/**
 * Interface providing the operations which determine the strategy of an r-tree.
 * 
 * @version 2.20	22.08.2003	read- and write-methods, sortForReinsert added; parameters SpatialSearchTreeEntry instead of MBR
 * @version 2.10	18.01.2003	set- and get-methods added
 * @version 2.00	05.10.2002	Rectangle becomes MBR
 * @version 1.00	05.04.2000	first version
 * @author Thomas Brinkhoff
 */
public interface RTreeStrategy {
	
	/**
	 * Determines the best subtree for inserting an new object.
	 * @return  the index of the subtree
	 * @param  node  non-leaf node where the object should to be inserted
	 * @param  obj  the new object or the reinserted entry
	 */
	int chooseSubtree (SpatialSearchTreeNode node, SpatialSearchTreeEntry obj);
	/**
	 * Distributes the entries of a node to two nodes.
	 * @return  Bitset indicating the entries which should leave the node
	 * @param  node  the node
	 * @param  entry  the entry describing the node
	 */
	BitSet distribute (SpatialSearchTreeNode node, SpatialSearchTreeEntry entry);
	/**
	 * Gets the minimum percentage of entries.
	 * @return  the percentage
	 */
	int getMinimum ();
	/**
	 * Returns the percentage of objects which should be reinserted.
	 * @return  the reinsert factor (0 = no reinsert)
	 * @param  the height in the tree
	 */
	int getReinsertFactor (int height);
	/**
	 * Returns whether the strategy is ordering or not.
	 * @return  is ordering?
	 */
	boolean isOrdering();
	/**
	 * Reads parameters from DataInputStream.
	 * @param  in  stream
	 * @throws  IOException
	 */
	void readParameters (DataInputStream in) throws IOException;
	/**
	 * Sets the minimum percentage of entries.
	 * @param  m  the new value
	 */
	void setMinimum (int m);
	/**
	 * Sets the quota of entries to be reinserted.
	 * @param  p  the new value
	 */
	void setReinsertFactor (int p);
	/**
	 * Sorts the entries of the given node according to their distance to the center of the parent entry.
	 * The reinsert factor controls the number of exactly sorted entries.
	 * The object that should be reinserted are stored at the end of the node.
	 * The last entry should be reinserted first.
	 * @param  node  the node
	 */
	void sortForReinsert (SpatialSearchTreeNode node);
	/**
	 * Writes parameters to DataOutputStream.
	 * @param  out  stream
	 * @throws  IOException
	 */
	void writeParameters (DataOutputStream out) throws IOException;

}
