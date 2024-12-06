package com.demo.jgen;

import io.swagger.v3.oas.models.media.Schema;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

import static com.demo.jgen.OpenApiCodeGenerator.mapSchemaTypeToJavaType;

public class TestDataSQLGenerator implements BaseCodeGenerator {

    private Random random = new Random();

    @Override
    public void generateCode(String packageName, String resourceName, String packageDir, Schema schema) throws IOException {
        String tableName = resourceName.toLowerCase(); // 테이블 이름은 대개 resourceName을 소문자로 사용
        StringBuilder createTableBuilder = new StringBuilder();
        StringBuilder sqlBuilder = new StringBuilder();

        // 테이블 생성 구문 시작
        // Drop table if exists
        sqlBuilder.append("DROP TABLE IF EXISTS ").append(tableName).append(";\n");
        createTableBuilder.append("CREATE TABLE ").append(tableName).append(" (\n");
        // Define the id field explicitly
        createTableBuilder.append("    id BIGINT AUTO_INCREMENT PRIMARY KEY,\n");
        // 테이블에 삽입할 필드를 찾는다
        Map<String, Schema> properties = schema.getProperties();

        for (Map.Entry<String, Schema> entry : properties.entrySet()) {
            String fieldName = entry.getKey();
            if(fieldName.toLowerCase().equals("id"))
                continue;

            String fieldType = mapSchemaTypeToJavaSqlType(entry.getValue().getType(), entry.getValue().getFormat());

            // 각 필드 추가
            createTableBuilder.append("    ").append(fieldName).append(" ").append(fieldType).append(",\n");
        }

        // 마지막 쉼표 제거 후 PRIMARY KEY 추가
        if (createTableBuilder.length() > 0) {
            createTableBuilder.setLength(createTableBuilder.length() - 2);
            createTableBuilder.append("\n);");
        }

        sqlBuilder.append(createTableBuilder.toString()).append("\n\n");

        for (int i = 1; i <= 3; i++) {
            StringBuilder values = new StringBuilder();

            for (Map.Entry<String, Schema> entry : properties.entrySet()) {
                String fieldName = entry.getKey();
                if(fieldName.toLowerCase().equals("id"))
                    continue;
                String fieldType = mapSchemaTypeToJavaType(entry.getValue().getType(), entry.getValue().getFormat());

                // 값 형식 추가
                values.append(generateSqlValueForType(fieldType, i)).append(", ");
            }

            // 마지막 쉼표 제거
            if (values.length() > 0) {
                values.setLength(values.length() - 2);
            }

            // SQL 삽입 구문 완성
            sqlBuilder.append("INSERT INTO ").append(tableName).append(" ( ")
                    .append(String.join(", ", properties.keySet().stream()
                            .filter(field -> !field.equalsIgnoreCase("id"))
                            .toList()))
                    .append(") VALUES (")
                    .append(values).append(");\n");
        }

        // 결과 출력 (또는 파일로 저장)
        System.out.println("Generated SQL for " + tableName + ":");
        System.out.println(sqlBuilder.toString());

        // 파일로 저장하는 코드도 추가 가능
        writeToFile(packageDir + resourceName + "Test-data.sql",
                sqlBuilder.toString());
    }
    String mapSchemaTypeToJavaSqlType(String schemaType, String schemaFormat) {
        if (schemaType == null) {
            return "TEXT";
        }

        switch (schemaType) {
            case "string":
                if ("date".equals(schemaFormat)) {
                    return "DATE";
                } else if ("date-time".equals(schemaFormat)) {
                    return "TIMESTAMP";
                } else {
                    return "VARCHAR(255)";
                }

            case "integer":
                if ("int32".equals(schemaFormat)) {
                    return "INT";
                } else if ("int64".equals(schemaFormat)) {
                    return "BIGINT";
                } else {
                    return "INT"; // Default to INT for unspecified formats
                }

            case "number":
                if ("float".equals(schemaFormat)) {
                    return "FLOAT";
                } else if ("double".equals(schemaFormat)) {
                    return "DOUBLE";
                } else {
                    return "DOUBLE"; // Default to DOUBLE for unspecified formats
                }

            case "boolean":
                return "BOOLEAN";

            case "array":
                return "TEXT"; // Arrays can be stored as JSON or text in SQL

            case "object":
                return "JSON"; // Assuming object types are stored as JSON

            default:
                return "TEXT"; // Fallback for unknown types
        }
    }

    // Java 타입에 맞는 SQL 값을 생성
    private String generateSqlValueForType(String fieldType, int index) {
        switch (fieldType) {
            case "String":
                return String.format("'example_value_%d'", index); // Unique example values

            case "Integer":
            case "int":
                return String.valueOf(index); // Sequential integers

            case "Long":
                return String.valueOf((long) index * 1000); // Larger sequential numbers

            case "Double":
                return String.format("%.2f", random.nextDouble() * 100); // Random double between 0.0 and 100.0

            case "Float":
                return String.format("%.2f", random.nextFloat() * 100); // Random float between 0.0 and 100.0

            case "Boolean":
                return random.nextBoolean() ? "true" : "false"; // Random true/false

            case "java.time.LocalDate":
                return "'2024-01-01'"; // Example date

            case "java.time.LocalDateTime":
                return "'2024-01-01T12:00:00'"; // Example datetime

            case "java.util.UUID":
                return "'123e4567-e89b-12d3-a456-426614174000'"; // Example UUID

            case "List":
            case "Map":
            case "Object":
                return "'{}'"; // Empty JSON-like structure

            default:
                return String.format("'example_value_%d'", index); // Default unique example value
        }
    }

}
