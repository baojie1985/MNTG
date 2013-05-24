package generator2;

import java.awt.*;
import java.util.*;

/**
 * Definition of the properties of the classes of external objects.
 * 
 * @version 2.01	27.08.2003	using RandomGenerator
 * @version 2.00	04.09.2001	revision for generator v2.0
 * @version 1.00	29.04.2000	first version
 * @author FH Oldenburg
 */
public class ExternalObjectClasses {

	/**
	 * The properties of the generator.
	 */
	protected Properties properties;

	/**
	 * The time object.
	 */
	protected Time time;
	/**
	 * The dataspace.
	 */
	protected DataSpace ds;

	/**
	 * The number of classes.
	 */
	protected int num = 3;
	/**
	 * Lifetime of the classes.
	 */
	protected int lifetime[];
	/**
	 * Extension of the classes in per cent of the data space *100.
	 */
	protected int extension[];
	/**
	 * Decreasing factors of the classes in per cent.
	 */
	protected int decreasingFactor[];
	/**
	 * Sum of all portions.
	 */
	protected final int PORTIONSUM = 1000;
	/**
	 * Portion of the classes.
	 */
	protected int portion[];
	/**
	 * Portion of the classes at the beginning.
	 */
	protected int beginPortion[];
	/**
	 * The colors.
	 */
	protected Color color[];
	
	/**
	 * Random generator
	 */
	protected Random random;

/**
 * ObjectClasses constructor.
 * @param properties properties of the generator
 * @param time the time object
 * @param ds data space
 * @param numOfClasses number of object classes
 */
public ExternalObjectClasses (Properties properties, Time time, DataSpace ds, int numOfClasses) {
	this.num = numOfClasses;
	this.properties = properties;
	this.time = time;
	this.ds = ds;
	random = RandomGenerator.get(properties);
	if (num == 0)
		return;
	// change here ...
	// definition of extension
	extension = new int[num];
	extension[num-1] = (ds.getMaxX()-ds.getMinX()+ds.getMaxY()-ds.getMinY()) / 5;
	for (int i=num-2; i>=0; i--)
		extension[i] = extension[i+1]/2;
	// definition of lifetime
	lifetime = new int[num];
	lifetime[num-1] = 10;
	for (int i=num-2; i>=0; i--)
		lifetime[i] = 10;
	// definition of decreasing factors
	decreasingFactor = new int[num];
	decreasingFactor[0] = 64;
	for (int i=1; i<num; i++)
		if (decreasingFactor[i-1] > 8)
			decreasingFactor[i] = decreasingFactor[i-1]/2;
		else
			decreasingFactor[i] = decreasingFactor[i-1];
	// definition of portions
	portion = new int[num];
	portion[0] = PORTIONSUM/2;
	for (int i=1; i<num-1; i++)
		portion[i] = portion[i-1]/2;
	if (num > 1)
		portion[num-1] = portion[num-2];
	// definition of portions at the beginning
	beginPortion = new int[num];
	for (int i=0; i<num; i++)
		beginPortion[i] = PORTIONSUM/num;
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
 * Computes the object class of a new external object.
 * @return object class
 * @param time time stamp
 */
public int computeNewExternalObjectClass (int time) {
	int value = Math.abs(random.nextInt())%PORTIONSUM;
	int[] p = portion;
	if (time == 0)
		p = beginPortion;
	for (int i=0; i<num; i++)
		if (value-p[i]<0)
			return i;
		else
			value -= p[i];
	return num-1;
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
 * Returns the factor of the class for the decrease of speed.
 * @return decreasing factor in per cent
 * @param c object class
 */
public int getDecreasingFactor (int c) {
	if ((c >= 0) && (c < num)) {
		return decreasingFactor[c];
	}
	else
		return 100;
}

/**
 * Returns the (maximum) extension of the class.
 * @return extension in per cent of the data space * 100
 * @param c object class
 * @param dim dimension (0 = x, 1 = y)
 */
public int getExtension (int c, int dim) {
	if ((c >= 0) && (c < num))
		return extension[c];
	else
		return 0;
}

/**
 * Returns the (maximum) life time of the class.
 * @return life time
 * @param c object class
 */
public int getLifetime (int c) {
	if ((c >= 0) && (c < num))
		return lifetime[c];
	else
		return 1;
}

/**
 * Returns the number of object classes.
 * @return number of object classes
 */
public int getNumber() {
	return num;
}

}