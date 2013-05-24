package spatial;

/**
 * Class offering the operations required for a is-contained query performed by a spatial search tree:
 * The query rectangle must be contained by the object. 
 * 
 * @version 3.00	01.03.2003	adapted to modified interface ST_Query
 * @version 2.00	05.10.2002	Rectangle becomes MBR
 * @version 1.20	09.04.2000	ST_Query replaced by BasicSpatialQuery
 * @version 1.10	25.03.2000	conditionedFulfilled changed
 * @version 1.00	29.01.2000	first version
 * @author Thomas Brinkhoff
 */
public class IsContainedQuery extends BasicSpatialQuery {
	
/**
 * Tests whether the query rectangle is contained by the MBR of a non-leaf entry.
 * The tolerance attribute is not considered.
 * @return  true if the condition is fulfilled
 * @param  rect      the query rectangle
 * @param  entryMBR  the minimum bounding rectangle of non-leaf entry
 */
public boolean conditionFulfilled (MBR rect, MBR entryMBR) {
	if (entryMBR == null)
		return false;
	else
		return entryMBR.contains(rect);
}
/**
 * Tests whether the query rectangle is contained by the MBR of an leaf entry intersect.
 * The tolerance attribute is not considered.
 * @return  true if the condition is fulfilled
 * @param  rect   the query rectangle
 * @param  entry  the entry of a leaf entry
 */
public boolean conditionFulfilled (MBR rect, SpatialSearchTreeEntry entry) {
	if ((rect == null) || (entry == null))
		return false;
	else
		return entry.getMBR().contains (rect);
}
/**
 * Test whether the query rectangle is contained by the object or not.
 * The tolerance attribute is not considered.
 * @return  true if the condition fulfilled
 * @param  rect  the query rectangle
 * @param  obj   the object
 */
public boolean conditionFulfilled (MBR rect, SpatialSearchTreeObject obj) {
	if (obj == null)
		return false;
	else
		return obj.contains(rect);
}
}
