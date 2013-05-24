package generator2;

import java.awt.Rectangle;
import java.util.*;

import drawables.*;

/**
 * Abstract class for reporting the computed moving objects.
 * Non-abstract subclasses are generator2.DefaultReporter and generator2.OracleReporter.
 *
 * @version 2.10	27.08.2003	additional parameters reported, use of RandomGenerator
 * @version 2.00	06.07.2001	complete revision
 * @version 1.50	15.06.2001	report of external objects added
 * @version 1.40	28.10.2000	report number and probability added
 * @version 1.30	10.10.2000	report of new/disappearing object and edges added
 * @version 1.20	21.04.2000	report of real numbers
 * @version 1.10	01.02.2000	new order of reporting
 * @version 1.00	18.01.2000	first version
 * @author FH Oldenburg
 */
public class Reporter {

	/**
	 * Action code "new object".
	 */
	public static final byte NEW_OBJECT = 0;
	/**
	 * Action code "moving object".
	 */
	public static final byte MOVE_OBJECT = 1;
	/**
	 * Action code "disappering object".
	 */
	public static final byte DEL_OBJECT = 2;

	/**
	 * Properties of the generator.
	 */
	protected Properties properties = null;
	/**
	 * Should the objects be visualized?
	 */
	protected boolean visualize = false;
	/**
	 * Name of property indicating a visualization.
	 */
	public static final String VIZ = "VIZ";

	/**
	 * Layer of the drawable objects.
	 */
	protected static final int SYMBOLLAYER = 4;
	/**
	 * Container of drawable objects.
	 */
	protected DrawableObjects objects = null;

	/**
	 * Number of reported points.
	 */
	protected int numOfPoints = 0;
	/**
	 * Number of reported edges.
	 */
	protected int numOfEdges = 0;

	/**
	 * The random generator.
	 */
	protected Random random;

/**
 * Reporter constructor.
 * Must be called by constructors of subclasses.
 * @param  properties  properties of the generator
 * @param  objects  container of drawable objects
 */
public Reporter (Properties properties, DrawableObjects objects) {
	this.properties = properties;
	this.objects = objects;
	this.visualize = properties.getProperty (VIZ) != null;
	this.random = RandomGenerator.getForReport(properties);
}

/**
 * Does nothing.
 * Can be overwritten for closing the reporter.
 */
public void close() {
}

/**
 * Returns the number of reported edges.
 * @return number of reported edges
 */
public int getNumberOfReportedEdges() {
	return numOfEdges;
}

/**
 * Returns the number of reported points.
 * @return number of reported points
 */
public int getNumberOfReportedPoints() {
	return numOfPoints;
}

/**
 * Removes the reported moving objects.
 */
public void removeReportedObjects() {
	if (visualize && (objects != null))
		objects.removeAllObjectsOfLayer(SYMBOLLAYER);
}

/**
 * Does nothing.
 * Can be overwritten for reporting a disappearing external object.
 * @param time time stamp
 * @param id object id
 * @param repNum report number
 * @param objClass object class
 * @param rect the area
 */
public void reportDisappearingExternalObject (int time, long id, int repNum, int objClass, Rectangle rect) {
}

/**
 * Does nothing.
 * Should be overwritten for reporting the characteristic properties of a dispappering object at a time stamp
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
}

/**
 * Reports a double with an explaining text on the standard output.
 * @param text explaining text
 * @param value output value
 */
public void reportDouble (String text, double value) {
	System.out.println (text+value);
}

/**
 * Does nothing.
 * Can be overwritten for reporting the coordinates of a traversed edge if the report probability > 0.
 * @param  time  time stamp (with fraction) when the edge is entried
 * @param  objId  the id of the moving object id
 * @param  edgeRepNum  the edge report number 
 * @param  objClass  object class
 * @param  edgeId  the edge id
 * @param  edgeClass  the edge class
 * @param  x1  the first (= current) x-coordinate
 * @param  y1  the first (= current) y-coordinate
 * @param  speed  current speed
 * @param  x2  the second (= later) x-coordinate
 * @param  y2  the second (= later) y-coordinate
 * @param  reportProbability (0..1000)
 */
public void reportEdge (double time, long objId, int edgeRepNum, int objClass, long edgeId, int edgeClass, int x1, int y1, double speed, int x2, int y2, int reportProbability) {
}

/**
 * Reports an integer number with an explaining text on the stardard output.
 * @param text explaining text
 * @param value output value
 */
public void reportInt (String text, long value) {
	System.out.println (text+value);
}

/**
 * Reports a moving external object.
 * @param time time stamp
 * @param id object id
 * @param repNum report number
 * @param objClass object class
 * @param rect the area
 */
public void reportMovingExternalObject (int time, long id, int repNum, int objClass, Rectangle rect) {
	if (visualize && (objects != null))
		visualizeExternalObject (rect,objClass,time);
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
	if (reportProbability == 0)
		return repNum;
	if (Math.abs(random.nextInt())%1000 >= reportProbability)
		return repNum;
	numOfPoints++;
	repNum++;
	if (visualize && (objects != null))
		visualizeMovingObject ((int)x,(int)y,objClass,time);
	return repNum;
}

/**
 * Reports a new external object.
 * @param time time stamp
 * @param id object id
 * @param objClass object class
 * @param rect the area
 */
public void reportNewExternalObject (int time, long id, int objClass, Rectangle rect) {
	if (visualize && (objects != null))
		visualizeExternalObject (rect,objClass,time);
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
	int repNum = 0;
	if (reportProbability > 0) {
		numOfPoints++;
		repNum++;
		if (visualize && (objects != null))
			visualizeMovingObject (x,y,objClass,time);
	}
	return repNum;
}

/**
 * Visualizes an external object.
 * @param rect the object as rectangle
 * @param objClass object class
 * @param time time stamp
 */
protected void visualizeExternalObject (Rectangle rect, int objClass, int time) {
	DrawableRectangle area = new DrawableRectangle (rect.x,rect.y,rect.x+rect.width,rect.y+rect.height,false,"Rectangle"+objClass+"-"+time);
	area.setLayer(SYMBOLLAYER);
	objects.addDrawable(area);
}

/**
 * Visualizes a moving object.
 * @param x x
 * @param y x
 * @param objClass object class
 * @param time time stamp
 */
protected void visualizeMovingObject (int x, int y, int objClass, int time) {
	DrawableSymbol symbol = new DrawableSymbol (x,y,"Point"+objClass+"-"+time);
	symbol.setLayer(SYMBOLLAYER);
	objects.addDrawable(symbol);
}

}