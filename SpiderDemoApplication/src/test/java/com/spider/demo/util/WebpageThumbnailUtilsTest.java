package com.spider.demo.util;

import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class WebpageThumbnailUtilsTest {

    @Test
    void testCaptureWebpageAsThumbnail() throws IOException {
        System.out.println("[TEST] 웹사이트 캡처 및 썸네일 생성 기능 테스트");
        String url = "https://www.ibk.co.kr";
        String outputPath = "src/test/resources/img/ibk-thumbnail.png";
        int width = 400;
        int height = 300;

        // 썸네일 생성
        WebpageThumbnailUtils.captureWebpageAsThumbnail(url, outputPath, width, height);

        // 생성된 썸네일 파일 확인
        File thumbnailFile = new File(outputPath);
        assertTrue(thumbnailFile.exists(), "썸네일 파일이 생성되지 않았습니다.");


    }
}
