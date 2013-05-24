package generator2;

import java.util.Properties;

import routing.Nodes;
import drawables.DrawableObjects;

/**
 * Default controller applet for the computation of network-based spatiotemporal
 * datasets. generator2.PositionReporter is used. It is also possible to run
 * this class as Java application.
 * 
 * @version 2.01 03.09.2003 call of PositionGenerator, makeAbsolute
 * @version 2.00 07.07.2001 separated from the class "DataGenerator"
 * @author FH Oldenburg
 */
public class DefaultDataGenerator extends DataGenerator {

	/**
	 * Calls the constructor of EdgeClasses.
	 * 
	 * @return new created object of the class EdgeClasses
	 * @param properties
	 *            the properties of the generator
	 */
	public EdgeClasses createEdgeClasses(Properties properties) {
		return new EdgeClasses(properties);
	}

	/**
	 * Calls the constructor of ExternalObjectClasses.
	 * 
	 * @return an object of ExternalObjectClasses
	 * @param properties
	 *            properties of the generator
	 * @param time
	 *            the time object
	 * @param ds
	 *            the data space
	 * @param numOfClasses
	 *            number of external object classes
	 */
	public ExternalObjectClasses createExternalObjectClasses(
			Properties properties, Time time, DataSpace ds, int numOfClasses) {
		return new ExternalObjectClasses(properties, time, ds, numOfClasses);
	}

	/**
	 * Calls the constructor of ExternalObjectGenerator.
	 * 
	 * @return an external object generator
	 * @param properties
	 *            properties of the generator
	 * @param time
	 *            the time object
	 * @param dataspace
	 *            the dataspace
	 * @param classes
	 *            the classes of external objects
	 * @param numOfExtObjPerTime
	 *            number of external objects per time
	 * @param numAtBeginning
	 *            number of external objects at the beginning
	 */
	public ExternalObjectGenerator createExternalObjectGenerator(
			Properties properties, Time time, DataSpace dataspace,
			ExternalObjectClasses classes, int numOfExtObjPerTime,
			int numAtBeginning) {
		return new ExternalObjectGenerator(properties, time, dataspace,
				classes, numOfExtObjPerTime, numAtBeginning);
	}

	/**
	 * Calls the constructor of ObjectClasses.
	 * 
	 * @return an object of ObjectClasses
	 * @param properties
	 *            properties of the generator
	 * @param time
	 *            the time object
	 * @param ds
	 *            the data space
	 * @param numOfClasses
	 *            number of object classes
	 * @param reportProb
	 *            report probability (0-1000)
	 * @param maxSpeedDivisor
	 *            maximum speed divisor
	 */
	public ObjectClasses createObjectClasses(Properties properties, Time time,
			DataSpace ds, int numOfClasses, int reportProb, int maxSpeedDivisor) {
		return new ObjectClasses(properties, time, ds, numOfClasses,
				reportProb, maxSpeedDivisor);
	}

	/**
	 * Calls the constructor of ObjectGenerator.
	 * 
	 * @return an object generator
	 * @param properties
	 *            properties of the generator
	 * @param time
	 *            the time object
	 * @param dataspace
	 *            the dataspace
	 * @param nodes
	 *            the nodes of the network
	 * @param objClasses
	 *            description of the object classes
	 * @param numOfObjPerTime
	 *            indicator for the number of moving objects per time
	 * @param numOfObjAtBeginning
	 *            indicator for the number of moving objects at the beginning
	 */
	public ObjectGenerator createObjectGenerator(Properties properties,
			Time time, DataSpace ds, Nodes nodes, ObjectClasses objClasses,
			int numOfObjPerTime, int numOfObjAtBeginning) {
		return new ObjectGenerator(properties, time, ds, nodes, objClasses,
				numOfObjPerTime, numOfObjAtBeginning);
	}

	/**
	 * Calls the constructor of PositionReporter.
	 * 
	 * @return the reporter
	 * @param properties
	 *            properties of the generator
	 * @param objects
	 *            container of drawable objects
	 */
	public Reporter createReporter(Properties properties,
			DrawableObjects objects) {
		return new PositionReporter(properties, objects);
	}

	/**
	 * Calls the constructor of ReRoute.
	 * 
	 * @return an object of ReRoute
	 * @param properties
	 *            properties of the generator
	 * @param time
	 *            the time object
	 * @param ds
	 *            the data space
	 */
	public ReRoute createReRoute(Properties properties, Time time, DataSpace ds) {
		return new ReRoute(properties, time, ds);
	}

	/**
	 * main entrypoint - starts the part when it is run as an application
	 * 
	 * @param args
	 *            args[0] = name of the property file
	 */
	public static void main(java.lang.String[] args) {
		String requestid = args[1];
		if ((args.length > 0) && (args[0] != null)) {
			propFilename = makeAbsolute(args[0]);
		}
		main("generator2.DefaultDataGenerator", args);
	}

}
