package generator2;

import java.util.Properties;
import routing.Nodes;

/**
 * Controller applet for the computation of network-based spatiotemporal datasets.
 * The generator2.ConstantObjectGenerator is used. 
 * It is also possible to run this class as Java application.
 *
 * @version	1.00	03.09.2003	first version
 * @author FH Oldenburg
 */
public class ConstantDataGenerator extends DefaultDataGenerator {

	/**
	 * Calls the constructor of ConstantObjectGenerator.
	 * @return  the object generator
	 * @param  properties  properties of the generator
	 * @param  time  the time object
	 * @param  dataspace  the dataspace
	 * @param  nodes  the nodes of the network
	 * @param  objClasses  description of the object classes
	 * @param  numOfObjPerTime  indicator for the number of moving objects per time
	 * @param  numOfObjAtBeginning  indicator for the number of moving objects at the beginning
	 */
	public ObjectGenerator createObjectGenerator (Properties properties, Time time, DataSpace ds, Nodes nodes, ObjectClasses objClasses, int numOfObjPerTime, int numOfObjAtBeginning) {
		return new ConstantObjectGenerator (properties, time,ds, nodes, objClasses,numOfObjPerTime,numOfObjAtBeginning);
	}

	/**
	 * main entrypoint - starts the part when it is run as an application
	 * @param  args  args[0] = name of the property file
	 */
	public static void main(java.lang.String[] args) {
		if ((args.length > 0) && (args[0] != null))
			propFilename = makeAbsolute(args[0]);
		main ("generator2.ConstantDataGenerator");
	}

}
