package com.spider.demo.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeleniumConfig {

    @Bean
    public WebDriver webDriver() {
    	// 크롬 창 없이 실행
    	ChromeOptions chromeOptions = new ChromeOptions();
    	chromeOptions.addArguments("--headless");
    	chromeOptions.addArguments("--no-sandbox");
    	
        WebDriverManager.chromedriver().setup();
        return new ChromeDriver(chromeOptions);
    }
}
