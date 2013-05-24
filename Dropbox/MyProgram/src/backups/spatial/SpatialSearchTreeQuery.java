package spatial;

/**
 * Interface for objects performing a general region query on search trees.
 * 
 * @version 1.00	21.08.2003	first version
 * @author Thomas Brinkhoff
 */
public interface SpatialSearchTreeQuery {
	
	/**
	 * Returns the next object fulfilling the query condition.
	 * Will return null if no object is found.
	 * @return  the found object
	 */
	SpatialSearchTreeObject getNextObject();
	
	/**
	 * Returns the number of queries.
	 * @return  number of queries
	 */
	int getQueryNum();
	
	/**
	 * Returns the query time.
	 * @return  query time
	 */
	long getQueryTime();

	/**
	 * Returns the number of results.
	 * @return number of results
	 */
	int getResultNum();
	
	/**
	 * Initializes a query without spatial conditions.
	 * @param  tree  the searchtree
	 */
	void init (SpatialSearchTree tree);
	
	/**
	 * Resets the query.
	 */
	void reset();
	
	/**
	 * Resets the counters and timers.
	 */
	void resetCounters();
	
}