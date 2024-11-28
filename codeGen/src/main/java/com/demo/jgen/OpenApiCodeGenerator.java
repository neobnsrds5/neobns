package com.demo.jgen;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.parser.OpenAPIV3Parser;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class OpenApiCodeGenerator {
	
	private static final String PACKAGEPRIFIX = "com.demo.";

    public static void main(String[] args) {
        try {
            String yamlFilePath = System.getProperty("yamlFilePath", "./src/main/resources/api-docs.yml");
            String outputDir = System.getProperty("outputDir", "./Customer/");
            
            File targetDir = new File(outputDir);
            System.out.println("Generating code to " + targetDir.getAbsolutePath());
			if (!targetDir.exists()) {
				targetDir.mkdirs();
			}

            OpenAPI openAPI = new OpenAPIV3Parser().read(new File(yamlFilePath).getAbsolutePath());
            generateCode(openAPI, outputDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void generateCode(OpenAPI openAPI, String outputDir) throws IOException {
        Map<String, Schema> schemas = openAPI.getComponents().getSchemas();

        for (String schemaName : schemas.keySet()) {
            String resourceName = capitalize(schemaName);
            String packageName = PACKAGEPRIFIX + ((!PACKAGEPRIFIX.endsWith("."))? ".":"") + resourceName.toLowerCase();
            String packageDir = outputDir + packageName.replace(".", "/") + "/";

            new File(packageDir).mkdirs();

            BaseCodeGenerator dtoGenerator = new DTOCodeGenerator();
            BaseCodeGenerator mapperGenerator = new MapperCodeGenerator();
            BaseCodeGenerator mapperXmlGenerator = new MapperXmlCodeGenerator();
            BaseCodeGenerator serviceGenerator = new ServiceCodeGenerator();
            BaseCodeGenerator controllerGenerator = new ControllerCodeGenerator();
            BaseCodeGenerator controllerTestGenerator = new ControllerTestCodeGenerator();
            BaseCodeGenerator serviceTestGenerator = new ServiceTestCodeGenerator();
            BaseCodeGenerator jmeterTestPlanGenerator = new JMeterTestPlanCodeGenerator();
            BaseCodeGenerator asciiDocGenerator = new AsciiDocCodeGenerator();

            dtoGenerator.generateCode(packageName, resourceName, packageDir, schemas.get(schemaName));
            mapperGenerator.generateCode(packageName, resourceName, packageDir, schemas.get(schemaName));
            mapperXmlGenerator.generateCode(packageName, resourceName, packageDir, schemas.get(schemaName));
            serviceGenerator.generateCode(packageName, resourceName, packageDir, schemas.get(schemaName));
            controllerGenerator.generateCode(packageName, resourceName, packageDir, schemas.get(schemaName));
            controllerTestGenerator.generateCode(packageName, resourceName, packageDir, schemas.get(schemaName));
            serviceTestGenerator.generateCode(packageName, resourceName, packageDir, schemas.get(schemaName));
            jmeterTestPlanGenerator.generateCode(packageName, resourceName, packageDir, schemas.get(schemaName));
            asciiDocGenerator.generateCode(packageName, resourceName, packageDir, schemas.get(schemaName));
            
//            AsciiDocCodeGenerator asciiDocGenerator = new AsciiDocCodeGenerator();
//            asciiDocGenerator.generateAsciiDoc(packageDir);  // Generate AsciiDoc in the resource-specific directory
        }
    }

    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String mapSchemaTypeToJavaType(String schemaType) {
        if (schemaType == null) {
            return "Object";
        }

        switch (schemaType) {
            case "string":
                return "String";
            case "integer":
                return "Integer";
            case "number":
                return "Double";
            case "boolean":
                return "Boolean";
            case "array":
                return "List";
            case "object":
                return "Map";
            default:
                return "Object";
        }
    }
}
