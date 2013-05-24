package generator2;

import java.util.*;
import routing.*;

/**
 * Class for defining the properties of moving objects. An example is the computations of new starting and
 * destination nodes.
 *
 * @version 2.10	27.08.2003	distance becomes double, use RandomGenerator
 * @version 2.00	04.09.2001	revision for generator v2.0
 * @version 1.30	11.04.2001	reachDestination added
 * @version 1.21	01.06.2000	adapted to new version of DrawableObjects
 * @version 1.20	30.04.2000	time object added
 * @version 1.10	01.02.2000	DrawableObjects added
 * @version 1.00	16.01.2000	first version
 * @author FH Oldenburg
 */

public class ObjectGenerator {

	/**
	 * The value of the next identifier.
	 */
	static protected int currId = 0;

	/**
	 * Properties of the generator.
	 */
	protected Properties properties = null;

	/**
	 * The time object.
	 */
	protected Time time;
	/**
	 * The dataspace of the network.
	 */
	protected DataSpace dataspace = null;
	/**
	 * The object classes.
	 */
	protected ObjectClasses objClasses = null;
	/**
	 * The nodes of the network.
	 */
	protected Nodes nodes = null;
	/**
	 * The nodes of the network as an array.
	 */
	protected Node node[] = null;

	/**
	 * Indicator for the number of moving objects per time.
	 */
	protected int numOfObjPerTime = 1;
	/**
	 * Indicator for the number of moving objects at the beginning.
	 */
	protected int numOfObjAtBeginning = 0;
	/**
	 * The maximum length of routes.
	 */
	protected double maxLength = 0;
	/**
	 * The number of generated node pairs.
	 */
	protected int numOfGeneratedNodes = 0;
	/**
	 * The total length of routes.
	 */
	protected double totalLength = 0;

	/**
	 * The random generator.
	 */
	protected Random random = null;

/**
 * ObjectGenerator constructor.
 * @param properties properties of the generator
 * @param time the time object
 * @param dataspace the dataspace
 * @param nodes the nodes of the network
 * @param objClasses description of the object classes
 * @param numOfObjPerTime indicator for the number of objects per time
 * @param numOfObjAtBeginning indicator for the number of moving objects at the beginning
 */
public ObjectGenerator (Properties properties, Time time, DataSpace ds, Nodes nodes, ObjectClasses objClasses, int numOfObjPerTime, int numOfObjAtBeginning) {
	this.numOfObjPerTime = numOfObjPerTime;
	this.numOfObjAtBeginning = numOfObjAtBeginning;
	this.time = time;
	this.properties = properties;
	this.nodes = nodes;
	this.objClasses = objClasses;
	this.dataspace = ds;
	// transfer node to array
	node = new Node[nodes.numOfNodes()];
	int i=0;
	for (Enumeration e = nodes.elements(); e.hasMoreElements();)
		node[i++] = (Node)e.nextElement();
	// initialize other variables
	double dx = dataspace.getMaxX()-dataspace.getMinX();
	double dy = dataspace.getMaxY()-dataspace.getMinY();
	maxLength = (Math.sqrt(dx*dx+dy*dy) / 5);
	random = RandomGenerator.get(properties);
}

/**
 * Computes a new destination node of a route.
 * @return starting node
 * @param time the time stamp
 * @param startingNode the starting node of the route
 * @param length preferred length of the route
 * @param objClass the class of the object
 */
public Node computeDestinationNode(int time, Node startingNode, int length, int objClass) {
	// may be changed ...
	Node bestNode = computeNode(time,objClass,false);
	while (bestNode.equals(startingNode))
		bestNode = computeNode(time,objClass,false);
	double bestDist = bestNode.distanceTo(startingNode);
	double bestDiff = Math.abs(bestDist-length);
	long goodDiff = length / 10;
	int i = 0;
	while ((i<50) && (bestDiff > goodDiff)) {
		Node newNode = computeNode(time,objClass,false);
		double newDist = newNode.distanceTo(startingNode);
		double newDiff = Math.abs(newDist-length);
		if ((newDiff < bestDiff) && (!newNode.equals(startingNode))) {
			bestNode = newNode;
			bestDist = newDist;
			bestDiff = newDiff;
		}
		i++;
	}
	numOfGeneratedNodes++;
	totalLength += bestDist;
	return bestNode;
}

/**
 * Returns the identifier of the next new moving object.
 * @return the id
 * @param  currTime  the current time
 */
public int computeId (int currTime) {
	// may be modified
	return currId++;
}

/**
 * Generates the length of a new route.
 * @return length
 * @param time time stamp
 * @param objClass object class of the moving object
 */
public int computeLengthOfRoute(int time, int objClass) {
	double r = Math.abs(random.nextGaussian());
	int ext = (int)(r * maxLength);
	return ext;
}

/**
 * Computes a new node.
 * @return  the new node
 * @param  time  the time stamp
 * @param  objClass  the class of the object
 * @param  start  start node?
 */
private Node computeNode (int time, int objClass, boolean start) {
	// the dataspace-oriented approach
	if (properties.getProperty("DSO") != null) {
		int dx = dataspace.getMaxX()-dataspace.getMinX();
		int dy = dataspace.getMaxY()-dataspace.getMinY();
		int x = Math.abs(random.nextInt())%(dx+1) + dataspace.getMinX();
		int y = Math.abs(random.nextInt())%(dy+1) + dataspace.getMinY();
		return nodes.findNearest (x,y);
	}
	// the network-based approach
	else {
		int index = Math.abs(random.nextInt())%node.length;
		try {
		return node[index];
		}
		catch (Exception ex) {
			System.err.println(index);
			return node[index-1];
		}
	}
}

/**
 * Computes the object class of a new object.
 * @return  the class
 * @param  currTime  the current time
 */
public int computeObjectClass (int currTime) {
	return objClasses.computeNewObjectClass (currTime);
}

/**
 * Computes a new starting node.
 * @return starting node
 * @param time the time stamp
 * @param objClass the class of the object
 */
public Node computeStartingNode (int time, int objClass) {
	return computeNode (time,objClass,true);
}

/**
 * Returns the average distance of routes.
 * @return the average distance of routes
 */
public int getAverageRouteLength () {
	if (numOfGeneratedNodes > 0)
		return (int) (totalLength / numOfGeneratedNodes);
	else
		return 0;
}

/**
 * Returns the number of new objects at a time stamp.
 * @return number of objects
 * @param time the time stamp
 */
public int numberOfNewObjects (int time) {
	// may be changed ...
	if (Time.isFirstTimeStamp(time))
		return numOfObjAtBeginning;
	//else if (time > this.time.getMaxTime()-10)
	//	return 0;
	else
		return numOfObjPerTime;
}

/**
 * Method, which is called, when a moving object reaches its destination.
 * Can be used for infuencing the behavior of the methods computing the behavior of new objects.
 * @param  obj  the moving object
 */
public void reachDestination (MovingObject obj) {
}

}