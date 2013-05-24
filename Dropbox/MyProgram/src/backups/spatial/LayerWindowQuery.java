package spatial;

/**
 * Class offering the operations required for a layer window query performed by a spatial search tree which stores the layer in the leafs:
 * The query rectangle must be intersected by the object and the layer must be equal to the query layer. 
 * 
 * @version 1.01	01.03.2003	adapted to LayerSpatialSearchTreeObject
 * @version 1.00	13.10.2002	first version
 * @author Thomas Brinkhoff
 */
public class LayerWindowQuery extends WindowQuery implements LayerQuery {

	/**
	 * The index of the query layer.
	 */
	private int queryLayer = 0;
	
/**
 * Constructor.
 * @param  queryLayer  the index of the query layer
 */
public LayerWindowQuery (int queryLayer) {
	this.queryLayer = queryLayer;
}
/**
 * Test for intersection. Supports tolerance.
 * @return  condition fulfilled?
 * @param  rect  query rectangle
 * @param  obj  leaf object
 */
public boolean conditionFulfilled (MBR rect, SpatialSearchTreeObject obj) {
	LayerSpatialSearchTreeObject lObj = (LayerSpatialSearchTreeObject) obj;
	if ((rect == null) || (obj == null))
		return false;
	else
		return rect.intersects (obj.getMBR(),tolerance) && (queryLayer == lObj.getLayer());
}
/**
 * Returns the index of the query layer.
 * @return  the index of the query layer
 */
public int getQueryLayer() {
	return queryLayer;
}
}
