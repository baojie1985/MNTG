package spatial;

/**
 * Class offering the operations required for a layer all query performed by a spatial search tree which stores the layer in the leafs:
 * The layer must be equal to the query layer. 
 * 
 * @version 1.00	31.07.2003	first version
 * @author Thomas Brinkhoff
 */
public class LayerAllQuery extends AllQuery implements LayerQuery {

	/**
	 * The index of the query layer.
	 */
	private int queryLayer = 0;
	
/**
 * Constructor.
 * @param  queryLayer  the index of the query layer
 */
public LayerAllQuery (int queryLayer) {
	this.queryLayer = queryLayer;
}

/**
 * Test for layer. Supports tolerance.
 * @return  condition fulfilled?
 * @param  rect  query rectangle
 * @param  obj  leaf object
 */
public boolean conditionFulfilled (MBR rect, SpatialSearchTreeObject obj) {
	LayerSpatialSearchTreeObject lObj = (LayerSpatialSearchTreeObject) obj;
	if (obj == null)
		return false;
	else
		return (queryLayer == lObj.getLayer());
}

/**
 * Returns the index of the query layer.
 * @return  the index of the query layer
 */
public int getQueryLayer() {
	return queryLayer;
}
}
