package generator2;

import java.awt.Rectangle;
import java.sql.Connection;
import java.sql.Statement;

import routing.Edge;
import routing.Edges;
import routing.Network;
import routing.Node;
import routing.Nodes;
import spatialdb.GeometryConversion;
import spatialdb.SpatialResultSet;

/**
 * Controller applet for the computation of network-based spatiotemporal
 * datasets. It is also possible to run this class as Java application. Uses the
 * Oracle database reporter and reads the network from the database, where it is
 * stored using Oracle Spatial. See the additional documentation for the
 * necessary table definitions and the supported properties in the property
 * file. Requires classes12.zip and sdoapi.zip in the classpath.
 * 
 * @version 1.01 03.09.2003 constructing of DrawableObject-objects removed,
 *          makeAbsolute called
 * @version 1.00 03.01.2001 first version
 * @author FH Oldenburg
 */
public class OracleSpatialDataGenerator extends OracleDataGenerator {

	/**
	 * Creates a new edge.
	 * 
	 * @return the new edge
	 * @param id
	 *            identifier
	 * @param edgeClass
	 *            edge class
	 * @param n1
	 *            1st node
	 * @param n2
	 *            2nd node
	 * @param edges
	 *            container of edges
	 */
	private Edge computeEdge(long id, int edgeClass, Node n1, Node n2,
			Edges edges) {
		Edge actEdge = edges.newEdge(id, edgeClass, n1, n2, null);
		drawableObjects.addDrawable(actEdge);
		return actEdge;
	}

	/**
	 * Creates a node. If a node with the given coordinate exists, this node
	 * will be returned. Otherwise, a new node will be constructed.
	 * 
	 * @return the node
	 * @param id
	 *            identifier
	 * @param x
	 *            x-coordinate
	 * @param y
	 *            y-coordinate
	 * @param nodes
	 *            container of nodes
	 */
	private Node computeNode(long id, int x, int y, Nodes nodes) {
		Node cn = nodes.findNearest(x, y);
		if ((cn != null) && (x == cn.getX()) && (y == cn.getY()))
			return cn;
		Node actNode = nodes.newNode(id, x, y, null);
		drawableObjects.addDrawable(actNode);
		return actNode;
	}

	/**
	 * main entrypoint - starts the part when it is run as an application
	 * 
	 * @param args
	 *            args[0] = name of the property file
	 */
	public static void main(java.lang.String[] args) {
		if ((args.length > 0) && (args[0] != null))
			propFilename = makeAbsolute(args[0]);
		DataGenerator.main("generator2.OracleSpatialDataGenerator");
	}

	/**
	 * Reads the street map from the spatial database.
	 */
	protected void startLoadingThread() {
		try {
			// create edges and nodes
			if (net == null)
				net = new Network();
			Edges edges = net.getEdges();
			Nodes nodes = net.getNodes();
			// prepare database
			System.out.println("open db connection ...");
			showStatus("open db connection ...");
			Connection dbCon = OracleReporter.getConnection(properties);
			Statement query = dbCon.createStatement();
			String tableName = "streets";
			if (properties.getProperty("dbStreetTableName") != null)
				tableName = properties.getProperty("dbStreetTableName");
			String geoAttrName = "geometry";
			if (properties.getProperty("dbStreetGeomName") != null)
				geoAttrName = properties.getProperty("dbStreetGeomName");
			String classAttrName = "class";
			if (properties.getProperty("dbStreetClassName") != null)
				classAttrName = properties.getProperty("dbStreetClassName");
			// query extent
			SpatialResultSet extent = new SpatialResultSet(
					query.executeQuery("SELECT SDO_TUNE.EXTENT_OF('"
							+ tableName + "','" + geoAttrName + "') FROM DUAL"));
			extent.next();
			Rectangle mbr = GeometryConversion.toAwtRectangle(extent
					.getGeometry(1));
			// construct network
			System.out.println("construct network, please wait ...");
			showStatus("construct network, please wait ...");
			SpatialResultSet feature = new SpatialResultSet(
					query.executeQuery("SELECT * FROM " + tableName));
			long id = 0;
			while (feature.next()) {
				int edgeClass = feature.getInt(classAttrName);
				java.awt.Polygon pol = GeometryConversion.toAwtPolyline(feature
						.getGeometry(geoAttrName));
				// construct nodes and edges
				Node n1 = computeNode(++id, pol.xpoints[0], pol.ypoints[0],
						nodes);
				for (int i = 1; i < pol.npoints; i++) {
					Node n2 = computeNode(++id, pol.xpoints[i], pol.ypoints[i],
							nodes);
					computeEdge(++id, edgeClass, n1, n2, edges);
					n1 = n2;
				}
			}
			dbCon.close();
			System.out.println("construction finished");
			// set map properties
			mapWidth = mbr.x + mbr.width;
			mapHeight = mbr.y + mbr.height;
		} catch (Exception ex) {
			System.err
					.println("OracleSpatialDataGenerator.startLoadingThread: "
							+ ex);
		}
		// make applet ready
		setViewToPrefinedValue();
		setState(COMPLETE);
	}

}
