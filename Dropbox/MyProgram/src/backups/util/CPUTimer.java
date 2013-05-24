package util;

/**
 * Timers for measuring the CPU time.
 * 
 * @version 1.00	01.04.00	first version
 * @author Thomas Brinkhoff
 */
public class CPUTimer {

	/**
	 * Maximum number of timers.
	 */
	static final private long NOTSTARTED = -1;

	/**
	 * Measured time.
	 */
	private long time = 0;
	/**
	 * Start time.
	 */
	private long start = NOTSTARTED;
	
/**
 * Returns the measured time.
 * @return time in milliseconds
 */
public long get() {
	return time;
}
/**
 * Resets the timer.
 */
public void reset () {
	time = 0;
	start = NOTSTARTED;
}
/**
 * Sets the timer.
 * @param value value of time
 */
public void set (long value) {
	time = value;
}
/**
 * Starts the timer.
 */
public void start () {
	if (start == NOTSTARTED)
		start = System.currentTimeMillis();
}
/**
 * Stops the timer.
 * @return time in milliseconds
 */
public long stop () {
	if (start != NOTSTARTED)
		time += System.currentTimeMillis()-start;
	start = NOTSTARTED;
	return time;
}
}
