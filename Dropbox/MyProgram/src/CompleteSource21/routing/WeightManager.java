package routing;

/**
 * Interface for classes that weight the edges.
 * 
 * @version	2.00	16.08.2003	long become double
 * @version	1.00	04.01.2000	first version
 * @author Thomas Brinkhoff
 */

public interface WeightManager {
	
	/**
	 * Computation of the weight of an edge of class 0.
	 * @return  the weight
	 * @param  distance  the length of the edge
	 */
	double computeWeight (double distance);
	
	/**
	 * Computation of the weight of an edge for a given speed.
	 * @return  the weight
	 * @param  distance  the length of the edge
	 * @param  speed  the speed on the edge
	 */
	double computeWeight (double distance, double speed);
	
	/**
	 * Computation of the weight of an edge.
	 * @return  the weight
	 * @param  distance  the edge
	 */
	double getWeight (Edge edge);
	
	/**
	 * Computation of the weight of an edge in a given direction.
	 * @return  the weight
	 * @param  distance  the edge
	 * @param  forwards  true: in direction of the edge; false: in the other direction
	 */
	double getWeight (Edge edge, boolean forwards);
}
