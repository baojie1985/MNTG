package generator2;

import java.util.*;
import spatial.MBR;

/**
 * Class for creating and modifying external objects.
 * 
 * @version 2.11	27.08.2003	using RandomGenerator
 * @version 2.10	06.10.2002	use of MBR
 * @version 2.00	04.09.2001	revision for generator v2.0
 * @version 1.00	29.04.2000	first version
 * @author FH Oldenburg
 */
 
public class ExternalObjectGenerator {
	
	/**
	 * The counter for the identifiers.
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
	 * Dataspace of the network.
	 */
	protected DataSpace dataspace;
	/**
	 * Classes of external objects.
	 */
	protected ExternalObjectClasses classes;

	/**
	 * Number of generated external objects per time.
	 */
	protected int numOfExtObjPerTime = 1;
	/**
	 * Number of external objects at the beginning.
	 */
	protected int numOfExtObjAtBeginning = 0;

	/**
	 * Random generator.
	 */
	protected Random random;

/**
 * Constructor.
 * @param properties properties of the generator
 * @param time the time object
 * @param dataspace the dataspace
 * @param classes the classes of external objects
 * @param numOfExtObjPerTime number of external objects per time
 * @param numOfExtObjAtBeginning number of external objects at the beginning
 */
public ExternalObjectGenerator (Properties properties, Time time, DataSpace dataspace, ExternalObjectClasses classes, int numOfExtObjPerTime, int numOfExtObjAtBeginning) {
	// initialize variables
	this.properties = properties;
	this.time = time;
	this.dataspace = dataspace;
	this.classes = classes;
	random = RandomGenerator.get(properties);
	this.numOfExtObjPerTime = numOfExtObjPerTime;
	this.numOfExtObjAtBeginning = numOfExtObjAtBeginning;
}
/**
 * Computes a new external object.
 * @return the new object
 * @param time the time stamp
 */
public ExternalObject computeExternalObject (int time) {
	// to be changed ...
	int objClass = classes.computeNewExternalObjectClass (time);
	int dx = dataspace.getMaxX()-dataspace.getMinX();
	int dy = dataspace.getMaxY()-dataspace.getMinY();
	int x = Math.abs(random.nextInt())%(dx+1) + dataspace.getMinX();
	int y = Math.abs(random.nextInt())%(dy+1) + dataspace.getMinY();
	int extX = Math.abs(random.nextInt())%classes.getExtension(objClass,0)+1;
	int extY = Math.abs(random.nextInt())%classes.getExtension(objClass,1)+1;
	return new ExternalObject (currId++,time,classes.getLifetime(objClass),objClass,x,y,extX,extY);
}
/**
 * Computes the new position and size of an external object.
 * @return the mbr of the external object if no change has occured or
 *         a new created rectangle with the new position and/or size
 * @param time actual time
 * @param obj the external object
 */
public MBR computeNewPositionAndSize (int time, ExternalObject obj) {
	// to be changed ...
	MBR mbr = obj.getMBR();
//	if (Math.abs(random.nextInt())%20 == 0) {
		int[] delta = new int[2];
		delta[MBR.X] = (dataspace.getMaxX()-dataspace.getMinX()) / 500;
		delta[MBR.Y] = (dataspace.getMaxY()-dataspace.getMinY()) / 500;
		MBR newMBR = (MBR) mbr.clone();
		newMBR.translate(delta);
		newMBR.grow(delta);
		return newMBR;
//	}
//	else
//		return mbr;
}
/**
 * Returns whether external objects exist or not.
 * @return exist?
 */
public boolean externalObjectsExist () {
	return (numOfExtObjPerTime > 0) || (numOfExtObjAtBeginning > 0);
}
/**
 * Returns the number of new external objects at a time stamp.
 * @return number of objects
 * @param time the time stamp
 */
public int numberOfNewObjects (int time) {
	// may be changed ...
	if (Time.isFirstTimeStamp(time))
		return numOfExtObjAtBeginning;
	else
		return numOfExtObjPerTime;
}
}
