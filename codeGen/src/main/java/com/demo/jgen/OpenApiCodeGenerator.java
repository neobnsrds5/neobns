package com.demo.jgen;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.parser.OpenAPIV3Parser;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Slf4j
public class OpenApiCodeGenerator {
	
	private static final String PACKAGEPRIFIX = "com.example.demo"; // 패키지명

    public static void main(String[] args) {
        try {
            String yamlFilePath = System.getProperty("yamlFilePath", "./src/main/resources/banking-api.yml"); // swagger 형식으로 작성한 yml 파일
            String outputDir = System.getProperty("outputDir", "./generated/"); // 생성 파일들 저장할 폴더명
            
            File targetDir = new File(outputDir);
            System.out.println("Generating code to " + targetDir.getAbsolutePath());
			if (!targetDir.exists()) {
				targetDir.mkdirs();
			}

            OpenAPI openAPI = new OpenAPIV3Parser().read(new File(yamlFilePath).getAbsolutePath());

            // 데이터베이스 configuration 파일 생성
            String configPackageName = PACKAGEPRIFIX + ((PACKAGEPRIFIX.endsWith("."))? "":".") + "config";
            String packageDir = outputDir + configPackageName.replace(".", "/") + "/";
            new File(packageDir).mkdirs();
            BaseCodeGenerator dbConfigGenerator = new DbConfigGenerator();
            dbConfigGenerator.generateCode(configPackageName , "resourceName", packageDir, new Schema() );

            // 배치 스케줄러 파일 생성
            String schedulePackageName = PACKAGEPRIFIX + ((PACKAGEPRIFIX.endsWith("."))? "":".") + "schedule";
            String schedulePackageDir = outputDir + schedulePackageName.replace(".", "/") + "/";
            new File(schedulePackageDir).mkdirs();
            BaseCodeGenerator scheduleGenerator = new ScheduleGenerator();
            scheduleGenerator.generateCode(schedulePackageName , "resourceName", schedulePackageDir, new Schema() );

            generateCode(openAPI, outputDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void generateCode(OpenAPI openAPI, String outputDir) throws IOException {
        Map<String, Schema> schemas = openAPI.getComponents().getSchemas();

        for (String schemaName : schemas.keySet()) {
            String resourceName = capitalize(schemaName);
            String packageName = PACKAGEPRIFIX + ((PACKAGEPRIFIX.endsWith("."))? "":".") + resourceName.toLowerCase();
            String packageDir = outputDir + packageName.replace(".", "/") + "/";
            
            new File(packageDir).mkdirs();

            BaseCodeGenerator dtoGenerator = new DTOCodeGenerator();
            BaseCodeGenerator mapperGenerator = new MapperCodeGenerator();
            BaseCodeGenerator mapperXmlGenerator = new MapperXmlCodeGenerator();
            BaseCodeGenerator serviceGenerator = new ServiceCodeGenerator();
            BaseCodeGenerator controllerGenerator = new ControllerCodeGenerator();
            BaseCodeGenerator controllerTestGenerator = new ControllerTestCodeGenerator();
            BaseCodeGenerator controllerMockTestGenerator = new ControllerMockTestCodeGenerator();
            BaseCodeGenerator controllerBootTestGenerator = new ControllerBootTestCodeGenerator();
            BaseCodeGenerator serviceBootTestCodeGenerator = new ServiceBootTestCodeGenerator();
            BaseCodeGenerator serviceTestGenerator = new ServiceTestCodeGenerator();
            BaseCodeGenerator serviceMySQLCodeGenerator = new ServiceMySQLTestCodeGenerator();
            BaseCodeGenerator jmeterTestPlanGenerator = new JMeterTestPlanCodeGenerator();
            BaseCodeGenerator testDataSQLGenerator = new TestDataSQLGenerator();
            BaseCodeGenerator asciiDocGenerator = new AsciiDocCodeGenerator();

            BaseCodeGenerator dbToDbGenerator = new DbToDbGenerator();
            BaseCodeGenerator fileToDbGenerator = new FileToDbGenerator();

            dbToDbGenerator.generateCode(packageName, resourceName, packageDir, schemas.get(schemaName));
            fileToDbGenerator.generateCode(packageName, resourceName, packageDir, schemas.get(schemaName));

            dtoGenerator.generateCode(packageName, resourceName, packageDir, schemas.get(schemaName));
            mapperGenerator.generateCode(packageName, resourceName, packageDir, schemas.get(schemaName));
            mapperXmlGenerator.generateCode(packageName, resourceName, packageDir, schemas.get(schemaName));
            serviceGenerator.generateCode(packageName, resourceName, packageDir, schemas.get(schemaName));
            controllerGenerator.generateCode(packageName, resourceName, packageDir, schemas.get(schemaName));
            controllerTestGenerator.generateCode(packageName, resourceName, packageDir, schemas.get(schemaName));
            controllerMockTestGenerator.generateCode(packageName, resourceName, packageDir, schemas.get(schemaName));
            controllerBootTestGenerator.generateCode(packageName, resourceName, packageDir, schemas.get(schemaName));
            serviceBootTestCodeGenerator.generateCode(packageName, resourceName, packageDir, schemas.get(schemaName));
            serviceTestGenerator.generateCode(packageName, resourceName, packageDir, schemas.get(schemaName));
            serviceMySQLCodeGenerator.generateCode(packageName, resourceName, packageDir, schemas.get(schemaName));
            jmeterTestPlanGenerator.generateCode(packageName, resourceName, packageDir, schemas.get(schemaName));
            testDataSQLGenerator.generateCode(packageName, resourceName, packageDir, schemas.get(schemaName));
            asciiDocGenerator.generateCode(packageName, resourceName, packageDir, schemas.get(schemaName));
            
//            AsciiDocCodeGenerator asciiDocGenerator = new AsciiDocCodeGenerator();
//            asciiDocGenerator.generateAsciiDoc(packageDir);  // Generate AsciiDoc in the resource-specific directory
        }
    }

    /*
     * 첫번째 알파벳 대문자로 변경
     */
    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }


    /*
     * schemas의 type을 java 데이터 형식으로 변경
     */
    public static String mapSchemaTypeToJavaType(String schemaType, String schemaFormat) {
        if (schemaType == null) {
            return "Object";
        }

        switch (schemaType) {
            case "string":
                if ("date".equals(schemaFormat)) {
                    return "java.time.LocalDate";
                } else if ("date-time".equals(schemaFormat)) {
                    return "java.time.LocalDateTime";
                } else if ("uuid".equals(schemaFormat)) {
                    return "java.util.UUID";
                } else {
                    return "String";
                }

            case "integer":
                if ("int32".equals(schemaFormat)) {
                    return "Integer";
                } else if ("int64".equals(schemaFormat)) {
                    return "Long";
                } else {
                    return "Integer"; // Default to Integer for unspecified formats
                }

            case "number":
                if ("float".equals(schemaFormat)) {
                    return "Float";
                } else if ("double".equals(schemaFormat)) {
                    return "Double";
                } else {
                    return "Double"; // Default to Double for unspecified formats
                }

            case "boolean":
                return "Boolean";

            case "array":
                return "List"; // Could be extended to include generics if items are provided

            case "object":
                return "Map"; // Could be extended for specific object definitions

            default:
                return "Object"; // Fallback for unknown types
        }
    }

}
