package generator2;

import java.awt.*;
import java.util.*;

/**
 * Definition of the properties of the classes of moving objects.
 *
 * @version 2.10	13.09.2003	maxSpeed, portion and getMaxSpeed become double
 * @version 2.01	27.08.2003	using RandomGenerator
 * @version 2.00	04.09.2001	revision for generator v2.0
 * @version 1.40	07.05.2001	report probability added as parameter
 * @version 1.30	09.04.2001	maxSpeedDivisor added as parameter
 * @version 1.20	28.10.2000	report probability added
 * @version 1.10	29.04.2000	time object added
 * @version 1.00	18.01.2000	first version
 * @author FH Oldenburg
 */
public class ObjectClasses {
	
	/**
	 * The time object.
	 */
	protected Time time;

	/**
	 * The number of classes.
	 */
	protected int num = 6;
	/**
	 * Maximum speed of the classes.
	 */
	protected double maxSpeed[];
	/**
	 * The portion of the classes (sum = 1 = 100%).
	 */
	protected double portion[];
	/**
	 * The report probability of the classes (1000 = 100%).
	 */
	protected int reportProbability[];
	/**
	 * The colors.
	 */
	protected Color color[];

	/**
	 * The random generator.
	 */
	private Random random;

/**
 * ObjectClasses constructor.
 * @param  properties  properties of the generator
 * @param  time  the time object
 * @param  ds  the data space
 * @param  numOfClasses  number of object classes
 * @param  reportProb  report probability (0-1000)
 * @param  maxSpeedDivisor  maximum speed divisor
 */
public ObjectClasses (Properties properties, Time time, DataSpace ds, int numOfClasses, int reportProb, int maxSpeedDivisor) {
	this.num = numOfClasses;
	this.time = time;
	// change the following if necessary ...
	random = RandomGenerator.get(properties);
	// definition of maximum speed
	maxSpeed = new double[num];
	int dist = ds.getMaxX()-ds.getMinX()+ds.getMaxY()-ds.getMinY();
	maxSpeed[0] = dist / maxSpeedDivisor;
	for (int i=1; i<num; i++) {
		maxSpeed[i] = maxSpeed[i-1]/2;
		if (maxSpeed[i] <= 0)
			maxSpeed[i] = 0.001;
	}
	// definition of portions
	portion = new double[num];
	portion[0] = 0.5;
	for (int i=1; i<num-1; i++)
		portion[i] = portion[i-1]/2;
	if (num > 1)
		portion[num-1] = portion[num-2];
	else
		portion[0] = 1;
	// definition of report probabilities
	int prob = reportProb;
	reportProbability = new int[num];
	for (int i=0; i<num; i++)
		reportProbability[i] = prob;
	// definition of color
	color = new Color[num];
	color[0] = Color.red;
	if (num > 1) color[1] = Color.blue;
	if (num > 2) color[2] = Color.orange;
	if (num > 3) color[3] = Color.magenta;
	if (num > 4) color[4] = Color.black;
	if (num > 5) color[5] = Color.green;
	if (num > 6) color[6] = Color.pink;
	for (int i=7; i<num; i++)
		color[i] = new Color (Math.abs(RandomGenerator.getForVisualization(properties).nextInt())%16777216);
}

/**
 * Computes the object class of a new moving object.
 * @return  object class
 * @param  time  current time stamp
 */
public int computeNewObjectClass (int time) {
	double value = random.nextDouble();
	int res = num-1;
	for (int i=0; i<num; i++)
		if (value-portion[i]<0) {
			res = i;
			break;
		}
		else
			value -= portion[i];
	return res;
}

/**
 * Returns the color of the class.
 * @return color
 * @param c object class
 */
public Color getColor (int c) {
	if ((c >= 0) && (c < num))
		return color[c];
	else
		return Color.red;
}

/**
 * Returns the maximum speed of an object class.
 * @return maximum speed of class c
 * @param c object class
 */
public double getMaxSpeed (int c) {
	if ((c >= 0) && (c < num))
		return maxSpeed[c];
	else
		return 0;
}

/**
 * Returns the number of object classes.
 * @return number of object classes
 */
public int getNumber() {
	return num;
}

/**
 * Returns the report probability of an object class.
 * @return report probability (0..1000)
 * @param c object class
 */
public int getReportProbability (int c) {
	if ((c >= 0) && (c < num))
		return reportProbability[c];
	else
		return 0;
}

}