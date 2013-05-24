package generator2;

import java.util.Properties;
import drawables.DrawableObjects;

/**
 * Controller applet for the computation of network-based spatiotemporal datasets.
 * generator2.NodeReporter is used. 
 * It is also possible to run this class as Java application.
 *
 * @version	1.00	03.09.2003	first version
 * @author FH Oldenburg
 */
public class NodeDataGenerator extends DefaultDataGenerator {

	/**
	 * Calls the constructor of NodeReporter.
	 * @return  the reporter
	 * @param  properties  properties of the generator
	 * @param  objects  container of drawable objects
	 */
	public Reporter createReporter (Properties properties, DrawableObjects objects) {
		return new NodeReporter (properties,objects);
	}

	/**
	 * main entrypoint - starts the part when it is run as an application
	 * @param  args  args[0] = name of the property file
	 */
	public static void main(java.lang.String[] args) {
		if ((args.length > 0) && (args[0] != null))
			propFilename = makeAbsolute(args[0]);
		main ("generator2.NodeDataGenerator");
	}

}
