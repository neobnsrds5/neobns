package com.demo.jgen;

import io.swagger.v3.oas.models.media.Schema;
import java.io.IOException;

public class ControllerMockTestCodeGenerator implements BaseCodeGenerator {

    @Override
    public void generateCode(String packageName, String resourceName, String packageDir, Schema schema) throws IOException {
        String controllerTestCode = """
                package %s;

                import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
                import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
                import static org.mockito.Mockito.*;

                import org.junit.jupiter.api.Test;
                import org.springframework.beans.factory.annotation.Autowired;
                import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
                import org.springframework.boot.test.mock.mockito.MockBean;
                import org.springframework.test.web.servlet.MockMvc;

                import java.util.List;
                import java.util.Map;

                @WebMvcTest(%sController.class)
                public class %sControllerMockTest {

                    @Autowired
                    private MockMvc mockMvc;

                    @MockBean
                    private %sService service;
                    
                    @MockBean
                    private %sMapper mapper;

                    @Test
                    public void testFindAll() throws Exception {
                        // Arrange
                        when(service.findAll()).thenReturn(List.of(new %sDto()));

                        // Act & Assert
                        mockMvc.perform(get("/%s"))
                               .andExpect(status().isOk());
                    }

                    @Test
                    public void testFindByPage() throws Exception {
                        // Arrange
                        when(service.findByPage(eq(1), eq(10))).thenReturn(List.of(new %sDto()));

                        // Act & Assert
                        mockMvc.perform(get("/%s/page?page=1&size=10"))
                               .andExpect(status().isOk());
                    }

                    @Test
                    public void testFindById() throws Exception {
                        // Arrange
                        Long mockId = 1L;
                        when(service.findById(mockId)).thenReturn(new %sDto());

                        // Act & Assert
                        mockMvc.perform(get("/%s/{id}", mockId))
                               .andExpect(status().isOk());
                    }

                    @Test
                    public void testCreate() throws Exception {
                        // Act & Assert
                        mockMvc.perform(post("/%s")
                                .contentType("application/json")
                                .content("{}"))
                                .andExpect(status().isOk());
                    }

                    @Test
                    public void testUpdate() throws Exception {
                        // Arrange
                        Long mockId = 1L;

                        // Act & Assert
                        mockMvc.perform(put("/%s/{id}", mockId)
                                .contentType("application/json")
                                .content("{}"))
                                .andExpect(status().isOk());
                    }

                    @Test
                    public void testDelete() throws Exception {
                        // Arrange
                        Long mockId = 1L;

                        // Act & Assert
                        mockMvc.perform(delete("/%s/{id}", mockId))
                               .andExpect(status().isOk());
                    }
                }
                """.formatted(packageName, resourceName, resourceName, resourceName, resourceName, resourceName, resourceName.toLowerCase(),
                              resourceName, resourceName.toLowerCase(), resourceName, resourceName.toLowerCase(),
                              resourceName.toLowerCase(), resourceName.toLowerCase(), resourceName.toLowerCase());

        writeToFile(packageDir + resourceName + "ControllerMockTest.java", controllerTestCode);
    }
}
