package com.spider.demo.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class RecaptchaService {
	
	private static final Logger logger = LoggerFactory.getLogger(RecaptchaService.class);
	
    private final RestTemplate restTemplate;
    private final String secretKey;
    private final String verifyUrl;

    public RecaptchaService(
            RestTemplate restTemplate,
            @Value("${google.recaptcha.key.secret}") String secretKey,
            @Value("${google.recaptcha.verify.url}") String verifyUrl) {
        this.restTemplate = restTemplate;
        this.secretKey = secretKey;
        this.verifyUrl = verifyUrl;
    }

    public boolean verifyRecaptcha(String recaptchaResponse) {
        if (recaptchaResponse == null || recaptchaResponse.isEmpty()) {
            return false;
        }

        try {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("secret", secretKey);
            params.add("response", recaptchaResponse);
            

            ResponseEntity<RecaptchaResponse> response = restTemplate.postForEntity(
                    verifyUrl,
                    params,
                    RecaptchaResponse.class
            );
            

            return response.getBody() != null && response.getBody().isSuccess();
        } catch (RestClientException e) {
            return false;
        }
    }
}