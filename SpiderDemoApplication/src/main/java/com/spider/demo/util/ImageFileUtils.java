package com.spider.demo.util;

/**
 * 이미지 파일 처리를 위한 유틸리티 클래스.
 */
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;

public class ImageFileUtils {

    /**
     * 이미지 파일을 읽어 BufferedImage로 반환합니다.
     *
     * @param filePath 읽을 이미지 파일 경로
     * @return BufferedImage 객체
     * @throws IOException 파일을 읽지 못할 경우
     */
    public static BufferedImage readImage(String filePath) throws IOException {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty.");
        }
        File file = new File(filePath);
        return ImageIO.read(file);
    }

    /**
     * BufferedImage를 지정된 파일 경로에 저장합니다.
     *
     * @param image 저장할 BufferedImage 객체
     * @param format 이미지 포맷 (예: "png", "jpg")
     * @param outputPath 저장할 파일 경로
     * @throws IOException 파일 저장 실패 시
     */
    public static void writeImage(BufferedImage image, String format, String outputPath) throws IOException {
        if (image == null) {
            throw new IllegalArgumentException("Image cannot be null.");
        }
        if (format == null || format.isEmpty()) {
            throw new IllegalArgumentException("Format cannot be null or empty.");
        }
        if (outputPath == null || outputPath.isEmpty()) {
            throw new IllegalArgumentException("Output path cannot be null or empty.");
        }
        File outputFile = new File(outputPath);
        System.out.println("출력 경로 확인: " + outputFile.getAbsolutePath());
        System.out.println("경로 쓰기 가능 여부: " + outputFile.getParentFile().canWrite());
        boolean result = ImageIO.write(image, format, outputFile);
		if (!result) {
			throw new IOException("Failed to save image to file.");
		}
    }

    /**
     * 이미지의 크기를 조정합니다.
     *
     * @param image 원본 BufferedImage 객체
     * @param width 변경할 너비
     * @param height 변경할 높이
     * @return 크기가 조정된 BufferedImage 객체
     */
    public static BufferedImage resizeImage(BufferedImage image, int width, int height) {
        if (image == null) {
            throw new IllegalArgumentException("Image cannot be null.");
        }
        BufferedImage resizedImage = new BufferedImage(width, height, image.getType());
        java.awt.Graphics2D g = resizedImage.createGraphics();
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

    /**
     * 이미지의 확장자를 변경합니다.
     *
     * @param inputPath 원본 파일 경로
     * @param newExtension 새 확장자 (예: "png", "jpg")
     * @return 확장자가 변경된 파일 경로
     */
    public static String changeImageExtension(String inputPath, String newExtension) {
        if (inputPath == null || inputPath.isEmpty()) {
            throw new IllegalArgumentException("Input path cannot be null or empty.");
        }
        if (newExtension == null || newExtension.isEmpty()) {
            throw new IllegalArgumentException("New extension cannot be null or empty.");
        }
        int dotIndex = inputPath.lastIndexOf('.');
        if (dotIndex == -1) {
            return inputPath + "." + newExtension;
        }
        return inputPath.substring(0, dotIndex + 1) + newExtension;
    }
    
    /**
     * 원본 이미지에서 지정된 크기의 썸네일 이미지를 생성합니다.
     *
     * @param inputPath 원본 이미지 경로
     * @param outputPath 썸네일 저장 경로
     * @param width 썸네일 너비
     * @param height 썸네일 높이
     * @param format 이미지 포맷 (예: "png", "jpg")
     * @throws IOException 파일 읽기 또는 저장 실패 시
     */
    public static void createThumbnail(String inputPath, String outputPath, int width, int height, String format) throws IOException {
        if (inputPath == null || inputPath.isEmpty()) {
            throw new IllegalArgumentException("Input path cannot be null or empty.");
        }
        if (outputPath == null || outputPath.isEmpty()) {
            throw new IllegalArgumentException("Output path cannot be null or empty.");
        }
        if (format == null || format.isEmpty()) {
            throw new IllegalArgumentException("Format cannot be null or empty.");
        }
        BufferedImage originalImage = readImage(inputPath);
        System.out.println("Original Image: " + originalImage.getWidth() + "x" + originalImage.getHeight());
        BufferedImage thumbnail = resizeImage(originalImage, width, height);
        System.out.println("Thumbnail Image: " + thumbnail.getWidth() + "x" + thumbnail.getHeight());
        writeImage(thumbnail, format, outputPath);
        System.out.println("Thumbnail saved to: " + outputPath);
    }
    
    /**
     * 이미지 파일을 Base64 문자열로 인코딩합니다.
     *
     * @param filePath 인코딩할 이미지 파일 경로
     * @param format 이미지 포맷 (예: "png", "jpg")
     * @return Base64로 인코딩된 문자열
     * @throws IOException 파일 읽기 실패 시
     */
    public static String encodeImageToBase64(String filePath, String format) throws IOException {
        BufferedImage image = readImage(filePath);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, format, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    /**
     * Base64 문자열을 디코딩하여 이미지 파일로 저장합니다.
     *
     * @param base64Encoded Base64로 인코딩된 문자열
     * @param outputPath 저장할 파일 경로
     * @throws IOException 파일 쓰기 실패 시
     */
    public static void decodeBase64ToImage(String base64Encoded, String outputPath) throws IOException {
        byte[] decodedBytes = Base64.getDecoder().decode(base64Encoded);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(decodedBytes);
        BufferedImage image = ImageIO.read(inputStream);
        writeImage(image, "png", outputPath);
    }

}
