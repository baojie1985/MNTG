package spatial;

/**
 * Class offering the operations required for retrieving all objects from a spatial search tree.
 * 
 * @version 3.10	31.07.2003	conditionFulfilled (MBR, MBR) added
 * @version 3.00	01.03.2003	adapted to modified interface ST_Query
 * @version 2.00	05.10.2002	Rectangle becomes MBR
 * @version 1.20	09.04.2000	ST_Query replaced by BasicSpatialQuery
 * @version 1.10	25.03.2000	conditionedFulfilled changed
 * @version 1.00	29.01.2000	first version
 * @author Thomas Brinkhoff
 */
public class AllQuery extends BasicSpatialQuery {
	
/**
 * Returns true.
 * @return  true
 * @param  queryObj   the query object
 * @param  storedObj  the stored object
 */
public boolean areEqual (SpatialSearchTreeObject queryObj, SpatialSearchTreeObject storedObj) {
	return true;
}
/**
 * Tests whether the query rectangle and the MBR of a non-leaf entry intersect.
 * The tolerance attribute is considered.
 * @return  true
 * @param  rect      the query rectangle
 * @param  entryMBR  the minimum bounding rectangle of non-leaf entry
 */
public boolean conditionFulfilled (MBR rect, MBR entryMBR) {
	return true;
}
/**
 * Returns true.
 * @return  true
 * @param  rect   the query rectangle
 * @param  entry  the entry of a leaf entry
 */
public boolean conditionFulfilled (MBR rect, SpatialSearchTreeEntry entry) {
	return true;
}
/**
 * Returns true.
 * @return  true
 * @param  rect  the query rectangle
 * @param  obj   the object
 */
public boolean conditionFulfilled (MBR rect, SpatialSearchTreeObject obj) {
	return true;
}

}
