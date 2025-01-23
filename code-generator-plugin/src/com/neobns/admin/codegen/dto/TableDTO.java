package com.neobns.admin.codegen.dto;

import java.util.List;

public class TableDTO {
	private String tableName; // 테이블명
	private List<ColumnDTO> columns; // <컬럼명, 기본키 여부>

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<ColumnDTO> getColumns() {
		return columns;
	}

	public void setColumns(List<ColumnDTO> columns) {
		this.columns = columns;
	}

	@Override
	public String toString() { // javascript 에서 사용하기 위해 json 형식으로 toString()
		StringBuilder columnsJson = new StringBuilder("[");
        for (int i = 0; i < columns.size(); i++) {
            columnsJson.append(columns.get(i).toString());
            if (i < columns.size() - 1) {
                columnsJson.append(", ");
            }
        }
        columnsJson.append("]");
        return String.format("{\"tableName\": \"%s\", \"columns\": %s}", tableName, columnsJson);
	}

}
