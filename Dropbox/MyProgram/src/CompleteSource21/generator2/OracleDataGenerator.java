package generator2;

import java.util.Properties;
import drawables.DrawableObjects;

/**
 * Controller applet for the computation of network-based spatiotemporal datasets.
 * It is also possible to run this class as Java application.
 * See the additional documentation for the necessary table definitions and the supported properties in the property file.
 * Uses the Oracle database reporter which requires classes111.zip or classes12.zip in the classpath.
 *
 * @version	1.01	03.09.2003	call of makeAbsolute
 * @version	1.00	03.01.2001	first version
 * @author FH Oldenburg
 */
public class OracleDataGenerator extends DefaultDataGenerator {
/**
 * Calls the constructor of OracleReporter.
 * @return the reporter
 * @param properties properties of the generator
 * @param objects container of drawable objects
 */
public Reporter createReporter (Properties properties, DrawableObjects objects) {
	return new OracleReporter (properties,objects);
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args args[0] = name of the property file
 */
public static void main(java.lang.String[] args) {
	if ((args.length > 0) && (args[0] != null))
		propFilename = makeAbsolute(args[0]);
	DataGenerator.main ("generator2.OracleDataGenerator");
}
}
