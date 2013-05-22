package connection.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import connection.ConnectionProvider;

public class MysqlConnectionProvider extends ConnectionProvider {

	@Override
	public Connection createConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		// Setup the connection with the DB
		try {
			return DriverManager
					.getConnection("jdbc:mysql://localhost/traffic?"
							+ "user=traffic&password=thetrafficgenerator");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
