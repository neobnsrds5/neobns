package com.demo.customer;

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

@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService service;

    @Test
    public void testFindAll() throws Exception {
        // Arrange
        when(service.findAll(anyMap())).thenReturn(List.of(new CustomerDto()));

        // Act & Assert
        mockMvc.perform(get("/customer"))
               .andExpect(status().isOk());
    }

    @Test
    public void testFindByPage() throws Exception {
        // Arrange
        when(service.findByPage(anyMap(), eq(1), eq(10))).thenReturn(List.of(new CustomerDto()));

        // Act & Assert
        mockMvc.perform(get("/customer/page?page=1&size=10"))
               .andExpect(status().isOk());
    }

    @Test
    public void testFindById() throws Exception {
        // Arrange
        Long mockId = 1L;
        when(service.findById(mockId)).thenReturn(new CustomerDto());

        // Act & Assert
        mockMvc.perform(get("/customer/{id}", mockId))
               .andExpect(status().isOk());
    }

    @Test
    public void testCreate() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/customer")
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdate() throws Exception {
        // Arrange
        Long mockId = 1L;

        // Act & Assert
        mockMvc.perform(put("/customer/{id}", mockId)
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDelete() throws Exception {
        // Arrange
        Long mockId = 1L;

        // Act & Assert
        mockMvc.perform(delete("/customer/{id}", mockId))
               .andExpect(status().isOk());
    }
}
