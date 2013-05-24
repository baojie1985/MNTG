package generator2;

import java.awt.*;
import java.util.Properties;

/**
 * Definition of the properties of the classes of the network edges.
 *
 * @version 2.01	19.08.2003	capacities reduced, minimum scales modified
 * @version 2.00	04.09.2001	revision for generator v2.0
 * @version 1.20	07.05.2001	maxSpeedDivisor added
 * @version 1.10	30.04.2000	announceDataspace added
 * @version 1.00	18.01.2000	first version
 * @author FH Oldenburg
 */
public class EdgeClasses {

	/**
	 * The properties of the generator.
	 */
	protected Properties properties = null;

	/**
	 * The time object.
	 */
	protected Time time = null;

	/**
	 * Number of edge classes.
	 */
	protected int num = 0;
	/**
	 * Initial maximum speed of the classes.
	 */
	protected int maxSpeedDivisor = 1;
	/**
	 * Maximum speed of the classes.
	 */
	protected int maxSpeed[] = null;
	/**
	 * Capacity of the classes.
	 */
	protected int capacity[] = null;
	/**
	 * Minimum scales for depicting the edge classes.
	 */
	protected int minScale[] = null;
	/**
	 * Color of the edge classes.
	 */
	protected Color color[] = null;

/**
 * EdgeClasses constructor. Is called by the init method of the DataGenerator.
 * Further attributes are announced by the method "announce".
 * @param properties properties of the generator
 */
public EdgeClasses (Properties properties) {
	// the following code may be modified
	this.properties = properties;
	this.num = 8;
	// maximum speed
	maxSpeed = new int[num]; // the values in the array are calculated in the method announceDataspace
	// maximum capacity
	capacity = new int[num];
	capacity[0] = 5;
	capacity[1] = 5;
	capacity[2] = 4;
	capacity[3] = 4;
	capacity[4] = 3;
	capacity[5] = 3;
	capacity[6] = 2;
	capacity[7] = 2;
	// scale for depicting edges
	minScale = new int[num];
	minScale[0] = 327680;
	minScale[1] = 81920;
	minScale[2] = 10240;
	minScale[3] = 1280;
	minScale[4] = 320;
	minScale[5] = 160;
	minScale[6] = 80;
	minScale[7] = 80;
	// color for depicting edges
	color = new Color[num];
	color[0] = Color.blue;
	color[1] = Color.red/*cyan*/;
	color[2] = Color.orange/*darkGray*/;
	color[3] = Color.gray;
	color[4] = Color.cyan/*lightGray*/;
	color[5] = Color.green/*lightGray*/;
	color[6] = Color.lightGray;
	color[7] = Color.lightGray;
}
/**
 * Announces the time object, the dataspace and the divisor of maximum speed to the object.
 * This method must be called before the function getMaxSpeed is called.
 * @param time the time object
 * @param ds the dataspace
 * @param maxSpeedDivisor divisor of maximum speed (10=fast, 50=middle, 250=slow)
 */
public void announce (Time time, DataSpace ds, int maxSpeedDivisor) {
	this.time = time;
	this.maxSpeedDivisor = maxSpeedDivisor;
	// compute max speed; code may be modified
	int dist = ds.getMaxX()-ds.getMinX()+ds.getMaxY()-ds.getMinY();
	maxSpeed[0] = dist / maxSpeedDivisor;
	for (int i=1; i<num; i++)
		maxSpeed[i] = 2*maxSpeed[i-1]/3;
}
/**
 * Computes the decelerated speed of an edge class depending on the usage and the capacity
 * of the edge.
 * @return decelerated speed
 * @param c edge class
 * @param edgeUsage usage of the edge
 */
public int deceleratedSpeed (int c, int edgeUsage) {
	//return maxSpeed[c];   // <- for no speed deceleration
	if ((c >= 0) && (c < num))
		if (edgeUsage <= capacity[c])
			return maxSpeed[c];
		else {
			// the computation of the decreased speed may be changed
			int speed = maxSpeed[c];
			if ((speed > 1) && (edgeUsage-capacity[c] > 0)) {
				edgeUsage -= capacity[c];
				speed = speed / 2;
			}
			if ((speed > 1) && (edgeUsage-capacity[c] > 0)) {
				edgeUsage -= capacity[c];
				speed = speed / 2;
			}
			return speed;
		}
	else
		return 0;
}
/**
 * Returns the capacity of an edge class.
 * @return capacity of class c
 * @param c edge class
 */
public int getCapacity (int c) {
	if ((c >= 0) && (c < num))
		return capacity[c];
	else
		return 0;
}
/**
 * Returns the color for depicting an edge class.
 * @return color of class c
 * @param c edge class
 */
public Color getColor (int c) {
	if ((c >= 0) && (c < num))
		return color[c];
	else
		return Color.gray;
}
/**
 * Returns the maximum speed of an edge class.
 * @return maximum speed of class c
 * @param c edge class
 */
public int getMaxSpeed (int c) {
	if ((c >= 0) && (c < num))
		return maxSpeed[c];
	else
		return 0;
}
/**
 * Returns the maximum speed divisor.
 * @return maximum speed divisor
 */
public int getMaxSpeedDivisor () {
	return maxSpeedDivisor;
}
/**
 * Returns the minimum scale for depicting an edge class.
 * @return scale of class c
 * @param c edge class
 */
public int getMinScale (int c) {
	if ((c >= 0) && (c < num))
		return minScale[c];
	else
		return 0;
}
/**
 * Returns the number of edge classes.
 * @return number of edge classes
 */
public int getNumber() {
	return num;
}
}