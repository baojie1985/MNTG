package generator2;

import java.awt.Rectangle;
import java.sql.*;
import java.util.Properties;

import drawables.DrawableObjects;

/**
 * Class for reporting the computed moving objects into an Oracle database.
 * Requires classes111.zip or classes12.zip in the classpath.
 *
 * @version 1.10	16.08.2003	additional parameters reported
 * @version 1.00	03.09.2001	first version
 * @author FH Oldenburg
 */
public class OracleReporter extends Reporter {

	/**
	 * Database connection.
	 */
	protected Connection con = null;
	/**
	 * Name of the table storing the external objects.
	 */
	protected String dbExternalObjectTableName = "externalobjects";
	/**
	 * Name of the table storing the moving objects.
	 */
	protected String dbMovingObjectTableName = "movingobjects";
	/**
	 * Prepared database statement for storing external objects.
	 */
	protected PreparedStatement prepExtObjStmt = null;
	/**
	 * Prepared database statement for storing moving objects.
	 */
	protected PreparedStatement prepMovObjStmt = null;

/**
 * Constructor of the Oracle reporter.
 * @param properties properties of the generator
 * @param objects container of drawable objects
 */
public OracleReporter (Properties properties, DrawableObjects objects) {
	super (properties,objects);
	if (properties.getProperty("dbMovingObjectTableName") != null)
		dbMovingObjectTableName = properties.getProperty("dbMovingObjectTableName");
	if (properties.getProperty("dbExternalObjectTableName") != null)
		dbExternalObjectTableName = properties.getProperty("dbExternalObjectTableName");
	try {
		con = getConnection(properties);
		prepMovObjStmt = con.prepareStatement(
			"INSERT INTO "+dbMovingObjectTableName+"(id,num,time,class,x,y,dbtime) VALUES (?,?,?,?,?,?,sysdate)");
		prepExtObjStmt = con.prepareStatement(
			"INSERT INTO "+dbExternalObjectTableName+"(id,num,time,class,x,y,width,height,dbtime) VALUES (?,?,?,?,?,?,?,?,sysdate)");
	}
	catch (Exception ex) {
		System.err.println("OracleReporter: Error while connecting: "+ex);
		con = null;
	}
}

/**
 * Closes the reporter.
 */
public void close() {
	if (con != null)
		try {
			con.close();
		}
		catch (Exception ex) {}
}

/**
 * Returns the connection to the database.
 * @param properties properties of the generator
 */
public static Connection getConnection (Properties properties) throws SQLException,ClassNotFoundException {
	String dbDriverClassName = "oracle.jdbc.driver.OracleDriver";
	String dbConnectionName = "jdbc:oracle:oci8:@geodata";
	String dbUserName = "scott";
	String dbPassword = "tiger";
	if (properties.getProperty("dbDriverClassName") != null)
		dbDriverClassName = properties.getProperty("dbDriverClassName");
	if (properties.getProperty("dbConnectionName") != null)
		dbConnectionName = properties.getProperty("dbConnectionName");
	if (properties.getProperty("dbUserName") != null)
		dbUserName = properties.getProperty("dbUserName");
	if (properties.getProperty("dbPassword") != null)
		dbPassword = properties.getProperty("dbPassword");
	Class.forName(dbDriverClassName);
	return DriverManager.getConnection(dbConnectionName,dbUserName,dbPassword);
}

/**
 * Removes all reported objects.
 */
public void removeReportedObjects() {
	super.removeReportedObjects();
	try {
		Connection con = getConnection(properties);
		Statement stmt = con.createStatement();
		stmt.executeUpdate("DELETE FROM "+dbMovingObjectTableName);
		stmt.executeUpdate("DELETE FROM "+dbExternalObjectTableName);
		stmt.close();
		con.close();
	}
	catch (Exception e) {
		System.err.println("removeReportedObjects: "+e);
	}
}

/**
 * Reports a disappearing external object.
 * @param time time stamp
 * @param id object id
 * @param repNum report number
 * @param objClass object class
 * @param rect the area
 */
public void reportDisappearingExternalObject (int time, long id, int repNum, int objClass, Rectangle rect) {
	if (con != null)
		try {
			prepExtObjStmt.setLong (1,id);
			prepExtObjStmt.setInt (2,repNum);
			prepExtObjStmt.setInt (3,time);
			prepExtObjStmt.setInt (4,objClass);
			prepExtObjStmt.setInt (5,-1);
			prepExtObjStmt.setInt (6,-1);
			prepExtObjStmt.setInt (7,0);
			prepExtObjStmt.setInt (8,0);
			prepExtObjStmt.execute();
		}
		catch (Exception ex) {
			System.err.println("reportDisappearingExternalObject: "+ex+" VALUES ("+
				id+","+repNum+","+time+","+objClass+","+rect+")");
		}
}

/**
 * Reports the characteristic properties of a dispappering object at a time stamp
 * @param  time  time stamp
 * @param  id  object id
 * @param  repNum  report number
 * @param  objClass  object class
 * @param  x  x-coordinate
 * @param  y  y-coordinate
 * @param  doneDist  the distance since the last reporting
 * @param  reportProbability  value between (0..1000)
 */
public void reportDisappearingObject (int time, long id, int repNum, int objClass, int x, int y, double doneDist, int reportProbability) {
	if ((reportProbability > 0) && (con != null)) {
		try {
			prepMovObjStmt.setLong (1,id);
			prepMovObjStmt.setInt (2,repNum+1);
			prepMovObjStmt.setInt (3,time);
			prepMovObjStmt.setInt (4,objClass);
			prepMovObjStmt.setInt (5,-1);
			prepMovObjStmt.setInt (6,-1);
			prepMovObjStmt.execute();
		}
		catch (Exception ex) {
			System.err.println("reportDisappearingObject: "+ex+" VALUES ("+
				id+","+(repNum+1)+","+time+","+objClass+","+x+","+y+")");
		}
	}
}

/**
 * Reports a moved external object.
 * @param time time stamp
 * @param id object id
 * @param repNum report number
 * @param objClass object class
 * @param rect the area
 */
public void reportMovingExternalObject (int time, long id, int repNum, int objClass, Rectangle rect) {
	super.reportMovingExternalObject (time,id,repNum,objClass,rect);
	if (con != null)
		try {
			prepExtObjStmt.setLong (1,id);
			prepExtObjStmt.setInt (2,repNum);
			prepExtObjStmt.setInt (3,time);
			prepExtObjStmt.setInt (4,objClass);
			prepExtObjStmt.setInt (5,rect.x);
			prepExtObjStmt.setInt (6,rect.y);
			prepExtObjStmt.setInt (7,rect.width);
			prepExtObjStmt.setInt (8,rect.height);
			prepExtObjStmt.execute();
		}
		catch (Exception ex) {
			System.err.println("reportMovingExternalObject: "+ex+" VALUES ("+
				id+","+repNum+","+time+","+objClass+","+rect+")");
		}
}

/**
 * Reports the characteristic properties of a moving object at a time stamp
 * according to its report probability.
 * @param  time  time stamp
 * @param  id  object id
 * @param  repNum  report number
 * @param  objClass  object class
 * @param  x  x-coordinate
 * @param  y  y-coordinate
 * @param  speed  current speed
 * @param  doneDist  the distance since the last reporting
 * @param  nextNodeX  x-coordinate of the next node
 * @param  nextNodeY  y-coordinate of the next node
 * @param  reportProbability  value between (0..1000)
 */
public int reportMovingObject (int time, long id, int repNum, int objClass, double x, double y, double speed, double doneDist, int nextNodeX, int nextNodeY, int reportProbability) {
	int newRepNum = super.reportMovingObject (time,id,repNum,objClass,x,y,speed,doneDist,nextNodeX,nextNodeY,reportProbability);
	if ((repNum != newRepNum) && (con != null))
		try {
			prepMovObjStmt.setLong (1,id);
			prepMovObjStmt.setInt (2,newRepNum);
			prepMovObjStmt.setInt (3,time);
			prepMovObjStmt.setInt (4,objClass);
			prepMovObjStmt.setInt (5,(int)x);
			prepMovObjStmt.setInt (6,(int)y);
			prepMovObjStmt.execute();
		}
		catch (Exception ex) {
			System.err.println("reportMovingOject: "+ex+" VALUES ("+
				id+","+newRepNum+","+time+","+objClass+","+x+","+y+")");
		}
	return newRepNum;
}

/**
 * Reports a new external object.
 * @param time time stamp
 * @param id object id
 * @param objClass object class
 * @param rect the area
 */
public void reportNewExternalObject (int time, long id, int objClass, Rectangle rect) {
	super.reportNewExternalObject (time,id,objClass,rect);
	if (con != null)
		try {
			prepExtObjStmt.setLong (1,id);
			prepExtObjStmt.setInt (2,1);
			prepExtObjStmt.setInt (3,time);
			prepExtObjStmt.setInt (4,objClass);
			prepExtObjStmt.setInt (5,rect.x);
			prepExtObjStmt.setInt (6,rect.y);
			prepExtObjStmt.setInt (7,rect.width);
			prepExtObjStmt.setInt (8,rect.height);
			prepExtObjStmt.execute();
		}
		catch (Exception ex) {
			System.err.println("reportNewExternalObject: "+ex+" VALUES ("+
				id+",1,"+time+","+objClass+","+rect+")");
		}
}

/**
 * Reports an new moving object if its report probability > 0.
 * @param  time  time stamp
 * @param  id  object id
 * @param  objClass  object class
 * @param  x  x-coordinate of start
 * @param  y  y-coordinate of start
 * @param  speed  current speed
 * @param  nextNodeX  x-coordinate of the next node
 * @param  nextNodeY  y-coordinate of the next node
 * @param  reportProbability  value between (0..1000)
 */
public int reportNewMovingObject (int time, long id, int objClass, int x, int y, double speed, int nextNodeX, int nextNodeY, int reportProbability) {
	int repNum = super.reportNewMovingObject(time,id,objClass,x,y,speed,nextNodeX,nextNodeY,reportProbability);
	if ((repNum > 0) && (con != null))
		try {
			prepMovObjStmt.setLong (1,id);
			prepMovObjStmt.setInt (2,repNum);
			prepMovObjStmt.setInt (3,time);
			prepMovObjStmt.setInt (4,objClass);
			prepMovObjStmt.setInt (5,x);
			prepMovObjStmt.setInt (6,y);
			prepMovObjStmt.execute();
		}
		catch (Exception ex) {
			System.err.println("reportNewMovingOject: "+ex+" VALUES ("+
				id+","+repNum+","+time+","+objClass+","+x+","+y+")");
		}
	return repNum;
}

}