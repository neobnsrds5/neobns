package com.demo.customer;

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
public class CustomerServiceTest {

    @InjectMocks
    private CustomerService service;

    @Mock
    private CustomerMapper mapper;

    @Test
    public void testFindAll() {
        // Arrange
        List<CustomerDto> mockList = List.of(new CustomerDto());
        when(mapper.findAll(anyMap())).thenReturn(mockList);

        // Act
        List<CustomerDto> result = service.findAll(Map.of());

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(mapper, times(1)).findAll(anyMap());
    }

    @Test
    public void testFindByPage() {
        // Arrange
        List<CustomerDto> mockList = List.of(new CustomerDto());
        when(mapper.findByPage(anyMap(), eq(0), eq(10))).thenReturn(mockList);

        // Act
        List<CustomerDto> result = service.findByPage(Map.of(), 1, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(mapper, times(1)).findByPage(anyMap(), eq(0), eq(10));
    }

    @Test
    public void testFindById() {
        // Arrange
        Long mockId = 1L;
        CustomerDto mockDto = new CustomerDto();
        when(mapper.findById(mockId)).thenReturn(mockDto);

        // Act
        CustomerDto result = service.findById(mockId);

        // Assert
        assertNotNull(result);
        assertEquals(mockDto, result);
        verify(mapper, times(1)).findById(mockId);
    }

    @Test
    public void testCreate() {
        // Arrange
        CustomerDto dto = new CustomerDto();

        // Act
        service.create(dto);

        // Assert
        verify(mapper, times(1)).insert(dto);
    }

    @Test
    public void testUpdate() {
        // Arrange
        Long mockId = 1L;
        CustomerDto dto = new CustomerDto();

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
