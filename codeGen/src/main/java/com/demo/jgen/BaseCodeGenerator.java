package com.demo.jgen;

import io.swagger.v3.oas.models.media.Schema;
import java.io.IOException;

public interface BaseCodeGenerator {
    void generateCode(String packageName, String resourceName, String packageDir, Schema schema) throws IOException;
    
    public default void writeToFile(String filePath, String content) throws IOException {
        try (var writer = new java.io.FileWriter(filePath)) {
            writer.write(content);
        }
    }
}
