package com.demo.jgen;

import io.swagger.v3.oas.models.media.Schema;

import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * 서비스 테스트 코드 생성기
 */
public class ServiceBootTestCodeGenerator implements BaseCodeGenerator {
    private final AtomicInteger counter = new AtomicInteger(1); // 순서를 위한 카운터
    private final Random random = new Random();

    @Override
    public void generateCode(String packageName, String resourceName, String packageDir, Schema<?> schema) throws IOException {
        // Extracting schema properties (fields)
    	// yml 파일에서 components > schemas > properties에 정의된 속성의 이름과 타입 정보를 Map 형태로 반환
        Map<String, Schema> properties = schema.getProperties();

        // Generate `setUp` code for two DTOs
        StringBuilder setUpCode = new StringBuilder();
        StringBuilder testCreateSetupCode = new StringBuilder();
        StringBuilder testUpdateSetupCode = new StringBuilder();
        setUpCode.append("// 생성된 첫 번째 DTO\n");
        setUpCode.append("        %sDto dto1 = new %sDto();\n".formatted(resourceName, resourceName));
        for (Map.Entry<String, Schema> entry : properties.entrySet()) {
            String fieldName = entry.getKey(); // 속성의 이름
            Schema fieldSchema = entry.getValue(); // 속성의 스키마 정보
            String fieldType = OpenApiCodeGenerator.mapSchemaTypeToJavaType(fieldSchema.getType(), fieldSchema.getFormat()); // 속성의 데이터타입
            setUpCode.append(generateFieldInitializationCode(fieldName, fieldType).replace("dto", "dto1")); // 데이터 세팅
            testCreateSetupCode.append(generateFieldInitializationCode(fieldName, fieldType)); // testCreate 테스트 코드
            testUpdateSetupCode.append(generateFieldInitializationCodeForUpdate(fieldName, fieldType)); // testUpdate 테스트 코드
        }

        setUpCode.append("\n        // 생성된 두 번째 DTO\n");
        setUpCode.append("        %sDto dto2 = new %sDto();\n".formatted(resourceName, resourceName));
        for (Map.Entry<String, Schema> entry : properties.entrySet()) {
            String fieldName = entry.getKey(); // 속성의 이름
            Schema fieldSchema = entry.getValue(); // 속성의 스키마 정보
            String fieldType = OpenApiCodeGenerator.mapSchemaTypeToJavaType(fieldSchema.getType(), fieldSchema.getFormat()); // 속성의 데이터타입
            setUpCode.append(generateFieldInitializationCode(fieldName, fieldType).replace("dto", "dto2")); // 데이터 세팅
        }

        setUpCode.append("\n        mapper.insert(dto1);\n");
        setUpCode.append("        mapper.insert(dto2);\n");
        setUpCode.append("        dto1Id = dto1.getId();\n");
        setUpCode.append("        dto2Id = dto2.getId();\n");

        // Generate the full test class code
        String serviceBootTestCode = """
				package %s;
				
				import static org.assertj.core.api.Assertions.assertThat;
				
				import org.junit.jupiter.api.BeforeEach;
				import org.junit.jupiter.api.Test;
				import org.springframework.beans.factory.annotation.Autowired;
				import org.springframework.boot.test.context.SpringBootTest;
				import org.springframework.transaction.annotation.Transactional;
				
				import java.util.List;
				
				@SpringBootTest
				@Transactional
				//@ActiveProfiles("test")   //application-test.yml로 테스트를 진행할 경우
				public class %sServiceBootTest {
				                
				    private Long dto1Id;
				    private Long dto2Id;
				     
				    @Autowired
				    private %sService service;
				    
				    @Autowired
				    private %sMapper mapper;
				    
				    @BeforeEach
				    public void setUp() {
				        %s
				    }
				    
				    @Test
				    public void testFindAll() {
				        // Act
				        List<%sDto> result = service.findAll();
				    
				        // Assert
				        assertThat(result).isNotNull();
				    }
				    
				    @Test
				    public void testFindByPage() {
				        // Arrange
				        int page = 1;
				        int size = 10;
				    
				        // Act
				        List<%sDto> result = service.findByPage(page, size);
				    
				        // Assert
				        assertThat(result).isNotNull();
				    }
				    
				    @Test
				    public void testFindById() {
				        // Arrange
				        Long mockId = dto1Id;
				    
				        // Act
				        %sDto result = service.findById(mockId);
				    
				        // Assert
				        assertThat(result).isNotNull();
				        assertThat(mockId).isEqualTo(result.getId());
				    }
				    
				    @Test
				    public void testCreate() {
				        // Arrange
				        %sDto dto = new %sDto();
				%s
				        // Act
				        service.create(dto);
				    
				        // Assert
				        assertThat(service.findById(dto.getId())).isNotNull();
				    }
				    
				    @Test
				    public void testUpdate() {
				        // Arrange
				        Long mockId = dto1Id;
				        %sDto dto = service.findById(mockId);
				%s
				        // Act
				        service.update(mockId, dto);
				    
				        // Assert
				        %sDto updated = service.findById(mockId);
				        assertThat(updated).isNotNull();
				        //assertThat(dto.getName()).isEqualTo(updated.getName());   //예시. 바뀐 값 확인하기
				    }
				    
				    @Test
				    public void testDelete() {
				        // Arrange
				        Long mockId = dto1Id;
				    
				        // Act
				        service.delete(mockId);
				    
				        // Assert
				        assertThat(service.findById(mockId)).isNull();
				    }
				}
	            """.formatted(
	                packageName, // 패키지명
	                resourceName, // 클래스명
	                resourceName, resourceName, // Service, Mapper
	                setUpCode.toString(), //@BeforeEach setUp()
	                resourceName, // testFindAll()
	                resourceName, // testFindByPage()
	                resourceName, // testFindById()
	                resourceName, resourceName, testCreateSetupCode.toString(), // testCreate()
	                resourceName, testUpdateSetupCode.toString(), resourceName); // testUpdate()

        // Write the generated code to a file
        writeToFile(packageDir + resourceName + "ServiceBootTest.java", serviceBootTestCode);
    }

    /*
     * dto에 setter를 이용하여 데이터 초기화
     */
    private String generateFieldInitializationCode(String fieldName, String fieldType) {
        int uniqueId = counter.getAndIncrement();

        // Handle the 'id' field separately to always set it as Long
        if ("id".equals(fieldName)) {
            return String.format("        dto.set%s(%dL);\n", capitalize(fieldName), random.nextLong(10000)); // Long type for 'id'
        }

        // 타입에 맞춰서 데이터 설정
        switch (fieldType) {
            case "String":
                return String.format("        dto.set%s(\"%s_value_%d\");\n", capitalize(fieldName), fieldName, uniqueId);
            case "Integer":
                return String.format("        dto.set%s(%d);\n", capitalize(fieldName), random.nextInt(1000));
            case "Long":
                return String.format("        dto.set%s(%dL);\n", capitalize(fieldName), random.nextLong(10000));
            case "Double":
                return String.format("        dto.set%s(%.2f);\n", capitalize(fieldName), random.nextDouble() * 100);
            case "Boolean":
                return String.format("        dto.set%s(%s);\n", capitalize(fieldName), uniqueId % 2 == 0 ? "true" : "false");
            case "List":
                return String.format("        dto.set%s(List.of(\"item_%d\", \"item_%d\"));\n", capitalize(fieldName), uniqueId, uniqueId + 1);
            case "Map":
                return String.format("        dto.set%s(Map.of(\"key_%d\", \"value_%d\", \"key_%d\", \"value_%d\"));\n",
                        capitalize(fieldName), uniqueId, uniqueId, uniqueId + 1, uniqueId + 1);
            default:
                return String.format("        dto.set%s(\"default_value_%d\");\n", capitalize(fieldName), uniqueId);
        }
    }

    // Modified to avoid setting the 'id' field in the update test (update test code에선 'id' 필드를 설정하지 않도록 수정)
    private String generateFieldInitializationCodeForUpdate(String fieldName, String fieldType) {
        int uniqueId = counter.getAndIncrement();

        // Skip setting the 'id' field for update test
        if ("id".equals(fieldName)) {
            return "";  // Do not include the id field for update
        }

        switch (fieldType) {
            case "String":
                return String.format("        dto.set%s(\"%s_value_%d\");\n", capitalize(fieldName), fieldName, uniqueId);
            case "Integer":
                return String.format("        dto.set%s(%d);\n", capitalize(fieldName), random.nextInt(1000));
            case "Long":
                return String.format("        dto.set%s(%dL);\n", capitalize(fieldName), random.nextLong(10000));
            case "Double":
                return String.format("        dto.set%s(%.2f);\n", capitalize(fieldName), random.nextDouble() * 100);
            case "Boolean":
                return String.format("        dto.set%s(%s);\n", capitalize(fieldName), uniqueId % 2 == 0 ? "true" : "false");
            case "List":
                return String.format("        dto.set%s(List.of(\"item_%d\", \"item_%d\"));\n", capitalize(fieldName), uniqueId, uniqueId + 1);
            case "Map":
                return String.format("        dto.set%s(Map.of(\"key_%d\", \"value_%d\", \"key_%d\", \"value_%d\"));\n",
                        capitalize(fieldName), uniqueId, uniqueId, uniqueId + 1, uniqueId + 1);
            default:
                return String.format("        dto.set%s(\"default_value_%d\");\n", capitalize(fieldName), uniqueId);
        }
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);

    }
}


