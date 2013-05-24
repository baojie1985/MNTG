package spatialdb;

import java.io.InputStream;
import java.io.Reader;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.NClob;
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

import oracle.sdoapi.geom.Geometry;
import oracle.sdoapi.geom.InvalidGeometryException;
import oracle.sdoapi.geom.Point;

/**
 * A result set including spatial and spatiotemporal attributes.
 * 
 * @author Thomas Brinkhoff
 * @version 1.11 05.07.2003 adapted to sdoapi 1.0.1
 * @version 1.10 24.06.2002 adapted to Java v1.4
 * @version 1.00 08.06.2001 first version
 */
public class SpatialResultSet implements ResultSet {

	/**
	 * The original result set.
	 */
	private ResultSet s;
	/**
	 * The geometry adapter.
	 */
	private oracle.sdoapi.adapter.GeometryAdapter adapter;

	/**
	 * The constant indicating that <code>ResultSet</code> objects should not be
	 * closed when the method <code>Connection.commit</code> is called.
	 */
	public int HOLD_CURSORS_OVER_COMMIT = 1;
	/**
	 * The constant indicating that <code>ResultSet</code> objects should be
	 * closed when the method <code>Connection.commit</code> is called.
	 */
	public int CLOSE_CURSORS_AT_COMMIT = 2;

	/**
	 * SpatialResultSet constructor.
	 * 
	 * @param the
	 *            original result set
	 */
	public SpatialResultSet(ResultSet s) throws SQLException {
		this(s, s.getStatement().getConnection());
	}

	/**
	 * SpatialResultSet constructor.
	 * 
	 * @param the
	 *            original result set
	 * @param the
	 *            database connection
	 */
	public SpatialResultSet(ResultSet s, Connection con) {
		this.s = s;
		adapter = oracle.sdoapi.OraSpatialManager.getGeometryAdapter("SDO",
				"8.1.6", oracle.sql.STRUCT.class, null, null,
				(oracle.jdbc.OracleConnection) con);
	}

	/**
	 * JDBC 2.0
	 * 
	 * <p>
	 * Moves the cursor to the given row number in the result set.
	 * 
	 * <p>
	 * If the row number is positive, the cursor moves to the given row number
	 * with respect to the beginning of the result set. The first row is row 1,
	 * the second is row 2, and so on.
	 * 
	 * <p>
	 * If the given row number is negative, the cursor moves to an absolute row
	 * position with respect to the end of the result set. For example, calling
	 * <code>absolute(-1)</code> positions the cursor on the last row,
	 * <code>absolute(-2)</code> indicates the next-to-last row, and so on.
	 * 
	 * <p>
	 * An attempt to position the cursor beyond the first/last row in the result
	 * set leaves the cursor before/after the first/last row, respectively.
	 * 
	 * <p>
	 * Note: Calling <code>absolute(1)</code> is the same as calling
	 * <code>first()</code>. Calling <code>absolute(-1)</code> is the same as
	 * calling <code>last()</code>.
	 * 
	 * @return true if the cursor is on the result set; false otherwise
	 * @exception SQLException
	 *                if a database access error occurs or row is 0, or result
	 *                set type is TYPE_FORWARD_ONLY.
	 */
	public boolean absolute(int row) throws SQLException {
		return s.absolute(row);
	}

	/**
	 * JDBC 2.0
	 * 
	 * <p>
	 * Moves the cursor to the end of the result set, just after the last row.
	 * Has no effect if the result set contains no rows.
	 * 
	 * @exception SQLException
	 *                if a database access error occurs or the result set type
	 *                is TYPE_FORWARD_ONLY
	 */
	public void afterLast() throws SQLException {
		s.afterLast();
	}

	/**
	 * JDBC 2.0
	 * 
	 * <p>
	 * Moves the cursor to the front of the result set, just before the first
	 * row. Has no effect if the result set contains no rows.
	 * 
	 * @exception SQLException
	 *                if a database access error occurs or the result set type
	 *                is TYPE_FORWARD_ONLY
	 */
	public void beforeFirst() throws SQLException {
		s.beforeFirst();
	}

	/**
	 * JDBC 2.0
	 * 
	 * Cancels the updates made to a row. This method may be called after
	 * calling an <code>updateXXX</code> method(s) and before calling
	 * <code>updateRow</code> to rollback the updates made to a row. If no
	 * updates have been made or <code>updateRow</code> has already been called,
	 * then this method has no effect.
	 * 
	 * @exception SQLException
	 *                if a database access error occurs or if called when on the
	 *                insert row
	 * 
	 */
	public void cancelRowUpdates() throws SQLException {
		s.cancelRowUpdates();
	}

	/**
	 * After this call getWarnings returns null until a new warning is reported
	 * for this ResultSet.
	 * 
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void clearWarnings() throws SQLException {
		s.clearWarnings();
	}

	/**
	 * Releases this <code>ResultSet</code> object's database and JDBC resources
	 * immediately instead of waiting for this to happen when it is
	 * automatically closed.
	 * 
	 * <P>
	 * <B>Note:</B> A ResultSet is automatically closed by the Statement that
	 * generated it when that Statement is closed, re-executed, or is used to
	 * retrieve the next result from a sequence of multiple results. A ResultSet
	 * is also automatically closed when it is garbage collected.
	 * 
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void close() throws SQLException {
		s.close();
	}

	/**
	 * JDBC 2.0
	 * 
	 * Deletes the current row from the result set and the underlying database.
	 * Cannot be called when on the insert row.
	 * 
	 * @exception SQLException
	 *                if a database access error occurs or if called when on the
	 *                insert row.
	 */
	public void deleteRow() throws SQLException {
		s.deleteRow();
	}

	/**
	 * Maps the given Resultset column name to its ResultSet column index.
	 * 
	 * @param columnName
	 *            the name of the column
	 * @return the column index
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public int findColumn(String columnName) throws SQLException {
		return s.findColumn(columnName);
	}

	/**
	 * JDBC 2.0
	 * 
	 * <p>
	 * Moves the cursor to the first row in the result set.
	 * 
	 * @return true if the cursor is on a valid row; false if there are no rows
	 *         in the result set
	 * @exception SQLException
	 *                if a database access error occurs or the result set type
	 *                is TYPE_FORWARD_ONLY
	 */
	public boolean first() throws SQLException {
		return s.first();
	}

	/**
	 * JDBC 2.0
	 * 
	 * Gets an SQL ARRAY value from the current row of this
	 * <code>ResultSet</code> object.
	 * 
	 * @param i
	 *            the first column is 1, the second is 2, ...
	 * @return an <code>Array</code> object representing the SQL ARRAY value in
	 *         the specified column
	 */
	public Array getArray(int i) throws SQLException {
		return s.getArray(i);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Gets an SQL ARRAY value in the current row of this <code>ResultSet</code>
	 * object.
	 * 
	 * @param colName
	 *            the name of the column from which to retrieve the value
	 * @return an <code>Array</code> object representing the SQL ARRAY value in
	 *         the specified column
	 */
	public Array getArray(String colName) throws SQLException {
		return s.getArray(colName);
	}

	/**
	 * Gets the value of a column in the current row as a stream of ASCII
	 * characters. The value can then be read in chunks from the stream. This
	 * method is particularly suitable for retrieving large LONGVARCHAR values.
	 * The JDBC driver will do any necessary conversion from the database format
	 * into ASCII.
	 * 
	 * <P>
	 * <B>Note:</B> All the data in the returned stream must be read prior to
	 * getting the value of any other column. The next call to a get method
	 * implicitly closes the stream. Also, a stream may return 0 when the method
	 * <code>available</code> is called whether there is data available or not.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @return a Java input stream that delivers the database column value as a
	 *         stream of one byte ASCII characters. If the value is SQL NULL
	 *         then the result is null.
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public java.io.InputStream getAsciiStream(int columnIndex)
			throws SQLException {
		return s.getAsciiStream(columnIndex);
	}

	/**
	 * Gets the value of a column in the current row as a stream of ASCII
	 * characters. The value can then be read in chunks from the stream. This
	 * method is particularly suitable for retrieving large LONGVARCHAR values.
	 * The JDBC driver will do any necessary conversion from the database format
	 * into ASCII.
	 * 
	 * <P>
	 * <B>Note:</B> All the data in the returned stream must be read prior to
	 * getting the value of any other column. The next call to a get method
	 * implicitly closes the stream. Also, a stream may return 0 when the method
	 * <code>available</code> is called whether there is data available or not.
	 * 
	 * @param columnName
	 *            the SQL name of the column
	 * @return a Java input stream that delivers the database column value as a
	 *         stream of one byte ASCII characters. If the value is SQL NULL
	 *         then the result is null.
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public java.io.InputStream getAsciiStream(String columnName)
			throws SQLException {
		return s.getAsciiStream(columnName);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Gets the value of a column in the current row as a java.math.BigDecimal
	 * object with full precision.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @return the column value (full precision); if the value is SQL NULL, the
	 *         result is null
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public java.math.BigDecimal getBigDecimal(int columnIndex)
			throws SQLException {
		return s.getBigDecimal(columnIndex);
	}

	/**
	 * Gets the value of a column in the current row as a java.math.BigDecimal
	 * object.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @param scale
	 *            the number of digits to the right of the decimal
	 * @return the column value; if the value is SQL NULL, the result is null
	 * @exception SQLException
	 *                if a database access error occurs
	 * @deprecated
	 */
	public java.math.BigDecimal getBigDecimal(int columnIndex, int scale)
			throws SQLException {
		System.err
				.println("SpatialResultSet.getBigDecimal(int,int) is not implemented.");
		return null;
	}

	/**
	 * JDBC 2.0
	 * 
	 * Gets the value of a column in the current row as a java.math.BigDecimal
	 * object with full precision.
	 * 
	 * @param columnName
	 *            the column name
	 * @return the column value (full precision); if the value is SQL NULL, the
	 *         result is null
	 * @exception SQLException
	 *                if a database access error occurs
	 * 
	 */
	public java.math.BigDecimal getBigDecimal(String columnName)
			throws SQLException {
		return s.getBigDecimal(columnName);
	}

	/**
	 * Gets the value of a column in the current row as a java.math.BigDecimal
	 * object.
	 * 
	 * @param columnName
	 *            the SQL name of the column
	 * @param scale
	 *            the number of digits to the right of the decimal
	 * @return the column value; if the value is SQL NULL, the result is null
	 * @exception SQLException
	 *                if a database access error occurs
	 * @deprecated
	 */
	public java.math.BigDecimal getBigDecimal(String columnName, int scale)
			throws SQLException {
		System.err
				.println("SpatialResultSet.getBigDecimal(int,int) is not implemented.");
		return null;
	}

	/**
	 * Gets the value of a column in the current row as a stream of
	 * uninterpreted bytes. The value can then be read in chunks from the
	 * stream. This method is particularly suitable for retrieving large
	 * LONGVARBINARY values.
	 * 
	 * <P>
	 * <B>Note:</B> All the data in the returned stream must be read prior to
	 * getting the value of any other column. The next call to a get method
	 * implicitly closes the stream. Also, a stream may return 0 when the method
	 * <code>available</code> is called whether there is data available or not.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @return a Java input stream that delivers the database column value as a
	 *         stream of uninterpreted bytes. If the value is SQL NULL then the
	 *         result is null.
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public java.io.InputStream getBinaryStream(int columnIndex)
			throws SQLException {
		return s.getBinaryStream(columnIndex);
	}

	/**
	 * Gets the value of a column in the current row as a stream of
	 * uninterpreted bytes. The value can then be read in chunks from the
	 * stream. This method is particularly suitable for retrieving large
	 * LONGVARBINARY values. The JDBC driver will do any necessary conversion
	 * from the database format into uninterpreted bytes.
	 * 
	 * <P>
	 * <B>Note:</B> All the data in the returned stream must be read prior to
	 * getting the value of any other column. The next call to a get method
	 * implicitly closes the stream. Also, a stream may return 0 when the method
	 * <code>available</code> is called whether there is data available or not.
	 * 
	 * @param columnName
	 *            the SQL name of the column
	 * @return a Java input stream that delivers the database column value as a
	 *         stream of uninterpreted bytes. If the value is SQL NULL then the
	 *         result is null.
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public java.io.InputStream getBinaryStream(String columnName)
			throws SQLException {
		return s.getBinaryStream(columnName);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Gets a BLOB value in the current row of this <code>ResultSet</code>
	 * object.
	 * 
	 * @param i
	 *            the first column is 1, the second is 2, ...
	 * @return a <code>Blob</code> object representing the SQL BLOB value in the
	 *         specified column
	 */
	public Blob getBlob(int i) throws SQLException {
		return s.getBlob(i);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Gets a BLOB value in the current row of this <code>ResultSet</code>
	 * object.
	 * 
	 * @param colName
	 *            the name of the column from which to retrieve the value
	 * @return a <code>Blob</code> object representing the SQL BLOB value in the
	 *         specified column
	 */
	public Blob getBlob(String colName) throws SQLException {
		return s.getBlob(colName);
	}

	/**
	 * Gets the value of a column in the current row as a Java boolean.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @return the column value; if the value is SQL NULL, the result is false
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public boolean getBoolean(int columnIndex) throws SQLException {
		return s.getBoolean(columnIndex);
	}

	/**
	 * Gets the value of a column in the current row as a Java boolean.
	 * 
	 * @param columnName
	 *            the SQL name of the column
	 * @return the column value; if the value is SQL NULL, the result is false
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public boolean getBoolean(String columnName) throws SQLException {
		return getBoolean(columnName);
	}

	/**
	 * Gets the value of a column in the current row as a Java byte.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @return the column value; if the value is SQL NULL, the result is 0
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public byte getByte(int columnIndex) throws SQLException {
		return s.getByte(columnIndex);
	}

	/**
	 * Gets the value of a column in the current row as a Java byte.
	 * 
	 * @param columnName
	 *            the SQL name of the column
	 * @return the column value; if the value is SQL NULL, the result is 0
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public byte getByte(String columnName) throws SQLException {
		return s.getByte(columnName);
	}

	/**
	 * Gets the value of a column in the current row as a Java byte array. The
	 * bytes represent the raw values returned by the driver.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @return the column value; if the value is SQL NULL, the result is null
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public byte[] getBytes(int columnIndex) throws SQLException {
		return s.getBytes(columnIndex);
	}

	/**
	 * Gets the value of a column in the current row as a Java byte array. The
	 * bytes represent the raw values returned by the driver.
	 * 
	 * @param columnName
	 *            the SQL name of the column
	 * @return the column value; if the value is SQL NULL, the result is null
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public byte[] getBytes(String columnName) throws SQLException {
		return s.getBytes(columnName);
	}

	/**
	 * JDBC 2.0
	 * 
	 * <p>
	 * Gets the value of a column in the current row as a java.io.Reader.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 */
	public java.io.Reader getCharacterStream(int columnIndex)
			throws SQLException {
		return s.getCharacterStream(columnIndex);
	}

	/**
	 * JDBC 2.0
	 * 
	 * <p>
	 * Gets the value of a column in the current row as a java.io.Reader.
	 * 
	 * @param columnName
	 *            the name of the column
	 * @return the value in the specified column as a
	 *         <code>java.io.Reader</code>
	 */
	public java.io.Reader getCharacterStream(String columnName)
			throws SQLException {
		return s.getCharacterStream(columnName);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Gets a CLOB value in the current row of this <code>ResultSet</code>
	 * object.
	 * 
	 * @param i
	 *            the first column is 1, the second is 2, ...
	 * @return a <code>Clob</code> object representing the SQL CLOB value in the
	 *         specified column
	 */
	public Clob getClob(int i) throws SQLException {
		return s.getClob(i);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Gets a CLOB value in the current row of this <code>ResultSet</code>
	 * object.
	 * 
	 * @param colName
	 *            the name of the column from which to retrieve the value
	 * @return a <code>Clob</code> object representing the SQL CLOB value in the
	 *         specified column
	 */
	public Clob getClob(String colName) throws SQLException {
		return s.getClob(colName);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Returns the concurrency mode of this result set. The concurrency used is
	 * determined by the statement that created the result set.
	 * 
	 * @return the concurrency type, CONCUR_READ_ONLY or CONCUR_UPDATABLE
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public int getConcurrency() throws SQLException {
		return s.getConcurrency();
	}

	/**
	 * Gets the name of the SQL cursor used by this ResultSet.
	 * 
	 * <P>
	 * In SQL, a result table is retrieved through a cursor that is named. The
	 * current row of a result can be updated or deleted using a positioned
	 * update/delete statement that references the cursor name. To insure that
	 * the cursor has the proper isolation level to support update, the cursor's
	 * select statement should be of the form 'select for update'. If the 'for
	 * update' clause is omitted the positioned updates may fail.
	 * 
	 * <P>
	 * JDBC supports this SQL feature by providing the name of the SQL cursor
	 * used by a ResultSet. The current row of a ResultSet is also the current
	 * row of this SQL cursor.
	 * 
	 * <P>
	 * <B>Note:</B> If positioned update is not supported a SQLException is
	 * thrown
	 * 
	 * @return the ResultSet's SQL cursor name
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public String getCursorName() throws SQLException {
		return s.getCursorName();
	}

	/**
	 * Gets the value of a column in the current row as a java.sql.Date object.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @return the column value; if the value is SQL NULL, the result is null
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public Date getDate(int columnIndex) throws SQLException {
		return s.getDate(columnIndex);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Gets the value of a column in the current row as a java.sql.Date object.
	 * This method uses the given calendar to construct an appropriate
	 * millisecond value for the Date if the underlying database does not store
	 * timezone information.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @param cal
	 *            the calendar to use in constructing the date
	 * @return the column value; if the value is SQL NULL, the result is null
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public Date getDate(int columnIndex, java.util.Calendar cal)
			throws SQLException {
		return s.getDate(columnIndex, cal);
	}

	/**
	 * Gets the value of a column in the current row as a java.sql.Date object.
	 * 
	 * @param columnName
	 *            the SQL name of the column
	 * @return the column value; if the value is SQL NULL, the result is null
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public Date getDate(String columnName) throws SQLException {
		return s.getDate(columnName);
	}

	/**
	 * Gets the value of a column in the current row as a java.sql.Date object.
	 * This method uses the given calendar to construct an appropriate
	 * millisecond value for the Date, if the underlying database does not store
	 * timezone information.
	 * 
	 * @param columnName
	 *            the SQL name of the column from which to retrieve the value
	 * @param cal
	 *            the calendar to use in constructing the date
	 * @return the column value; if the value is SQL NULL, the result is null
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public Date getDate(String columnName, java.util.Calendar cal)
			throws SQLException {
		return s.getDate(columnName, cal);
	}

	/**
	 * Gets the value of a column in the current row as a Java double.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @return the column value; if the value is SQL NULL, the result is 0
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public double getDouble(int columnIndex) throws SQLException {
		return s.getDouble(columnIndex);
	}

	/**
	 * Gets the value of a column in the current row as a Java double.
	 * 
	 * @param columnName
	 *            the SQL name of the column
	 * @return the column value; if the value is SQL NULL, the result is 0
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public double getDouble(String columnName) throws SQLException {
		return s.getDouble(columnName);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Returns the fetch direction for this result set.
	 * 
	 * @return the current fetch direction for this result set
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public int getFetchDirection() throws SQLException {
		return s.getFetchDirection();
	}

	/**
	 * JDBC 2.0
	 * 
	 * Returns the fetch size for this result set.
	 * 
	 * @return the current fetch size for this result set
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public int getFetchSize() throws SQLException {
		return s.getFetchSize();
	}

	/**
	 * Gets the value of a column in the current row as a Java float.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @return the column value; if the value is SQL NULL, the result is 0
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public float getFloat(int columnIndex) throws SQLException {
		return s.getFloat(columnIndex);
	}

	/**
	 * Gets the value of a column in the current row as a Java float.
	 * 
	 * @param columnName
	 *            the SQL name of the column
	 * @return the column value; if the value is SQL NULL, the result is 0
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public float getFloat(String columnName) throws SQLException {
		return s.getFloat(columnName);
	}

	/**
	 * Gets the value of a column in the current row as a sdoapi.geom.Geometry.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @return the column value; if the value is SQL NULL, the result is 0
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public Geometry getGeometry(int columnIndex) throws SQLException {
		try {
			oracle.sql.STRUCT dbObject = (oracle.sql.STRUCT) s
					.getObject(columnIndex);
			if (dbObject != null)
				return adapter.importGeometry(dbObject);
			else
				return null;
		} catch (InvalidGeometryException e) {
			throw new SQLException(e.toString());
		} catch (oracle.sdoapi.adapter.GeometryInputTypeNotSupportedException e) {
			throw new SQLException(e.toString());
		}
	}

	/**
	 * Gets the value of a column in the current row as a sdoapi.geom.Geometry.
	 * 
	 * @param columnName
	 *            the SQL name of the column
	 * @return the column value; if the value is SQL NULL, the result is 0
	 * @exception SQLException
	 *                if a database access, geometry or adapter error occurs
	 */
	public Geometry getGeometry(String columnName) throws SQLException {
		try {
			oracle.sql.STRUCT dbObject = (oracle.sql.STRUCT) s
					.getObject(columnName);
			if (dbObject != null)
				return adapter.importGeometry(dbObject);
			else
				return null;
		} catch (InvalidGeometryException e) {
			throw new SQLException(e.toString());
		} catch (oracle.sdoapi.adapter.GeometryInputTypeNotSupportedException e) {
			throw new SQLException(e.toString());
		}
	}

	/**
	 * Gets the value of a column in the current row as a Java int.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @return the column value; if the value is SQL NULL, the result is 0
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public int getInt(int columnIndex) throws SQLException {
		return s.getInt(columnIndex);
	}

	/**
	 * Gets the value of a column in the current row as a Java int.
	 * 
	 * @param columnName
	 *            the SQL name of the column
	 * @return the column value; if the value is SQL NULL, the result is 0
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public int getInt(String columnName) throws SQLException {
		return s.getInt(columnName);
	}

	/**
	 * Gets the value of a column in the current row as a Java long.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @return the column value; if the value is SQL NULL, the result is 0
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public long getLong(int columnIndex) throws SQLException {
		return s.getLong(columnIndex);
	}

	/**
	 * Gets the value of a column in the current row as a Java long.
	 * 
	 * @param columnName
	 *            the SQL name of the column
	 * @return the column value; if the value is SQL NULL, the result is 0
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public long getLong(String columnName) throws SQLException {
		return s.getLong(columnName);
	}

	/**
	 * Retrieves the number, types and properties of a ResultSet's columns.
	 * 
	 * @return the description of a ResultSet's columns
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public ResultSetMetaData getMetaData() throws SQLException {
		return s.getMetaData();
	}

	/**
	 * <p>
	 * Gets the value of a column in the current row as a Java object.
	 * 
	 * <p>
	 * This method will return the value of the given column as a Java object.
	 * The type of the Java object will be the default Java object type
	 * corresponding to the column's SQL type, following the mapping for
	 * built-in types specified in the JDBC spec.
	 * 
	 * <p>
	 * This method may also be used to read datatabase-specific abstract data
	 * types.
	 * 
	 * JDBC 2.0
	 * 
	 * 
	 * In the JDBC 2.0 API, the behavior of method <code>getObject</code> is
	 * extended to materialize data of SQL user-defined types. When the a column
	 * contains a structured or distinct value, the behavior of this method is
	 * as if it were a call to: getObject(columnIndex,
	 * this.getStatement().getConnection().getTypeMap()).
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @return a java.lang.Object holding the column value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public Object getObject(int columnIndex) throws SQLException {
		return s.getObject(columnIndex);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Returns the value of a column in the current row as a Java object. This
	 * method uses the given <code>Map</code> object for the custom mapping of
	 * the SQL structured or distinct type that is being retrieved.
	 * 
	 * @param i
	 *            the first column is 1, the second is 2, ...
	 * @param map
	 *            the mapping from SQL type names to Java classes
	 * @return an object representing the SQL value
	 */
	public Object getObject(int i, java.util.Map map) throws SQLException {
		return s.getObject(i, map);
	}

	/**
	 * <p>
	 * Gets the value of a column in the current row as a Java object.
	 * 
	 * <p>
	 * This method will return the value of the given column as a Java object.
	 * The type of the Java object will be the default Java object type
	 * corresponding to the column's SQL type, following the mapping for
	 * built-in types specified in the JDBC spec.
	 * 
	 * <p>
	 * This method may also be used to read datatabase-specific abstract data
	 * types.
	 * 
	 * JDBC 2.0
	 * 
	 * In the JDBC 2.0 API, the behavior of method <code>getObject</code> is
	 * extended to materialize data of SQL user-defined types. When the a column
	 * contains a structured or distinct value, the behavior of this method is
	 * as if it were a call to: getObject(columnIndex,
	 * this.getStatement().getConnection().getTypeMap()).
	 * 
	 * @param columnName
	 *            the SQL name of the column
	 * @return a java.lang.Object holding the column value.
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public Object getObject(String columnName) throws SQLException {
		return s.getObject(columnName);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Returns the value in the specified column as a Java object. This method
	 * uses the specified <code>Map</code> object for custom mapping if
	 * appropriate.
	 * 
	 * @param colName
	 *            the name of the column from which to retrieve the value
	 * @param map
	 *            the mapping from SQL type names to Java classes
	 * @return an object representing the SQL value in the specified column
	 */
	public Object getObject(String colName, java.util.Map map)
			throws SQLException {
		return s.getObject(colName, map);
	}

	/**
	 * Gets the value of a column in the current row as a java.awt.Point.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @return the column value; if the value is SQL NULL, the result is 0
	 * @exception SQLException
	 *                if a database access, geometry or adapter error occurs
	 */
	public java.awt.Point getPoint(int columnIndex) throws SQLException {
		try {
			Point p = (Point) getGeometry(columnIndex);
			return new java.awt.Point((int) p.getX(), (int) p.getY());
		} catch (Exception e) {
			throw new SQLException(e.toString());
		}
	}

	/**
	 * Gets the value of a column in the current row as a java.awt.Point.
	 * 
	 * @param columnName
	 *            the SQL name of the column
	 * @return the column value; if the value is SQL NULL, the result is 0
	 * @exception SQLException
	 *                if a database access, geometry or adapter error occurs
	 */
	public java.awt.Point getPoint(String columnName) throws SQLException {
		try {
			Point p = (Point) getGeometry(columnName);
			return new java.awt.Point((int) p.getX(), (int) p.getY());
		} catch (Exception e) {
			throw new SQLException(e.toString());
		}
	}

	/**
	 * JDBC 2.0
	 * 
	 * Gets a REF(&lt;structured-type&gt;) column value from the current row.
	 * 
	 * @param i
	 *            the first column is 1, the second is 2, ...
	 * @return a <code>Ref</code> object representing an SQL REF value
	 */
	public Ref getRef(int i) throws SQLException {
		return s.getRef(i);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Gets a REF(&lt;structured-type&gt;) column value from the current row.
	 * 
	 * @param colName
	 *            the column name
	 * @return a <code>Ref</code> object representing the SQL REF value in the
	 *         specified column
	 */
	public Ref getRef(String colName) throws SQLException {
		return s.getRef(colName);
	}

	/**
	 * JDBC 2.0
	 * 
	 * <p>
	 * Retrieves the current row number. The first row is number 1, the second
	 * number 2, and so on.
	 * 
	 * @return the current row number; 0 if there is no current row
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public int getRow() throws SQLException {
		return s.getRow();
	}

	/**
	 * Gets the value of a column in the current row as a Java short.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @return the column value; if the value is SQL NULL, the result is 0
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public short getShort(int columnIndex) throws SQLException {
		return s.getShort(columnIndex);
	}

	/**
	 * Gets the value of a column in the current row as a Java short.
	 * 
	 * @param columnName
	 *            the SQL name of the column
	 * @return the column value; if the value is SQL NULL, the result is 0
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public short getShort(String columnName) throws SQLException {
		return s.getShort(columnName);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Returns the Statement that produced this <code>ResultSet</code> object.
	 * If the result set was generated some other way, such as by a
	 * <code>DatabaseMetaData</code> method, this method returns
	 * <code>null</code>.
	 * 
	 * @return the Statment that produced the result set or null if the result
	 *         set was produced some other way
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public Statement getStatement() throws SQLException {
		return s.getStatement();
	}

	/**
	 * Gets the value of a column in the current row as a Java String.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @return the column value; if the value is SQL NULL, the result is null
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public String getString(int columnIndex) throws SQLException {
		return s.getString(columnIndex);
	}

	/**
	 * Gets the value of a column in the current row as a Java String.
	 * 
	 * @param columnName
	 *            the SQL name of the column
	 * @return the column value; if the value is SQL NULL, the result is null
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public String getString(String columnName) throws SQLException {
		return s.getString(columnName);
	}

	/**
	 * Gets the value of a column in the current row as a java.sql.Time object.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @return the column value; if the value is SQL NULL, the result is null
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public Time getTime(int columnIndex) throws SQLException {
		return s.getTime(columnIndex);
	}

	/**
	 * Gets the value of a column in the current row as a java.sql.Time object.
	 * This method uses the given calendar to construct an appropriate
	 * millisecond value for the Time if the underlying database does not store
	 * timezone information.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @param cal
	 *            the calendar to use in constructing the time
	 * @return the column value; if the value is SQL NULL, the result is null
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public Time getTime(int columnIndex, java.util.Calendar cal)
			throws SQLException {
		return s.getTime(columnIndex, cal);
	}

	/**
	 * Gets the value of a column in the current row as a java.sql.Time object.
	 * 
	 * @param columnName
	 *            the SQL name of the column
	 * @return the column value; if the value is SQL NULL, the result is null
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public Time getTime(String columnName) throws SQLException {
		return s.getTime(columnName);
	}

	/**
	 * Gets the value of a column in the current row as a java.sql.Time object.
	 * This method uses the given calendar to construct an appropriate
	 * millisecond value for the Time if the underlying database does not store
	 * timezone information.
	 * 
	 * @param columnName
	 *            the SQL name of the column
	 * @param cal
	 *            the calendar to use in constructing the time
	 * @return the column value; if the value is SQL NULL, the result is null
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public Time getTime(String columnName, java.util.Calendar cal)
			throws SQLException {
		return s.getTime(columnName, cal);
	}

	/**
	 * Gets the value of a column in the current row as a java.sql.Timestamp
	 * object.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @return the column value; if the value is SQL NULL, the result is null
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public Timestamp getTimestamp(int columnIndex) throws SQLException {
		return s.getTimestamp(columnIndex);
	}

	/**
	 * Gets the value of a column in the current row as a java.sql.Timestamp
	 * object. This method uses the given calendar to construct an appropriate
	 * millisecond value for the Timestamp if the underlying database does not
	 * store timezone information.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @param cal
	 *            the calendar to use in constructing the timestamp
	 * @return the column value; if the value is SQL NULL, the result is null
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public Timestamp getTimestamp(int columnIndex, java.util.Calendar cal)
			throws SQLException {
		return s.getTimestamp(columnIndex, cal);
	}

	/**
	 * Gets the value of a column in the current row as a java.sql.Timestamp
	 * object.
	 * 
	 * @param columnName
	 *            the SQL name of the column
	 * @return the column value; if the value is SQL NULL, the result is null
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public Timestamp getTimestamp(String columnName) throws SQLException {
		return s.getTimestamp(columnName);
	}

	/**
	 * Gets the value of a column in the current row as a java.sql.Timestamp
	 * object. This method uses the given calendar to construct an appropriate
	 * millisecond value for the Timestamp if the underlying database does not
	 * store timezone information.
	 * 
	 * @param columnName
	 *            the SQL name of the column
	 * @param cal
	 *            the calendar to use in constructing the timestamp
	 * @return the column value; if the value is SQL NULL, the result is null
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public Timestamp getTimestamp(String columnName, java.util.Calendar cal)
			throws SQLException {
		return s.getTimestamp(columnName, cal);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Returns the type of this result set. The type is determined by the
	 * statement that created the result set.
	 * 
	 * @return TYPE_FORWARD_ONLY, TYPE_SCROLL_INSENSITIVE, or
	 *         TYPE_SCROLL_SENSITIVE
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public int getType() throws SQLException {
		return s.getType();
	}

	/**
	 * Gets the value of a column in the current row as a stream of Unicode
	 * characters. The value can then be read in chunks from the stream. This
	 * method is particularly suitable for retrieving large LONGVARCHAR values.
	 * The JDBC driver will do any necessary conversion from the database format
	 * into Unicode. The byte format of the Unicode stream must Java UTF-8, as
	 * specified in the Java Virtual Machine Specification.
	 * 
	 * <P>
	 * <B>Note:</B> All the data in the returned stream must be read prior to
	 * getting the value of any other column. The next call to a get method
	 * implicitly closes the stream. Also, a stream may return 0 when the method
	 * <code>available</code> is called whether there is data available or not.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @return a Java input stream that delivers the database column value as a
	 *         stream of two-byte Unicode characters. If the value is SQL NULL
	 *         then the result is null.
	 * @exception SQLException
	 *                if a database access error occurs
	 * @deprecated
	 */
	public java.io.InputStream getUnicodeStream(int columnIndex)
			throws SQLException {
		System.err
				.println("SpatialResultSet.getUnicodeStream(int) is not implemented.");
		return null;
	}

	/**
	 * Gets the value of a column in the current row as a stream of Unicode
	 * characters. The value can then be read in chunks from the stream. This
	 * method is particularly suitable for retrieving large LONGVARCHAR values.
	 * The JDBC driver will do any necessary conversion from the database format
	 * into Unicode. The byte format of the Unicode stream must be Java UTF-8,
	 * as defined in the Java Virtual Machine Specification.
	 * 
	 * <P>
	 * <B>Note:</B> All the data in the returned stream must be read prior to
	 * getting the value of any other column. The next call to a get method
	 * implicitly closes the stream. Also, a stream may return 0 when the method
	 * <code>available</code> is called whether there is data available or not.
	 * 
	 * @param columnName
	 *            the SQL name of the column
	 * @return a Java input stream that delivers the database column value as a
	 *         stream of two-byte Unicode characters. If the value is SQL NULL
	 *         then the result is null.
	 * @exception SQLException
	 *                if a database access error occurs
	 * @deprecated
	 */
	public java.io.InputStream getUnicodeStream(String columnName)
			throws SQLException {
		System.err
				.println("SpatialResultSet.getUnicodeStream(String) is not implemented.");
		return null;
	}

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>ResultSet</code> object as a <code>java.net.URL</code> object in
	 * the Java programming language.
	 * 
	 * @param columnIndex
	 *            the index of the column 1 is the first, 2 is the second,...
	 * @return the column value as a <code>java.net.URL</code> object; if the
	 *         value is SQL <code>NULL</code>, the value returned is
	 *         <code>null</code> in the Java programming language
	 * @exception SQLException
	 *                if a database access error occurs, or if a URL is
	 *                malformed
	 */
	public java.net.URL getURL(int columnIndex) throws SQLException {
		System.err.println("SpatialResultSet.getURL(int) is not implemented.");
		return null;
	}

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>ResultSet</code> object as a <code>java.net.URL</code> object in
	 * the Java programming language.
	 * 
	 * @param columnName
	 *            the SQL name of the column
	 * @return the column value as a <code>java.net.URL</code> object; if the
	 *         value is SQL <code>NULL</code>, the value returned is
	 *         <code>null</code> in the Java programming language
	 * @exception SQLException
	 *                if a database access error occurs or if a URL is malformed
	 */
	public java.net.URL getURL(String columnName) throws SQLException {
		System.err
				.println("SpatialResultSet.getURL(String) is not implemented.");
		return null;
	}

	/**
	 * <p>
	 * The first warning reported by calls on this ResultSet is returned.
	 * Subsequent ResultSet warnings will be chained to this SQLWarning.
	 * 
	 * <P>
	 * The warning chain is automatically cleared each time a new row is read.
	 * 
	 * <P>
	 * <B>Note:</B> This warning chain only covers warnings caused by ResultSet
	 * methods. Any warning caused by statement methods (such as reading OUT
	 * parameters) will be chained on the Statement object.
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
	 * Inserts the contents of the insert row into the result set and the
	 * database. Must be on the insert row when this method is called.
	 * 
	 * @exception SQLException
	 *                if a database access error occurs, if called when not on
	 *                the insert row, or if not all of non-nullable columns in
	 *                the insert row have been given a value
	 */
	public void insertRow() throws SQLException {
		s.insertRow();
	}

	/**
	 * JDBC 2.0
	 * 
	 * <p>
	 * Indicates whether the cursor is after the last row in the result set.
	 * 
	 * @return true if the cursor is after the last row, false otherwise.
	 *         Returns false when the result set contains no rows.
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public boolean isAfterLast() throws SQLException {
		return s.isAfterLast();
	}

	/**
	 * JDBC 2.0
	 * 
	 * <p>
	 * Indicates whether the cursor is before the first row in the result set.
	 * 
	 * @return true if the cursor is before the first row, false otherwise.
	 *         Returns false when the result set contains no rows.
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public boolean isBeforeFirst() throws SQLException {
		return s.isBeforeFirst();
	}

	/**
	 * JDBC 2.0
	 * 
	 * <p>
	 * Indicates whether the cursor is on the first row of the result set.
	 * 
	 * @return true if the cursor is on the first row, false otherwise.
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public boolean isFirst() throws SQLException {
		return s.isFirst();
	}

	/**
	 * JDBC 2.0
	 * 
	 * <p>
	 * Indicates whether the cursor is on the last row of the result set. Note:
	 * Calling the method <code>isLast</code> may be expensive because the JDBC
	 * driver might need to fetch ahead one row in order to determine whether
	 * the current row is the last row in the result set.
	 * 
	 * @return true if the cursor is on the last row, false otherwise.
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public boolean isLast() throws SQLException {
		return s.isLast();
	}

	/**
	 * JDBC 2.0
	 * 
	 * <p>
	 * Moves the cursor to the last row in the result set.
	 * 
	 * @return true if the cursor is on a valid row; false if there are no rows
	 *         in the result set
	 * @exception SQLException
	 *                if a database access error occurs or the result set type
	 *                is TYPE_FORWARD_ONLY.
	 */
	public boolean last() throws SQLException {
		return s.last();
	}

	/**
	 * JDBC 2.0
	 * 
	 * Moves the cursor to the remembered cursor position, usually the current
	 * row. This method has no effect if the cursor is not on the insert row.
	 * 
	 * @exception SQLException
	 *                if a database access error occurs or the result set is not
	 *                updatable
	 */
	public void moveToCurrentRow() throws SQLException {
		s.moveToCurrentRow();
	}

	/**
	 * JDBC 2.0
	 * 
	 * Moves the cursor to the insert row. The current cursor position is
	 * remembered while the cursor is positioned on the insert row.
	 * 
	 * The insert row is a special row associated with an updatable result set.
	 * It is essentially a buffer where a new row may be constructed by calling
	 * the <code>updateXXX</code> methods prior to inserting the row into the
	 * result set.
	 * 
	 * Only the <code>updateXXX</code>, <code>getXXX</code>, and
	 * <code>insertRow</code> methods may be called when the cursor is on the
	 * insert row. All of the columns in a result set must be given a value each
	 * time this method is called before calling <code>insertRow</code>. The
	 * method <code>updateXXX</code> must be called before a <code>getXXX</code>
	 * method can be called on a column value.
	 * 
	 * @exception SQLException
	 *                if a database access error occurs or the result set is not
	 *                updatable
	 */
	public void moveToInsertRow() throws SQLException {
		s.moveToInsertRow();
	}

	/**
	 * Constructor as static method.
	 * 
	 * @return the spatiotemporal result set (or null)
	 * @param set
	 *            the original result set (or null)
	 * @param the
	 *            database connection
	 */
	public static SpatialResultSet newSet(ResultSet set, Connection con) {
		return set == null ? null : new SpatialResultSet(set, con);
	}

	/**
	 * Moves the cursor down one row from its current position. A ResultSet
	 * cursor is initially positioned before the first row; the first call to
	 * next makes the first row the current row; the second call makes the
	 * second row the current row, and so on.
	 * 
	 * <P>
	 * If an input stream is open for the current row, a call to the method
	 * <code>next</code> will implicitly close it. The ResultSet's warning chain
	 * is cleared when a new row is read.
	 * 
	 * @return true if the new current row is valid; false if there are no more
	 *         rows
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public boolean next() throws SQLException {
		return s.next();
	}

	/**
	 * JDBC 2.0
	 * 
	 * <p>
	 * Moves the cursor to the previous row in the result set.
	 * 
	 * <p>
	 * Note: <code>previous()</code> is not the same as
	 * <code>relative(-1)</code> because it makes sense to
	 * call</code>previous()</code> when there is no current row.
	 * 
	 * @return true if the cursor is on a valid row; false if it is off the
	 *         result set
	 * @exception SQLException
	 *                if a database access error occurs or the result set type
	 *                is TYPE_FORWARD_ONLY
	 */
	public boolean previous() throws SQLException {
		return s.previous();
	}

	/**
	 * JDBC 2.0
	 * 
	 * Refreshes the current row with its most recent value in the database.
	 * Cannot be called when on the insert row.
	 * 
	 * The <code>refreshRow</code> method provides a way for an application to
	 * explicitly tell the JDBC driver to refetch a row(s) from the database. An
	 * application may want to call <code>refreshRow</code> when caching or
	 * prefetching is being done by the JDBC driver to fetch the latest value of
	 * a row from the database. The JDBC driver may actually refresh multiple
	 * rows at once if the fetch size is greater than one.
	 * 
	 * All values are refetched subject to the transaction isolation level and
	 * cursor sensitivity. If <code>refreshRow</code> is called after calling
	 * <code>updateXXX</code>, but before calling <code>updateRow</code>, then
	 * the updates made to the row are lost. Calling the method
	 * <code>refreshRow</code> frequently will likely slow performance.
	 * 
	 * @exception SQLException
	 *                if a database access error occurs or if called when on the
	 *                insert row
	 */
	public void refreshRow() throws SQLException {
		s.refreshRow();
	}

	/**
	 * JDBC 2.0
	 * 
	 * <p>
	 * Moves the cursor a relative number of rows, either positive or negative.
	 * Attempting to move beyond the first/last row in the result set positions
	 * the cursor before/after the the first/last row. Calling
	 * <code>relative(0)</code> is valid, but does not change the cursor
	 * position.
	 * 
	 * <p>
	 * Note: Calling <code>relative(1)</code> is different from calling
	 * <code>next()</code> because is makes sense to call <code>next()</code>
	 * when there is no current row, for example, when the cursor is positioned
	 * before the first row or after the last row of the result set.
	 * 
	 * @return true if the cursor is on a row; false otherwise
	 * @exception SQLException
	 *                if a database access error occurs, there is no current
	 *                row, or the result set type is TYPE_FORWARD_ONLY
	 */
	public boolean relative(int rows) throws SQLException {
		return s.relative(rows);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Indicates whether a row has been deleted. A deleted row may leave a
	 * visible "hole" in a result set. This method can be used to detect holes
	 * in a result set. The value returned depends on whether or not the result
	 * set can detect deletions.
	 * 
	 * @return true if a row was deleted and deletions are detected
	 * @exception SQLException
	 *                if a database access error occurs
	 * 
	 * @see DatabaseMetaData#deletesAreDetected
	 */
	public boolean rowDeleted() throws SQLException {
		return s.rowDeleted();
	}

	/**
	 * JDBC 2.0
	 * 
	 * Indicates whether the current row has had an insertion. The value
	 * returned depends on whether or not the result set can detect visible
	 * inserts.
	 * 
	 * @return true if a row has had an insertion and insertions are detected
	 * @exception SQLException
	 *                if a database access error occurs
	 * 
	 * @see DatabaseMetaData#insertsAreDetected
	 */
	public boolean rowInserted() throws SQLException {
		return s.rowInserted();
	}

	/**
	 * JDBC 2.0
	 * 
	 * Indicates whether the current row has been updated. The value returned
	 * depends on whether or not the result set can detect updates.
	 * 
	 * @return true if the row has been visibly updated by the owner or another,
	 *         and updates are detected
	 * @exception SQLException
	 *                if a database access error occurs
	 * 
	 * @see DatabaseMetaData#updatesAreDetected
	 */
	public boolean rowUpdated() throws SQLException {
		return s.rowUpdated();
	}

	/**
	 * JDBC 2.0
	 * 
	 * Gives a hint as to the direction in which the rows in this result set
	 * will be processed. The initial value is determined by the statement that
	 * produced the result set. The fetch direction may be changed at any time.
	 * 
	 * @exception SQLException
	 *                if a database access error occurs or the result set type
	 *                is TYPE_FORWARD_ONLY and the fetch direction is not
	 *                FETCH_FORWARD.
	 */
	public void setFetchDirection(int direction) throws SQLException {
		s.setFetchDirection(direction);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Gives the JDBC driver a hint as to the number of rows that should be
	 * fetched from the database when more rows are needed for this result set.
	 * If the fetch size specified is zero, the JDBC driver ignores the value
	 * and is free to make its own best guess as to what the fetch size should
	 * be. The default value is set by the statement that created the result
	 * set. The fetch size may be changed at any time.
	 * 
	 * @param rows
	 *            the number of rows to fetch
	 * @exception SQLException
	 *                if a database access error occurs or the condition 0 <=
	 *                rows <= this.getMaxRows() is not satisfied.
	 */
	public void setFetchSize(int rows) throws SQLException {
		s.setFetchSize(rows);
	}

	/**
	 * Updates the designated column with a <code>java.sql.Array</code> value.
	 * The updater methods are used to update column values in the current row
	 * or the insert row. The updater methods do not update the underlying
	 * database; instead the <code>updateRow</code> or <code>insertRow</code>
	 * methods are called to update the database.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @param x
	 *            the new column value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void updateArray(int columnIndex, java.sql.Array x)
			throws SQLException {
		System.err
				.println("SpatialResultSet.updateArray(int,Array) is not implemented.");
	}

	/**
	 * Updates the designated column with a <code>java.sql.Array</code> value.
	 * The updater methods are used to update column values in the current row
	 * or the insert row. The updater methods do not update the underlying
	 * database; instead the <code>updateRow</code> or <code>insertRow</code>
	 * methods are called to update the database.
	 * 
	 * @param columnName
	 *            the name of the column
	 * @param x
	 *            the new column value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void updateArray(String columnName, java.sql.Array x)
			throws SQLException {
		System.err
				.println("SpatialResultSet.updateArray(String,Array) is not implemented.");
	}

	/**
	 * JDBC 2.0
	 * 
	 * Updates a column with an ascii stream value.
	 * 
	 * The <code>updateXXX</code> methods are used to update column values in
	 * the current row, or the insert row. The <code>updateXXX</code> methods do
	 * not update the underlying database; instead the <code>updateRow</code> or
	 * <code>insertRow</code> methods are called to update the database.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @param x
	 *            the new column value
	 * @param length
	 *            the length of the stream
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void updateAsciiStream(int columnIndex, java.io.InputStream x,
			int length) throws SQLException {
		s.updateAsciiStream(columnIndex, x, length);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Updates a column with an ascii stream value.
	 * 
	 * The <code>updateXXX</code> methods are used to update column values in
	 * the current row, or the insert row. The <code>updateXXX</code> methods do
	 * not update the underlying database; instead the <code>updateRow</code> or
	 * <code>insertRow</code> methods are called to update the database.
	 * 
	 * @param columnName
	 *            the name of the column
	 * @param x
	 *            the new column value
	 * @param length
	 *            of the stream
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void updateAsciiStream(String columnName, java.io.InputStream x,
			int length) throws SQLException {
		s.updateAsciiStream(columnName, x, length);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Updates a column with a BigDecimal value.
	 * 
	 * The <code>updateXXX</code> methods are used to update column values in
	 * the current row, or the insert row. The <code>updateXXX</code> methods do
	 * not update the underlying database; instead the <code>updateRow</code> or
	 * <code>insertRow</code> methods are called to update the database.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @param x
	 *            the new column value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void updateBigDecimal(int columnIndex, java.math.BigDecimal x)
			throws SQLException {
		s.updateBigDecimal(columnIndex, x);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Updates a column with a BigDecimal value.
	 * 
	 * The <code>updateXXX</code> methods are used to update column values in
	 * the current row, or the insert row. The <code>updateXXX</code> methods do
	 * not update the underlying database; instead the <code>updateRow</code> or
	 * <code>insertRow</code> methods are called to update the database.
	 * 
	 * @param columnName
	 *            the name of the column
	 * @param x
	 *            the new column value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void updateBigDecimal(String columnName, java.math.BigDecimal x)
			throws SQLException {
		s.updateBigDecimal(columnName, x);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Updates a column with a binary stream value.
	 * 
	 * The <code>updateXXX</code> methods are used to update column values in
	 * the current row, or the insert row. The <code>updateXXX</code> methods do
	 * not update the underlying database; instead the <code>updateRow</code> or
	 * <code>insertRow</code> methods are called to update the database.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @param x
	 *            the new column value
	 * @param length
	 *            the length of the stream
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void updateBinaryStream(int columnIndex, java.io.InputStream x,
			int length) throws SQLException {
		s.updateBinaryStream(columnIndex, x, length);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Updates a column with a binary stream value.
	 * 
	 * The <code>updateXXX</code> methods are used to update column values in
	 * the current row, or the insert row. The <code>updateXXX</code> methods do
	 * not update the underlying database; instead the <code>updateRow</code> or
	 * <code>insertRow</code> methods are called to update the database.
	 * 
	 * @param columnName
	 *            the name of the column
	 * @param x
	 *            the new column value
	 * @param length
	 *            of the stream
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void updateBinaryStream(String columnName, java.io.InputStream x,
			int length) throws SQLException {
		s.updateBinaryStream(columnName, x, length);
	}

	/**
	 * Updates the designated column with a <code>java.sql.Blob</code> value.
	 * The updater methods are used to update column values in the current row
	 * or the insert row. The updater methods do not update the underlying
	 * database; instead the <code>updateRow</code> or <code>insertRow</code>
	 * methods are called to update the database.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @param x
	 *            the new column value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void updateBlob(int columnIndex, java.sql.Blob x)
			throws SQLException {
		System.err
				.println("SpatialResultSet.updateBlob(int,Blob) is not implemented.");
	}

	/**
	 * Updates the designated column with a <code>java.sql.Blob</code> value.
	 * The updater methods are used to update column values in the current row
	 * or the insert row. The updater methods do not update the underlying
	 * database; instead the <code>updateRow</code> or <code>insertRow</code>
	 * methods are called to update the database.
	 * 
	 * @param columnName
	 *            the name of the column
	 * @param x
	 *            the new column value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void updateBlob(String columnName, java.sql.Blob x)
			throws SQLException {
		System.err
				.println("SpatialResultSet.updateBlob(String,Blob) is not implemented.");
	}

	/**
	 * JDBC 2.0
	 * 
	 * Updates a column with a boolean value.
	 * 
	 * The <code>updateXXX</code> methods are used to update column values in
	 * the current row, or the insert row. The <code>updateXXX</code> methods do
	 * not update the underlying database; instead the <code>updateRow</code> or
	 * <code>insertRow</code> methods are called to update the database.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @param x
	 *            the new column value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void updateBoolean(int columnIndex, boolean x) throws SQLException {
		s.updateBoolean(columnIndex, x);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Updates a column with a boolean value.
	 * 
	 * The <code>updateXXX</code> methods are used to update column values in
	 * the current row, or the insert row. The <code>updateXXX</code> methods do
	 * not update the underlying database; instead the <code>updateRow</code> or
	 * <code>insertRow</code> methods are called to update the database.
	 * 
	 * @param columnName
	 *            the name of the column
	 * @param x
	 *            the new column value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void updateBoolean(String columnName, boolean x) throws SQLException {
		s.updateBoolean(columnName, x);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Updates a column with a byte value.
	 * 
	 * The <code>updateXXX</code> methods are used to update column values in
	 * the current row, or the insert row. The <code>updateXXX</code> methods do
	 * not update the underlying database; instead the <code>updateRow</code> or
	 * <code>insertRow</code> methods are called to update the database.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @param x
	 *            the new column value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void updateByte(int columnIndex, byte x) throws SQLException {
		s.updateByte(columnIndex, x);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Updates a column with a byte value.
	 * 
	 * The <code>updateXXX</code> methods are used to update column values in
	 * the current row, or the insert row. The <code>updateXXX</code> methods do
	 * not update the underlying database; instead the <code>updateRow</code> or
	 * <code>insertRow</code> methods are called to update the database.
	 * 
	 * @param columnName
	 *            the name of the column
	 * @param x
	 *            the new column value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void updateByte(String columnName, byte x) throws SQLException {
		s.updateByte(columnName, x);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Updates a column with a byte array value.
	 * 
	 * The <code>updateXXX</code> methods are used to update column values in
	 * the current row, or the insert row. The <code>updateXXX</code> methods do
	 * not update the underlying database; instead the <code>updateRow</code> or
	 * <code>insertRow</code> methods are called to update the database.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @param x
	 *            the new column value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void updateBytes(int columnIndex, byte[] x) throws SQLException {
		s.updateBytes(columnIndex, x);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Updates a column with a byte array value.
	 * 
	 * The <code>updateXXX</code> methods are used to update column values in
	 * the current row, or the insert row. The <code>updateXXX</code> methods do
	 * not update the underlying database; instead the <code>updateRow</code> or
	 * <code>insertRow</code> methods are called to update the database.
	 * 
	 * @param columnName
	 *            the name of the column
	 * @param x
	 *            the new column value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void updateBytes(String columnName, byte[] x) throws SQLException {
		s.updateBytes(columnName, x);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Updates a column with a character stream value.
	 * 
	 * The <code>updateXXX</code> methods are used to update column values in
	 * the current row, or the insert row. The <code>updateXXX</code> methods do
	 * not update the underlying database; instead the <code>updateRow</code> or
	 * <code>insertRow</code> methods are called to update the database.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @param x
	 *            the new column value
	 * @param length
	 *            the length of the stream
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void updateCharacterStream(int columnIndex, java.io.Reader x,
			int length) throws SQLException {
		s.updateCharacterStream(columnIndex, x, length);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Updates a column with a character stream value.
	 * 
	 * The <code>updateXXX</code> methods are used to update column values in
	 * the current row, or the insert row. The <code>updateXXX</code> methods do
	 * not update the underlying database; instead the <code>updateRow</code> or
	 * <code>insertRow</code> methods are called to update the database.
	 * 
	 * @param columnName
	 *            the name of the column
	 * @param x
	 *            the new column value
	 * @param length
	 *            of the stream
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void updateCharacterStream(String columnName, java.io.Reader x,
			int length) throws SQLException {
		s.updateCharacterStream(columnName, x, length);
	}

	/**
	 * Updates the designated column with a <code>java.sql.Clob</code> value.
	 * The updater methods are used to update column values in the current row
	 * or the insert row. The updater methods do not update the underlying
	 * database; instead the <code>updateRow</code> or <code>insertRow</code>
	 * methods are called to update the database.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @param x
	 *            the new column value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void updateClob(int columnIndex, java.sql.Clob x)
			throws SQLException {
		System.err
				.println("SpatialResultSet.updateClob(int,Clob) is not implemented.");
	}

	/**
	 * Updates the designated column with a <code>java.sql.Clob</code> value.
	 * The updater methods are used to update column values in the current row
	 * or the insert row. The updater methods do not update the underlying
	 * database; instead the <code>updateRow</code> or <code>insertRow</code>
	 * methods are called to update the database.
	 * 
	 * @param columnName
	 *            the name of the column
	 * @param x
	 *            the new column value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void updateClob(String columnName, java.sql.Clob x)
			throws SQLException {
		System.err
				.println("SpatialResultSet.updateClob(String,Clob) is not implemented.");
	}

	/**
	 * JDBC 2.0
	 * 
	 * Updates a column with a Date value.
	 * 
	 * The <code>updateXXX</code> methods are used to update column values in
	 * the current row, or the insert row. The <code>updateXXX</code> methods do
	 * not update the underlying database; instead the <code>updateRow</code> or
	 * <code>insertRow</code> methods are called to update the database.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @param x
	 *            the new column value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void updateDate(int columnIndex, Date x) throws SQLException {
		s.updateDate(columnIndex, x);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Updates a column with a Date value.
	 * 
	 * The <code>updateXXX</code> methods are used to update column values in
	 * the current row, or the insert row. The <code>updateXXX</code> methods do
	 * not update the underlying database; instead the <code>updateRow</code> or
	 * <code>insertRow</code> methods are called to update the database.
	 * 
	 * @param columnName
	 *            the name of the column
	 * @param x
	 *            the new column value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void updateDate(String columnName, Date x) throws SQLException {
		s.updateDate(columnName, x);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Updates a column with a Double value.
	 * 
	 * The <code>updateXXX</code> methods are used to update column values in
	 * the current row, or the insert row. The <code>updateXXX</code> methods do
	 * not update the underlying database; instead the <code>updateRow</code> or
	 * <code>insertRow</code> methods are called to update the database.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @param x
	 *            the new column value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void updateDouble(int columnIndex, double x) throws SQLException {
		s.updateDouble(columnIndex, x);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Updates a column with a double value.
	 * 
	 * The <code>updateXXX</code> methods are used to update column values in
	 * the current row, or the insert row. The <code>updateXXX</code> methods do
	 * not update the underlying database; instead the <code>updateRow</code> or
	 * <code>insertRow</code> methods are called to update the database.
	 * 
	 * @param columnName
	 *            the name of the column
	 * @param x
	 *            the new column value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void updateDouble(String columnName, double x) throws SQLException {
		s.updateDouble(columnName, x);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Updates a column with a float value.
	 * 
	 * The <code>updateXXX</code> methods are used to update column values in
	 * the current row, or the insert row. The <code>updateXXX</code> methods do
	 * not update the underlying database; instead the <code>updateRow</code> or
	 * <code>insertRow</code> methods are called to update the database.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @param x
	 *            the new column value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void updateFloat(int columnIndex, float x) throws SQLException {
		s.updateFloat(columnIndex, x);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Updates a column with a float value.
	 * 
	 * The <code>updateXXX</code> methods are used to update column values in
	 * the current row, or the insert row. The <code>updateXXX</code> methods do
	 * not update the underlying database; instead the <code>updateRow</code> or
	 * <code>insertRow</code> methods are called to update the database.
	 * 
	 * @param columnName
	 *            the name of the column
	 * @param x
	 *            the new column value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void updateFloat(String columnName, float x) throws SQLException {
		s.updateFloat(columnName, x);
	}

	/**
	 * Updates a column with a sdoapi.geom.Geometry value.
	 * 
	 * The <code>updateXXX</code> methods are used to update column values in
	 * the current row, or the insert row. The <code>updateXXX</code> methods do
	 * not update the underlying database; instead the <code>updateRow</code> or
	 * <code>insertRow</code> methods are called to update the database.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @param x
	 *            the new column value
	 * @exception SQLException
	 *                if an error occurs
	 */
	public void updateGeometry(int columnIndex, Geometry x) throws SQLException {
		try {
			s.updateObject(columnIndex,
					adapter.exportGeometry(oracle.sql.STRUCT.class, x));
		} catch (InvalidGeometryException e) {
			throw new SQLException(e.toString());
		} catch (oracle.sdoapi.adapter.GeometryOutputTypeNotSupportedException e) {
			throw new SQLException(e.toString());
		}
	}

	/**
	 * Updates a column with a sdoapi.geom.Geometry value.
	 * 
	 * The <code>updateXXX</code> methods are used to update column values in
	 * the current row, or the insert row. The <code>updateXXX</code> methods do
	 * not update the underlying database; instead the <code>updateRow</code> or
	 * <code>insertRow</code> methods are called to update the database.
	 * 
	 * @param columnName
	 *            the name of the column
	 * @param x
	 *            the new column value
	 * @exception SQLException
	 *                if an error occurs
	 */
	public void updateGeometry(String columnName, Geometry x)
			throws SQLException {
		try {
			s.updateObject(columnName,
					adapter.exportGeometry(oracle.sql.STRUCT.class, x));
		} catch (InvalidGeometryException e) {
			throw new SQLException(e.toString());
		} catch (oracle.sdoapi.adapter.GeometryOutputTypeNotSupportedException e) {
			throw new SQLException(e.toString());
		}
	}

	/**
	 * JDBC 2.0
	 * 
	 * Updates a column with an integer value.
	 * 
	 * The <code>updateXXX</code> methods are used to update column values in
	 * the current row, or the insert row. The <code>updateXXX</code> methods do
	 * not update the underlying database; instead the <code>updateRow</code> or
	 * <code>insertRow</code> methods are called to update the database.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @param x
	 *            the new column value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void updateInt(int columnIndex, int x) throws SQLException {
		s.updateInt(columnIndex, x);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Updates a column with an integer value.
	 * 
	 * The <code>updateXXX</code> methods are used to update column values in
	 * the current row, or the insert row. The <code>updateXXX</code> methods do
	 * not update the underlying database; instead the <code>updateRow</code> or
	 * <code>insertRow</code> methods are called to update the database.
	 * 
	 * @param columnName
	 *            the name of the column
	 * @param x
	 *            the new column value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void updateInt(String columnName, int x) throws SQLException {
		s.updateInt(columnName, x);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Updates a column with a long value.
	 * 
	 * The <code>updateXXX</code> methods are used to update column values in
	 * the current row, or the insert row. The <code>updateXXX</code> methods do
	 * not update the underlying database; instead the <code>updateRow</code> or
	 * <code>insertRow</code> methods are called to update the database.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @param x
	 *            the new column value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void updateLong(int columnIndex, long x) throws SQLException {
		s.updateLong(columnIndex, x);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Updates a column with a long value.
	 * 
	 * The <code>updateXXX</code> methods are used to update column values in
	 * the current row, or the insert row. The <code>updateXXX</code> methods do
	 * not update the underlying database; instead the <code>updateRow</code> or
	 * <code>insertRow</code> methods are called to update the database.
	 * 
	 * @param columnName
	 *            the name of the column
	 * @param x
	 *            the new column value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void updateLong(String columnName, long x) throws SQLException {
		s.updateLong(columnName, x);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Give a nullable column a null value.
	 * 
	 * The <code>updateXXX</code> methods are used to update column values in
	 * the current row, or the insert row. The <code>updateXXX</code> methods do
	 * not update the underlying database; instead the <code>updateRow</code> or
	 * <code>insertRow</code> methods are called to update the database.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void updateNull(int columnIndex) throws SQLException {
		s.updateNull(columnIndex);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Updates a column with a null value.
	 * 
	 * The <code>updateXXX</code> methods are used to update column values in
	 * the current row, or the insert row. The <code>updateXXX</code> methods do
	 * not update the underlying database; instead the <code>updateRow</code> or
	 * <code>insertRow</code> methods are called to update the database.
	 * 
	 * @param columnName
	 *            the name of the column
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void updateNull(String columnName) throws SQLException {
		s.updateNull(columnName);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Updates a column with an Object value.
	 * 
	 * The <code>updateXXX</code> methods are used to update column values in
	 * the current row, or the insert row. The <code>updateXXX</code> methods do
	 * not update the underlying database; instead the <code>updateRow</code> or
	 * <code>insertRow</code> methods are called to update the database.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @param x
	 *            the new column value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void updateObject(int columnIndex, Object x) throws SQLException {
		s.updateObject(columnIndex, x);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Updates a column with an Object value.
	 * 
	 * The <code>updateXXX</code> methods are used to update column values in
	 * the current row, or the insert row. The <code>updateXXX</code> methods do
	 * not update the underlying database; instead the <code>updateRow</code> or
	 * <code>insertRow</code> methods are called to update the database.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @param x
	 *            the new column value
	 * @param scale
	 *            For java.sql.Types.DECIMAL or java.sql.Types.NUMERIC types
	 *            this is the number of digits after the decimal. For all other
	 *            types this value will be ignored.
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void updateObject(int columnIndex, Object x, int scale)
			throws SQLException {
		s.updateObject(columnIndex, x, scale);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Updates a column with an Object value.
	 * 
	 * The <code>updateXXX</code> methods are used to update column values in
	 * the current row, or the insert row. The <code>updateXXX</code> methods do
	 * not update the underlying database; instead the <code>updateRow</code> or
	 * <code>insertRow</code> methods are called to update the database.
	 * 
	 * @param columnName
	 *            the name of the column
	 * @param x
	 *            the new column value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void updateObject(String columnName, Object x) throws SQLException {
		s.updateObject(columnName, x);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Updates a column with an Object value.
	 * 
	 * The <code>updateXXX</code> methods are used to update column values in
	 * the current row, or the insert row. The <code>updateXXX</code> methods do
	 * not update the underlying database; instead the <code>updateRow</code> or
	 * <code>insertRow</code> methods are called to update the database.
	 * 
	 * @param columnName
	 *            the name of the column
	 * @param x
	 *            the new column value
	 * @param scale
	 *            For java.sql.Types.DECIMAL or java.sql.Types.NUMERIC types
	 *            this is the number of digits after the decimal. For all other
	 *            types this value will be ignored.
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void updateObject(String columnName, Object x, int scale)
			throws SQLException {
		s.updateObject(columnName, x, scale);
	}

	/**
	 * Updates the designated column with a <code>java.sql.Ref</code> value. The
	 * updater methods are used to update column values in the current row or
	 * the insert row. The updater methods do not update the underlying
	 * database; instead the <code>updateRow</code> or <code>insertRow</code>
	 * methods are called to update the database.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @param x
	 *            the new column value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void updateRef(int columnIndex, java.sql.Ref x) throws SQLException {
		System.err
				.println("SpatialResultSet.updateRef(int,Ref) is not implemented.");
	}

	/**
	 * Updates the designated column with a <code>java.sql.Ref</code> value. The
	 * updater methods are used to update column values in the current row or
	 * the insert row. The updater methods do not update the underlying
	 * database; instead the <code>updateRow</code> or <code>insertRow</code>
	 * methods are called to update the database.
	 * 
	 * @param columnName
	 *            the name of the column
	 * @param x
	 *            the new column value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void updateRef(String columnName, java.sql.Ref x)
			throws SQLException {
		System.err
				.println("SpatialResultSet.updateRef(String,Ref) is not implemented.");
	}

	/**
	 * JDBC 2.0
	 * 
	 * Updates the underlying database with the new contents of the current row.
	 * Cannot be called when on the insert row.
	 * 
	 * @exception SQLException
	 *                if a database access error occurs or if called when on the
	 *                insert row
	 */
	public void updateRow() throws SQLException {
		s.updateRow();
	}

	/**
	 * JDBC 2.0
	 * 
	 * Updates a column with a short value.
	 * 
	 * The <code>updateXXX</code> methods are used to update column values in
	 * the current row, or the insert row. The <code>updateXXX</code> methods do
	 * not update the underlying database; instead the <code>updateRow</code> or
	 * <code>insertRow</code> methods are called to update the database.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @param x
	 *            the new column value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void updateShort(int columnIndex, short x) throws SQLException {
		s.updateShort(columnIndex, x);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Updates a column with a short value.
	 * 
	 * The <code>updateXXX</code> methods are used to update column values in
	 * the current row, or the insert row. The <code>updateXXX</code> methods do
	 * not update the underlying database; instead the <code>updateRow</code> or
	 * <code>insertRow</code> methods are called to update the database.
	 * 
	 * @param columnName
	 *            the name of the column
	 * @param x
	 *            the new column value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void updateShort(String columnName, short x) throws SQLException {
		s.updateShort(columnName, x);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Updates a column with a String value.
	 * 
	 * The <code>updateXXX</code> methods are used to update column values in
	 * the current row, or the insert row. The <code>updateXXX</code> methods do
	 * not update the underlying database; instead the <code>updateRow</code> or
	 * <code>insertRow</code> methods are called to update the database.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @param x
	 *            the new column value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void updateString(int columnIndex, String x) throws SQLException {
		s.updateString(columnIndex, x);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Updates a column with a String value.
	 * 
	 * The <code>updateXXX</code> methods are used to update column values in
	 * the current row, or the insert row. The <code>updateXXX</code> methods do
	 * not update the underlying database; instead the <code>updateRow</code> or
	 * <code>insertRow</code> methods are called to update the database.
	 * 
	 * @param columnName
	 *            the name of the column
	 * @param x
	 *            the new column value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void updateString(String columnName, String x) throws SQLException {
		s.updateString(columnName, x);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Updates a column with a Time value.
	 * 
	 * The <code>updateXXX</code> methods are used to update column values in
	 * the current row, or the insert row. The <code>updateXXX</code> methods do
	 * not update the underlying database; instead the <code>updateRow</code> or
	 * <code>insertRow</code> methods are called to update the database.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @param x
	 *            the new column value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void updateTime(int columnIndex, Time x) throws SQLException {
		s.updateTime(columnIndex, x);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Updates a column with a Time value.
	 * 
	 * The <code>updateXXX</code> methods are used to update column values in
	 * the current row, or the insert row. The <code>updateXXX</code> methods do
	 * not update the underlying database; instead the <code>updateRow</code> or
	 * <code>insertRow</code> methods are called to update the database.
	 * 
	 * @param columnName
	 *            the name of the column
	 * @param x
	 *            the new column value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void updateTime(String columnName, Time x) throws SQLException {
		s.updateTime(columnName, x);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Updates a column with a Timestamp value.
	 * 
	 * The <code>updateXXX</code> methods are used to update column values in
	 * the current row, or the insert row. The <code>updateXXX</code> methods do
	 * not update the underlying database; instead the <code>updateRow</code> or
	 * <code>insertRow</code> methods are called to update the database.
	 * 
	 * @param columnIndex
	 *            the first column is 1, the second is 2, ...
	 * @param x
	 *            the new column value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void updateTimestamp(int columnIndex, Timestamp x)
			throws SQLException {
		s.updateTimestamp(columnIndex, x);
	}

	/**
	 * JDBC 2.0
	 * 
	 * Updates a column with a Timestamp value.
	 * 
	 * The <code>updateXXX</code> methods are used to update column values in
	 * the current row, or the insert row. The <code>updateXXX</code> methods do
	 * not update the underlying database; instead the <code>updateRow</code> or
	 * <code>insertRow</code> methods are called to update the database.
	 * 
	 * @param columnName
	 *            the name of the column
	 * @param x
	 *            the new column value
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public void updateTimestamp(String columnName, Timestamp x)
			throws SQLException {
		s.updateTimestamp(columnName, x);
	}

	/**
	 * Reports whether the last column read had a value of SQL NULL. Note that
	 * you must first call getXXX on a column to try to read its value and then
	 * call wasNull() to see if the value read was SQL NULL.
	 * 
	 * @return true if last column read was SQL NULL and false otherwise
	 * @exception SQLException
	 *                if a database access error occurs
	 */
	public boolean wasNull() throws SQLException {
		return s.wasNull();
	}

	public boolean isWrapperFor(Class arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public Object unwrap(Class arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getHoldability() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public Reader getNCharacterStream(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Reader getNCharacterStream(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public NClob getNClob(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public NClob getNClob(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNString(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNString(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public RowId getRowId(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public RowId getRowId(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public SQLXML getSQLXML(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public SQLXML getSQLXML(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isClosed() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public void updateAsciiStream(int arg0, InputStream arg1)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateAsciiStream(String arg0, InputStream arg1)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateAsciiStream(int arg0, InputStream arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateAsciiStream(String arg0, InputStream arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateBinaryStream(int arg0, InputStream arg1)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateBinaryStream(String arg0, InputStream arg1)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateBinaryStream(int arg0, InputStream arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateBinaryStream(String arg0, InputStream arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateBlob(int arg0, InputStream arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateBlob(String arg0, InputStream arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateBlob(int arg0, InputStream arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateBlob(String arg0, InputStream arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateCharacterStream(int arg0, Reader arg1)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateCharacterStream(String arg0, Reader arg1)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateCharacterStream(int arg0, Reader arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateCharacterStream(String arg0, Reader arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateClob(int arg0, Reader arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateClob(String arg0, Reader arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateClob(int arg0, Reader arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateClob(String arg0, Reader arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateNCharacterStream(int arg0, Reader arg1)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateNCharacterStream(String arg0, Reader arg1)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateNCharacterStream(int arg0, Reader arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateNCharacterStream(String arg0, Reader arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateNClob(int arg0, NClob arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateNClob(String arg0, NClob arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateNClob(int arg0, Reader arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateNClob(String arg0, Reader arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateNClob(int arg0, Reader arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateNClob(String arg0, Reader arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateNString(int arg0, String arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateNString(String arg0, String arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateRowId(int arg0, RowId arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateRowId(String arg0, RowId arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateSQLXML(int arg0, SQLXML arg1) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateSQLXML(String arg0, SQLXML arg1) throws SQLException {
		// TODO Auto-generated method stub

	}
}
