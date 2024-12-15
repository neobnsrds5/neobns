package com.spider.demo.util;

/**
 * 암호화, 복호화 및 Base64 변환을 위한 유틸리티 클래스.
 */
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;


public class CryptoUtils {

    private static final String AES = "AES";

    /**
     * AES 키를 생성합니다.
     *
     * @return 생성된 AES SecretKey
     * @throws Exception 키 생성 실패 시
     */
    public static SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(AES);
        keyGen.init(128); // 128-bit AES
        return keyGen.generateKey();
    }

    /**
     * 문자열을 AES로 암호화합니다.
     *
     * @param plainText 암호화할 문자열
     * @param secretKey 암호화에 사용할 키
     * @return 암호화된 Base64 문자열
     * @throws Exception 암호화 실패 시
     */
    public static String encryptAES(String plainText, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * AES로 암호화된 문자열을 복호화합니다.
     *
     * @param encryptedText 복호화할 Base64 문자열
     * @param secretKey 복호화에 사용할 키
     * @return 복호화된 문자열
     * @throws Exception 복호화 실패 시
     */
    public static String decryptAES(String encryptedText, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }

    /**
     * 문자열을 Base64로 인코딩합니다.
     *
     * @param input 인코딩할 문자열
     * @return Base64로 인코딩된 문자열
     */
    public static String encodeBase64(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes());
    }

    /**
     * Base64로 인코딩된 문자열을 디코딩합니다.
     *
     * @param base64Encoded Base64로 인코딩된 문자열
     * @return 디코딩된 문자열
     */
    public static String decodeBase64(String base64Encoded) {
        byte[] decodedBytes = Base64.getDecoder().decode(base64Encoded);
        return new String(decodedBytes);
    }
}
