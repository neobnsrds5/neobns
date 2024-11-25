package com.demo.account;

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

@WebMvcTest(AccountController.class)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService service;

    @Test
    public void testFindAll() throws Exception {
        // Arrange
        when(service.findAll(anyMap())).thenReturn(List.of(new AccountDto()));

        // Act & Assert
        mockMvc.perform(get("/account"))
               .andExpect(status().isOk());
    }

    @Test
    public void testFindByPage() throws Exception {
        // Arrange
        when(service.findByPage(anyMap(), eq(1), eq(10))).thenReturn(List.of(new AccountDto()));

        // Act & Assert
        mockMvc.perform(get("/account/page?page=1&size=10"))
               .andExpect(status().isOk());
    }

    @Test
    public void testFindById() throws Exception {
        // Arrange
        Long mockId = 1L;
        when(service.findById(mockId)).thenReturn(new AccountDto());

        // Act & Assert
        mockMvc.perform(get("/account/{id}", mockId))
               .andExpect(status().isOk());
    }

    @Test
    public void testCreate() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/account")
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdate() throws Exception {
        // Arrange
        Long mockId = 1L;

        // Act & Assert
        mockMvc.perform(put("/account/{id}", mockId)
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDelete() throws Exception {
        // Arrange
        Long mockId = 1L;

        // Act & Assert
        mockMvc.perform(delete("/account/{id}", mockId))
               .andExpect(status().isOk());
    }
}
