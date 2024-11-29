package com.example.demo.transfer;

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

@WebMvcTest(TransferController.class)
public class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransferService service;

    @Test
    public void testFindAll() throws Exception {
        // Arrange
        when(service.findAll()).thenReturn(List.of(new TransferDto()));

        // Act & Assert
        mockMvc.perform(get("/transfer"))
               .andExpect(status().isOk());
    }

    @Test
    public void testFindByPage() throws Exception {
        // Arrange
        when(service.findByPage(anyMap(), eq(1), eq(10))).thenReturn(List.of(new TransferDto()));

        // Act & Assert
        mockMvc.perform(get("/transfer/page?page=1&size=10"))
               .andExpect(status().isOk());
    }

    @Test
    public void testFindById() throws Exception {
        // Arrange
        Long mockId = 1L;
        when(service.findById(mockId)).thenReturn(new TransferDto());

        // Act & Assert
        mockMvc.perform(get("/transfer/{id}", mockId))
               .andExpect(status().isOk());
    }

    @Test
    public void testCreate() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/transfer")
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdate() throws Exception {
        // Arrange
        Long mockId = 1L;

        // Act & Assert
        mockMvc.perform(put("/transfer/{id}", mockId)
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDelete() throws Exception {
        // Arrange
        Long mockId = 1L;

        // Act & Assert
        mockMvc.perform(delete("/transfer/{id}", mockId))
               .andExpect(status().isOk());
    }
}
