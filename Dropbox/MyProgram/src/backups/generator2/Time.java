package generator2;

import java.util.Properties;

/**
 * Representation of the time.
 * 
 * @version 2.00	04.09.01	revision for generator v2.0
 * @version 1.10	25.04.00	setTime added
 * @version 1.00	16.01.00	first version
 * @author FH Oldenburg
 */
public class Time {
	
	/**
	 * The maximum time.
	 */
	private int maxTime = 20;
	/**
	 * The current time.
	 */
	private int currTime = 0;

/**
 * Time constructor.
 * @param properties properties of the generator
 * @param maxTime the maximum time
 */
public Time (Properties properties, int maxTime) {
	this.maxTime = maxTime;
}

/**
 * Returns the current time.
 * @return current time
 */
public int getCurrTime() {
	return currTime;
}

/**
 * Returns the maximum time.
 * @return maximum time
 */
public int getMaxTime() {
	return maxTime;
}

/**
 * Increases the current time.
 */
public void increaseCurrTime() {
	if (currTime <= maxTime)
		currTime++;
}

/**
 * Returns if the maximum time is reached.
 * @return maximum time is reached?
 */
public boolean isMaximumTimeReached() {
	return currTime >= maxTime;
}

/**
 * Tests whether the time stamp is the first or not.
 * @return is first?
 * @param  time  the time stamp
 */
public static boolean isFirstTimeStamp (int time) {
	return (time == 0);
}

/**
 * Returns if the maximum time is exceeded.
 * @return maximum time is exceeded?
 */
public boolean isMaximumTimeExceeded() {
	return currTime > maxTime;
}

/**
 * Resets the current time.
 */
public void reset() {
	currTime = 0;
}

/**
 * Sets the current to a new value.
 * @param newCurrTime new current time
 */
protected void setCurrTime (int newCurrTime) {
	currTime = newCurrTime;
}

}