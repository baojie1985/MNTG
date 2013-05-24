package generator2;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import routing.BorderHeap;
import routing.Edge;
import routing.Edges;
import routing.Network;
import routing.Node;
import routing.Nodes;
import showmap.LoadDrawables;
import showmap.ShowNetworkMap;
import spatial.MemoryRTree;
import util.ColorDefiner;
import drawables.DrawableObjects;
import drawables.DrawableObjectsWithSearchTree;
import drawables.DrawablePresentation;
import drawables.DrawableSymbol;

/**
 * Abstract controller applet for the computation of network-based
 * spatiotemporal datasets. The abstract methods allow to use user-defined
 * classes determining the bahavior of the generator. Non-abtract subclasses are
 * the classes generator2.DefaultDataGenerator, generator2.OracleDataGenerator,
 * and generator2.OracleSpatialDataGenerator. It is also possible to run these
 * subclasses as Java applications. See the additional documentation for the
 * supported properties in the property file.
 * 
 * @version 2.10 19.08.2003 considering null routes, tuned
 * @version 2.00 04.09.2001 complete revision
 * @version 1.22 15.06.2001 report of external objects added
 * @version 1.21 07.06.2001 compute() and compute(Reporter) separated, mode
 *          protected
 * @version 1.20 07.05.2001 textfields for msd & report probability added
 * @version 1.12 11.04.2001 waittime, maxSpeedDivisor introduced, adapted to
 *          changed classes
 * @version 1.11 01.06.2000 changed creation of container with drawable objects
 * @version 1.10 30.04.2000 support of external objects, reporting improved
 * @version 1.00 16.01.2000 first version
 * @author FH Oldenburg
 */
public abstract class DataGenerator extends ShowNetworkMap {

	/**
	 * Properties of the data generator
	 */
	protected Properties properties = new Properties();
	/**
	 * Name of the property file
	 */
	protected static String propFilename = "properties.txt";

	/**
	 * Current displayed time (0 = all times)
	 */
	protected int actTime = 0;

	/**
	 * The time object.
	 */
	protected Time time = null;
	/**
	 * The data space.
	 */
	protected DataSpace dataspace = null;
	/**
	 * The edge classes.
	 */
	protected EdgeClasses edgeClasses = null;
	/**
	 * The classes of the moving objects.
	 */
	protected ObjectClasses objClasses = null;
	/**
	 * The external objects.
	 */
	protected ExternalObjects extObjects = null;
	/**
	 * The classes of external objects.
	 */
	protected ExternalObjectClasses extObjClasses = null;
	/**
	 * The reporter.
	 */
	protected Reporter reporter = null;

	/**
	 * Property file error
	 */
	public static final int PROPERTY_FILE_ERROR = -1;
	/**
	 * Application start error
	 */
	public static final int APPLICATION_START_ERROR = -2;
	/**
	 * Network file error
	 */
	public static final int NETWORKFILE_ERROR = -3;

	/**
	 * Maximum value for number of external objects at the beginning
	 */
	public static int MAX_EXTOBJBEGIN = 100;
	/**
	 * Maximum value for number of external object classes
	 */
	public static int MAX_EXTOBJCLASSES = 10;
	/**
	 * Maximum value for number of external objects per time
	 */
	public static int MAX_EXTOBJPERTIME = 10;
	/**
	 * Maximum value for maximum time
	 */
	public static int MAX_MAXTIME = 64000;
	/**
	 * Maximum value for number of moving objects at the beginning
	 */
	public static int MAX_OBJBEGIN = 1000;
	/**
	 * Maximum value for number of moving object classes
	 */
	public static int MAX_OBJCLASSES = 20;
	/**
	 * Maximum value for number of moving objects per time
	 */
	public static int MAX_OBJPERTIME = 800;
	/**
	 * Minimum value for maximum time
	 */
	public static int MIN_MAXTIME = 5;

	/**
	 * Waiting period between two time stamps in msec (a value larger 0 is
	 * required for painting the objects while the computation)
	 */
	protected int waitingPeriod = 0;
	/**
	 * Maximum time text field
	 */
	private String maxTimeText = null;

	/**
	 * Number of moving object classes text field
	 */
	private String numObjClassesText = null;

	/**
	 * Number of external object classes text field
	 */
	private String numExtObjClassesText = null;

	/**
	 * Moving objects per time text field
	 */
	private String objPerTimeText = null;

	/**
	 * External objects per time text field
	 */
	private String extobjPerTimeText = null;

	/**
	 * External objects at the beginning text field
	 */
	private String extobjBeginText = null;

	/**
	 * Maximum speed divisor text field
	 */
	private String msdText = null;
	/**
	 * Moving objects at the beginning text field
	 */
	private String objBeginText = null;

	/**
	 * Report probability text field
	 */
	private String reportProbText = null;

	private static String networkFileName = null;

	private static boolean computeDone = false;

	private static boolean applicationReady = false;

	public static void main(String nameOfApplet) {
		main(nameOfApplet, null);
	}

	/**
	 * main entrypoint - starts the part when it is run as an application
	 * 
	 * @param nameOfApplet
	 *            complete name of the calling subclass
	 */
	public static void main(String nameOfApplet, String[] args) {
		try {
			DataGenerator.networkFileName = "file:" + args[1];

			propFilename = "/home/yackel/TrafficGenerator/properties.txt";

			// frame = new Frame("Network Generator");
			final DataGenerator aDataGenerator;
			aDataGenerator = new DefaultDataGenerator();
			/*
			 * // aDataGenerator = new DefaultDataGenerator();
			 * frame.add("Center", aDataGenerator); Dimension size =
			 * aDataGenerator.getSize(); size.setSize(size.width + 10,
			 * size.height + 50); frame.setSize(size); // add a windowListener
			 * for the windowClosedEvent frame.addWindowListener(new
			 * WindowAdapter() { public void windowClosing(WindowEvent e) {
			 * System.exit(0); }; }); frame.setVisible(false);
			 */

			aDataGenerator.init();

			Thread.sleep(5000);

			setTextFieldValues(aDataGenerator, args);

			aDataGenerator.compute();

			while (!computeDone) {
				Thread.sleep(200);
			}

			System.exit(0);
		} catch (Throwable exception) {
			exception.printStackTrace();
			System.exit(APPLICATION_START_ERROR);
		}
	}

	private static void setTextFieldValues(DataGenerator dataGenerator,
			String[] args) {
		dataGenerator.setObjBeginText(args[2]);
		dataGenerator.setExtobjBeginText(args[3]);
		dataGenerator.setObjPerTimeText(args[4]);
		dataGenerator.setExtobjPerTimeText(args[5]);
		dataGenerator.setNumObjClassesText(args[6]);
		dataGenerator.setNumExtObjClassesText(args[7]);
		dataGenerator.setMaxTimeText(args[8]);
		dataGenerator.setReportProbText(args[9]);
		dataGenerator.setMsdText(args[10]);
	}

	/**
	 * Computes the dataset. Order of initialization:
	 * <ol>
	 * <li>Time (only first call)
	 * <li>ObjectClasses (only first call)
	 * <li>ExternalObjectClasses (only first call)
	 * <li>ExternalObjectGenerator
	 * <li>ExternalObjects (only first call)
	 * <li>Reporter
	 * <li>WeightManagerForDataGenerator
	 * <li>ReRoute
	 * <li>ObjectGenerator
	 * <li>MovingObjects
	 * </ol>
	 */
	public synchronized void compute() {
		// getComputeButton().setEnabled(false);
		// showStatus("initialize generation...");
		// access to the network
		Network net = getNetwork();
		Nodes nodes = net.getNodes();
		Edges edges = net.getEdges();
		// initialization of the necessary classes
		if (time == null) {
			time = new Time(properties, getValueOfTextField(getMaxTimeText(),
					MIN_MAXTIME, MAX_MAXTIME, false));
			dataspace = new DataSpace(drawableObjects);
			objClasses = createObjectClasses(
					properties,
					time,
					dataspace,
					getValueOfTextField(getNumObjClassesText(), 1,
							MAX_OBJCLASSES, false),
					getValueOfTextField(getReportProbText(), 0, 1000, false),
					getValueOfTextField(getMsdText(), 1, 1000, true));
			extObjClasses = createExternalObjectClasses(
					properties,
					time,
					dataspace,
					getValueOfTextField(getNumExtObjClassesText(), 1,
							MAX_EXTOBJCLASSES, false));
			if (properties.getProperty(Reporter.VIZ) != null) {
				for (int c = 0; c < objClasses.getNumber(); c++)
					for (int i = 0; i <= time.getMaxTime(); i++)
						DrawablePresentation.newDrawablePresentation("Point"
								+ c + "-" + i, false, objClasses.getColor(c),
								Color.red, DrawableSymbol.CIRCLE, 8);
				for (int c = 0; c < extObjClasses.getNumber(); c++)
					for (int i = 0; i <= time.getMaxTime(); i++)
						DrawablePresentation.newDrawablePresentation(
								"Rectangle" + c + "-" + i, false,
								extObjClasses.getColor(c), Color.red);
			}
		}
		time.reset();
		edgeClasses.announce(time, dataspace,
				getValueOfTextField(getMsdText(), 1, 1000, true));
		ExternalObjectGenerator extObjGen = createExternalObjectGenerator(
				properties,
				time,
				dataspace,
				extObjClasses,
				getValueOfTextField(getExtobjPerTimeText(), 0,
						MAX_EXTOBJPERTIME, true),
				getValueOfTextField(getExtobjBeginText(), 0, MAX_EXTOBJBEGIN,
						true));
		boolean extObjectsExist = extObjGen.externalObjectsExist();
		if (extObjectsExist && (extObjects == null))
			extObjects = new ExternalObjects(properties, time, extObjClasses);
		reporter = createReporter(properties, drawableObjects);

		WeightManagerForDataGenerator wm = null;
		if (extObjectsExist)
			wm = new WeightManagerForDataGenerator(edgeClasses, objClasses,
					extObjects);
		else
			wm = new WeightManagerForDataGenerator(edgeClasses, objClasses,
					null);
		edges.setWeightManager(wm);
		ReRoute reroute = createReRoute(properties, time, dataspace);
		ObjectGenerator objGen = createObjectGenerator(
				properties,
				time,
				dataspace,
				nodes,
				objClasses,
				getValueOfTextField(getObjPerTimeText(), 0, MAX_OBJPERTIME,
						true),
				getValueOfTextField(getObjBeginText(), 0, MAX_OBJBEGIN, true));
		MovingObjects movingObjects = new MovingObjects(wm, net, objGen,
				reporter, reroute);
		// the time starts
		showStatus("generate data, please wait...");
		util.Timer.reset(1);
		util.Timer.reset(2);
		util.Timer.start(1);
		actTime = time.getCurrTime();
		// traverse the time
		while (!time.isMaximumTimeExceeded()) {
			// move and report all external objects, remove the desd objects
			if (extObjectsExist)
				extObjects.moveAndResizeAndRemoveObjects(actTime, extObjGen,
						reporter);
			// move and report all moving objects, remove the objects reaching
			// the destination
			movingObjects.move(actTime);
			// generate new external objects
			int numOfNewExtObjects = extObjGen.numberOfNewObjects(actTime);
			for (int i = 0; i < numOfNewExtObjects; i++) {
				ExternalObject extObj = extObjGen
						.computeExternalObject(actTime);
				extObj.addToContainer(extObjects);
				extObj.reportNewObject(reporter);
			}
			// generate new moving objects
			int numOfNewObjects = objGen.numberOfNewObjects(actTime);
			for (int i = 0; i < numOfNewObjects; i++) {
				// for each new moving object, determine its properties and
				// create it, ...
				int id = objGen.computeId(actTime);
				int objClass = objGen.computeObjectClass(actTime);
				Node start = objGen.computeStartingNode(actTime, objClass);
				Node dest = objGen.computeDestinationNode(actTime, start,
						objGen.computeLengthOfRoute(actTime, objClass),
						objClass);
				MovingObject obj = new MovingObject(id, objClass, start, dest,
						actTime);
				obj.addToContainer(movingObjects);
				// and compute the (first) route
				while (!obj.computeRoute()) {
					obj.setStart(objGen.computeStartingNode(actTime, objClass));
					obj.setDestination(objGen.computeDestinationNode(actTime,
							start,
							objGen.computeLengthOfRoute(actTime, objClass),
							objClass));
				}
				obj.reportNewObject(reporter);
			}
			// show object if there is enough time
			if (waitingPeriod > 0) {
				if (!Time.isFirstTimeStamp(actTime)) {
					for (int c = 0; c < objClasses.getNumber(); c++)
						DrawablePresentation.get(
								"Point" + c + "-" + (this.actTime - 1))
								.setVisibility(false);
					for (int c = 0; c < extObjClasses.getNumber(); c++)
						DrawablePresentation.get(
								"Rectangle" + c + "-" + (this.actTime - 1))
								.setVisibility(false);
				}
				for (int c = 0; c < objClasses.getNumber(); c++)
					DrawablePresentation.get("Point" + c + "-" + this.actTime)
							.setVisibility(true);
				for (int c = 0; c < extObjClasses.getNumber(); c++)
					DrawablePresentation.get(
							"Rectangle" + c + "-" + this.actTime)
							.setVisibility(true);
			}
			// to the next time stamp
			time.increaseCurrTime();
			actTime = time.getCurrTime();
			if (actTime % 25 == 0)
				System.gc();
			// wait
			if (waitingPeriod > 0)
				try {
					wait(waitingPeriod);
				} catch (Exception e) {
					System.err.println("wait: " + e);
				}
		}
		util.Timer.stop(1);
		// report and remove all still existing objects
		// showStatus("remove remaining objects and report statistics...");
		movingObjects.removeObjects();
		if (extObjectsExist)
			extObjects.removeObjects();
		// report statistics
		long totalTime = util.Timer.get(1);
		long routingTime = util.Timer.get(2);
		int numOfRoutes = movingObjects.getTotalNumOfObjects()
				+ reroute.getNumberOfRoutesByEvent()
				+ reroute.getNumberOfRoutesByComparison();
		int dx = dataspace.getMaxX() - dataspace.getMinX();
		int dy = dataspace.getMaxY() - dataspace.getMinY();
		reporter.reportInt("data space width: ", dx);
		reporter.reportInt("data space height: ", dy);
		reporter.reportInt("number of nodes: ", nodes.numOfNodes());
		reporter.reportInt("number of edges: ", edges.numOfEdges());
		reporter.reportInt("maximum time: ", time.getMaxTime());
		reporter.reportInt("# moving objects: ",
				movingObjects.getTotalNumOfObjects());
		reporter.reportInt("# points: ", reporter.getNumberOfReportedPoints());
		reporter.reportInt("# traversed nodes: ",
				movingObjects.getTotalNumberOfTraversedNodes());
		reporter.reportDouble("# nodes/obj: ",
				((double) movingObjects.getTotalNumberOfTraversedNodes())
						/ movingObjects.getTotalNumOfObjects());
		reporter.reportDouble("# traversed degree: ",
				movingObjects.getTotalDegreeOfTraversedNodes());
		double nodeDegree = (double) (movingObjects
				.getTotalDegreeOfTraversedNodes() - movingObjects
				.getTotalNumberOfTraversedNodes())
				/ movingObjects.getTotalNumberOfTraversedNodes();
		reporter.reportDouble("# node degree: ", nodeDegree);
		reporter.reportInt("# all routes: ", numOfRoutes);
		reporter.reportInt("# routes by event: ",
				reroute.getNumberOfRoutesByEvent());
		reporter.reportInt("# routes by comparison: ",
				reroute.getNumberOfRoutesByComparison());
		reporter.reportInt("total time in ms: ", totalTime);
		reporter.reportDouble("total time/obj: ",
				((double) totalTime / movingObjects.getTotalNumOfObjects()));
		reporter.reportDouble("total time/point: ",
				((double) totalTime / reporter.getNumberOfReportedPoints()));
		reporter.reportDouble("routing time in ms: ", routingTime);
		reporter.reportDouble("insert time: ", BorderHeap.insertTimer.get());
		reporter.reportDouble("fetch time:  ", BorderHeap.fetchTimer.get());
		reporter.reportDouble("change time: ", BorderHeap.changeTimer.get());
		reporter.reportDouble("routing time/obj: ",
				((double) routingTime / movingObjects.getTotalNumOfObjects()));
		reporter.reportDouble("routing time/point: ",
				((double) routingTime / reporter.getNumberOfReportedPoints()));
		reporter.reportDouble("routing time/node: ",
				((double) routingTime / movingObjects
						.getTotalNumberOfTraversedNodes()));
		reporter.reportDouble(
				"routing time/node/nodedegr: ",
				((double) routingTime
						/ movingObjects.getTotalNumberOfTraversedNodes() / nodeDegree));
		reporter.reportDouble("routing time/routing: ",
				((double) routingTime / numOfRoutes));
		if (extObjectsExist) {
			reporter.reportInt("# computed decreases: ",
					extObjects.getNumOfComputedDecreases());
			reporter.reportInt("# real decreases: ",
					extObjects.getNumOfRealDecreases());
			reporter.reportInt("time for external objects in ms: ",
					extObjects.getUsedTime());
		}
		long totalDistance = 0;
		for (Enumeration e = edges.elements(); e.hasMoreElements();) {
			Edge edge = (Edge) e.nextElement();
			totalDistance += edge.getLength();
		}
		int avDistance = (int) (totalDistance / edges.numOfEdges());
		reporter.reportInt("average edge length: ", avDistance);
		reporter.reportInt("average route length: ",
				objGen.getAverageRouteLength());
		reporter.close();

		setTime(0);
		// this.addTimeButton.setEnabled(true);
		// repaint();
		// showStatus("ready...");
		computeDone = true;
	}

	/**
	 * Calls the constructor of EdgeClasses. Must be implemented by a subclass
	 * of DataGenerator.
	 * 
	 * @return an object of the class EdgeClasses
	 * @param properties
	 *            the properties of the generator
	 */
	public abstract EdgeClasses createEdgeClasses(Properties properties);

	/**
	 * Calls the constructor of ExternalObjectClasses. Must be implemented by a
	 * subclass of DataGenerator.
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
	public abstract ExternalObjectClasses createExternalObjectClasses(
			Properties properties, Time time, DataSpace ds, int numOfClasses);

	/**
	 * Calls the constructor of ExternalObjectGenerator. Must be implemented by
	 * a subclass of DataGenerator.
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
	public abstract ExternalObjectGenerator createExternalObjectGenerator(
			Properties properties, Time time, DataSpace dataspace,
			ExternalObjectClasses classes, int numOfExtObjPerTime,
			int numAtBeginning);

	/**
	 * Calls the constructor of ObjectClasses. Must be implemented by a subclass
	 * of DataGenerator.
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
	public abstract ObjectClasses createObjectClasses(Properties properties,
			Time time, DataSpace ds, int numOfClasses, int reportProb,
			int maxSpeedDivisor);

	/**
	 * Calls the constructor of ObjectGenerator. Must be implemented by a
	 * subclass of DataGenerator.
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
	 *            indicator for the number of objects per time
	 * @param numAtBeginning
	 *            indicator for the number of objects at the beginning
	 */
	public abstract ObjectGenerator createObjectGenerator(
			Properties properties, Time time, DataSpace ds, Nodes nodes,
			ObjectClasses objClasses, int numOfObjPerTime,
			int numOfObjAtBeginning);

	/**
	 * Calls the constructor of Reporter. Must be implemented by a subclass of
	 * DataGenerator.
	 * 
	 * @return the reporter
	 * @param properties
	 *            properties of the generator
	 * @param objects
	 *            container of drawable objects
	 */
	public abstract Reporter createReporter(Properties properties,
			DrawableObjects objects);

	/**
	 * Calls the constructor of ReRoute. Must be implemented by a subclass of
	 * DataGenerator.
	 * 
	 * @return an object of ReRoute
	 * @param properties
	 *            properties of the generator
	 * @param time
	 *            the time object
	 * @param ds
	 *            the data space
	 */
	public abstract ReRoute createReRoute(Properties properties, Time time,
			DataSpace ds);

	/**
	 * Deletes the generated moving and external objects.
	 */
	protected void deleteObjects() {
		if (reporter != null)
			reporter.removeReportedObjects();
	}

	/**
	 * Returns an integer property.
	 * 
	 * @return the integer value
	 * @param key
	 *            name of the key
	 * @param defaultValue
	 *            the default value
	 */
	protected int getProperty(String key, int defaultValue) {
		return getProperty(properties, key, defaultValue);
	}

	/**
	 * Returns an integer property.
	 * 
	 * @return the integer value
	 * @param properties
	 *            the properties
	 * @param key
	 *            name of the key
	 * @param defaultValue
	 *            the default value
	 */
	public static int getProperty(Properties properties, String key,
			int defaultValue) {
		try {
			return new Integer(properties.getProperty(key)).intValue();
		} catch (Exception ex) {
			return defaultValue;
		}
	}

	/**
	 * Computes the value of the text field and adapt it.
	 * 
	 * @return integer value of text field
	 * @param tf
	 *            the text field
	 * @param min
	 *            minimum allowed value
	 * @param max
	 *            maximum allowed value
	 * @param enabledAfter
	 *            should the text filed be enabled after computing the value?
	 */
	protected int getValueOfTextField(String tf, int min, int max,
			boolean enabledAfter) {
		int intValue = new Integer(tf).intValue();
		if (intValue < min)
			intValue = min;
		else if (intValue > max)
			intValue = max;
		return intValue;
	}

	/**
	 * Initializes the data generator. Initializes the class EdgeClasses.
	 */
	public void init() {
		// read properties
		try {
			FileInputStream in = new FileInputStream(propFilename);
			properties.load(in);
		} catch (MalformedURLException mex) {
			System.err.println("DataGenerator.init: " + mex);
			System.exit(PROPERTY_FILE_ERROR);
		} catch (IOException ioex) {
			System.err.println("DataGenerator.init: " + ioex);
			System.exit(PROPERTY_FILE_ERROR);
		}

		if (networkFileName != null) {
			properties.setProperty("urlne", networkFileName);
		}
		// init drawable objects
		edgeClasses = createEdgeClasses(properties);
		if (drawableObjects == null)
			drawableObjects = new DrawableObjectsWithSearchTree(numOfLayers,
					new MemoryRTree());
		super.init();
	}

	/**
	 * Inits the presentation of the network.
	 */
	protected void initDrawablePresentation() {
		super.initDrawablePresentation();
		int num = edgeClasses.getNumber();
		// Minimum scales for depicting edges and nodes
		int[] nodeMinScale = new int[num + 1];
		for (int i = 0; i < num + 1; i++)
			nodeMinScale[i] = 0;
		int[] edgeMinScale = new int[num];
		for (int i = 0; i < num; i++)
			edgeMinScale[i] = edgeClasses.getMinScale(i);
		// Colors of edges and nodes
		Color[] nodeColor = new Color[num + 1];
		for (int i = 0; i < num + 1; i++)
			nodeColor[i] = Color.gray;
		Color[] edgeColor = new Color[num];
		for (int i = 0; i < num; i++)
			edgeColor[i] = edgeClasses.getColor(i);
		// Setting
		net.getEdges().setNumOfClasses(num);
		net.getEdges().initPresentation(edgeColor, Color.red);
		net.getEdges().setMinScaleArray(edgeMinScale);
		net.getNodes().setNumOfClasses(num + 1);
		net.getNodes().initPresentation(nodeColor, Color.red);
		net.getNodes().setMinScaleArray(nodeMinScale);
	}

	/**
	 * Evaluates the properties of the property file.
	 */
	protected void interpretParameters() {
		// evaluate generator properties
		MIN_MAXTIME = getProperty("MIN_MAXTIME", MIN_MAXTIME);
		MAX_MAXTIME = getProperty("MAX_MAXTIME", MAX_MAXTIME);
		MAX_OBJCLASSES = getProperty("MAX_OBJCLASSES", MAX_OBJCLASSES);
		MAX_OBJPERTIME = getProperty("MAX_OBJPERTIME", MAX_OBJPERTIME);
		MAX_OBJBEGIN = getProperty("MAX_OBJBEGIN", MAX_OBJBEGIN);
		MAX_EXTOBJCLASSES = getProperty("MAX_EXTOBJCLASSES", MAX_EXTOBJCLASSES);
		MAX_EXTOBJPERTIME = getProperty("MAX_EXTOBJPERTIME", MAX_EXTOBJPERTIME);
		MAX_EXTOBJBEGIN = getProperty("MAX_EXTOBJBEGIN", MAX_EXTOBJBEGIN);

		waitingPeriod = getProperty("waitingPeriod", waitingPeriod);
		// evaluate showmap parameters
		baseScaleFactor = getProperty("baseScaleFactor", 1);
		minScale = getProperty("minScale", minScale);
		maxScale = getProperty("maxScale", maxScale);
		scale = getProperty("scale", maxScale);
		viewWidth = getProperty("viewWidth", viewWidth);
		viewHeight = getProperty("viewHeight", viewHeight);
		mapWidth = getProperty("mapWidth", scale * viewWidth);
		mapHeight = getProperty("mapHeight", scale * viewHeight);
		viewX = getProperty("viewX", viewX);
		viewY = getProperty("viewY", viewY);
		String p = properties.getProperty("color");
		if (p != null)
			backgroundColor = ColorDefiner.getColor(p);
		p = properties.getProperty("mapColor");
		if (p != null)
			mapColor = ColorDefiner.getColor(p);
		p = properties.getProperty("language");
		if ((p != null) && p.equals("D"))
			language = GERMAN;
	}

	/**
	 * Makes the file name to an absolute file name
	 * 
	 * @param fileName
	 *            the name of the file
	 */
	protected static String makeAbsolute(String fileName) {
		try {
			return new File(fileName).getAbsolutePath();
		} catch (Exception ex) {
			return fileName;
		}
	}

	/**
	 * Sets the status of the applet. If the status is COMPLETE, the compute
	 * button will be enabled. If the maximum time text field has been changed,
	 * the computation will automatically be started.
	 * 
	 * @param state
	 *            the new state
	 */
	protected void setState(int state) {
		super.setState(state);
		if (state != COMPLETE)
			return;

		// Automatic computation
		if (getValueOfTextField(getMaxTimeText(), MIN_MAXTIME, MAX_MAXTIME,
				true) != 20)
			compute();
		// Compute button
		applicationReady = true;
	}

	/**
	 * Sets the actual time for displaying purposes.
	 * 
	 * @param actTime
	 *            int
	 */
	protected void setTime(int actTime) {
		// reset old state
		if (this.actTime != 0) {
			for (int c = 0; c < objClasses.getNumber(); c++)
				DrawablePresentation.get("Point" + c + "-" + this.actTime)
						.setVisibility(false);
			for (int c = 0; c < extObjClasses.getNumber(); c++)
				DrawablePresentation.get("Rectangle" + c + "-" + this.actTime)
						.setVisibility(false);
		} else
			for (int t = 0; t <= time.getMaxTime(); t++) {
				for (int c = 0; c < objClasses.getNumber(); c++)
					DrawablePresentation.get("Point" + c + "-" + t)
							.setVisibility(false);
				for (int c = 0; c < extObjClasses.getNumber(); c++)
					DrawablePresentation.get("Rectangle" + c + "-" + t)
							.setVisibility(false);
			}
		// set new state
		if (actTime != 0) {
			for (int c = 0; c < objClasses.getNumber(); c++)
				DrawablePresentation.get("Point" + c + "-" + actTime)
						.setVisibility(true);
			for (int c = 0; c < extObjClasses.getNumber(); c++)
				DrawablePresentation.get("Rectangle" + c + "-" + actTime)
						.setVisibility(true);
		} else
			for (int t = 0; t <= time.getMaxTime(); t++) {
				for (int c = 0; c < objClasses.getNumber(); c++)
					DrawablePresentation.get("Point" + c + "-" + t)
							.setVisibility(true);
				for (int c = 0; c < extObjClasses.getNumber(); c++)
					DrawablePresentation.get("Rectangle" + c + "-" + t)
							.setVisibility(true);
			}
		this.actTime = actTime;
		time.setCurrTime(actTime);
	}

	/**
	 * Shows the status.
	 * 
	 * @param text
	 *            text to be displayed
	 */
	public void showStatus(String text) {

	}

	/**
	 * Interprets the properties "urlne" (base name of unzipped network files)
	 * or "urlnez" (base name of zipped network files) and starts the loading
	 * thread. This method must be overwritten by a superclass, which reads the
	 * network from elsewhere.
	 */
	protected void startLoadingThread() {
		// determine filenames
		URL url[] = { null, null, null };
		String filename = properties.getProperty("urlne");
		if (filename != null) {
			url[1] = computeURL(filename + ".node");
			url[2] = computeURL(filename + ".edge");
		} else {
			filename = properties.getProperty("urlnez");
			if (filename == null) {
				System.err.println("no network file determined");
				System.exit(NETWORKFILE_ERROR);
			}
			url[1] = computeURL(filename + ".node.zip");
			url[2] = computeURL(filename + ".edge.zip");
		}
		// start thread
		new LoadDrawables(this, url, 0).start();
	}

	/**
	 * Sets the viewpoint to the value predefined by the parameters.
	 */
	public void setViewToPrefinedValue() {
		viewMapX = getProperty("posx", mapWidth / 2 / scale);
		viewMapY = getProperty("posy", mapHeight / 2 / scale);
	}

	/**
	 * @return the maxTimeText
	 */
	public String getMaxTimeText() {
		return maxTimeText;
	}

	/**
	 * @param maxTimeText
	 *            the maxTimeText to set
	 */
	public void setMaxTimeText(String maxTimeText) {
		this.maxTimeText = maxTimeText;
	}

	/**
	 * @return the numObjClassesText
	 */
	public String getNumObjClassesText() {
		return numObjClassesText;
	}

	/**
	 * @param numObjClassesText
	 *            the numObjClassesText to set
	 */
	public void setNumObjClassesText(String numObjClassesText) {
		this.numObjClassesText = numObjClassesText;
	}

	/**
	 * @return the numExtObjClassesText
	 */
	public String getNumExtObjClassesText() {
		return numExtObjClassesText;
	}

	/**
	 * @param numExtObjClassesText
	 *            the numExtObjClassesText to set
	 */
	public void setNumExtObjClassesText(String numExtObjClassesText) {
		this.numExtObjClassesText = numExtObjClassesText;
	}

	/**
	 * @return the objPerTimeText
	 */
	public String getObjPerTimeText() {
		return objPerTimeText;
	}

	/**
	 * @param objPerTimeText
	 *            the objPerTimeText to set
	 */
	public void setObjPerTimeText(String objPerTimeText) {
		this.objPerTimeText = objPerTimeText;
	}

	/**
	 * @return the extobjPerTimeText
	 */
	public String getExtobjPerTimeText() {
		return extobjPerTimeText;
	}

	/**
	 * @param extobjPerTimeText
	 *            the extobjPerTimeText to set
	 */
	public void setExtobjPerTimeText(String extobjPerTimeText) {
		this.extobjPerTimeText = extobjPerTimeText;
	}

	/**
	 * @return the extobjBeginText
	 */
	public String getExtobjBeginText() {
		return extobjBeginText;
	}

	/**
	 * @param extobjBeginText
	 *            the extobjBeginText to set
	 */
	public void setExtobjBeginText(String extobjBeginText) {
		this.extobjBeginText = extobjBeginText;
	}

	/**
	 * @return the msdText
	 */
	public String getMsdText() {
		return msdText;
	}

	/**
	 * @param msdText
	 *            the msdText to set
	 */
	public void setMsdText(String msdText) {
		this.msdText = msdText;
	}

	/**
	 * @return the objBeginText
	 */
	public String getObjBeginText() {
		return objBeginText;
	}

	/**
	 * @param objBeginText
	 *            the objBeginText to set
	 */
	public void setObjBeginText(String objBeginText) {
		this.objBeginText = objBeginText;
	}

	/**
	 * @return the reportProbText
	 */
	public String getReportProbText() {
		return reportProbText;
	}

	/**
	 * @param reportProbText
	 *            the reportProbText to set
	 */
	public void setReportProbText(String reportProbText) {
		this.reportProbText = reportProbText;
	}
}