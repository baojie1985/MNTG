package generator2;

import java.io.*;
import java.util.Arrays;
import java.util.Properties;
import java.util.Vector;

import drawables.DrawableObjects;

/**
 * Class for reporting the passed nodes into a file.
 * Requires SDK 1.2 or higher.
 *
 * @version 1.00	27.08.2003	first version
 * @author FH Oldenburg
 */
public class NodeReporter extends Reporter {

	/**
	 * Internal class for representing report objects.
	 */
	protected class ReportObject implements Comparable {

		protected byte action;	// codes the action of the object
		protected long id;	// object id
		protected int objClass;	// object class
		protected double time;	// report time
		protected int x1;	// current x-coordinate
		protected int y1;	// current y-coordinate
		protected double speed;	// current speed
		protected int x2;	// next x-coordinate
		protected int y2;	// next y-coordinate

		// constructor:
		protected ReportObject (byte action, long id, int objClass, double time, int x1, int y1, double speed, int x2, int y2) {
			this.action = action;
			this.id = id;
			this.objClass = objClass;
			this.time = time;
			this.x1 = x1;
			this.y1 = y1;
			this.speed = speed;
			this.x2 = x2;
			this.y2 = y2;
		}

		// compare method
		public int compareTo (Object obj) {
			try {
				ReportObject o = (ReportObject)obj;
				if (time < o.time)
					return -1;
				else
					return 1; 
			} catch (Exception ex) {
				return 1;
			}
		}

		// output method
		protected void print (PrintWriter out) {
			if (out == null)
				return;
			out.print(action); out.print('\t');
			out.print(id); out.print('\t');
			out.print(objClass); out.print('\t');
			out.print(time); out.print('\t');
			out.print(x1); out.print('\t');
			out.print(y1); out.print('\t');
			out.print(speed); out.print('\t');
			out.print(x2); out.print('\t');
			out.println(y2);
		}

		// output method
		protected void print (DataOutputStream out) {
			if (out == null)
				return;
			try {
				out.writeByte(action);
				out.writeLong(id);
				out.writeInt(objClass);
				out.writeDouble(time);
				out.writeInt(x1); 
				out.writeInt(y1);
				out.writeDouble(speed);
				out.writeInt(x2); 
				out.writeInt(y2);
			} catch (Exception ex) {
			}
		}
	}

	/**
	 * The data output stream.
	 */
	protected DataOutputStream dOut = null;
	/**
	 * The print writer.
	 */
	protected PrintWriter pOut = null;
	/**
	 * Vector for storing report objects.
	 */
	protected Vector repObjs = new Vector();
	
/**
 * Constructor of the reporter.
 * @param  properties  properties of the generator
 * @param  objects  container of drawable objects
 */
public NodeReporter (Properties properties, DrawableObjects objects) {
	super (properties,objects);
	String name = properties.getProperty("outputFile");
	if (name != null)
		try {
			if (name.endsWith(".mof"))
				this.dOut = new DataOutputStream(new FileOutputStream(name));
			else
				this.pOut = new PrintWriter(new FileOutputStream(name));
		}
		catch (Exception ioe) {
			System.err.println("Error occured by creating the output file "+name);
		}
}

/**
 * Closes the reporter.
 */
public void close() {
	try {
		reportObjects();
		if (dOut != null)
			dOut.close();
		if (pOut != null)
			pOut.close();
	} catch (Exception ex) {
	}
}

/**
 * Reports the characteristic properties of a dispappering object at a time stamp
 * @param  time  the arrival time (with fraction)
 * @param  id  object id
 * @param  repNum  report number
 * @param  objClass  object class
 * @param  x  x-coordinate
 * @param  y  y-coordinate
 * @param  doneDist  the distance since the last reporting
 * @param  reportProbability  value between (0..1000)
 */
public void reportDisappearingObject (double time, long id, int repNum, int objClass, int x, int y, double doneDist, int reportProbability) {
	if (reportProbability > 0) {
		repObjs.add(new ReportObject(DEL_OBJECT,id,objClass,time,x,y,0.0,x,y));
	}
}

/**
 * Does nothing.
 * Can be modified for reporting the coordinates of a traversed edge if the report probability > 0.
 * @param  time  time stamp (with fraction) when the edge is entried
 * @param  objId  the id of the moving object id
 * @param  edgeRepNum  the edge report number 
 * @param  objClass  object class
 * @param  edgeId  the edge id
 * @param  edgeClass  the edge class
 * @param  x1  the first x-coordinate
 * @param  y1  the first y-coordinate
 * @param  speed  current speed
 * @param  x2  the second x-coordinate
 * @param  y2  the second y-coordinate
 * @param  reportProbability (0..1000)
 */
public void reportEdge (double time, long objId, int edgeRepNum, int objClass, long edgeId, int edgeClass, int x1, int y1, double speed, int x2, int y2, int reportProbability) {
	if (reportProbability > 0) {
		repObjs.add(new ReportObject(edgeRepNum==1 ? NEW_OBJECT : MOVE_OBJECT,
		                             objId,objClass,time,x1,y1,speed,x2,y2));
		if (edgeRepNum == 1)
			reportObjects();
	}
}

/**
 * Reports the characteristic properties of a moving object at a time stamp
 * according to its report probability.
 * @return  new report number
 * @param  time  time stamp
 * @param  id  object id
 * @param  repNum  report number
 * @param  objClass  object class
 * @param  x  x-coordinate
 * @param  y  y-coordinate
 * @param  speed  current speed
 * @param  doneDist  the distance since the last reporting
 * @param  nextNodeX  x-coordinate of the next node
 * @param  nextNodeY  y-coordinate of the next node
 * @param  reportProbability  value between (0..1000)
 */
public int reportMovingObject (int time, long id, int repNum, int objClass, double x, double y, double speed, double doneDist, int nextNodeX, int nextNodeY, int reportProbability) {
	return super.reportMovingObject (time,id,repNum,objClass,x,y,speed,doneDist,nextNodeX,nextNodeY,reportProbability);
}

/**
 * Reports an new moving object if its report probability > 0.
 * @param  time  time stamp
 * @param  id  object id
 * @param  objClass  object class
 * @param  x  x-coordinate of start
 * @param  y  y-coordinate of start
 * @param  speed  current speed
 * @param  nextNodeX  x-coordinate of the next node
 * @param  nextNodeY  y-coordinate of the next node
 * @param  reportProbability  value between (0..1000)
 */
public int reportNewMovingObject (int time, long id, int objClass, int x, int y, double speed, int nextNodeX, int nextNodeY, int reportProbability) {
	return super.reportNewMovingObject(time,id,objClass,x,y,speed,nextNodeX,nextNodeY,reportProbability);
}

/**
 * Reports the objects from the "repObjs" vector.
 */
protected void reportObjects() {
	if (repObjs.isEmpty())
		return;
	// case: more than one object -> sort
	Object[] ro = repObjs.toArray();
	if (ro.length > 1)
		Arrays.sort(ro);
	for (int i=0; i<ro.length; i++) {
		((ReportObject)ro[i]).print(dOut);
		((ReportObject)ro[i]).print(pOut);
	}
	repObjs.clear();
}

}