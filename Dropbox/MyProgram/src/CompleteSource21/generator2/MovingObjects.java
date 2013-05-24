package generator2;

import java.util.*;
import routing.*;

/**
 * Container class for all current moving objects.
 *
 * @version 2.00	04.09.01	revision for generator v2.0
 * @version 1.20	11.04.01	object generator added
 * @version 1.11	10.10.00	calling reportEnd
 * @version 1.10	24.04.00	travNodes & travDegree added, use of decreaseUsage changed
 * @version 1.01	01.02.00	reporting adjusted
 * @version 1.00	13.01.00	first version
 * @author FH Oldenburg
 */

public class MovingObjects {

	/**
	 * the moving objects
	 */
	private Vector objs = null;

	/**
	 * the weight manager
	 */
	private WeightManagerForDataGenerator wm = null;
	/**
	 * description of the object classes
	 */
	private ObjectClasses objClasses = null;
	/**
	 * description of the network
	 */
	private Network net = null;
	/**
	 * object generator
	 */
	private ObjectGenerator objGen = null;
	/**
	 * reporter
	 */
	private Reporter reporter = null;
	/**
	 * re-routing decider
	 */
	private ReRoute reroute = null;

	/**
	 * number of moving objects
	 */
	private int num = 0;
	/**
	 * number of created moving objects
	 */
	private int totalNum = 0;
	/**
	 * total number of traversed nodes
	 */
	private int travNodes = 0;
	/**
	 * total degree of traversed nodes
	 */
	private int travDegree = 0;

/**
 * MovingObjects constructor.
 * @param wm the weight manager
 * @param net the network
 * @param objGen the object generator
 * @param reporter the reporter
 * @param reroute reroute decider
 */
public MovingObjects (WeightManagerForDataGenerator wm, Network net, ObjectGenerator objGen, Reporter reporter, ReRoute reroute) {
	this.wm = wm;
	this.objClasses = wm.getObjectClasses();
	this.net = net;
	this.objGen = objGen;
	this.reporter = reporter;
	this.reroute = reroute;
	objs = new Vector (10000,10000);
}
/**
 * Adds a moving object to the container.
 * @param obj moving object
 */
protected void add (MovingObject obj) {
	objs.addElement(obj);
	num++;
	totalNum++;
}
/**
 * Returns the network.
 * @return network
 */
public Network getNetwork() {
	return net;
}
/**
 * Returns the description of the object classes.
 * @return object classes
 */
public ObjectClasses getObjectClasses() {
	return objClasses;
}
/**
 * Returns the rerouting decider.
 * @return reroute
 */
public ReRoute getReRoute () {
	return reroute;
}
/**
 * Returns the total degree of traversed nodes.
 * @return degree of traversed nodes
 */
public int getTotalDegreeOfTraversedNodes() {
	return travDegree;
}
/**
 * Returns the total number of traversed nodes.
 * @return number of traversed nodes
 */
public int getTotalNumberOfTraversedNodes() {
	return travNodes;
}
/**
 * Returns the total number of created moving objects.
 * @return int
 */
public int getTotalNumOfObjects () {
	return totalNum;
}
/**
 * Returns the weight manager.
 * @return weight manager
 */
public WeightManagerForDataGenerator getWeightManager () {
	return wm;
}
/**
 * Increments the counter for the degree of traversed nodes by a given value.
 * @param value the increment
 */
public void incTraversedDegreeBy (int value) {
	travDegree += value;
}
/**
 * Increments the counter for the number of traversed nodes by a given value.
 * @param value the increment
 */
public void incTraversedNodesBy (int value) {
	travNodes += value;
}
/**
 * Moves all objects. The positions during the route of the objects, which have reached
 * the destination node, are reported; these objects are removed from the container.
 * @param time the current time stamp
 */
public void move (int time) {
	for (int i=num-1; i>=0; i--) {
		MovingObject obj = (MovingObject)objs.elementAt(i);
		if (obj.move (time,reporter)) {
			obj.reportEnd(reporter);
			remove(i);
		}
	}
}
/**
 * Removes the moving object at a given index.
 * @param index index of the object
 */
public void remove (int index) {
	MovingObject obj = (MovingObject)objs.elementAt(index);
	if ((objGen != null) && (obj!=null))
		objGen.reachDestination(obj);
	objs.setElementAt(objs.elementAt(num-1),index);
	objs.setElementAt(null,num-1);
	num--;
	objs.setSize(num);

}
/**
 * The positions during the route of all objects in the container are reported.
 * Then, all objects are removed from the container.
 */
public void removeObjects() {
	for (int i=0; i<num; i++) {
		MovingObject obj = (MovingObject)objs.elementAt(i);
		obj.decreaseUsage (obj.getActPathEdge());
		obj.getActPathEdge().getEdge().decUsage();
		objs.setElementAt(null,i);
	}
	num = 0;
	objs.setSize(num);
}
}