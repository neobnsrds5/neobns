package com.demo.jgen;

import io.swagger.v3.oas.models.media.Schema;
import java.io.IOException;

public class MapperXmlCodeGenerator implements BaseCodeGenerator {

    @Override
    public void generateCode(String packageName, String resourceName, String packageDir, Schema schema) throws IOException {
        String tableName = resourceName.toLowerCase(); // 테이블 이름은 소문자로 가정
        StringBuilder insertFields = new StringBuilder();
        StringBuilder insertValues = new StringBuilder();
        StringBuilder updateFields = new StringBuilder();

        schema.getProperties().forEach((name, property) -> {
            insertFields.append(name).append(", ");
            insertValues.append("#{").append(name).append("}, ");
            updateFields.append(name).append(" = #{").append(name).append("}, ");
        });

        // Remove trailing commas and spaces
        if (insertFields.length() > 0) insertFields.setLength(insertFields.length() - 2);
        if (insertValues.length() > 0) insertValues.setLength(insertValues.length() - 2);
        if (updateFields.length() > 0) updateFields.setLength(updateFields.length() - 2);

        String mapperXml = """
                <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
                <mapper namespace="%s.%sMapper">

                    <select id="findAll" resultType="%sDto">
                        SELECT * FROM %s WHERE 1=1
                        <!-- Add dynamic filters here -->
                    </select>

                    <select id="findByPage" resultType="%sDto">
                        SELECT * FROM %s
                        WHERE 1=1
                        <!-- Add dynamic filters here -->
                        LIMIT #{limit} OFFSET #{offset}
                    </select>

                    <select id="findById" resultType="%sDto">
                        SELECT * FROM %s WHERE id = #{id}
                    </select>

                    <insert id="insert">
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
                packageName, resourceName, resourceName, tableName,
                resourceName, tableName,
                resourceName, tableName,
                tableName, insertFields, insertValues,
                tableName, updateFields,
                tableName
        );

        writeToFile(packageDir + resourceName + "Mapper.xml", mapperXml);
    }
}
