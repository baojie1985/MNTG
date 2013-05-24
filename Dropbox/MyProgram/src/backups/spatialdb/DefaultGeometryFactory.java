package spatialdb;

import java.sql.Connection;
import oracle.sdoapi.geom.GeometryFactory;
import oracle.sdoapi.OraSpatialManager;

/**
 * Class delivering the geometry factory and the geometries for the MOX project.
 * @author Thomas Brinkhoff
 * 
 * @version 1.01	05.07.2003	adapted to sdoapi 1.0.1
 * @version 1.00	24.05.2001	first version
 */
public class DefaultGeometryFactory {

	/**
	 * The geometry factory.
	 */
	private static GeometryFactory factory = null;

/**
 * Returns the geometry factory.
 * @return  the factory
 */
public static GeometryFactory getFactory () {
	if (factory == null)
		factory = OraSpatialManager.getGeometryFactory();
	return factory;
}
/**
 * Sets the spatial reference system.
 * @param conn the database connection
 */
public static void setSpatialReference (Connection conn) {
	if (factory == null)
		getFactory();
	try {
	    oracle.sdoapi.sref.SRManager srManager = OraSpatialManager.getSpatialReferenceManager((oracle.jdbc.OracleConnection)conn);
	    oracle.sdoapi.sref.SpatialReference sref = srManager.retrieve(8265);   // SRID = 8265: LAT-LON (NAD 83)
	    factory.setSpatialReference(sref);
	}
	catch (oracle.sdoapi.sref.SRException e) {
	    System.err.println("MoxGeometryFactory.getFactory: "+e);
	}
}
}
