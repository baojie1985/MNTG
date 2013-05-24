package spatial;

/**
 * Interface defining the operations required for spatial queries performed by a spatial search tree.
 * 
 * @version 4.00	05.10.2002	types are changed, setTolerance, setScale are removed
 * @version 3.00	05.10.2002	Rectangle becomes MBR
 * @version 2.00	09.04.2000	changed to interface
 * @version 1.10	25.03.2000	conditionedFulfilled for entries changed to rectangles
 * @version 1.00	29.01.2000	first version
 * @author Thomas Brinkhoff
 */
public interface SpatialQuery {

	/**
	 * Tests whether the query object and the stored object are equal.
	 * @return  true if equal
	 * @param  queryObj   the query object
	 * @param  storedObj  the stored object
	 */
	boolean areEqual (SpatialSearchTreeObject queryObj, SpatialSearchTreeObject storedObj);
	/**
	 * Tests whether the query rectangle and the MBR of a non-leaf entry fulfill the query condition.
	 * @return  true if the condition is fulfilled
	 * @param  rect      the query rectangle
	 * @param  entryMBR  the minimum bounding rectangle of non-leaf entry
	 */
	boolean conditionFulfilled (MBR rect, MBR entryMBR);
	/**
	 * Tests whether the query rectangle and an leaf entry fulfill the query condition.
	 * @return  true if the condition is fulfilled
	 * @param  rect   the query rectangle
	 * @param  entry  the entry of a leaf entry
	 */
	boolean conditionFulfilled (MBR rect, SpatialSearchTreeEntry entry);
	/**
	 * Tests whether the query rectangle and an object fulfill the query condition.
	 * @return  true if the condition fulfilled
	 * @param  rect  the query rectangle
	 * @param  obj   the object
	 */
	boolean conditionFulfilled (MBR rect, SpatialSearchTreeObject obj);

}
