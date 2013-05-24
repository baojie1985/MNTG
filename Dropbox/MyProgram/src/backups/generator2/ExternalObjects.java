package generator2;

import java.util.*;
import routing.*;
import spatial.*;

/**
 * Container class for all external objects.
 *
 * @version 2.02	05.04.2003	adapted to ST_RegionQuery
 * @version 2.01	06.10.2002	adapted to MBR
 * @version 2.00	03.09.2001	revision for generator v2.0
 * @version 1.10	15.06.2001	reporter, removeDeadObjects added
 * @version 1.01	10.10.2000	Timer -> util.Timer
 * @version 1.00	29.04.2000	first version
 * @author FH Oldenburg
 */

public class ExternalObjects {

	/**
	 * The properties of the generator.
	 */
	private Properties properties = null;

	/**
	 * The time object.
	 */
	private Time time;
	/**
	 * The description of the object classes.
	 */
	private ExternalObjectClasses objClasses;

	/**
	 * R-tree storing all external objects.
	 */
	private MemoryRTree objs = null;
	/**
	 * Vector temporarily storing the dead external objects.
	 */
	private Vector deadObjs = new Vector (20,20);
	/**
	 * Vector temporarily storing the living external objects.
	 */
	private Vector moveObjs = new Vector (200,200);

	/**
	 * Query for determining all external objects.
	 */
	private RegionQuery allQuery = new RegionQuery(new AllQuery());
	/**
	 * Query for determining external objects intersecting a window.
	 */
	private RegionQuery intersectionQuery = new RegionQuery(new WindowQuery());

	/**
	 * The number of created external objects.
	 */
	private int totalNum = 0;
	/**
	 * The number of computed decreases.
	 */
	private long decNum = 0;
	/**
	 * The number of real decreases.
	 */
	private long realDecNum = 0;

/**
 * MovingObjects constructor.
 * @param properties properties of the generator
 * @param time the time object
 * @param objClasses description of the external object classes
 */
public ExternalObjects (Properties properties, Time time, ExternalObjectClasses objClasses) {
	this.properties = properties;
	this.time = time;
	this.objClasses = objClasses;
	objs = new MemoryRTree();
	util.Timer.reset(7);
}
/**
 * Adds an external object to the container.
 * @param obj external object
 */
protected void add (ExternalObject obj) {
	util.Timer.start(7);
	objs.insert(obj);
	totalNum++;
	util.Timer.stop(7);
}
/**
 * Computes the decrease of speed on the given edge by external objects.
 * @return decrease in per cent
 * @param edge the edge
 */
public int computeDecrease (Edge edge) {
	util.Timer.start(7);
	decNum++;
	// if no object exists, no decrease
	if (objs.getTotalNumberOfObjects()==0) {
		util.Timer.stop(7);
		return 100;
	}
	// find intersecting external object(s) and determine highest decrease
	intersectionQuery.initWithRect (objs,new MBR(edge.getMBR()));
	int minFactor = 100;
	ExternalObject extObj = (ExternalObject)intersectionQuery.getNextObject();
	while (extObj != null) {
		int decF = objClasses.getDecreasingFactor(extObj.getObjectClass());
		if (decF < minFactor) {
			minFactor = decF;
		}
		extObj = (ExternalObject)intersectionQuery.getNextObject();
	}
	if (minFactor < 100)
		realDecNum++;
	util.Timer.stop(7);
	return minFactor;
}
/**
 * Returns the number of computed decreases.
 * @return number
 */
public long getNumOfComputedDecreases () {
	return decNum;
}
/**
 * Returns the number of real decreases.
 * @return number
 */
public long getNumOfRealDecreases () {
	return realDecNum;
}
/**
 * Returns the description of the external object classes.
 * @return object classes
 */
public ExternalObjectClasses getObjectClasses() {
	return objClasses;
}
/**
 * Returns the time object.
 * @return the time object
 */
public Time getTime () {
	return time;
}
/**
 * Returns the total number of created moving objects.
 * @return total number
 */
public int getTotalNumOfObjects () {
	return totalNum;
}
/**
 * Returns the time used for the management of external objects.
 * @return time in msec
 */
public long getUsedTime () {
	return util.Timer.get(7);
}
/**
 * Updates (moves and resizes) all external objects.
 * @param  time  the current time stamp
 * @param  gen  the generator of external object classes
 * @param  reporter  the reporter
 */
public void moveAndResizeAndRemoveObjects (int time, ExternalObjectGenerator gen, Reporter reporter) {
	util.Timer.start(7);
	allQuery.init (objs);
	// determine moving and dead objects
	ExternalObject extObj = (ExternalObject)allQuery.getNextObject();
	while (extObj != null) {
		if (!extObj.isAlive(time))
			deadObjs.addElement(extObj);
		else
			moveObjs.addElement(extObj);
		extObj = (ExternalObject)allQuery.getNextObject();
	}
	// delete dead objects from tree
	for (int i=0; i<deadObjs.size(); i++) {
		ExternalObject actObj = (ExternalObject)deadObjs.elementAt(i);
		actObj.reportDeletedObject(reporter,time);
		if (!objs.remove(actObj))
			System.err.println("ext. obj. "+actObj.getMBR()+" not deleted!");
		deadObjs.setElementAt (null,i);
	}
	deadObjs.setSize(0);
	// move and resize external objects
	for (int i=0; i<moveObjs.size(); i++) {
		ExternalObject actObj = (ExternalObject)moveObjs.elementAt(i);
		MBR oldMBR = actObj.getMBR();
		MBR mbr = gen.computeNewPositionAndSize (time,actObj);
		if (mbr != oldMBR) {
			if (! oldMBR.contains(mbr,-1))
				if (!objs.remove(actObj))
					System.err.println("ext. obj. "+mbr+" not deleted (2)!");
				else {
					actObj.setMBR(mbr);
					objs.insert(actObj);
				}
			else
				actObj.setMBR(mbr);
		}
		actObj.reportMovingObject(reporter,time);
		moveObjs.setElementAt (null,i);
	}
	moveObjs.setSize(0);
	util.Timer.stop(7);
}
/**
 * All external objects are removed from the tree into the dead object container.
 */
public void removeObjects() {
	util.Timer.start(7);
	allQuery.init (objs);
	// move and store dead objects
	ExternalObject extObj = (ExternalObject)allQuery.getNextObject();
	while (extObj != null) {
		moveObjs.addElement(extObj);
		extObj = (ExternalObject)allQuery.getNextObject();
	}
	// delete all objects from tree
	for (int i=0; i<moveObjs.size(); i++) {
		if (!objs.remove((ExternalObject)moveObjs.elementAt(i)))
			System.err.println("ext. obj. "+((ExternalObject)deadObjs.elementAt(i)).getMBR()+" not deleted!");
		moveObjs.setElementAt (null,i);
	}
	moveObjs.setSize(0);
	// reset variables
	objs = new MemoryRTree();
	util.Timer.stop(7);
}
}
