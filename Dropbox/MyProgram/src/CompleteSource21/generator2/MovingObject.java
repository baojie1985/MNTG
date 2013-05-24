package generator2;

import routing.*;

/**
 * Class representing a moving object.
 *
 * @version 2.10	26.08.2003	computeRoute returns boolean result, setXXXNode added, modified reporting
 * @version 2.00	04.09.2001	revision for generator v2.0
 * @version 1.50	11.04.2001	getDestinationNode added
 * @version 1.40	28.10.2000	support of report numbers and probability
 * @version 1.30	10.10.2000	more detailed reporting
 * @version 1.20	30.04.2000	decreaseUsage corrected, traversed nodes and their degree counted
 * @version 1.10	02.03.2000	reporting adjusted
 * @version 1.00	18.01.2000	first version
 * @author FH Oldenburg
 */

public class MovingObject {

	/**
	 * Description of the object classes.
	 */
	private static ObjectClasses objClasses = null;

	/**
	 * The container of the object.
	 */
	private MovingObjects container = null;

	/**
	 * The identifier.
	 */
	private int id = 0;
	/**
	 * The object class.
	 */
	private int objClass = 0;
	/**
	 * The starting node.
	 */
	private Node start = null;
	/**
	 * The destination node.
	 */
	private Node dest = null;
	/**
	 * The starting time.
	 */
	private int startTime = 0;
	/**
	 * The time when the last route was computed.
	 */
	private int lastTime = 0;
	/**
	 * The current time
	 */
	private int actTime = 0;
	/**
	 * The current report number.
	 */
	private int repNum = 0;
	/**
	 * The remaining route.
	 */
	private PathEdge route = null;
	/**
	 * The part of the route traversed during the last time period.
	 */
	private PathEdge lastRoute = null;
	/**
	 * The last traversed node.
	 */
	private Node lastNode = null;
	/**
	 * The relative distance of the current edge.
	 */
	private double relDist = 0;
	/**
	 * The distance since the last report.
	 */
	private double doneDist = 0;
	/**
	 * The last computed x-position.
	 */
	private double lastX = 0;
	/**
	 * The last computed y-position.
	 */
	private double lastY = 0;
	/**
	 * The arrival time (set at the end of the movement).
	 */
	private double arrivalTime;
	/**
	 * The number of passed edges.
	 */
	private int edgeNum = 0;

/**
 * Constructor.
 * @param  id object  identifier
 * @param  objClass  object class
 * @param  start  starting node
 * @param  dest  destination node
 * @param  time  starting time
 */
public MovingObject (int id, int objClass, Node start, Node dest, int time) {
	this.id = id;
	this.objClass = objClass;
	this.start = start;
	this.dest = dest;
	this.startTime = time;
	this.lastTime = time;
	this.actTime = time;
	this.lastNode = start;
	lastX = start.getX();
	lastY = start.getY();
}

/**
 * Adds the moving object to the container.
 * @param container container
 */
public void addToContainer (MovingObjects container) {
	if (container != null) {
		this.container = container;
		container.add(this);
		if (objClasses == null)
			objClasses = container.getObjectClasses();
	}
}

/**
 * Computes the distance between two points.
 * @return  distance
 * @param  x1  x-coordinate of the 1st point
 * @param  y1  y-coordinate of the 1st point
 * @param  x2  x-coordinate of the 2nd point
 * @param  y2  y-coordinate of the 2nd point
 */
public static double computeDistance (double x1, double y1, double x2, double y2) {
	double xDist = Math.abs(x1-x2);
	double yDist = Math.abs(y1-y2);
	return Math.sqrt(xDist*xDist + yDist*yDist);
}

/**
 * Computes and reports next point on the route.
 * @return destination reached?
 * @param newTime new time
 * @param reporter reporter
 */
private boolean computeNextPoint (int newTime, Reporter reporter) {
	if (route == null)
		return true;
	// decrease usage of the edge traversed during the last time period
	Edge actEdge = route.getEdge();
	decreaseUsage (route);
	lastRoute = route;
	// if event then re-route
	if (container.getReRoute().computeNewRouteByEvent (lastTime,actTime)) {
		reroute(actEdge);
	}
	// traverse path
	double remainingTime = 1;
	double llx = lastX;
	double lly = lastY;
	// set edge characteristics
	double actDist = actEdge.getLength();
	if (actDist == 0)	// in the case of identical nodes
		actDist = 1;
	double actWeight = actEdge.getWeight();
	double speed = actDist/actWeight;
	double maxDistOnEdge = remainingTime*speed;
	while (true) {
		// case 1: next node is not reached
		if (relDist+maxDistOnEdge < actDist) {
			relDist += maxDistOnEdge;
			computePoint(actEdge,lastNode,relDist);
			util.Timer.stop(1);
			doneDist += computeDistance(llx,lly,lastX,lastY);
			int oldRepNum = repNum;
			Node nextNode = route.getDestinationNode();
 			repNum = reporter.reportMovingObject(newTime,id,repNum,objClass,lastX,lastY,speed,doneDist,nextNode.getX(),nextNode.getY(),objClasses.getReportProbability(objClass));
 			if (repNum != oldRepNum)
 				doneDist = 0;
			util.Timer.start(1);
			return false;
		}
		// case 2: next node is reached
		else {
			remainingTime -= (actDist-relDist)*actWeight/actDist;
			relDist = 0;
			lastNode = actEdge.getOppositeNode(lastNode);
			doneDist += computeDistance(llx,lly,lastNode.getX(),lastNode.getY());
			llx = lastNode.getX();
			lly = lastNode.getY();
			// case 2a next node is destination
			if (lastNode == dest) {
				decreaseUsage (null);
				arrivalTime = newTime-remainingTime;
				return true;
			}
			// else: fetch next edge
			route = route.getNext();
			if (route == null) {
				//System.err.println("computeNextPoint: route == null! (2)"+id+" at "+newTime);
				dest = lastNode;
				decreaseUsage (null);
				arrivalTime = newTime-remainingTime;
				return true;
			}
			actEdge = route.getEdge();
			actEdge.incUsage();
			// update edge characteristics
			actDist = actEdge.getLength();
			if (actDist == 0)	// in the case of identical nodes
				actDist = 1;
			actWeight = actEdge.getWeight();
			speed = actDist/actWeight;
			maxDistOnEdge = remainingTime*speed;
			// report
			util.Timer.stop(1);
			reporter.reportEdge(newTime-remainingTime,id,++edgeNum,objClass,actEdge.getId(),actEdge.getEdgeClass(),route.getStartingNode().getX(),route.getStartingNode().getY(),speed,route.getDestinationNode().getX(),route.getDestinationNode().getY(),objClasses.getReportProbability(objClass));
			util.Timer.start(1);
			// if significant speed change then re-route
			if (container.getReRoute().computeNewRouteByComparison (lastTime,actTime,(int)(0x7fffffff/route.getOrigWeight()),(int)(0x7fffffff/actWeight))) {
				reroute(actEdge);
			}
		}
	}
}

/**
 * Computes intermediate point (lastX,lastY) on an edge.
 * @param x1 x1-coordinate of the edge
 * @param y1 y1-coordinate of the edge
 * @param x2 x2-coordinate of the edge
 * @param y2 y2-coordinate of the edge
 * @param portion portion of the edge
 * @param length distance between (x1,y1) and (x2,y2)
 */
private void computePoint (int x1, int y1, int x2, int y2, double portion, double length) {
	if (length == 0)
		length = 1;
	lastX = x1+(x2-x1)*portion/length;
	lastY = y1+(y2-y1)*portion/length;
}

/**
 * Computes the position (lastX,lastY) on the edge.
 * @param edge the edge
 * @param start the node which the object traversed last
 * @param portion distance from start
 */
private void computePoint (Edge edge, Node start, double portion) {
	if (start == edge.getNode1())
		computePoint (edge.getNode1().getX(),edge.getNode1().getY(),edge.getNode2().getX(),edge.getNode2().getY(),portion,edge.getLength());
	else
		computePoint (edge.getNode2().getX(),edge.getNode2().getY(),edge.getNode1().getX(),edge.getNode1().getY(),portion,edge.getLength());
}

/**
 * Computes a new route from the current position of the object to its destination.
 * @return  computation successful?
 */
public boolean computeRoute() {
	if (container != null) {
		container.getWeightManager().setActualObjectClass(objClass);
		util.Timer.start(2);
		route = container.getNetwork().computeFastWay2 (start,dest);
		util.Timer.stop(2);
		if (route != null) {
			if ((route.getNext() == null) || (route.getNext().getNext() == null))
				return false;
			route.getEdge().incUsage();
			if (container != null) {
				container.incTraversedNodesBy(route.computeNumber());
				container.incTraversedDegreeBy(route.computeDegree());
			}
		}
		else
			return false;
	}
	return true;
}

/**
 * Decreases the usage in the path until one path edge is reached.
 * @param actPathEdge the path edge which stops the operation
 */
protected void decreaseUsage (PathEdge actPathEdge) {
	PathEdge path = lastRoute;
	if (path != null) {
		Edge travEdge = path.getEdge();
		while ((path != null) && (path != actPathEdge)) {
			travEdge.decUsage();
			path = path.getNext();
			if (path != null)
				travEdge = path.getEdge();
		}
	}
}

/**
 * Return the current path edge where the object is.
 * @return current path edge
 */
protected PathEdge getActPathEdge () {
	return route;
}

/**
 * Return the destination node.
 * @return destination
 */
protected Node getDestinationNode () {
	return dest;
}

/**
 * Returns the identifier of the object.
 * @return the id
 */
public int getId() {
	return id;
}

/**
 * Returns the object class.
 * @return the class
 */
public int getObjectClass() {
	return objClass;
}

/**
 * Returns the report number of the object.
 * @return report number
 */
public int getRepNum() {
	return repNum;
}

/**
 * Return the starting node.
 * @return start
 */
protected Node getStartingNode () {
	return start;
}

/**
 * Moves the object to the position at the new time.
 * The position(s) are reported.
 * @return has the object reached its destination?
 * @param newTime new time
 * @param reporter reporter
 */
public boolean move (int newTime, Reporter reporter) {
	if (container != null) {
		container.getWeightManager().setActualObjectClass(objClass);
		while (actTime < newTime) {
			actTime++;
			if (computeNextPoint(actTime,reporter))
				return true;
		}
	}
	return false;
}

/**
 * Reports that the moving object has reached its destination.
 * @param reporter reporter
 */
public void reportEnd (Reporter reporter) {
	util.Timer.stop(1);
	if (reporter != null) {
 		reporter.reportDisappearingObject (arrivalTime,id,repNum,objClass,dest.getX(),dest.getY(),doneDist,objClasses.getReportProbability(objClass));
	}
	util.Timer.start(1);
}

/**
 * Reports a new moving object.
 * @param  reporter  the reporter object
 */
public void reportNewObject (Reporter reporter) {
	util.Timer.stop(1);
	if (reporter != null) {
		Edge currEdge = route.getEdge();
		double actDist = currEdge.getLength();
		double actWeight = currEdge.getWeight();
		double speed = actDist/actWeight;
		Node nextNode = route.getDestinationNode();
		repNum = reporter.reportNewMovingObject (startTime,id,objClass,start.getX(),start.getY(),speed,nextNode.getX(),nextNode.getY(),objClasses.getReportProbability(objClass));
		reporter.reportEdge(startTime,id,++edgeNum,objClass,currEdge.getId(),currEdge.getEdgeClass(),route.getStartingNode().getX(),route.getStartingNode().getY(),speed,route.getDestinationNode().getX(),route.getDestinationNode().getY(),objClasses.getReportProbability(objClass));
	}
	util.Timer.start(1);
}

/**
 * Reroutes the object.
 * @param actPath current edge
 */
private void reroute (Edge actEdge) {
	container.getWeightManager().setActualObjectClass(objClass);
	util.Timer.start(2);
	PathEdge newPath = container.getNetwork().computeFastWay2 (actEdge.getOppositeNode(lastNode),dest);
	util.Timer.stop(2);
	if ((newPath != null) && (container != null)) {
		route.setNext(newPath);
		container.incTraversedNodesBy(route.computeNumber());
		container.incTraversedDegreeBy(route.computeDegree());
	}
	lastTime = actTime;
}

/**
 * Sets the destination node.
 * @param  node  the new destination node
 */
public void setDestination (Node dest) {
	this.dest = dest;
}

/**
 * Sets the report number of the object.
 * @param  num  the new report number
 */
public void setRepNum (int num) {
	repNum = num;
}

/**
 * Sets the starting node.
 * @param  node  the new starting node
 */
public void setStart (Node start) {
	this.start = start;
	this.lastNode = start;
	lastX = start.getX();
	lastY = start.getY();
}

}