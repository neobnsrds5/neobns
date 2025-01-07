package com.demo.jgen;

import io.swagger.v3.oas.models.media.Schema;
import java.io.IOException;

/*
 * 서비스 Mock 테스트 코드 생성기, Mapper와의 상호작용만 검증
 */
public class ServiceTestCodeGenerator implements BaseCodeGenerator {

    @Override
    public void generateCode(String packageName, String resourceName, String packageDir, Schema<?> schema) throws IOException {
        String serviceTestCode = """
	            package %s;
	
	            import static org.mockito.Mockito.*;
	            import static org.junit.jupiter.api.Assertions.*;
	
	            import org.junit.jupiter.api.Test;
	            import org.junit.jupiter.api.extension.ExtendWith;
	            import org.mockito.InjectMocks;
	            import org.mockito.Mock;
	            import org.mockito.junit.jupiter.MockitoExtension;
	                
	            import org.springframework.boot.test.context.SpringBootTest;
	            import org.springframework.test.context.ActiveProfiles;
	
	            import java.util.List;
	            import java.util.Map;
	        		
	            @SpringBootTest
	            //@ActiveProfiles("test")   //application-test.yml로 테스트를 진행할 경우
	            public class %sServiceTest {
	
	                @InjectMocks
	                private %sService service;
	
	                @Mock
	                private %sMapper mapper;
	
	                @Test
	                public void testFindAll() {
	                    // Arrange
	                    List<%sDto> mockList = List.of(new %sDto());
	                    when(mapper.findAll()).thenReturn(mockList);
	
	                    // Act
	                    List<%sDto> result = service.findAll();
	
	                    // Assert
	                    assertNotNull(result);
	                    assertEquals(1, result.size());
	                    verify(mapper, times(1)).findAll();
	                }
	
	                @Test
	                public void testFindByPage() {
	                    int page = 1;
	                    int size = 10;
	                        
	                    List<%sDto> entities = mapper.findByPage(page, size);
	                        
	                    assertNotNull(entities);
	                    assertTrue(entities.size() <= size);
	                }
	
	                @Test
	                public void testFindById() {
	                    // Arrange
	                    Long mockId = 1L;
	                    %sDto mockDto = new %sDto();
	                    when(mapper.findById(mockId)).thenReturn(mockDto);
	
	                    // Act
	                    %sDto result = service.findById(mockId);
	
	                    // Assert
	                    assertNotNull(result);
	                    assertEquals(mockDto, result);
	                    verify(mapper, times(1)).findById(mockId);
	                }
	
	                @Test
	                public void testCreate() {
	                    // Arrange
	                    %sDto dto = new %sDto();
	
	                    // Act
	                    service.create(dto);
	
	                    // Assert
	                    verify(mapper, times(1)).insert(dto);
	                }
	
	                @Test
	                public void testUpdate() {
	                    // Arrange
	                    Long mockId = 1L;
	                    %sDto dto = new %sDto();
	
	                    // Act
	                    service.update(mockId, dto);
	
	                    // Assert
	                    verify(mapper, times(1)).update(dto);
	                }
	
	                @Test
	                public void testDelete() {
	                    // Arrange
	                    Long mockId = 1L;
	
	                    // Act
	                    service.delete(mockId);
	
	                    // Assert
	                    verify(mapper, times(1)).deleteById(mockId);
	                }
	            }
	            """.formatted(
	        		packageName, // 패키지명
	        		resourceName, // 클래스명
	        		resourceName, // Service
	        		resourceName, // Mapper
	        		resourceName, resourceName, resourceName, // testFindAll()
	                resourceName, // testFindByPage()
	                resourceName, resourceName, resourceName, // testFindById()
	                resourceName, resourceName, // testCreate()
	                resourceName, resourceName); // testUpdate()

        writeToFile(packageDir + resourceName + "ServiceTest.java", serviceTestCode);
    }
}
