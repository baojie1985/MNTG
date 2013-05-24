package spatial;

/**
 * Class offering the operations required for an exact match query performed by a spatial search tree.
 * 
 * @version 3.00	01.03.2003	adapted to modified interface ST_Query
 * @version 2.00	05.10.2002	Rectangle becomes MBR
 * @version 1.00	09.04.2000	first version
 * @author Thomas Brinkhoff
 */
public class ExactMatchQuery extends BasicSpatialQuery {
	
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
 * Tests whether the query rectangle and the MBR of an leaf entry are equal.
 * The tolerance attribute is not considered.
 * @return  true if the condition is fulfilled
 * @param  rect   the query rectangle
 * @param  entry  the entry of a leaf entry
 */
public boolean conditionFulfilled (MBR rect, SpatialSearchTreeEntry entry) {
	if ((rect == null) || (entry == null))
		return false;
	else
		return rect.equals(entry.getMBR());
}
/**
 * Tests whether the query rectangle and the MBR of an object are equal.
 * The tolerance attribute is not considered.
 * @return  true if the condition fulfilled
 * @param  rect  the query rectangle
 * @param  obj   the object
 */
public boolean conditionFulfilled (MBR rect, SpatialSearchTreeObject obj) {
	if (obj == null)
		return false;
	else
		return obj.getMBR().equals(rect);
}

}
