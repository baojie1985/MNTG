package routing;

/**
 * Standard class for weighting the edges.
 * 
 * @version	2.00	17.08.2003	long become double, adapted to Edge
 * @version	1.01	09.03.2000	constructor corrected
 * @version	1.00	04.01.2000	first version
 * @author Thomas Brinkhoff
 */
public class StandardWeightManager implements WeightManager {

	/**
	 * Container for the edges.
	 */
	protected Edges edges = null;
	
	/**
	 * Constructor.
	 * @param  egdes  the container of the edges
	 */
	public StandardWeightManager (Edges edges) {
		this.edges = edges;
	}
	
	/**
	 * Computation of the weight of an edge of class 0.
	 * @return  the weight
	 * @param  distance  the length of the edge
	 */
	public double computeWeight (double distance) {
		return computeWeight (distance,edges.getSpeed(0));
	}
	
	/**
	 * Computation of the weight of an edge for a given speed.
	 * @return  the weight
	 * @param  distance  the length of the edge
	 * @param  speed  the speed on the edge
	 */
	public double computeWeight (double distance, double speed) {
		if (speed != 0)
			return distance / speed;
		else
			return Double.MAX_VALUE;
	}

	/**
	 * Computation of the weight of an edge.
	 * @return  the weight
	 * @param  distance  the edge
	 */
	public double getWeight(Edge edge) {
		if (edges != null)
			return edge.getLength()/edges.getSpeed(edge.getEdgeClass());
		else
			return edge.getLength();
	}

	/**
	 * Computation of the weight of an edge in a given direction.
	 * @return  the weight
	 * @param  distance  the edge
	 * @param  forwards  true: in direction of the edge; false: in the other direction
	 */
	public double getWeight(Edge edge, boolean forwards) {
		if (edges != null)
			return edge.getLength()/edges.getSpeed(edge.getEdgeClass());
		else
			return edge.getLength();
	}
}
