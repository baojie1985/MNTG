package index;

import util.*;

/**
 * Basic class for queries
 * 
 * @version 1.20	09.07.2003	MOVE removed
 * @version 1.10	09.07.2000	changed package, update values added
 * @version 1.00	12.04.2000	first version
 * @author Thomas Brinkhoff
 */
public class Query {

	/**
	 * all layers.
	 */
	public static final int ALLLAYERS = -1;
	/**
	 * all scales.
	 */
	public static final int ALLSCALES = -1;
	/**
	 * normal query.
	 */
	public static final int QUERY = 0;
	/**
	 * remove exact.
	 */
	public static final int REMOVEEXACT = 1;
	/**
	 * remove all.
	 */
	public static final int REMOVEALL = 2;
	/**
	 * update exact.
	 */
	public static final int UPDATEEXACT = 5;
	/**
	 * update all.
	 */
	public static final int UPDATEALL = 6;

	/**
	 * number of queries.
	 */
	protected int queryNum = 0;
	/**
	 * number of results.
	 */
	protected int resultNum = 0;
	/**
	 * query time.
	 */
	protected CPUTimer timer = new CPUTimer();

/**
 * Returns the number of queries.
 * @return number of queries
 */
public int getQueryNum () {
	return queryNum;
}
/**
 * Returns the query time.
 * @return query time
 */
public long getQueryTime () {
	return timer.get();
}
/**
 * Returns the number of results.
 * @return number of results
 */
public int getResultNum () {
	return resultNum;
}
/**
 * Resets the counters and timers.
 */
public void resetCounters() {
	queryNum = 0;
	resultNum = 0;
	timer.reset();
}
/**
 * Sets the counters and timers.
 * @param queryNum number of queries
 * @param resultNum numer of results
 * @param time query time in msec
 */
public void setCounters (int queryNum, int resultNum, long time) {
	this.queryNum = queryNum;
	this.resultNum = resultNum;
	this.timer.set(time);
}
}
