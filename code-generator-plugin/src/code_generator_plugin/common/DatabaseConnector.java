package code_generator_plugin.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import code_generator_plugin.dto.TableDTO;

public class DatabaseConnector {

	public static List<TableDTO> getTablesInfo(String mysqlUrl, String userId, String password) throws SQLException {

		// Registering the Driver
		DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());

		List<String> tableNames = new ArrayList<String>();
		List<TableDTO> tableList = new ArrayList<TableDTO>();

		try (Connection conn = DriverManager.getConnection(mysqlUrl, userId, password)) {
			
			String showTablesSql = "SHOW TABLES";

			// 테이블 이름 조회
			try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(showTablesSql)) {
				while (rs.next()) {
					tableNames.add(rs.getString(1));
				}
			}
			
			// table을 조회할 schema
			int lastSlashIndex = mysqlUrl.lastIndexOf('/'); // 마지막 '/' 위치를 찾음
			String schemaName = mysqlUrl.substring(lastSlashIndex + 1); // '/' 이후 문자열 추출
			String columnInfoSql = """
				    SELECT COLUMN_NAME,
				           IF(COLUMN_KEY = 'PRI', TRUE, FALSE) AS IS_PRIMARY_KEY
				    FROM INFORMATION_SCHEMA.COLUMNS
				    WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?
				    ORDER BY ORDINAL_POSITION
				""";
			
			// 테이블 컬럼 정보 조회
			try (PreparedStatement stmt = conn.prepareStatement(columnInfoSql)) {
                for (String tableName : tableNames) {
                    stmt.setString(1, schemaName);
                    stmt.setString(2, tableName);

                    try (ResultSet rs = stmt.executeQuery()) {
                    	TableDTO tableDto = new TableDTO();
                        HashMap<String, Boolean> columns = new LinkedHashMap<String, Boolean>();
                        
                        while (rs.next()) {
                            columns.put(rs.getString("COLUMN_NAME"), rs.getBoolean("IS_PRIMARY_KEY"));
                        }
                        
                        tableDto.setTableName(tableName);
                        tableDto.setColumns(columns);
                        tableList.add(tableDto);
                        
                    }
                }
                System.out.println(tableList);
            }
		}

		return tableList;
	}
}
