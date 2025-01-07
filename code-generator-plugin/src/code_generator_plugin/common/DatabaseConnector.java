package code_generator_plugin.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnector {

	public static List<String> getTables(String mysqlUrl, String userId, String password) throws SQLException {
		List<String> list = new ArrayList<>();
		// Registering the Driver
		DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
		
		// 확인용
		System.out.println("Connecting to DB: " + mysqlUrl);
		System.out.println("Username: " + userId);
		
		try (Connection con = DriverManager.getConnection(mysqlUrl, userId, password);
				Statement stmt = con.createStatement();) {
			ResultSet rs = stmt.executeQuery("Show tables");
			while (rs.next()) {
				list.add(rs.getString(1));
			}
		}
		return list;
	}
}
