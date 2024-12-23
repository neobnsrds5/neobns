package com.demo.jgen;

import io.swagger.v3.oas.models.media.Schema;
import java.io.IOException;

/*
 * 인터페이스, 각 CodeGenerator class들은 BasecodeGenerator를 구현
 */
public interface BaseCodeGenerator {
	
    void generateCode(String packageName, String resourceName, String packageDir, Schema<?> schema) throws IOException;
    
    public default void writeToFile(String filePath, String content) throws IOException {
        try (var writer = new java.io.FileWriter(filePath)) {
            writer.write(content);
        }
    }
}
