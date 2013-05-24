package spatial;

/**
 * Class offering the operations required for a selection query performed by a spatial search tree.
 * 
 * @version 3.10	31.07.2003	attribute scale moved into this class
 * @version 3.00	01.03.2003	adapted to modified interfaces ST_Query, VisualizedSpatialSearchTreeObject
 * @version 2.00	05.10.2002	Rectangle becomes MBR
 * @version 1.20	09.04.2000	ST_Query replaced by BasicSpatialQuery
 * @version 1.10	25.03.2000	conditionedFulfilled changed
 * @version 1.00	29.01.2000	first version
 * @author Thomas Brinkhoff
 */
public class SelectQuery extends BasicSpatialQuery {

	/**
	 * The scale.
	 */
	private int scale = 0;

/**
 * Constructor.
 * @param  scale  the scale
 */
public SelectQuery (int scale) {
	super();
	this.scale = scale;
}

/**
 * Tests whether the minimum coordinates of the query rectangle is contained by the MBR of a non-leaf entry intersect.
 * The tolerance attribute is considered.
 * @return  true if the condition is fulfilled
 * @param  rect      the query rectangle
 * @param  entryMBR  the minimum bounding rectangle of non-leaf entry
 */
public boolean conditionFulfilled (MBR rect, MBR entryMBR) {
	if ((rect == null) || (entryMBR == null))
		return false;
	else
		return entryMBR.contains (rect.getMinCoords(),tolerance);
}
/**
 * Tests whether the minimum coordinate of the query rectangle and the MBR of an leaf entry interact.
 * The tolerance attribute is considered.
 * @return  true if the condition is fulfilled
 * @param  rect   the query rectangle
 * @param  entry  the entry of a leaf entry
 */
public boolean conditionFulfilled (MBR rect, SpatialSearchTreeEntry entry) {
	if ((rect == null) || (entry == null))
		return false;
	else
		return entry.getMBR().contains (rect.getMinCoords(),tolerance);
}
/**
 * Tests whether the query rectangle and the object interact.
 * The scale attribute is considered.
 * @return  true if the condition fulfilled
 * @param  rect  the query rectangle
 * @param  obj   the object
 */
public boolean conditionFulfilled (MBR rect, SpatialSearchTreeObject obj) {
	VisualizedSpatialSearchTreeObject vObj = (VisualizedSpatialSearchTreeObject) obj;
	if ((rect == null) || (obj == null))
		return false;
	else
		return vObj.interacts(rect.min(MBR.X),rect.min(MBR.Y),(int)Math.round(scale));
}

}
