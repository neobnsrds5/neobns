package com.spider.demo.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

class ImageFileUtilsTest {

    @Test
    void testReadImage() throws IOException {
        System.out.println("[TEST] 이미지 읽기 기능 테스트");
        String filePath = "src/test/resources/img/test.png"; // 테스트용 파일 경로
        BufferedImage image = ImageFileUtils.readImage(filePath);
        assertNotNull(image);
    }

    @Test
    void testWriteImage() throws IOException {
        System.out.println("[TEST] 이미지 저장 기능 테스트");
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        String outputPath = "src/test/resources/img/output-test-image.png";
        ImageFileUtils.writeImage(image, "png", outputPath);

        File outputFile = new File(outputPath);
        assertTrue(outputFile.exists());

        // Cleanup
        outputFile.delete();
    }

    @Test
    void testResizeImage() {
        System.out.println("[TEST] 이미지 크기 조정 기능 테스트");
        BufferedImage originalImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        BufferedImage resizedImage = ImageFileUtils.resizeImage(originalImage, 100, 100);

        assertNotNull(resizedImage);
        assertEquals(100, resizedImage.getWidth());
        assertEquals(100, resizedImage.getHeight());
    }

    @Test
    void testChangeImageExtension() {
        System.out.println("[TEST] 이미지 확장자 변경 기능 테스트");
        String inputPath = "src/test/resources/img/jtest.jpg";
        String newExtension = "png";
        String updatedPath = ImageFileUtils.changeImageExtension(inputPath, newExtension);

        assertEquals("src/test/resources/img/jtest.png", updatedPath);
    }
    
    @Test
    void testCreateThumbnail() throws IOException {
        System.out.println("[TEST] 썸네일 생성 기능 테스트");
        
        File inputFile = new File("src/test/resources/img/test.png");
        File outputDir = new File("src/test/resources/img/thumbnail-test.png");
        System.out.println("입력 파일 존재 여부: " + inputFile.exists());
        System.out.println("출력 디렉터리 존재 여부: " + outputDir.exists());
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        
        String inputPath = "src/test/resources/img/test.png"; // 원본 이미지 경로
        String outputPath = "src/test/resources/img/thumbnail-test.png"; // 썸네일 저장 경로
        int width = 150;
        int height = 150;
        
        File thumbnailFile = new File(outputPath);
        
		if (thumbnailFile.exists()) {
			System.out.println("기존 썸네일 파일 삭제: " + thumbnailFile.delete());
		}else {
			System.out.println("기존 썸네일 파일 없음");
		}
        

        ImageFileUtils.createThumbnail(inputPath, outputPath, width, height, "png");

        BufferedImage thumbnail = ImageFileUtils.readImage(outputPath);
        

    }
    
    @Test
    void testEncodeImageToBase64() throws IOException {
        System.out.println("[TEST] 이미지 파일 Base64 인코딩 기능 테스트");
        String filePath = "src/test/resources/img/test.png";
        String base64Encoded = ImageFileUtils.encodeImageToBase64(filePath, "png");
        assertNotNull(base64Encoded, "Base64 인코딩 결과가 null입니다.");
        System.out.println("Base64 인코딩 결과: " + base64Encoded.substring(0, 50) + "...");
    }

    @Test
    void testDecodeBase64ToImage() throws IOException {
        System.out.println("[TEST] Base64 디코딩 및 원본 이미지 복원 기능 테스트");
        String filePath = "src/test/resources/img/test.png";
        String outputPath = "src/test/resources/img/decoded-image.png";

        // 이미지 파일을 Base64로 인코딩
        String base64Encoded = ImageFileUtils.encodeImageToBase64(filePath, "png");

        // Base64 문자열을 디코딩하여 파일로 저장
        ImageFileUtils.decodeBase64ToImage(base64Encoded, outputPath);

        // 디코딩된 파일 검증
        File decodedFile = new File(outputPath);
        assertTrue(decodedFile.exists(), "디코딩된 이미지 파일이 생성되지 않았습니다.");


    }
}
