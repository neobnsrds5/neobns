package com.demo.jgen;

import io.swagger.v3.oas.models.media.Schema;

import java.io.IOException;
import java.util.Map;

/*
 * DTO 코드 생성기
 */
public class DTOCodeGenerator implements BaseCodeGenerator {

    @Override
    public void generateCode(String packageName, String resourceName, String packageDir, Schema<?> schema) throws IOException {
    	// Extracting schema properties (fields)
    	// yml 파일에서 components > schemas > properties에 정의된 속성의 이름과 타입 정보를 Map 형태로 반환
        Map<String, Schema> properties = schema.getProperties();
    	StringBuilder dtoCode = new StringBuilder();
        
        dtoCode.append("package ").append(packageName).append(";\n\n")
                .append("import com.fasterxml.jackson.annotation.JsonProperty;\n")  // Import JsonProperty
                .append("import lombok.*;\n\n")
                .append("@Getter\n@Setter\n@NoArgsConstructor\n@AllArgsConstructor\n@Builder\n")
                .append("public class ").append(resourceName).append("Dto {\n")
                .append("    @JsonProperty(\"id\")\n")
                .append("    private Long id;\n");
        // 필드 생성
        properties.forEach((name, property) -> {
            // Special condition for the "id" field to always be Long (필드명이 id인 경우 항상 Long 타입)
            String javaType = "id".equals(name) ? "Long" : OpenApiCodeGenerator.mapSchemaTypeToJavaType(((Schema) property).getType(), ((Schema) property).getFormat());

            // Add JsonProperty annotation and the field declaration
            if(!name.equals("id")) {
                dtoCode.append("    @JsonProperty(\"").append(name).append("\")\n")
                        .append("    private ").append(javaType).append(" ").append(name).append(";\n");
            }
        });
        dtoCode.append("}\n");
        
        writeToFile(packageDir + resourceName + "Dto.java", dtoCode.toString());
    }
}
