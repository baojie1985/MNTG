package generator2;

import java.util.Properties;
import java.util.Random;

/**
 * Class for providing the random generator.
 * 
 * @version 1.00	27.08.2003	first version
 * @author FH Oldenburg
 */
public class RandomGenerator {

	/**
	 * The random generator for the data generation.
	 */
	static protected Random r = null;
	/**
	 * The random generator for reporting.
	 */
	static protected Random rep = null;
	/**
	 * The random generator for visualization.
	 */
	static protected Random viz = null;
	
	/**
	 * Returns the random generator for data generation.
	 * @param  properties  the properties of the generator
	 */
	static public Random get (Properties properties) {
		if (r != null)
			return r;
		if (properties.getProperty ("seed") != null)
			try {
				r = new Random(Long.parseLong(properties.getProperty ("seed")));
			} catch (Exception ex) {
				r = new Random();
			}
		else
			r = new Random();
		return r;
	}

	/**
	 * Returns the random generator for reporting.
	 * @param  properties  the properties of the generator
	 */
	static public Random getForReport (Properties properties) {
		if (rep != null)
			return rep;
		if (properties.getProperty ("seed") != null)
			try {
				rep = new Random(Long.parseLong(properties.getProperty ("seed")));
			} catch (Exception ex) {
				rep = new Random();
			}
		else
			rep = new Random();
		return rep;
	}

	/**
	 * Returns the random generator for visualization and other (unimportant) purposes.
	 * @param  properties  the properties of the generator
	 */
	static public Random getForVisualization (Properties properties) {
		if (viz != null)
			return viz;
		if (properties.getProperty ("seed") != null)
			try {
				viz = new Random(Long.parseLong(properties.getProperty ("seed")));
			} catch (Exception ex) {
				viz = new Random();
			}
		else
			viz = new Random();
		return viz;
	}

}
