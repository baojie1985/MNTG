package spatial;

/**
 * Interface describing a spatial search tree.
 * 
 * @version 3.00	18.01.2003	several methods removed
 * @version 2.00	09.10.2002	Rectangle becomes MBR, getNumOfDimensions added
 * @version 1.00	25.04.2000	first version
 * @author Thomas Brinkhoff
 */
public interface SpatialSearchTree {
/**
 * Closes the tree.
 */
void close();
/**
 * Returns the height of the tree.
 * @return height
 */
public int getHeight ();
/**
 * Returns the number of dimensions.
 * @return  the number of dimensions
 */
public int getNumOfDimensions();
/**
 * Returns the root. Must be used as starting point of queries. 
 * @return root
 */
SpatialSearchTreeNode getRoot ();
/**
 * Inserts the object into the tree.
 * @param obj spatial searchtree object
 */
void insert (SpatialSearchTreeObject obj);
/**
 * Removes an object from the tree.
 * @return was an object removed?
 * @param obj the object
 */
boolean remove (SpatialSearchTreeObject obj);
}
