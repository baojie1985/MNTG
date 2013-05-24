package spatial;

/**
 * Class offering the operations required for a layer window query performed by a spatial search tree which stores the layer in the leafs:
 * The query rectangle must be intersected by the object and the layer must be equal to the query layer. 
 * In addition, a defined scale must be exceeded.
 * 
 * @version 1.00	31.07.2003	first version
 * @author Thomas Brinkhoff
 */
public class LayerScaleWindowQuery extends LayerWindowQuery {

	/**
	 * The scale.
	 */
	private int scale = 0;
	
/**
 * Constructor.
 * @param  queryLayer  the index of the query layer
 */
public LayerScaleWindowQuery (int queryLayer, int scale) {
	super(queryLayer);
	this.scale = scale;
}

/**
 * Test for intersection. Supports tolerance.
 * The scale attribute is considered.
 * @return  condition fulfilled?
 * @param  rect  query rectangle
 * @param  obj  leaf object
 */
public boolean conditionFulfilled (MBR rect, SpatialSearchTreeObject obj) {
	VisualizedSpatialSearchTreeObject vObj = (VisualizedSpatialSearchTreeObject) obj;
	if ((vObj == null) || ! vObj.isVisible(scale))
		return false;
	else
		return super.conditionFulfilled(rect,obj);
}

}
