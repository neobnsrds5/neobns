package com.spider.demo.util;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 웹사이트의 화면을 캡처하여 썸네일로 저장하는 유틸리티 클래스.
 */
public class WebpageThumbnailUtils {

    /**
     * 웹사이트를 캡처하여 썸네일 이미지를 저장합니다.
     *
     * @param url        캡처할 웹사이트 URL
     * @param outputPath 썸네일 저장 경로
     * @param width      썸네일 너비
     * @param height     썸네일 높이
     * @throws IOException 파일 저장 실패 시 예외 발생
     */
    public static void captureWebpageAsThumbnail(String url, String outputPath, int width, int height) throws IOException {
        // WebDriver 설정
    	WebDriverManager.chromedriver().driverVersion("131.0.6778.108").setup();
    	ChromeOptions options = new ChromeOptions();
    	options.addArguments("--headless");
    	options.addArguments("--disable-gpu");
    	options.addArguments("--remote-allow-origins=*");
    	WebDriver driver = new ChromeDriver(options);


        try {
            // 브라우저 크기 설정
            driver.manage().window().setSize(new Dimension(1920, 1080));

            // URL로 이동
            driver.get(url);

            // 스크린샷 캡처
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            BufferedImage fullImage = ImageIO.read(screenshot);

            // 썸네일 크기로 조정
            BufferedImage thumbnail = resizeImage(fullImage, width, height);

            // 썸네일 저장
            ImageIO.write(thumbnail, "png", new File(outputPath));
        } finally {
            // WebDriver 종료
            driver.quit();
        }
    }

    /**
     * 이미지 크기를 조정합니다.
     *
     * @param original 원본 이미지
     * @param width    조정할 너비
     * @param height   조정할 높이
     * @return 크기 조정된 이미지
     */
    private static BufferedImage resizeImage(BufferedImage original, int width, int height) {
        BufferedImage resized = new BufferedImage(width, height, original.getType());
        java.awt.Graphics2D g = resized.createGraphics();
        g.drawImage(original, 0, 0, width, height, null);
        g.dispose();
        return resized;
    }
}
