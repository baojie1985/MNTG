package spatial;

/**
 * Interface describing an object in a spatial search tree
 * 
 * @version 2.00	05.10.2002	Rectangle becomes MBR
 * @version 1.00	27.01.2000	first version
 * @author Thomas Brinkhoff
 */
 
public interface SpatialSearchTreeEntry {

	/**
	 * Returns the minimum bounding rectangle.
	 * @return  minimum bounding rectangle
	 */
	MBR getMBR ();
	
	/**
	 * Returns the number of dimensions.
	 * @return  the dimensions 
	 */
	int getNumOfDimensions();

}
