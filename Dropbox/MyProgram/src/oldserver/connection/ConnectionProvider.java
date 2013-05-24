package connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import connection.mysql.MysqlConnectionProvider;

public abstract class ConnectionProvider {

	public static String HOST_NAME = "siwa-umh.cs.umn.edu";

	private static Connection connection;

	/**
	 * Return a database connection.
	 * 
	 * @return
	 */
	public abstract Connection createConnection();

	public static void resetConnection() {
		connection = null;
	}

	public static Connection getConnection() {
		if (connection == null) {
			ConnectionProvider cp = new MysqlConnectionProvider();
			connection = cp.createConnection();
		}
		return connection;
	}

	public static void safeClose(Statement statement) {
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				// can't do anything about this...just ignore it.
			}
		}
	}
}
