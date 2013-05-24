package generator2;

import java.util.*;

/**
 * Class which decides about the re-routing.
 * 
 * @version 2.01	27.08.2003	use of RandomGenerator
 * @version 2.00	04.09.2001	revision for generator v2.0
 * @version 1.10	29.04.2000	time object added
 * @version 1.00	13.01.2000	first version
 * @author FH Oldenburg
 */
public class ReRoute {

	/**
	 * The time object.
	 */
	protected Time time;
	/**
	 * The data space
	 */
	protected DataSpace ds;
	/**
	 * The properties of the generator.
	 */
	protected Properties properties;

	/**
	 * How often has "computeNewRouteByComparison" returned "true". 
	 */
	protected int numOfRoutesByComparison = 0;
	/**
	 * How often has "computeNewRouteByEvent" returned "true". 
	 */
	protected int numOfRoutesByEvent = 0;
	/**
	 * Probability for re-routing because of a speed change in 1/p.
	 */
	protected int speedProbability;
	/**
	 * Probability for re-routing because of an event in 1/p.
	 */
	protected int eventProbability;
	/**
	 * Time limit for re-routing.
	 */
	protected int timeLimit;
	/**
	 * Speed threshold (as factor) for re-routing
	 */
	protected int speedThreshold;
	
	/**
	 * Random generator
	 */
	protected Random random;

/**
 * ReRoute constructor.
 * @param properties properties of the generator
 * @param time the time object
 * @param ds the dataspace
 */
public ReRoute (Properties properties, Time time, DataSpace ds) {
	this.properties = properties;
	this.time = time;
	this.ds = ds;
	random = RandomGenerator.get(properties);
	// change here
	speedThreshold = DataGenerator.getProperty(properties,"speedThreshold",2);
	speedProbability = DataGenerator.getProperty(properties,"speedProbability",20);
	eventProbability = DataGenerator.getProperty(properties,"eventProbability",200);
	timeLimit = DataGenerator.getProperty(properties,"timeLimit",10);
}
/**
 * Decides whether a route should be recomputed or not because of the comparison
 * of the speed on an edge.
 * @return recompute?
 * @param lastTime time when the last route was computed
 * @param actTime actual time
 * @param origSpeed speed of the edge when the last route was computed
 * @param actTime actual speed of the edge
 */
public boolean computeNewRouteByComparison (int lastTime, int actTime, int origSpeed, int actSpeed) {
	// implementation to be changed
	if ((actTime-lastTime < timeLimit) || (actSpeed > origSpeed/speedThreshold))
		return false;
	int randNum = Math.abs(random.nextInt())%speedProbability;
	if (randNum > 0)
		return false;
	numOfRoutesByComparison++;
	return true;
}
/**
 * Decides whether a route should be recomputed or not because of an event.
 * @return recompute?
 * @param lastTime time when the last route was computed
 * @param actTime actual time
 */
public boolean computeNewRouteByEvent (int lastTime, int actTime) {
	// implementation to be changed
	if (actTime-lastTime < timeLimit)
		return false;
	int randNum = Math.abs(random.nextInt())%eventProbability;
	if (randNum > 0)
		return false;
	numOfRoutesByEvent++;
	return true;
}
/**
 * Returns how often has "computeNewRouteByComparison" returned "true".
 * @return number
 */
public int getNumberOfRoutesByComparison() {
	return numOfRoutesByComparison;
}
/**
 * Returns how often has "computeNewRouteByEvent" returned "true".
 * @return number
 */
public int getNumberOfRoutesByEvent() {
	return numOfRoutesByEvent;
}
}