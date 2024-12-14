package com.spider.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.spider.demo.service.RecaptchaResponse;
import com.spider.demo.service.RecaptchaService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecaptchaServiceTest {

    private static final String SECRET_KEY = "test-secret-key";
    private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    @Mock
    private RestTemplate restTemplate;

    private RecaptchaService recaptchaService;

    @BeforeEach
    void setUp() {
        recaptchaService = new RecaptchaService(restTemplate, SECRET_KEY, VERIFY_URL);
    }

    @Test
    void verifyRecaptcha_WithValidResponse_ReturnsTrue() {
        // Arrange
        String recaptchaResponse = "valid-response";
        RecaptchaResponse mockResponseBody = new RecaptchaResponse();
        mockResponseBody.setSuccess(true);
        mockResponseBody.setScore(0.9f);
        mockResponseBody.setChallengeTs("2024-12-13T10:00:00Z");
        mockResponseBody.setHostname("testhost.com");
        ResponseEntity<RecaptchaResponse> mockResponse = ResponseEntity.ok(mockResponseBody);

        when(restTemplate.postForEntity(
                eq(VERIFY_URL),
                any(),
                eq(RecaptchaResponse.class)
        )).thenReturn(mockResponse);

        // Act
        boolean result = recaptchaService.verifyRecaptcha(recaptchaResponse);

        // Assert
        assertTrue(result, "Verification should succeed for valid response");
    }

    @Test
    void verifyRecaptcha_WithInvalidResponse_ReturnsFalse() {
        // Arrange
        String recaptchaResponse = "invalid-response";
        RecaptchaResponse mockResponseBody = new RecaptchaResponse();
        mockResponseBody.setSuccess(false);
        mockResponseBody.setScore(0.1f);
        mockResponseBody.setChallengeTs("2024-12-13T10:00:00Z");
        mockResponseBody.setHostname("testhost.com");
        ResponseEntity<RecaptchaResponse> mockResponse = ResponseEntity.ok(mockResponseBody);

        when(restTemplate.postForEntity(
                eq(VERIFY_URL),
                any(),
                eq(RecaptchaResponse.class)
        )).thenReturn(mockResponse);

        // Act
        boolean result = recaptchaService.verifyRecaptcha(recaptchaResponse);

        // Assert
        assertFalse(result, "Verification should fail for invalid response");
    }

    @Test
    void verifyRecaptcha_WithNullResponse_ReturnsFalse() {
        // Arrange
        String recaptchaResponse = "test-response";
        ResponseEntity<RecaptchaResponse> mockResponse = ResponseEntity.ok(null);

        when(restTemplate.postForEntity(
                eq(VERIFY_URL),
                any(),
                eq(RecaptchaResponse.class)
        )).thenReturn(mockResponse);

        // Act
        boolean result = recaptchaService.verifyRecaptcha(recaptchaResponse);

        // Assert
        assertFalse(result, "Verification should fail for null response");
    }

    @Test
    void verifyRecaptcha_WithEmptyResponse_ReturnsFalse() {
        // Arrange
        String recaptchaResponse = "";

        // Act
        boolean result = recaptchaService.verifyRecaptcha(recaptchaResponse);

        // Assert
        assertFalse(result, "Verification should fail for empty response");
    }

    @Test
    void verifyRecaptcha_WithNullRecaptchaResponse_ReturnsFalse() {
        // Arrange
        String recaptchaResponse = null;

        // Act
        boolean result = recaptchaService.verifyRecaptcha(recaptchaResponse);

        // Assert
        assertFalse(result, "Verification should fail for null recaptcha response");
    }

    @Test
    void verifyRecaptcha_WithRestClientException_ReturnsFalse() {
        // Arrange
        String recaptchaResponse = "test-response";
        when(restTemplate.postForEntity(
                eq(VERIFY_URL),
                any(),
                eq(RecaptchaResponse.class)
        )).thenThrow(new RestClientException("API Error"));

        // Act
        boolean result = recaptchaService.verifyRecaptcha(recaptchaResponse);

        // Assert
        assertFalse(result, "Verification should fail when API throws exception");
    }

    @Test
    void verifyRecaptcha_WithSuccessAndHighScore_ReturnsTrue() {
        // Arrange
        String recaptchaResponse = "valid-response";
        RecaptchaResponse mockResponseBody = new RecaptchaResponse();
        mockResponseBody.setSuccess(true);
        mockResponseBody.setScore(0.9f);
        mockResponseBody.setChallengeTs("2024-12-13T10:00:00Z");
        mockResponseBody.setHostname("testhost.com");
        ResponseEntity<RecaptchaResponse> mockResponse = ResponseEntity.ok(mockResponseBody);

        when(restTemplate.postForEntity(
                eq(VERIFY_URL),
                any(),
                eq(RecaptchaResponse.class)
        )).thenReturn(mockResponse);

        // Act
        boolean result = recaptchaService.verifyRecaptcha(recaptchaResponse);

        // Assert
        assertTrue(result, "Verification should succeed for valid response with high score");
    }

    @Test
    void verifyRecaptcha_WithSuccessAndLowScore_ReturnsTrue() {
        // Arrange
        String recaptchaResponse = "valid-response";
        RecaptchaResponse mockResponseBody = new RecaptchaResponse();
        mockResponseBody.setSuccess(true);
        mockResponseBody.setScore(0.1f);
        mockResponseBody.setChallengeTs("2024-12-13T10:00:00Z");
        mockResponseBody.setHostname("testhost.com");
        ResponseEntity<RecaptchaResponse> mockResponse = ResponseEntity.ok(mockResponseBody);

        when(restTemplate.postForEntity(
                eq(VERIFY_URL),
                any(),
                eq(RecaptchaResponse.class)
        )).thenReturn(mockResponse);

        // Act
        boolean result = recaptchaService.verifyRecaptcha(recaptchaResponse);

        // Assert
        assertTrue(result, "Verification should succeed for valid response regardless of score");
    }
}