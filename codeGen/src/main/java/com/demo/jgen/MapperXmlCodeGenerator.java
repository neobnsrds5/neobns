package com.demo.jgen;

import io.swagger.v3.oas.models.media.Schema;
import java.io.IOException;
import java.util.Map;

/*
 * Mapper xml 코드 생성기
 */
public class MapperXmlCodeGenerator implements BaseCodeGenerator {

    @Override
    public void generateCode(String packageName, String resourceName, String packageDir, Schema<?> schema) throws IOException {
    	// Extracting schema properties (fields)
    	// yml 파일에서 components > schemas > properties에 정의된 속성의 이름과 타입 정보를 Map 형태로 반환
        Map<String, Schema> properties = schema.getProperties();
    	String tableName = resourceName.toLowerCase(); // 테이블 이름은 소문자로 가정
        StringBuilder insertFields = new StringBuilder();
        StringBuilder insertValues = new StringBuilder();
        StringBuilder updateFields = new StringBuilder();
        StringBuilder dynamicFilters = new StringBuilder();

        properties.forEach((name, property) -> {
            if(!name.equals("id")) {
                insertFields.append(name).append(", ");
                insertValues.append("#{").append(name).append("}, ");
            }
            if(!name.equals("id"))
                updateFields.append(name).append(" = #{").append(name).append("}, ");
            
            dynamicFilters.append("<if test=\"").append(name).append(" != null\">")
            .append(" AND ").append(name).append(" = #{").append(name).append("}")
            .append("</if>\n");
        });

        // Remove trailing commas and spaces (마지막 , 제거)
        if (insertFields.length() > 0) insertFields.setLength(insertFields.length() - 2);
        if (insertValues.length() > 0) insertValues.setLength(insertValues.length() - 2);
        if (updateFields.length() > 0) updateFields.setLength(updateFields.length() - 2);

        String mapperXml = """
                <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
                <mapper namespace="%s.%sMapper">
                	
                    <select id="findAll" resultType="%s.%sDto">
                        SELECT * FROM %s
                    </select>

                    <select id="findByPage" resultType="%s.%sDto">
                        SELECT * 
                        FROM %s
                        
                        LIMIT #{limit} OFFSET #{offset}
                    </select>

                    <select id="findById" resultType="%s.%sDto">
                        SELECT * FROM %s WHERE id = #{id}
                    </select>

                    <insert id="insert" useGeneratedKeys="true" keyProperty="id">
                        INSERT INTO %s (%s)
                        VALUES (%s)
                    </insert>

                    <update id="update">
                        UPDATE %s
                        SET %s
                        WHERE id = #{id}
                    </update>

                    <delete id="deleteById">
                        DELETE FROM %s WHERE id = #{id}
                    </delete>

                </mapper>
                """.formatted(
                packageName, resourceName, // mapper namespace
                packageName, resourceName, tableName, // findAll
                packageName, resourceName, tableName, // findByPage
                packageName, resourceName, tableName, // findById
                tableName, insertFields, insertValues, //insert
                tableName, updateFields, //update
                tableName); //delete

        // Write the generated code to a file
        writeToFile(packageDir + resourceName + "Mapper.xml", mapperXml);
    }
}
