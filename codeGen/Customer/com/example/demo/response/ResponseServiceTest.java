package com.example.demo.response;

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
public class ResponseServiceTest {

    @InjectMocks
    private ResponseService service;

    @Mock
    private ResponseMapper mapper;

    @Test
    public void testFindAll() {
        // Arrange
        List<ResponseDto> mockList = List.of(new ResponseDto());
        when(mapper.findAll()).thenReturn(mockList);

        // Act
        List<ResponseDto> result = service.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(mapper, times(1)).findAll();
    }

    @Test
    public void testFindByPage() {
        // Arrange
        List<ResponseDto> mockList = List.of(new ResponseDto());
        when(mapper.findByPage(anyMap(), eq(0), eq(10))).thenReturn(mockList);

        // Act
        List<ResponseDto> result = service.findByPage(Map.of(), 1, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(mapper, times(1)).findByPage(anyMap(), eq(0), eq(10));
    }

    @Test
    public void testFindById() {
        // Arrange
        Long mockId = 1L;
        ResponseDto mockDto = new ResponseDto();
        when(mapper.findById(mockId)).thenReturn(mockDto);

        // Act
        ResponseDto result = service.findById(mockId);

        // Assert
        assertNotNull(result);
        assertEquals(mockDto, result);
        verify(mapper, times(1)).findById(mockId);
    }

    @Test
    public void testCreate() {
        // Arrange
        ResponseDto dto = new ResponseDto();

        // Act
        service.create(dto);

        // Assert
        verify(mapper, times(1)).insert(dto);
    }

    @Test
    public void testUpdate() {
        // Arrange
        Long mockId = 1L;
        ResponseDto dto = new ResponseDto();

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
