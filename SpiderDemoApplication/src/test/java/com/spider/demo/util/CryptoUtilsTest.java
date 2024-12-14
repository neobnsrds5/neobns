package com.spider.demo.util;

import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.*;

class CryptoUtilsTest {

    @Test
    void testAESKeyGeneration() throws Exception {
        System.out.println("[TEST] AES 키 생성 기능 테스트");
        SecretKey key = CryptoUtils.generateAESKey();
        assertNotNull(key);
        assertEquals("AES", key.getAlgorithm());
    }

    @Test
    void testAESEncryptionDecryption() throws Exception {
        System.out.println("[TEST] AES 암호화 및 복호화 기능 테스트");
        SecretKey key = CryptoUtils.generateAESKey();
        String plainText = "Hello World!";

        String encryptedText = CryptoUtils.encryptAES(plainText, key);
        assertNotNull(encryptedText);
        System.out.println("암호화된 텍스트: " + encryptedText);

        String decryptedText = CryptoUtils.decryptAES(encryptedText, key);
        System.out.println("복호화된 텍스트: " + decryptedText);
        assertEquals(plainText, decryptedText);
    }

    @Test
    void testBase64EncodingDecoding() {
        System.out.println("[TEST] Base64 인코딩 및 디코딩 기능 테스트");
        String plainText = "Hello Base64!";

        String encodedText = CryptoUtils.encodeBase64(plainText);
        assertNotNull(encodedText);
        System.out.println("Base64 인코딩된 텍스트: " + encodedText);

        String decodedText = CryptoUtils.decodeBase64(encodedText);
        assertEquals(plainText, decodedText);
    }

    @Test
    void testInvalidBase64Decoding() {
        System.out.println("[TEST] 잘못된 Base64 디코딩 처리 테스트");
        String invalidBase64 = "Invalid Base64%%%";
        assertThrows(IllegalArgumentException.class, () -> CryptoUtils.decodeBase64(invalidBase64));
    }
}
