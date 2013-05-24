package spatialdb;

import java.io.InputStream;
import java.io.Reader;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;

import oracle.sdoapi.geom.CurvePolygon;
import oracle.sdoapi.geom.Geometry;
import oracle.sdoapi.geom.InvalidGeometryException;
import oracle.sdoapi.geom.LineString;
import oracle.sdoapi.geom.Point;
import oracle.sdoapi.geom.Polygon;

/**
 * The class extends a prepared statement by operations for handling spatial and
 * spatiotemporal attributes.
 * 
 * @author Thomas Brinkhoff
 * @version 1.11 05.07.2003 adapted to sdoapi 1.0.1
 * @version 1.10 24.06.2002 adapted to Java v1.4
 * @version 1.00 26.05.2001 first version
 */
public class PreparedSpatialStatement implements PreparedStatement {

	/**
	 * The original statement.
	 */
	private PreparedStatement s;
	/**
	 * The geometry factory.
	 */
	private oracle.sdoapi.geom.GeometryFactory gf;
	/**
	 * The geometry adapter.
	 */
	private oracle.sdoapi.adapter.GeometryAdapter adapter;

	/**
	 * The constant indicating that the current <code>ResultSet</code> object
	 * should be closed when calling <code>getMoreResults</code>.
	 */
	public int CLOSE_CURRENT_RESULT = 1;

	/**
	 * The constant indicating that the current <code>ResultSet</code> object
	 * should not be closed when calling <code>getMoreResults</code>.
	 */
	public int KEEP_CURRENT_RESULT = 2;

	/**
	 * The constant indicating that all <code>ResultSet</code> objects that have
	 * previously been kept open should be closed when calling
	 * <code>getMoreResults</code>.
	 */
	public int CLOSE_ALL_RESULTS = 3;

	/**
	 * The constant indicating that a batch statement executed successfully but
	 * that no count of the number of rows it affected is available.
	 */
	public int SUCCESS_NO_INFO = -2;

	/**
	 * The constant indicating that an error occured while executing a batch
	 * statement.
	 */
	public int EXECUTE_FAILED = -3;

	/**
	 * The constant indicating that generated keys should be made available for
	 * retrieval.
	 */
	public int RETURN_GENERATED_KEYS = 1;

	/**
	 * The constant indicating that generated keys should not be made available
	 * for retrieval.
	 */
	public int NO_GENERATED_KEYS = 2;

	/**
	 * PreparedGeometryStatement constructor.
	 * 
	 * @param prepStmt
	 *            prepared statement
	 */
	public PreparedSpatialStatement(PreparedStatement prepStmt)
			throws SQLException {
		s = prepStmt;
		gf = DefaultGeometryFactory.getFactory();
		adapter = oracle.sdoapi.OraSpatialManager.getGeometryAdapter("SDO",
				"8.1.6", null, oracle.sql.STRUCT.class, null,
				(oracle.jdbc.OracleConnection) s.getConnection());
	}

	/**
	 * JDBC 2.0
	 * 
	 * Adds a set of parameters to the batch.
	 * 
	 * @exception SQLException
	 *                if a database access error occurs
	 * @see Statement#addBatch
	 */
	public void addBatch() throws SQLException {
		s.addBatch();
	}

	/**
	 * JDBC 2.0
	 * 
	 * Adds a SQL command to the current batch of commmands for the statement.
	 * This method is optional.
	 * 
	 * @param sql
	 *            typically this is a static SQL INSERT or UPDATE statement
	 * @exception SQLException
	 *                if a database access error occurs, or the driver does not
	 *                support batch statements
	 */
	public void addBatch(String sql) throws SQLException {
		s.addBatch(sql);
	}

	/**
	 * Cancels this <code>Statement</code> object if both the DBMS and driver
	 * support aborting an SQL statement. This method can be used by one thread
	 * to cancel a statement that is being executed by another thread.
	 * 
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void cancel() throws SQLException {
		s.cancel();
	}

	/**
	 * JDBC 2.0
	 * 
	 * Makes the set of commands in the current batch empty. This method is
	 * optional.
	 * 
	 * @exception SQLException
	 *                if a database access error occurs or the driver does not
	 *                support batch statements
	 */
	public void clearBatch() throws SQLException {
		s.clearBatch();
	}

	/**
	 * Clears the current parameter values immediately.
	 * <P>
	 * In general, parameter values remain in force for repeated use of a
	 * Statement. Setting a parameter value automatically clears its previous
	 * value. However, in some cases it is useful to immediately release the
	 * resources used by the current parameter values; this can be done by
	 * calling clearParameters.
	 * 
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void clearParameters() throws SQLException {
		s.clearParameters();
	}

	/**
	 * Clears all the warnings reported on this <code>Statement</code> object.
	 * After a call to this method, the method <code>getWarnings</code> will
	 * return null until a new warning is reported for this Statement.
	 * 
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void clearWarnings() throws SQLException {
		s.clearWarnings();
	}

	/**
	 * Releases this <code>Statement</code> object's database and JDBC resources
	 * immediately instead of waiting for this to happen when it is
	 * automatically closed. It is generally good practice to release resources
	 * as soon as you are finished with them to avoid tying up database
	 * resources.
	 * <P>
	 * <B>Note:</B> A Statement is automatically closed when it is garbage
	 * collected. When a Statement is closed, its current ResultSet, if one
	 * exists, is also closed.
	 * 
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void close() throws SQLException {
		s.close();
	}

	/**
	 * Executes any kind of SQL statement. Some prepared statements return
	 * multiple results; the execute method handles these complex statements as
	 * well as the simpler form of statements handled by executeQuery and
	 * executeUpdate.
	 * 
	 * @exception SQLException
	 *                if a database access error occurs
	 * @see Statement#execute
	 */
	public boolean execute() throws SQLException {
		return s.execute();
	}

	/**
	 * Executes a SQL statement that may return multiple results. Under some
	 * (uncommon) situations a single SQL statement may return multiple result
	 * sets and/or update counts. Normally you can ignore this unless you are
	 * (1) executing a stored procedure that you know may return multiple
	 * results or (2) you are dynamically executing an unknown SQL string. The
	 * methods <code>execute</code>, <code>getMoreResults</code>,
	 * <code>getResultSet</code>, and <code>getUpdateCount</code> let you
	 * navigate through multiple results.
	 * 
	 * The <code>execute</code> method executes a SQL statement and indicates
	 * the form of the first result. You can then use getResultSet or
	 * getUpdateCount to retrieve the result, and getMoreResults to move to any
	 * subsequent result(s).
	 * 
	 * @param sql
	 *            any SQL statement
	 * @return true if the next result is a ResultSet; false if it is an update
	 *         count or there are no more results
	 * @exception SQLException
	 *                if a database access error occurs
	 * @see #getResultSet
	 * @see #getUpdateCount
	 * @see #getMoreResults
	 */
	public boolean execute(String sql) throws SQLException {
		return s.execute(sql);
	}

	/**
	 * Executes the given SQL statement, which may return multiple results, and
	 * signals the driver that the auto-generated keys indicated in the given
	 * array should be made available for retrieval. This array contains the
	 * indexes of the columns in the target table that contain the
	 * auto-generated keys that should be made available. The driver will ignore
	 * the array if the given SQL statement is not an <code>INSERT</code>
	 * statement.
	 * <P>
	 * Under some (uncommon) situations, a single SQL statement may return
	 * multiple result sets and/or update counts. Normally you can ignore this
	 * unless you are (1) executing a stored procedure that you know may return
	 * multiple results or (2) you are dynamically executing an unknown SQL
	 * string.
	 * <P>
	 * The <code>execute</code> method executes an SQL statement and indicates
	 * the form of the first result. You must then use the methods
	 * <code>getResultSet</code> or <code>getUpdateCount</code> to retrieve the
	 * result, and <code>getMoreResults</code> to move to any subsequent
	 * result(s).
	 * 
	 * @param sql
	 *            any SQL statement
	 * @param columnIndexes
	 *            an array of the indexes of the columns in the inserted row
	 *            that should be made available for retrieval by a call to the
	 *            method <code>getGeneratedKeys</code>
	 * @return <code>true</code> if the first result is a <code>ResultSet</code>
	 *         object; <code>false</code> if it is an update count or there are
	 *         no results
	 * @exception SQLException
	 *                if a database access error occurs
	 * @see #getResultSet
	 * @see #getUpdateCount
	 * @see #getMoreResults
	 */
	public boolean execute(String sql, int columnIndexes[]) throws SQLException {
		System.err
				.println("PreparedSpatialStatement.execute(String,int[]) is not implemented!");
		return false;
	}

	/**
	 * Executes the given SQL statement, which may return multiple results, and
	 * signals the driver that the auto-generated keys indicated in the given
	 * array should be made available for retrieval. This array contains the
	 * names of the columns in the target table that contain the auto-generated
	 * keys that should be made available. The driver will ignore the array if
	 * the given SQL statement is not an <code>INSERT</code> statement.
	 * <P>
	 * In some (uncommon) situations, a single SQL statement may return multiple
	 * result sets and/or update counts. Normally you can ignore this unless you
	 * are (1) executing a stored procedure that you know may return multiple
	 * results or (2) you are dynamically executing an unknown SQL string.
	 * <P>
	 * The <code>execute</code> method executes an SQL statement and indicates
	 * the form of the first result. You must then use the methods
	 * <code>getResultSet</code> or <code>getUpdateCount</code> to retrieve the
	 * result, and <code>getMoreResults</code> to move to any subsequent
	 * result(s).
	 * 
	 * @param sql
	 *            any SQL statement
	 * @param columnNames
	 *            an array of the names of the columns in the inserted row that
	 *            should be made available for retrieval by a call to the method
	 *            <code>getGeneratedKeys</code>
	 * @return <code>true</code> if the next result is a <code>ResultSet</code>
	 *         object; <code>false</code> if it is an update count or there are
	 *         no more results
	 * @exception SQLException
	 *                if a database access error occurs
	 * @see #getResultSet
	 * @see #getUpdateCount
	 * @see #getMoreResults
	 * @see #getGeneratedKeys
	 */
	public boolean execute(String sql, String columnNames[])
			throws SQLException {
		System.err
				.println("PreparedSpatialStatement.execute(String,String[]) is not implemented!");
		return false;
	}

	/**
	 * Executes the given SQL statement, which may return multiple results, and
	 * signals the driver that any auto-generated keys should be made available
	 * for retrieval. The driver will ignore this signal if the SQL statement is
	 * not an <code>INSERT</code> statement.
	 * <P>
	 * In some (uncommon) situations, a single SQL statement may return multiple
	 * result sets and/or update counts. Normally you can ignore this unless you
	 * are (1) executing a stored procedure that you know may return multiple
	 * results or (2) you are dynamically executing an unknown SQL string.
	 * <P>
	 * The <code>execute</code> method executes an SQL statement and indicates
	 * the form of the first result. You must then use the methods
	 * <code>getResultSet</code> or <code>getUpdateCount</code> to retrieve the
	 * result, and <code>getMoreResults</code> to move to any subsequent
	 * result(s).
	 * 
	 * @param sql
	 *            any SQL statement
	 * @param autoGeneratedKeys
	 *            a constant indicating whether auto-generated keys should be
	 *            made available for retrieval using the method
	 *            <code>getGeneratedKeys</code>; one of the following constants:
	 *            <code>Statement.RETURN_GENERATED_KEYS</code> or
	 *            <code>Statement.NO_GENERATED_KEYS</code>
	 * @return <code>true</code> if the first result is a <code>ResultSet</code>
	 *         object; <code>false</code> if it is an update count or there are
	 *         no results
	 * @exception SQLException
	 *                if a database access error occurs
	 * @see #getResultSet
	 * @see #getUpdateCount
	 * @see #getMoreResults
	 * @see #getGeneratedKeys
	 */
	public boolean execute(String sql, int autoGeneratedKeys)
			throws SQLException {
		System.err
				.println("PreparedSpatialStatement.execute(String,int) is not implemented!");
		return false;
	}

	/**
	 * JDBC 2.0
	 * 
	 * Submits a batch of commands to the database for execution. This method is
	 * optional.
	 * 
	 * @return an array of update counts containing one element for each command
	 *         in the batch. The array is ordered according to the order in
	 *         which commands were inserted into the batch.
	 * @exception SQLException
	 *                if a database access error occurs or the driver does not
	 *                support batch statements
	 */
	public int[] executeBatch() throws SQLException {
		return s.executeBatch();
	}

	/**
	 * Executes the SQL query in this <code>PreparedStatement</code> object and
	 * returns the result set generated by the query.
	 * 
	 * @return a SpatialResultSet that contains the data produced by the query;
	 *         never null
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public ResultSet executeQuery() throws SQLException {
		return SpatialResultSet.newSet(s.executeQuery(), s.getConnection());
	}

	/**
	 * Executes a SQL statement that returns a single ResultSet.
	 * 
	 * @param sql
	 *            typically this is a static SQL SELECT statement
	 * @return a SpatialResultSet that contains the data produced by the query;
	 *         never null
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public ResultSet executeQuery(String sql) throws SQLException {
		return SpatialResultSet.newSet(s.executeQuery(sql), s.getConnection());
	}

	/**
	 * Executes the SQL INSERT, UPDATE or DELETE statement in this
	 * <code>PreparedStatement</code> object. In addition, SQL statements that
	 * return nothing, such as SQL DDL statements, can be executed.
	 * 
	 * @return either the row count for INSERT, UPDATE or DELETE statements; or
	 *         0 for SQL statements that return nothing
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public int executeUpdate() throws SQLException {
		return s.executeUpdate();
	}

	/**
	 * Executes an SQL INSERT, UPDATE or DELETE statement. In addition, SQL
	 * statements that return nothing, such as SQL DDL statements, can be
	 * executed.
	 * 
	 * @param sql
	 *            a SQL INSERT, UPDATE or DELETE statement or a SQL statement
	 *            that returns nothing
	 * @return either the row count for INSERT, UPDATE or DELETE or 0 for SQL
	 *         statements that return nothing
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public int executeUpdate(String sql) throws SQLException {
		return s.executeUpdate(sql);
	}

	/**
	 * Executes the given SQL statement and signals the driver that the
	 * auto-generated keys indicated in the given array should be made available
	 * for retrieval. The driver will ignore the array if the SQL statement is
	 * not an <code>INSERT</code> statement.
	 * 
	 * @param sql
	 *            an SQL <code>INSERT</code>, <code>UPDATE</code> or
	 *            <code>DELETE</code> statement or an SQL statement that returns
	 *            nothing, such as an SQL DDL statement
	 * @param columnIndexes
	 *            an array of column indexes indicating the columns that should
	 *            be returned from the inserted row
	 * @return either the row count for <code>INSERT</code>, <code>UPDATE</code>
	 *         , or <code>DELETE</code> statements, or 0 for SQL statements that
	 *         return nothing
	 * @exception SQLException
	 *                if a database access error occurs or the SQL statement
	 *                returns a <code>ResultSet</code> object
	 */
	public int executeUpdate(String sql, int columnIndexes[])
			throws SQLException {
		System.err
				.println("PreparedSpatialStatement.executeUpdate(String,int[]) is not implemented!");
		return 0;
	}

	/**
	 * Executes the given SQL statement and signals the driver that the
	 * auto-generated keys indicated in the given array should be made available
	 * for retrieval. The driver will ignore the array if the SQL statement is
	 * not an <code>INSERT</code> statement.
	 * 
	 * @param sql
	 *            an SQL <code>INSERT</code>, <code>UPDATE</code> or
	 *            <code>DELETE</code> statement or an SQL statement that returns
	 *            nothing
	 * @param columnNames
	 *            an array of the names of the columns that should be returned
	 *            from the inserted row
	 * @return either the row count for <code>INSERT</code>, <code>UPDATE</code>
	 *         , or <code>DELETE</code> statements, or 0 for SQL statements that
	 *         return nothing
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public int executeUpdate(String sql, String columnNames[])
			throws SQLException {
		System.err
				.println("PreparedSpatialStatement.executeUpdate(String,String[]) is not implemented!");
		return 0;
	}

	/**
	 * Executes the given SQL statement and signals the driver with the given
	 * flag about whether the auto-generated keys produced by this
	 * <code>Statement</code> object should be made available for retrieval.
	 * 
	 * @param sql
	 *            must be an SQL <code>INSERT</code>, <code>UPDATE</code> or
	 *            <code>DELETE</code> statement or an SQL statement that returns
	 *            nothing
	 * @param autoGeneratedKeys
	 *            a flag indicating whether auto-generated keys should be made
	 *            available for retrieval; one of the following constants:
	 *            <code>Statement.RETURN_GENERATED_KEYS</code>
	 *            <code>Statement.NO_GENERATED_KEYS</code>
	 * @return either the row count for <code>INSERT</code>, <code>UPDATE</code>
	 *         or <code>DELETE</code> statements, or <code>0</code> for SQL
	 *         statements that return nothing
	 * @exception SQLException
	 *                if a database access error occurs, the given SQL statement
	 *                returns a <code>ResultSet</code> object, or the given
	 *                constant is not one of those allowed
	 */
	public int executeUpdate(String sql, int autoGeneratedKeys)
			throws SQLException {
		System.err
				.println("PreparedSpatialStatement.executeUpdate(String,int) is not implemented!");
		return 0;
	}

	/**
	 * JDBC 2.0
	 * 
	 * Returns the <code>Connection</code> object that produced this
	 * <code>Statement</code> object.
	 * 
	 * @return the connection that produced this statement
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public Connection getConnection() throws SQLException {
		return s.getConnection();
	}

	/**
	 * JDBC 2.0
	 * 
	 * Retrieves the direction for fetching rows from database tables that is
	 * the default for result sets generated from this <code>Statement</code>
	 * object. If this <code>Statement</code> object has not set a fetch
	 * direction by calling the method <code>setFetchDirection</code>, the
	 * return value is implementation-specific.
	 * 
	 * @return the default fetch direction for result sets generated from this
	 *         <code>Statement</code> object
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public int getFetchDirection() throws SQLException {
		return s.getFetchDirection();
	}

	/**
	 * JDBC 2.0
	 * 
	 * Retrieves the number of result set rows that is the default fetch size
	 * for result sets generated from this <code>Statement</code> object. If
	 * this <code>Statement</code> object has not set a fetch size by calling
	 * the method <code>setFetchSize</code>, the return value is
	 * implementation-specific.
	 * 
	 * @return the default fetch size for result sets generated from this
	 *         <code>Statement</code> object
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public int getFetchSize() throws SQLException {
		return s.getFetchSize();
	}

	/**
	 * Retrieves any auto-generated keys created as a result of executing this
	 * <code>Statement</code> object. If this <code>Statement</code> object did
	 * not generate any keys, an empty <code>ResultSet</code> object is
	 * returned.
	 * 
	 * @return a <code>ResultSet</code> object containing the auto-generated
	 *         key(s) generated by the execution of this <code>Statement</code>
	 *         object
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public ResultSet getGeneratedKeys() throws SQLException {
		System.err
				.println("PreparedSpatialStatement.getGeneratedKeys() is not implemented!");
		return null;
	}

	/**
	 * Returns the maximum number of bytes allowed for any column value. This
	 * limit is the maximum number of bytes that can be returned for any column
	 * value. The limit applies only to BINARY, VARBINARY, LONGVARBINARY, CHAR,
	 * VARCHAR, and LONGVARCHAR columns. If the limit is exceeded, the excess
	 * data is silently discarded.
	 * 
	 * @return the current max column size limit; zero means unlimited
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public int getMaxFieldSize() throws SQLException {
		return s.getMaxFieldSize();
	}

	/**
	 * Retrieves the maximum number of rows that a ResultSet can contain. If the
	 * limit is exceeded, the excess rows are silently dropped.
	 * 
	 * @return the current max row limit; zero means unlimited
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public int getMaxRows() throws SQLException {
		return s.getMaxRows();
	}

	/**
	 * JDBC 2.0
	 * 
	 * Gets the number, types and properties of a ResultSet's columns.
	 * 
	 * @return the description of a ResultSet's columns
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public ResultSetMetaData getMetaData() throws SQLException {
		return s.getMetaData();
	}

	/**
	 * Moves to a Statement's next result. It returns true if this result is a
	 * ResultSet. This method also implicitly closes any current ResultSet
	 * obtained with getResultSet.
	 * 
	 * There are no more results when (!getMoreResults() && (getUpdateCount() ==
	 * -1)
	 * 
	 * @return true if the next result is a ResultSet; false if it is an update
	 *         count or there are no more results
	 * @exception SQLException
	 *                if a database access error occurs
	 * @see #execute
	 */
	public boolean getMoreResults() throws SQLException {
		return s.getMoreResults();
	}

	/**
	 * Moves to this <code>Statement</code> object's next result, deals with any
	 * current <code>ResultSet</code> object(s) according to the instructions
	 * specified by the given flag, and returns <code>true</code> if the next
	 * result is a <code>ResultSet</code> object.
	 * 
	 * <P>
	 * There are no more results when the following is true:
	 * 
	 * <PRE>
	 *      <code>(!getMoreResults() && (getUpdateCount() == -1)</code>
	 * </PRE>
	 * 
	 * @param current
	 *            one of the following <code>Statement</code> constants
	 *            indicating what should happen to current
	 *            <code>ResultSet</code> objects obtained using the method
	 *            <code>getResultSet</code: <code>CLOSE_CURRENT_RESULT</code>,
	 *            <code>KEEP_CURRENT_RESULT</code>, or
	 *            <code>CLOSE_ALL_RESULTS</code>
	 * @return <code>true</code> if the next result is a <code>ResultSet</code>
	 *         object; <code>false</code> if it is an update count or there are
	 *         no more results
	 * @exception SQLException
	 *                if a database access error occurs
	 * @see #execute
	 */
	public boolean getMoreResults(int current) throws SQLException {
		System.err
				.println("PreparedSpatialStatement.getMoreResults(int) is not implemented!");
		return false;
	}

	/**
	 * Retrieves the number of seconds the driver will wait for a Statement to
	 * execute. If the limit is exceeded, a SQLException is thrown.
	 * 
	 * @return the current query timeout limit in seconds; zero means unlimited
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public int getQueryTimeout() throws SQLException {
		return s.getQueryTimeout();
	}

	/**
	 * Returns the current result as a <code>ResultSet</code> object. This
	 * method should be called only once per result.
	 * 
	 * @return the current result as a SpatialResultSet; null if the result is
	 *         an update count or there are no more results
	 * @exception SQLException
	 *                if a database access error occurs
	 * @see #execute
	 */
	public ResultSet getResultSet() throws SQLException {
		return SpatialResultSet.newSet(s.getResultSet(), s.getConnection());
	}

	/**
	 * JDBC 2.0
	 * 
	 * Retrieves the result set concurrency.
	 */
	public int getResultSetConcurrency() throws SQLException {
		return s.getResultSetConcurrency();
	}

	/**
	 * Retrieves the result set holdability for <code>ResultSet</code> objects
	 * generated by this <code>Statement</code> object.
	 * 
	 * @return either <code>ResultSet.HOLD_CURSORS_OVER_COMMIT</code> or
	 *         <code>ResultSet.CLOSE_CURSORS_AT_COMMIT</code>
	 * @exception SQLException
	 *                if a database access error occurs
	 * 
	 * @since 1.4
	 */
	public int getResultSetHoldability() throws SQLException {
		System.err
				.println("PreparedSpatialStatement.getResultSetHoldability() is not implemented!");
		return 0;
	}

	/**
	 * JDBC 2.0
	 * 
	 * Determine the result set type.
	 */
	public int getResultSetType() throws SQLException {
		return s.getResultSetType();
	}

	/**
	 * Returns the current result as an update count; if the result is a
	 * ResultSet or there are no more results, -1 is returned. This method
	 * should be called only once per result.
	 * 
	 * @return the current result as an update count; -1 if it is a ResultSet or
	 *         there are no more results
	 * @exception SQLException
	 *                if a database access error occurs
	 * @see #execute
	 */
	public int getUpdateCount() throws SQLException {
		return s.getUpdateCount();
	}

	/**
	 * Retrieves the first warning reported by calls on this Statement.
	 * Subsequent Statement warnings will be chained to this SQLWarning.
	 * 
	 * <p>
	 * The warning chain is automatically cleared each time a statement is
	 * (re)executed.
	 * 
	 * <P>
	 * <B>Note:</B> If you are processing a ResultSet, any warnings associated
	 * with ResultSet reads will be chained on the ResultSet object.
	 * 
	 * @return the first SQLWarning or null
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public SQLWarning getWarnings() throws SQLException {
		return s.getWarnings();
	}

	/**
	 * JDBC 2.0
	 * 
	 * Sets an Array parameter.
	 * 
	 * @param i
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            an object representing an SQL array
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void setArray(int i, Array x) throws SQLException {
		s.setArray(i, x);
	}

	/**
	 * Sets the designated parameter to the given input stream, which will have
	 * the specified number of bytes. When a very large ASCII value is input to
	 * a LONGVARCHAR parameter, it may be more practical to send it via a
	 * java.io.InputStream. JDBC will read the data from the stream as needed,
	 * until it reaches end-of-file. The JDBC driver will do any necessary
	 * conversion from ASCII to the database char format.
	 * 
	 * <P>
	 * <B>Note:</B> This stream object can either be a standard Java stream
	 * object or your own subclass that implements the standard interface.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the Java input stream that contains the ASCII parameter value
	 * @param length
	 *            the number of bytes in the stream
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void setAsciiStream(int parameterIndex, java.io.InputStream x,
			int length) throws SQLException {
		s.setAsciiStream(parameterIndex, x, length);
	}

	/**
	 * Sets the designated parameter to a java.lang.BigDecimal value. The driver
	 * converts this to an SQL NUMERIC value when it sends it to the database.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the parameter value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void setBigDecimal(int parameterIndex, java.math.BigDecimal x)
			throws SQLException {
		s.setBigDecimal(parameterIndex, x);
	}

	/**
	 * Sets the designated parameter to the given input stream, which will have
	 * the specified number of bytes. When a very large binary value is input to
	 * a LONGVARBINARY parameter, it may be more practical to send it via a
	 * java.io.InputStream. JDBC will read the data from the stream as needed,
	 * until it reaches end-of-file.
	 * 
	 * <P>
	 * <B>Note:</B> This stream object can either be a standard Java stream
	 * object or your own subclass that implements the standard interface.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the java input stream which contains the binary parameter
	 *            value
	 * @param length
	 *            the number of bytes in the stream
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void setBinaryStream(int parameterIndex, java.io.InputStream x,
			int length) throws SQLException {
		s.setBinaryStream(parameterIndex, x, length);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Sets a BLOB parameter.
	 * 
	 * @param i
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            an object representing a BLOB
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void setBlob(int i, Blob x) throws SQLException {
		s.setBlob(i, x);
	}

	/**
	 * Sets the designated parameter to a Java boolean value. The driver
	 * converts this to an SQL BIT value when it sends it to the database.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the parameter value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		s.setBoolean(parameterIndex, x);
	}

	/**
	 * Sets the designated parameter to a Java byte value. The driver converts
	 * this to an SQL TINYINT value when it sends it to the database.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the parameter value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void setByte(int parameterIndex, byte x) throws SQLException {
		s.setByte(parameterIndex, x);
	}

	/**
	 * Sets the designated parameter to a Java array of bytes. The driver
	 * converts this to an SQL VARBINARY or LONGVARBINARY (depending on the
	 * argument's size relative to the driver's limits on VARBINARYs) when it
	 * sends it to the database.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the parameter value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void setBytes(int parameterIndex, byte[] x) throws SQLException {
		s.setBytes(parameterIndex, x);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Sets the designated parameter to the given <code>Reader</code> object,
	 * which is the given number of characters long. When a very large UNICODE
	 * value is input to a LONGVARCHAR parameter, it may be more practical to
	 * send it via a java.io.Reader. JDBC will read the data from the stream as
	 * needed, until it reaches end-of-file. The JDBC driver will do any
	 * necessary conversion from UNICODE to the database char format.
	 * 
	 * <P>
	 * <B>Note:</B> This stream object can either be a standard Java stream
	 * object or your own subclass that implements the standard interface.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the java reader which contains the UNICODE data
	 * @param length
	 *            the number of characters in the stream
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void setCharacterStream(int parameterIndex, java.io.Reader reader,
			int length) throws SQLException {
		s.setCharacterStream(parameterIndex, reader, length);
	}

	/**
	 * Sets a circle parameter.
	 * 
	 * @param index
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            x-coordinate of center
	 * @param y
	 *            y-coordinate of center
	 * @param radius
	 *            the radius
	 * @exception SQLException
	 *                if a database access, geometry or adapter error occurs
	 */
	public void setCircle(int index, double x, double y, double radius)
			throws SQLException {
		try {
			CurvePolygon circle = gf.createCircle(x, y, radius);
			setGeometry(index, circle);
		} catch (InvalidGeometryException e) {
			throw new SQLException(e.toString());
		}
	}

	/**
	 * JDBC 2.0
	 * 
	 * Sets a CLOB parameter.
	 * 
	 * @param i
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            an object representing a CLOB
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void setClob(int i, Clob x) throws SQLException {
		s.setClob(i, x);
	}

	/**
	 * Defines the SQL cursor name that will be used by subsequent Statement
	 * <code>execute</code> methods. This name can then be used in SQL
	 * positioned update/delete statements to identify the current row in the
	 * ResultSet generated by this statement. If the database doesn't support
	 * positioned update/delete, this method is a noop. To insure that a cursor
	 * has the proper isolation level to support updates, the cursor's SELECT
	 * statement should be of the form 'select for update ...'. If the 'for
	 * update' phrase is omitted, positioned updates may fail.
	 * 
	 * <P>
	 * <B>Note:</B> By definition, positioned update/delete execution must be
	 * done by a different Statement than the one which generated the ResultSet
	 * being used for positioning. Also, cursor names must be unique within a
	 * connection.
	 * 
	 * @param name
	 *            the new cursor name, which must be unique within a connection
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void setCursorName(String name) throws SQLException {
		s.setCursorName(name);
	}

	/**
	 * Sets the designated parameter to a java.sql.Date value. The driver
	 * converts this to an SQL DATE value when it sends it to the database.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the parameter value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void setDate(int parameterIndex, Date x) throws SQLException {
		s.setDate(parameterIndex, x);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Sets the designated parameter to a java.sql.Date value, using the given
	 * <code>Calendar</code> object. The driver uses the <code>Calendar</code>
	 * object to construct an SQL DATE, which the driver then sends to the
	 * database. With a a <code>Calendar</code> object, the driver can calculate
	 * the date taking into account a custom timezone and locale. If no
	 * <code>Calendar</code> object is specified, the driver uses the default
	 * timezone and locale.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the parameter value
	 * @param cal
	 *            the <code>Calendar</code> object the driver will use to
	 *            construct the date
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void setDate(int parameterIndex, Date x, java.util.Calendar cal)
			throws SQLException {
		s.setDate(parameterIndex, x, cal);
	}

	/**
	 * Sets the designated parameter to a Java double value. The driver converts
	 * this to an SQL DOUBLE value when it sends it to the database.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the parameter value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void setDouble(int parameterIndex, double x) throws SQLException {
		s.setDouble(parameterIndex, x);
	}

	/**
	 * Sets escape processing on or off. If escape scanning is on (the default),
	 * the driver will do escape substitution before sending the SQL to the
	 * database.
	 * 
	 * Note: Since prepared statements have usually been parsed prior to making
	 * this call, disabling escape processing for prepared statements will have
	 * no effect.
	 * 
	 * @param enable
	 *            true to enable; false to disable
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void setEscapeProcessing(boolean enable) throws SQLException {
		s.setEscapeProcessing(enable);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Gives the driver a hint as to the direction in which the rows in a result
	 * set will be processed. The hint applies only to result sets created using
	 * this Statement object. The default value is ResultSet.FETCH_FORWARD.
	 * <p>
	 * Note that this method sets the default fetch direction for result sets
	 * generated by this <code>Statement</code> object. Each result set has its
	 * own methods for getting and setting its own fetch direction.
	 * 
	 * @param direction
	 *            the initial direction for processing rows
	 * @exception SQLException
	 *                if a database access error occurs or the given direction
	 *                is not one of ResultSet.FETCH_FORWARD,
	 *                ResultSet.FETCH_REVERSE, or ResultSet.FETCH_UNKNOWN
	 */
	public void setFetchDirection(int direction) throws SQLException {
		s.setFetchDirection(direction);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Gives the JDBC driver a hint as to the number of rows that should be
	 * fetched from the database when more rows are needed. The number of rows
	 * specified affects only result sets created using this statement. If the
	 * value specified is zero, then the hint is ignored. The default value is
	 * zero.
	 * 
	 * @param rows
	 *            the number of rows to fetch
	 * @exception SQLException
	 *                if a database access error occurs, or the condition 0 <=
	 *                rows <= this.getMaxRows() is not satisfied.
	 */
	public void setFetchSize(int rows) throws SQLException {
		s.setFetchSize(rows);
	}

	/**
	 * Sets the designated parameter to a Java float value. The driver converts
	 * this to an SQL FLOAT value when it sends it to the database.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the parameter value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void setFloat(int parameterIndex, float x) throws SQLException {
		s.setFloat(parameterIndex, x);
	}

	/**
	 * Sets a sdoapi.geom.Geometry parameter.
	 * 
	 * @param index
	 *            the first parameter is 1, the second is 2, ...
	 * @param geometry
	 *            the geometry
	 * @exception SQLException
	 *                if a database access, geometry or adapter error occurs
	 */
	public void setGeometry(int index, Geometry geometry) throws SQLException {
		try {
			// if (!geometry.isValid())
			// throw new SQLException("geometry is not valid");
			s.setObject(index,
					adapter.exportGeometry(oracle.sql.STRUCT.class, geometry));
		} catch (InvalidGeometryException e) {
			throw new SQLException(e.toString());
		} catch (oracle.sdoapi.adapter.GeometryOutputTypeNotSupportedException e) {
			throw new SQLException(e.toString());
		}
	}

	/**
	 * Sets the designated parameter to a Java int value. The driver converts
	 * this to an SQL INTEGER value when it sends it to the database.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the parameter value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void setInt(int parameterIndex, int x) throws SQLException {
		s.setInt(parameterIndex, x);
	}

	/**
	 * Sets the designated parameter to a Java long value. The driver converts
	 * this to an SQL BIGINT value when it sends it to the database.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the parameter value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void setLong(int parameterIndex, long x) throws SQLException {
		s.setLong(parameterIndex, x);
	}

	/**
	 * Sets the limit for the maximum number of bytes in a column to the given
	 * number of bytes. This is the maximum number of bytes that can be returned
	 * for any column value. This limit applies only to BINARY, VARBINARY,
	 * LONGVARBINARY, CHAR, VARCHAR, and LONGVARCHAR fields. If the limit is
	 * exceeded, the excess data is silently discarded. For maximum portability,
	 * use values greater than 256.
	 * 
	 * @param max
	 *            the new max column size limit; zero means unlimited
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void setMaxFieldSize(int max) throws SQLException {
		s.setMaxFieldSize(max);
	}

	/**
	 * Sets the limit for the maximum number of rows that any ResultSet can
	 * contain to the given number. If the limit is exceeded, the excess rows
	 * are silently dropped.
	 * 
	 * @param max
	 *            the new max rows limit; zero means unlimited
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void setMaxRows(int max) throws SQLException {
		s.setMaxRows(max);
	}

	/**
	 * Sets the designated parameter to SQL NULL.
	 * 
	 * <P>
	 * <B>Note:</B> You must specify the parameter's SQL type.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param sqlType
	 *            the SQL type code defined in java.sql.Types
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		s.setNull(parameterIndex, sqlType);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Sets the designated parameter to SQL NULL. This version of setNull should
	 * be used for user-named types and REF type parameters. Examples of
	 * user-named types include: STRUCT, DISTINCT, JAVA_OBJECT, and named array
	 * types.
	 * 
	 * <P>
	 * <B>Note:</B> To be portable, applications must give the SQL type code and
	 * the fully-qualified SQL type name when specifying a NULL user-defined or
	 * REF parameter. In the case of a user-named type the name is the type name
	 * of the parameter itself. For a REF parameter the name is the type name of
	 * the referenced type. If a JDBC driver does not need the type code or type
	 * name information, it may ignore it.
	 * 
	 * Although it is intended for user-named and Ref parameters, this method
	 * may be used to set a null parameter of any JDBC type. If the parameter
	 * does not have a user-named or REF type, the given typeName is ignored.
	 * 
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param sqlType
	 *            a value from java.sql.Types
	 * @param typeName
	 *            the fully-qualified name of an SQL user-named type, ignored if
	 *            the parameter is not a user-named type or REF
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void setNull(int paramIndex, int sqlType, String typeName)
			throws SQLException {
		s.setNull(paramIndex, sqlType, typeName);
	}

	/**
	 * <p>
	 * Sets the value of a parameter using an object; use the java.lang
	 * equivalent objects for integral values.
	 * 
	 * <p>
	 * The JDBC specification specifies a standard mapping from Java Object
	 * types to SQL types. The given argument java object will be converted to
	 * the corresponding SQL type before being sent to the database.
	 * 
	 * <p>
	 * Note that this method may be used to pass datatabase- specific abstract
	 * data types, by using a Driver-specific Java type.
	 * 
	 * If the object is of a class implementing SQLData, the JDBC driver should
	 * call its method <code>writeSQL</code> to write it to the SQL data stream.
	 * If, on the other hand, the object is of a class implementing Ref, Blob,
	 * Clob, Struct, or Array, then the driver should pass it to the database as
	 * a value of the corresponding SQL type.
	 * 
	 * This method throws an exception if there is an ambiguity, for example, if
	 * the object is of a class implementing more than one of those interfaces.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the object containing the input parameter value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void setObject(int parameterIndex, Object x) throws SQLException {
		s.setObject(parameterIndex, x);
	}

	/**
	 * Sets the value of the designated parameter with the given object. This
	 * method is like setObject above, except that it assumes a scale of zero.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the object containing the input parameter value
	 * @param targetSqlType
	 *            the SQL type (as defined in java.sql.Types) to be sent to the
	 *            database
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void setObject(int parameterIndex, Object x, int targetSqlType)
			throws SQLException {
		s.setObject(parameterIndex, x, targetSqlType);
	}

	/**
	 * <p>
	 * Sets the value of a parameter using an object. The second argument must
	 * be an object type; for integral values, the java.lang equivalent objects
	 * should be used.
	 * 
	 * <p>
	 * The given Java object will be converted to the targetSqlType before being
	 * sent to the database.
	 * 
	 * If the object has a custom mapping (is of a class implementing SQLData),
	 * the JDBC driver should call its method <code>writeSQL</code> to write it
	 * to the SQL data stream. If, on the other hand, the object is of a class
	 * implementing Ref, Blob, Clob, Struct, or Array, the driver should pass it
	 * to the database as a value of the corresponding SQL type.
	 * 
	 * <p>
	 * Note that this method may be used to pass datatabase- specific abstract
	 * data types.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the object containing the input parameter value
	 * @param targetSqlType
	 *            the SQL type (as defined in java.sql.Types) to be sent to the
	 *            database. The scale argument may further qualify this type.
	 * @param scale
	 *            for java.sql.Types.DECIMAL or java.sql.Types.NUMERIC types,
	 *            this is the number of digits after the decimal point. For all
	 *            other types, this value will be ignored.
	 * @exception SQLException
	 *                if a database access error occurs
	 * @see Types
	 */
	public void setObject(int parameterIndex, Object x, int targetSqlType,
			int scale) throws SQLException {
		s.setObject(parameterIndex, x, targetSqlType, scale);
	}

	/**
	 * Sets a point parameter.
	 * 
	 * @param index
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            x-coordinate
	 * @param y
	 *            y-coordinate
	 * @exception SQLException
	 *                if a database access, geometry or adapter error occurs
	 */
	public void setPoint(int index, double x, double y) throws SQLException {
		try {
			Point point = gf.createPoint(x, y);
			setGeometry(index, point);
		} catch (InvalidGeometryException e) {
			throw new SQLException(e.toString());
		}
	}

	/**
	 * Sets a polygon parameter.
	 * 
	 * @param index
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            array of x-coordinates
	 * @param y
	 *            array of y-coordinates
	 * @param n
	 *            number of points
	 * @exception SQLException
	 *                if a database access, geometry or adapter error occurs
	 */
	public void setPolygon(int index, double x[], double y[], int n)
			throws SQLException {
		if ((x == null) || (y == null))
			throw new SQLException("x- or y-array is not set");
		if ((x.length < n) || (y.length < n))
			throw new SQLException("x- or y-array too small");
		try {
			double points[] = new double[2 * n];
			for (int i = 0; i < n; i++) {
				points[2 * i] = x[i];
				points[2 * i + 1] = y[i];
			}
			Polygon polygon = gf.createPolygon(gf.createLineString(points),
					null);
			setGeometry(index, polygon);
		} catch (InvalidGeometryException e) {
			throw new SQLException(e.toString());
		}
	}

	/**
	 * Sets a polygon parameter.
	 * 
	 * @param index
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            array of x-coordinates
	 * @param y
	 *            array of y-coordinates
	 * @param n
	 *            number of points
	 * @exception SQLException
	 *                if a database access, geometry or adapter error occurs
	 */
	public void setPolygon(int index, int x[], int y[], int n)
			throws SQLException {
		if ((x == null) || (y == null))
			throw new SQLException("x- or y-array is not set");
		if ((x.length < n) || (y.length < n))
			throw new SQLException("x- or y-array too small");
		try {
			double points[] = new double[2 * n];
			for (int i = 0; i < n; i++) {
				points[2 * i] = x[i];
				points[2 * i + 1] = y[i];
			}
			Polygon polygon = gf.createPolygon(gf.createLineString(points),
					null);
			setGeometry(index, polygon);
		} catch (InvalidGeometryException e) {
			throw new SQLException(e.toString());
		}
	}

	/**
	 * Sets a polyline parameter.
	 * 
	 * @param index
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            array of x-coordinates
	 * @param y
	 *            array of y-coordinates
	 * @param n
	 *            number of points
	 * @exception SQLException
	 *                if a database access, geometry or adapter error occurs
	 */
	public void setPolyline(int index, double x[], double y[], int n)
			throws SQLException {
		if ((x == null) || (y == null))
			throw new SQLException("x- or y-array is not set");
		if ((x.length < n) || (y.length < n))
			throw new SQLException("x- or y-array too small");
		try {
			double points[] = new double[2 * n];
			for (int i = 0; i < n; i++) {
				points[2 * i] = x[i];
				points[2 * i + 1] = y[i];
			}
			LineString line = gf.createLineString(points);
			setGeometry(index, line);
		} catch (InvalidGeometryException e) {
			throw new SQLException(e.toString());
		}
	}

	/**
	 * Sets a polyline parameter.
	 * 
	 * @param index
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            array of x-coordinates
	 * @param y
	 *            array of y-coordinates
	 * @param n
	 *            number of points
	 * @exception SQLException
	 *                if a database access, geometry or adapter error occurs
	 */
	public void setPolyline(int index, int x[], int y[], int n)
			throws SQLException {
		if ((x == null) || (y == null))
			throw new SQLException("x- or y-array is not set");
		if ((x.length < n) || (y.length < n))
			throw new SQLException("x- or y-array too small");
		try {
			double points[] = new double[2 * n];
			for (int i = 0; i < n; i++) {
				points[2 * i] = x[i];
				points[2 * i + 1] = y[i];
			}
			LineString line = gf.createLineString(points);
			setGeometry(index, line);
		} catch (InvalidGeometryException e) {
			throw new SQLException(e.toString());
		}
	}

	/**
	 * Sets the number of seconds the driver will wait for a Statement to
	 * execute to the given number of seconds. If the limit is exceeded, a
	 * SQLException is thrown.
	 * 
	 * @param seconds
	 *            the new query timeout limit in seconds; zero means unlimited
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void setQueryTimeout(int seconds) throws SQLException {
		s.setQueryTimeout(seconds);
	}

	/**
	 * Sets a rectangle parameter.
	 * 
	 * @param index
	 *            the first parameter is 1, the second is 2, ...
	 * @param xmin
	 *            minimum x-coordinate
	 * @param ymin
	 *            minimum y-coordinate
	 * @param xmax
	 *            maximum x-coordinate
	 * @param ymax
	 *            maximum y-coordinate
	 * @exception SQLException
	 *                if a database access, geometry or adapter error occurs
	 */
	public void setRectangle(int index, double xmin, double ymin, double xmax,
			double ymax) throws SQLException {
		try {
			Polygon rect = gf.createRectangle(Math.min(xmin, xmax),
					Math.min(ymin, ymax), Math.max(xmin, xmax),
					Math.max(ymin, ymax));
			setGeometry(index, rect);
		} catch (InvalidGeometryException e) {
			throw new SQLException(e.toString());
		}
	}

	/**
	 * JDBC 2.0
	 * 
	 * Sets a REF(&lt;structured-type&gt;) parameter.
	 * 
	 * @param i
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            an object representing data of an SQL REF Type
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void setRef(int i, Ref x) throws SQLException {
		s.setRef(i, x);
	}

	/**
	 * Sets the designated parameter to a Java short value. The driver converts
	 * this to an SQL SMALLINT value when it sends it to the database.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the parameter value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void setShort(int parameterIndex, short x) throws SQLException {
		s.setShort(parameterIndex, x);
	}

	/**
	 * Sets the designated parameter to a Java String value. The driver converts
	 * this to an SQL VARCHAR or LONGVARCHAR value (depending on the argument's
	 * size relative to the driver's limits on VARCHARs) when it sends it to the
	 * database.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the parameter value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void setString(int parameterIndex, String x) throws SQLException {
		s.setString(parameterIndex, x);
	}

	/**
	 * Sets the designated parameter to a java.sql.Time value. The driver
	 * converts this to an SQL TIME value when it sends it to the database.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the parameter value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void setTime(int parameterIndex, Time x) throws SQLException {
		s.setTime(parameterIndex, x);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Sets the designated parameter to a java.sql.Time value, using the given
	 * <code>Calendar</code> object. The driver uses the <code>Calendar</code>
	 * object to construct an SQL TIME, which the driver then sends to the
	 * database. With a a <code>Calendar</code> object, the driver can calculate
	 * the time taking into account a custom timezone and locale. If no
	 * <code>Calendar</code> object is specified, the driver uses the default
	 * timezone and locale.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the parameter value
	 * @param cal
	 *            the <code>Calendar</code> object the driver will use to
	 *            construct the time
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void setTime(int parameterIndex, Time x, java.util.Calendar cal)
			throws SQLException {
		s.setTime(parameterIndex, x, cal);
	}

	/**
	 * Sets the designated parameter to a java.sql.Timestamp value. The driver
	 * converts this to an SQL TIMESTAMP value when it sends it to the database.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the parameter value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void setTimestamp(int parameterIndex, Timestamp x)
			throws SQLException {
		s.setTimestamp(parameterIndex, x);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Sets the designated parameter to a java.sql.Timestamp value, using the
	 * given <code>Calendar</code> object. The driver uses the
	 * <code>Calendar</code> object to construct an SQL TIMESTAMP, which the
	 * driver then sends to the database. With a a <code>Calendar</code> object,
	 * the driver can calculate the timestamp taking into account a custom
	 * timezone and locale. If no <code>Calendar</code> object is specified, the
	 * driver uses the default timezone and locale.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the parameter value
	 * @param cal
	 *            the <code>Calendar</code> object the driver will use to
	 *            construct the timestamp
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void setTimestamp(int parameterIndex, Timestamp x,
			java.util.Calendar cal) throws SQLException {
		s.setTimestamp(parameterIndex, x, cal);
	}

	/**
	 * Sets the designated parameter to the given input stream, which will have
	 * the specified number of bytes. When a very large UNICODE value is input
	 * to a LONGVARCHAR parameter, it may be more practical to send it via a
	 * java.io.InputStream. JDBC will read the data from the stream as needed,
	 * until it reaches end-of-file. The JDBC driver will do any necessary
	 * conversion from UNICODE to the database char format. The byte format of
	 * the Unicode stream must be Java UTF-8, as defined in the Java Virtual
	 * Machine Specification.
	 * 
	 * <P>
	 * <B>Note:</B> This stream object can either be a standard Java stream
	 * object or your own subclass that implements the standard interface.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the java input stream which contains the UNICODE parameter
	 *            value
	 * @param length
	 *            the number of bytes in the stream
	 * @exception SQLException
	 *                if a database access error occurs
	 * @deprecated
	 */
	public void setUnicodeStream(int parameterIndex, java.io.InputStream x,
			int length) throws SQLException {
		System.err
				.println("PreparedSpatialStatement.setUnicodeStream(int,InputStream,int) is not implemented!");
	}

	/**
	 * Sets the designated parameter to the given <code>java.net.URL</code>
	 * value. The driver converts this to an SQL <code>DATALINK</code> value
	 * when it sends it to the database.
	 * 
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the <code>java.net.URL</code> object to be set
	 * @exception SQLException
	 *                if a database access error occurs
	 * @since 1.4
	 */
	public void setURL(int parameterIndex, java.net.URL x) throws SQLException {
		System.err
				.println("PreparedSpatialStatement.setURL(int,URL) is not implemented!");
	}

	/**
	 * Retrieves the number, types and properties of this
	 * <code>PreparedStatement</code> object's parameters.
	 * 
	 * @return a <code>ParameterMetaData</code> object that contains information
	 *         about the number, types and properties of this
	 *         <code>PreparedStatement</code> object's parameters
	 * @exception SQLException
	 *                if a database access error occurs
	 * @see ParameterMetaData
	 * @since 1.4
	 */
	public ParameterMetaData getParameterMetaData() throws SQLException {
		System.err
				.println("PreparedSpatialStatement.getParameterMetaData() is not implemented!");
		return null;
	}

	public boolean isClosed() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isPoolable() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public void setPoolable(boolean arg0) throws SQLException {
		// TODO Auto-generated method stub

	}

	public boolean isWrapperFor(Class arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public Object unwrap(Class arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setAsciiStream(int arg0, InputStream arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void setAsciiStream(int arg0, InputStream arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void setBinaryStream(int arg0, InputStream arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void setBinaryStream(int arg0, InputStream arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void setBlob(int arg0, InputStream arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void setBlob(int arg0, InputStream arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void setCharacterStream(int arg0, Reader arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void setCharacterStream(int arg0, Reader arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void setClob(int arg0, Reader arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void setClob(int arg0, Reader arg1, long arg2) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void setNCharacterStream(int arg0, Reader arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void setNCharacterStream(int arg0, Reader arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void setNClob(int arg0, NClob arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void setNClob(int arg0, Reader arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void setNClob(int arg0, Reader arg1, long arg2) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void setNString(int arg0, String arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void setRowId(int arg0, RowId arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void setSQLXML(int arg0, SQLXML arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

}
