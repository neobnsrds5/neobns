package com.example.demo.item;

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

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService service;

    @Test
    public void testFindAll() throws Exception {
        // Arrange
        when(service.findAll()).thenReturn(List.of(new ItemDto()));

        // Act & Assert
        mockMvc.perform(get("/item"))
               .andExpect(status().isOk());
    }

    @Test
    public void testFindByPage() throws Exception {
        // Arrange
        when(service.findByPage(anyMap(), eq(1), eq(10))).thenReturn(List.of(new ItemDto()));

        // Act & Assert
        mockMvc.perform(get("/item/page?page=1&size=10"))
               .andExpect(status().isOk());
    }

    @Test
    public void testFindById() throws Exception {
        // Arrange
        Long mockId = 1L;
        when(service.findById(mockId)).thenReturn(new ItemDto());

        // Act & Assert
        mockMvc.perform(get("/item/{id}", mockId))
               .andExpect(status().isOk());
    }

    @Test
    public void testCreate() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/item")
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdate() throws Exception {
        // Arrange
        Long mockId = 1L;

        // Act & Assert
        mockMvc.perform(put("/item/{id}", mockId)
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDelete() throws Exception {
        // Arrange
        Long mockId = 1L;

        // Act & Assert
        mockMvc.perform(delete("/item/{id}", mockId))
               .andExpect(status().isOk());
    }
}
