package utils;

import java.sql.*;
import java.util.HashMap;

public class JDBCUtil {
	public JDBCUtil() {
	}

	/**
	 *  use it to access database
	 */
	public Connection connectToDatabase() {
		Connection db;
		try {
			String url = "jdbc:postgresql://localhost:5432/hw";
			String user = "postgres";
			String passWord = "root";
			db = DriverManager.getConnection(url, user, passWord);
		} catch (SQLException e) {
			System.out.println("Database: ======> Connecting Failed!");
			e.printStackTrace();
			return null;
		}
		System.out.println("Database: ======> Connecting successful!");
		return db;
	}


	/**
	 * @param db        type Connection
	 * @param tableName type String
	 * @return Put the column name and type into the Map
	 */
	public static HashMap<String, String> columnNameAndType(Connection db, String tableName) {
		HashMap<String, String> Name_Type = new HashMap<>();
		String query = "select * from " + tableName;
		String type = "";
		try {
			Statement stat = db.createStatement();
			ResultSet rs = stat.executeQuery(query);
			ResultSetMetaData mt = rs.getMetaData();
			for (int i = 1; i <= mt.getColumnCount(); i++) {

				type = mt.getColumnClassName(i);
				String[] ss = type.split("\\.");
				Name_Type.put(mt.getColumnName(i), ss[2]);
			}
		} catch (SQLException e) {
			System.out.print(e.getMessage());
		}
		return Name_Type;
	}
}
