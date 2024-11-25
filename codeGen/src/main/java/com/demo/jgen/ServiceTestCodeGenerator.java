package com.demo.jgen;

import io.swagger.v3.oas.models.media.Schema;
import java.io.IOException;

public class ServiceTestCodeGenerator implements BaseCodeGenerator {

    @Override
    public void generateCode(String packageName, String resourceName, String packageDir, Schema schema) throws IOException {
        String serviceTestCode = """
                package %s;

                import static org.mockito.Mockito.*;
                import static org.junit.jupiter.api.Assertions.*;

                import org.junit.jupiter.api.Test;
                import org.junit.jupiter.api.extension.ExtendWith;
                import org.mockito.InjectMocks;
                import org.mockito.Mock;
                import org.mockito.junit.jupiter.MockitoExtension;

                import java.util.List;
                import java.util.Map;

                @ExtendWith(MockitoExtension.class)
                public class %sServiceTest {

                    @InjectMocks
                    private %sService service;

                    @Mock
                    private %sMapper mapper;

                    @Test
                    public void testFindAll() {
                        // Arrange
                        List<%sDto> mockList = List.of(new %sDto());
                        when(mapper.findAll(anyMap())).thenReturn(mockList);

                        // Act
                        List<%sDto> result = service.findAll(Map.of());

                        // Assert
                        assertNotNull(result);
                        assertEquals(1, result.size());
                        verify(mapper, times(1)).findAll(anyMap());
                    }

                    @Test
                    public void testFindByPage() {
                        // Arrange
                        List<%sDto> mockList = List.of(new %sDto());
                        when(mapper.findByPage(anyMap(), eq(0), eq(10))).thenReturn(mockList);

                        // Act
                        List<%sDto> result = service.findByPage(Map.of(), 1, 10);

                        // Assert
                        assertNotNull(result);
                        assertEquals(1, result.size());
                        verify(mapper, times(1)).findByPage(anyMap(), eq(0), eq(10));
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
                """.formatted(packageName, resourceName, resourceName, resourceName, resourceName, resourceName, resourceName,
                              resourceName, resourceName, resourceName, resourceName, resourceName,
                              resourceName, resourceName, resourceName, resourceName, resourceName, resourceName);

        writeToFile(packageDir + resourceName + "ServiceTest.java", serviceTestCode);
    }
}
