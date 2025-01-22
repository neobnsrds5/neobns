package com.neobns.admin.codegen.dto;

import java.util.HashMap;
import java.util.List;

public class TableDTO {
	String tableName; // 테이블명
	HashMap<String, Boolean> columns; // <컬럼명, 기본키 여부>

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public HashMap<String, Boolean> getColumns() {
		return columns;
	}

	public void setColumns(HashMap<String, Boolean> columns) {
		this.columns = columns;
	}

	@Override
	public String toString() { // javascript 에서 사용하기 위해 json 형식으로 toString()
		StringBuilder jsonBuilder = new StringBuilder();
		jsonBuilder.append("{");

		jsonBuilder.append("\"tableName\":\"").append(tableName).append("\",");
		jsonBuilder.append("\"columns\":{");

		int columnIndex = 0;
		for (String columnName : columns.keySet()) {
			Boolean isPrimaryKey = columns.get(columnName);
			jsonBuilder.append("\"").append(columnName).append("\":").append(isPrimaryKey);

			// 마지막 column 이 아닌 경우 , 추가
			if (++columnIndex < columns.size()) {
				jsonBuilder.append(",");
			}
		}
		jsonBuilder.append("}"); // columns 객체 닫는 괄호

		jsonBuilder.append("}"); // 전체 객체 닫는 괄호

		return jsonBuilder.toString();
	}

}
