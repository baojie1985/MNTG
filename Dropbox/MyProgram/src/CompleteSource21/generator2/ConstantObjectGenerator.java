package generator2;

import java.util.*;
import routing.*;

/**
 * Example for an object generator garanteeing a constant number of moving objects during all time stamps.
 *
 * @version 1.00  04.09.2001  first version for generator v2.0 <br>
 *  version 1.01  14.02.2005  robust against non-connected networks
 * @author FH Oldenburg
 */

public class ConstantObjectGenerator extends ObjectGenerator {

	/**
	 * Container of dead objects.
	 */
	protected Stack deadObjs = new Stack();
	/**
	 * Current moving object.
	 */
	protected MovingObject currObject = null;

/**
 * ConstantObjectGenerator constructor.
 * @param properties properties of the generator
 * @param time the time object
 * @param dataspace the dataspace
 * @param nodes the nodes of the network
 * @param objClasses description of the object classes
 * @param numOfObjPerTime indicator for the number of objects per time
 * @param numOfObjAtBeginning indicator for the number of moving objects at the beginning
 */
public ConstantObjectGenerator (Properties properties, Time time, DataSpace ds, Nodes nodes, ObjectClasses objClasses, int numOfObjPerTime, int numOfObjAtBeginning) {
	super (properties,time,ds,nodes,objClasses,numOfObjPerTime,numOfObjAtBeginning);
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
	Node dest = super.computeDestinationNode (time,startingNode,length,objClass);
	return dest;
}

/**
 * Returns the identifier of the next new moving object.
 * @return the id
 * @param  currTime  the current time
 */
public int computeId (int currTime) {
	if (Time.isFirstTimeStamp (currTime))
		return super.computeId(currTime);
	else {
		currObject = (MovingObject) deadObjs.peek();
		deadObjs.pop();
		return currObject.getId();
	}
}

/**
 * Computes the object class of a new object.
 * @return  the class
 * @param  currTime  the current time
 */
public int computeObjectClass (int currTime) {
	if (Time.isFirstTimeStamp (currTime))
		return super.computeObjectClass(currTime);
	else {
		return currObject.getObjectClass();
	}
}

/**
 * Computes a new starting node.
 * @return starting node
 * @param time the time stamp
 * @param objClass the class of the object
 */
public Node computeStartingNode (int time, int objClass) {
	if (Time.isFirstTimeStamp (time))
		return super.computeStartingNode(time,objClass);
	else {
		return currObject.getDestinationNode();
	}
}

/**
 * Returns the number of new objects at a time stamp.
 * @return number of objects
 * @param time the time stamp
 */
public int numberOfNewObjects (int time) {
	if (Time.isFirstTimeStamp(time))
		return numOfObjAtBeginning;
	else
		return deadObjs.size();
}

/**
 * Stores the object for using its properties for new objects.
 * @param  obj  the moving object
 */
public void reachDestination (MovingObject obj) {
	deadObjs.push(obj);
}

}