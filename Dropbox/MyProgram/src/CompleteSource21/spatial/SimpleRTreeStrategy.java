package spatial;

import java.io.*;
import java.util.BitSet;

/**
 * Class providing the operations which determine the strategy of a simple r-tree.
 * 
 * @version 2.20	22.08.2003	read- and write-methods, sortAccordingTo added, adapted to new interface
 * @version 2.10	18.01.2003	set- and get-methods added
 * @version 2.00	06.10.2002	Rectangle becomes MBR; algorithm works n-dimensional
 * @version 1.10	29.00.2002	isOrdering added
 * @version 1.00	05.04.2000	first version
 * @author Thomas Brinkhoff
 */
public class SimpleRTreeStrategy implements RTreeStrategy {

	/**
	 * Prepared indicator for the distribute operation.
	 */
 	protected int reinsertFactor = 0;
	/**
	 * The minimum number of entries in a node after a split in %.
	 */
 	protected int m = 40;
	/**
	 * Prepared indicator for the distribute operation.
	 */
 	protected BitSet indicator = new BitSet(128);

 
/**
 * Constructor.
 */
public SimpleRTreeStrategy () {
	this(0);
}
/**
 * Constructor.
 * @param  reinsertFactor  percentage of objects which should be reinserted (0 = no reinsert)
 */
public SimpleRTreeStrategy (int reinsertFactor) {
	this.reinsertFactor = reinsertFactor;
}

/**
 * Determines the best subtree for inserting an new object according to
 * the Gutman's original r-tree algorithm 1984.
 * @return  the index of the subtree
 * @param  node  non-leaf node where the object should to be inserted
 * @param  obj  the new object or the reinserted entry
 */
public int chooseSubtree (SpatialSearchTreeNode node, SpatialSearchTreeEntry obj) {
	MBR objRect = obj.getMBR();
	int num = node.getNumberOfEntries();
	// determine index with minimum area cost
	int bestIndex = 0;
	MBR entryRect = node.getEntryMBR(bestIndex);
	double bestEntryArea = entryRect.computeVolume();
	double bestArea = entryRect.union(objRect).computeVolume() - bestEntryArea;
	for (int i=1; i<num; i++) {
		entryRect = node.getEntryMBR(i);
		double entryArea = entryRect.computeVolume();
		double area = entryRect.union(objRect).computeVolume() - entryArea;
		if ((area < bestArea) || ((area == bestArea) && (entryArea < bestEntryArea))) {
			bestArea = area;
			bestEntryArea = entryArea;
			bestIndex = i;
		}
	}
	return bestIndex;
}

/**
 * Distributes the entries of a node to two nodes according to a simple linear algorithm.
 * @return  Bitset indicating the entries which should leave the node
 * @param  node  the node
 * @param  entry  the entry describing the node
 */
public BitSet distribute (SpatialSearchTreeNode node, SpatialSearchTreeEntry entry) {
	MBR mbr = entry.getMBR();
	MBR actRect = node.getEntryMBR(0);
	int num = node.getNumberOfEntries();
	int distNum = 0;
	// compute maximum extension
	int axis = MBR.X;
	int ext = mbr.extension(MBR.X);
	for (int i=1; i<mbr.numOfDimensions(); i++)
		if (mbr.extension(i) > ext) {
			axis = i;
			ext = mbr.extension(i);
		}
	// find "median"
	long c = actRect.center(axis);
	for (int i=1; i<num; i++) {
		actRect = node.getEntryMBR(i);
		c += actRect.center(axis);
	}
	int median = (int)(c / num);
	// determine moving entries
	for (int i=0; i<num; i++) {
		actRect = node.getEntryMBR(i);
		if (actRect.center(axis) > median) {
			indicator.set(i);
			distNum++;
		}
		else
			indicator.clear(i);
	}		
	// distribution failed => random distribution
	if ((distNum == 0) || (distNum == num)) {
		//System.out.println("Random split");
		for (int i=0; i<num; i++)
			if (i >= num/2)
				indicator.set(i);
			else
				indicator.clear(i);
	}
	return indicator;
}

/**
 * Gets the minimum percentage of entries.
 * @return  the percentage
 */
public int getMinimum () {
	return m;
}

/**
 * Returns the percentage of objects which should be reinserted.
 * @return  the reinsert factor (0 = no reinsert)
 * @param  the height in the tree
 */
public int getReinsertFactor (int height) {
	return reinsertFactor;
}

/**
 * Return false because this it not an ordering strategy.
 * @return  false
 */
public boolean isOrdering() {
	return false;
}

/**
 * Reads parameters from DataInputStream.
 * @param  in  stream
 * @throws  IOException
 */
public void readParameters (DataInputStream in) throws IOException {
	m = in.readInt();
	reinsertFactor = in.readInt();
}

/**
 * Sets the minimum percentage of entries.
 * @param  m  the new value
 */
public void setMinimum (int m) {
	this.m = m;
}

/**
 * Sets the quota of entries to be reinserted.
 * @param  p  the new value
 */
public void setReinsertFactor (int p) {
	reinsertFactor = p;
}

/**
 * Performs the sorting of a node between two limits using quicksort and a given array with values.
 * @param  nd  the node
 * @param  value  the array with the values
 * @param  left  left index
 * @param  right  right index
 */
public void sortAccordingTo (SpatialSearchTreeNode nd, double[] value, int left, int right) {
	sortAccordingTo (nd,value,0,left,right);
}

/**
 * Performs the sorting of a node between two limits using quicksort and a given array with values.
 * @param  nd  the node
 * @param  value  the array with the values
 * @param  leftBorder  the index of the first entry to be sorted
 * @param  left  left index
 * @param  right  right index
 */
public void sortAccordingTo (SpatialSearchTreeNode node, double[] value, int leftBorder, int left, int right) {
	if ((right > left) && (right >= leftBorder)) {
		double x1 = value[left];
		double x2 = value[(left+right)/2];
		double x3 = value[right];
		double x = x3;
		if (((x1 <= x2) && (x2 <= x3)) || ((x1 >= x2) && (x2 >= x3)))
			x = x2;
		else if (((x2 <= x1) && (x1 <= x3)) || ((x2 >= x1) && (x1 >= x3)))
			x = x1;
		int i = left;
		int j = right;
		do {
			while (value[i] < x)
				i++;
			while (value[j] > x)
				j--;
			if (i <= j) {
				SpatialSearchTreeEntry he = node.getEntry(j);  double hv = value[j];
				node.setEntry(node.getEntry(i),j);             value[j] = value[i];
				node.setEntry(he,i);                           value[i] = hv;
				i++;
				j--;
			}
		} while (i <= j);
		sortAccordingTo (node,value,left,j);
		sortAccordingTo (node,value,i,right);
	}
}

/**
 * Sorts the entries of the given node according to their distance to the center of the parent entry.
 * The reinsert factor controls the number of exactly sorted entries.
 * The object that should be reinserted are stored at the end of the node.
 * The last entry should be reinserted first.
 * @param  node  the node
 */
public void sortForReinsert (SpatialSearchTreeNode node) {
//System.out.println("reinsert: "+parentEntry.getMBR().x+", "+parentEntry.getMBR().y+", "+parentEntry.getMBR().width+", "+parentEntry.getMBR().height+":");
	int num = node.getNumberOfEntries();
	int distNum = num*getReinsertFactor(node.getHeight()) / 100;
	int m[] = node.getMBR().getCenterCoords();
	double dist[] = new double[num];
	for (int i=0; i<num; i++)
		dist[i] = node.getEntry(i).getMBR().quadDistanceFromCenter(m);
	sortAccordingTo(node,dist,num-distNum,0,num-1);
	// change direction of sorting for "close reinsert"
	int i1 = num-distNum;
	int i2 = num-1;
	while (i1 < i2) {
		SpatialSearchTreeEntry h = node.getEntry(i1);
		node.setEntry(node.getEntry(i2),i1);
		node.setEntry(h,i2);
		i1++;
		i2--;
	}
}

/**
 * Writes parameters to DataOutputStream.
 * @param  out  stream
 * @throws  IOException
 */
public void writeParameters (DataOutputStream out) throws IOException {
	out.writeInt(m);
	out.writeInt(reinsertFactor);
}

}
