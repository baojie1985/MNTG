package spatial;

/**
 * Class offering some basic operations required for spatial queries performed by a spatial search tree.
 * 
 * @version 3.01	31.07.2003	attribute scale removed
 * @version 3.00	01.03.2003	adapted to modified interface ST_Query
 * @version 2.00	13.10.2002	Rectangle becomes MBR
 * @version 1.00	12.04.2000	first version after splitting ST_Query
 * @author Thomas Brinkhoff
 */
public abstract class BasicSpatialQuery implements SpatialQuery {

	/**
	 * tolerance, required by some queries
	 */
	protected int tolerance = 0;

/**
 * Tests whether the query object and the stored object are equal.
 * @return  true if equal
 * @param  queryObj   the query object
 * @param  storedObj  the stored object
 */
public boolean areEqual (SpatialSearchTreeObject queryObj, SpatialSearchTreeObject storedObj) {
	if (queryObj == null)
		return false;
	else
		return queryObj.equals (storedObj);
}
/**
 * Tests whether the query rectangle and the MBR of a non-leaf entry intersect.
 * The tolerance attribute is considered.
 * @return  true if the condition is fulfilled
 * @param  rect      the query rectangle
 * @param  entryMBR  the minimum bounding rectangle of non-leaf entry
 */
public boolean conditionFulfilled (MBR rect, MBR entryMBR) {
	if (rect == null)
		return false;
	else
		return rect.intersects (entryMBR,tolerance);
}
/**
 * Tests whether the query rectangle and the MBR of an leaf entry intersect.
 * The tolerance attribute is considered.
 * @return  true if the condition is fulfilled
 * @param  rect   the query rectangle
 * @param  entry  the entry of a leaf entry
 */
public boolean conditionFulfilled (MBR rect, SpatialSearchTreeEntry entry) {
	if ((rect == null) || (entry == null))
		return false;
	else
		return rect.intersects (entry.getMBR(),tolerance);
}
/**
 * Tests whether the query rectangle and the MBR of an object intersect.
 * The tolerance attribute is considered.
 * @return  true if the condition fulfilled
 * @param  rect  the query rectangle
 * @param  obj   the object
 */
public boolean conditionFulfilled (MBR rect, SpatialSearchTreeObject obj) {
	if ((rect == null) || (obj == null))
		return false;
	else
		return rect.intersects (obj.getMBR(),tolerance);
}
/**
 * Sets the tolerance attribute.
 * @param  tolerance  the new value of tolerance
 */
public void setTolerance (int tolerance) {
	this.tolerance = tolerance;
}
}
