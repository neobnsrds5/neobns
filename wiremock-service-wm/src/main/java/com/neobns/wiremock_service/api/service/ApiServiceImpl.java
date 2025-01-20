package com.neobns.wiremock_service.api.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neobns.wiremock_service.api.dao.ApiDao;
import com.neobns.wiremock_service.api.vo.ApiVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApiServiceImpl implements ApiService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final ApiDao apiDao;
	private final RestTemplate restTemplate;
	private final String WIREMOCK_ADMIN_URL = "http://localhost:56789/__admin/mappings"; // WIREMOCK_ADMIN_URL

	@Override
	public List<ApiVO> getAllApis() {
		return apiDao.findAll();
	}
	
	@Override
	public ApiVO getApi(int id) {
		return apiDao.findById(id);
	}
	
	@Override
	public List<ApiVO> getApis(List<Integer> ids) {
		return apiDao.findByIds(ids);
	}

	@Override
	public void saveNewApi(String apiName, String apiUrl) {
		ApiVO apiVO = new ApiVO();
		apiVO.setApiName(apiName);
		apiVO.setApiUrl(apiUrl);
		apiDao.saveApi(apiVO);
	}

	@Override
	public void updateCheckedApiInfo(int id, LocalDateTime checkedTime, Integer checkedStatus) {
		ApiVO apiVO = new ApiVO();
		apiVO.setId(id);
		apiVO.setLastCheckedTime(checkedTime);
		apiVO.setLastCheckedStatus(checkedStatus);
		apiDao.updateCheckedStatus(apiVO);
	}

	@Override
	public void toggleResponseStatusById(int id) {
		apiDao.toggleResponseStatusById(id);
	}

	@Override
	public void changeModeById(int id, boolean targetMode) {
		ApiVO apiVO = getApi(id);
		apiVO.setResponseStatus(targetMode);
		apiDao.changeResponseStatusById(apiVO);
	}
	
	@Override
	public void changeModeByIds(List<Integer> ids, boolean targetMode) {
		List<Integer> idsToUpdate = getApis(ids).stream()
				.filter(api -> api.getResponseStatus() != targetMode)
				.map(ApiVO::getId)
				.collect(Collectors.toList());
		if(idsToUpdate.isEmpty()) return;
		try {
	        apiDao.toggleResponseStatusByIds(idsToUpdate);
	    } catch (Exception e) {
	        logger.error("Unexpected error 발생: ", e);
	        return;
	    }
	}

	@Override
	public ApiVO performHealthCheck(int id) {
		ApiVO apiVO = apiDao.findById(id);
		if(apiVO == null) throw new IllegalArgumentException("해당 ID의 API가 존재하지 않습니다.");
		
		String apiUrl = apiVO.getApiUrl();
		String apiName = apiVO.getApiName();
		int statusCode = 0;	//0: 정상, 1: 장애, 2: 다운, 3: 지연
		
		try {
			ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
			saveStubToWireMock(apiName, apiUrl, response);
			// 응답 상태 코드 확인
			if(response.getStatusCode() == HttpStatus.OK) {
				statusCode = validateJson(response);
			} else { 
				statusCode = 1;
			}
			
		} catch(Exception e) {
			statusCode = handleException(apiUrl, e);
			saveFallbackStub(apiName, statusCode); // 비정상 응답에 대한 Stub 저장
			System.out.println("statusCode : " + statusCode);
		}
		
		updateCheckedApiInfo(id, LocalDateTime.now(), statusCode);
		
		if(statusCode != 0) changeModeById(id, false);//서버 문제 시, 자동 대응답 처리
		return apiDao.findById(id);
	}
	
	@Override
	public void checkAllApiHealthCheck() {
		List<ApiVO> apis = getAllApis();
		
		//고정된 스레드 풀 생성(최대 10개 스레드)
	    ExecutorService executorService = Executors.newFixedThreadPool(10);
        apis.forEach(api -> executorService.submit(() -> {
        	try {
        		performHealthCheck(api.getId());
        	} catch(Exception e) {
        		logger.error("HealthCheck 실패: ID = " + api.getId(), e);
        	}
        }));
        executorService.shutdown();
	}
	
	private int validateJson(ResponseEntity<String> response) {
		try {
	        if (response.getHeaders().getContentType() != null &&
	            response.getHeaders().getContentType().toString().toLowerCase().contains("application/json")) {
	            return 0; // 정상
	        }

	        // Content-Type이 없거나 JSON이 아니더라도 Body를 JSON으로 파싱 시도
	        ObjectMapper objectMapper = new ObjectMapper();
	        objectMapper.readTree(response.getBody());
	        return 0; // 정상
	    } catch (Exception e) {
	    	logger.error("validateJson : " + response.getBody(), e);
	        return 1; // JSON 파싱 오류
	    }
	}
	
	@Override
	public String isStubExists(String apiUrl) { // stub uuid 값 반환

	    try {
	        // WireMock Admin API 호출
	        ResponseEntity<String> response = restTemplate.getForEntity(WIREMOCK_ADMIN_URL, String.class);
	        ObjectMapper objectMapper = new ObjectMapper();
	        JsonNode mappings = objectMapper.readTree(response.getBody()).get("mappings");

	        if (mappings != null && mappings.isArray()) {
                for (JsonNode mapping : mappings) {
                    JsonNode requestNode  = mapping.get("request"); // Stub의 UUID 확인
                    if (requestNode  != null && requestNode.has("url")) {
                    	String stubUrl = requestNode.get("url").asText();
                        if (stubUrl.equals(apiUrl)) {
                            return mapping.get("id").asText(); // UUID 반환
                        }
                    }
                }
            }
	    } catch (Exception e) {
	    	logger.error("Failed to check stub existence", e);
	        throw new RuntimeException("Failed to check stub existence: " + e.getMessage());
	    }

	    return null; // Stub이 존재하지 않음
	}
	
	private String saveBodyToFile(String apiName, String bodyContent) {
	    String fileName = apiName + ".json";

	    Path filePath = Paths.get("src/main/resources/wiremock/__files", fileName);

	    // 파일이 이미 존재하면 재사용
	    if (Files.exists(filePath)) {
	        return fileName; // 기존 파일명을 반환
	    }

	    // 새 파일 생성
	    try {
	        Files.createDirectories(filePath.getParent());
	        Files.writeString(filePath, bodyContent);
	    } catch (IOException e) {
	        throw new RuntimeException("Failed to save body content to file: " + e.getMessage());
	    }

	    return fileName; // 새로 저장된 파일명 반환
	}
	
	private void saveStubToWireMock(String apiName, String apiUrl, ResponseEntity<String> response) {
	    // Stub이 이미 존재하면 저장하지 않음
	    if (isStubExists(apiName) != null) {
	        System.out.println("Stub already exists for APINAME: " + apiName);
	        return;
	    }

	    String bodyFileName = saveBodyToFile(apiName, response.getBody());

	    String stubJson = generateStubJson(
	    	apiName,
    		apiUrl,
	        response.getStatusCode().value(),
	        response.getHeaders().getContentType() != null ? response.getHeaders().getContentType().toString() : "application/octet-stream",
	        bodyFileName
	    );

	    HttpHeaders headers = new HttpHeaders();
	    headers.set("Content-Type", "application/json");
	    HttpEntity<String> requestEntity = new HttpEntity<>(stubJson, headers);
	    
	    restTemplate.postForEntity(WIREMOCK_ADMIN_URL, requestEntity, String.class);
	}
	
	private String generateStubJson(String apiName, String apiUrl, int statusCode, String contentType, String bodyFileName) {
	    return String.format("""
	        {
	            "request": {
	                "method": "GET",
	                "url": "%s"
	            },
	            "response": {
	                "status": %d,
	                "headers": {
	                    "Content-Type": "%s"
	                },
	                "bodyFileName": "%s"
	            }
	        }
	        """,
	        apiUrl, statusCode, contentType, bodyFileName
	    );
	}
	
	private void saveFallbackStub(String apiName, int statusCode) {
	    String stubUrl;
	    String fallbackResponse;
	    String encoderName = nameEncoder(apiName);

	    switch (statusCode) {
	        case 2: // 다운
	            stubUrl = "/mock/stub/" + encoderName + "/" + statusCode;
	            fallbackResponse = "{\"error\": \"Service is down.\"}";
	            break;
	        case 3: // 지연
	            stubUrl = "/mock/stub/" + encoderName + "/" + statusCode;
	            fallbackResponse = "{\"error\": \"Service is delayed.\"}";
	            break;
	        default: // 장애
	            stubUrl = "/mock/stub/" + encoderName + "/" + statusCode;
	            fallbackResponse = "{\"error\": \"An error occurred.\"}";
	            break;
	    }

	    // Stub JSON 생성
	    String stubJson = String.format("""
	        {
	            "request": {
	                "method": "GET",
	                "url": "%s"
	            },
	            "response": {
	                "status": 500,
	                "headers": {
	                    "Content-Type": "application/json"
	                },
	                "body": \"%s\"
	            }
	        }
	        """, stubUrl, escapeJsonString(fallbackResponse));

	    HttpHeaders headers = new HttpHeaders();
	    headers.set("Content-Type", "application/json");
	    HttpEntity<String> requestEntity = new HttpEntity<>(stubJson, headers);

	    // WireMock에 Stub 등록
	    restTemplate.postForEntity(WIREMOCK_ADMIN_URL, requestEntity, String.class);
	}
	
	private String escapeJsonString(String json) {
	    return json.replace("\"", "\\\"");
	}
	
	private String nameEncoder(String input) {
	    if (input == null || input.isEmpty()) {
	        throw new IllegalArgumentException("Input cannot be null or empty");
	    }

	    try {
	        return URLEncoder.encode(input, StandardCharsets.UTF_8.toString());
	    } catch (Exception e) {
	        throw new RuntimeException("Failed to encode URL", e);
	    }
	}
	
	@Override
	public ResponseEntity<String> getStubResponse(String stubUrl) {
		RestTemplate restTemplate = new RestTemplate();
	    return restTemplate.getForEntity(stubUrl, String.class);
	}
	
	@Override
	public void deleteApi(int id) {
		ApiVO apiVO = apiDao.findById(id);
		if(apiVO == null) {
			throw new IllegalArgumentException("삭제할 API가 존재하지 않습니다.");
		}
		
		String stubUUID = isStubExists(apiVO.getApiUrl());
		
		String fileName = apiVO.getApiName() + ".json";
		Path filePath = Paths.get("src/main/resources/wiremock/__files", fileName);
		
		if (Files.exists(filePath)) {
	        try {
				Files.delete(filePath);
				logger.info("Response file deleted successfully: {}", filePath);
			} catch (IOException e) {
				logger.error("Failed to delete file: filePath = {}", filePath, e);
			} 
	    } else {
	        logger.warn("Response file not found: {}", filePath);
	    }
		
		if (stubUUID != null) {
	        try {
	            String deleteUrl = WIREMOCK_ADMIN_URL + "/" + stubUUID; // WireMock Stub 삭제 URL
	            restTemplate.delete(deleteUrl);
	        } catch (Exception e) {
	            logger.error("Failed to delete Stub: UUID = {}", stubUUID, e);
	        }
	    } else {
	        logger.warn("No Stub found for API URL: {}", apiVO.getApiUrl());
	    }
		
		apiDao.deleteById(id);
	}
	
	//=== FUNCTION ===//
	private int handleException(String apiUrl, Exception e) {
		if(e.getCause() instanceof java.net.SocketTimeoutException) {
			logger.error("|!!! HEALTHCHECK INFO : FAILED !!!| ※ API Timeout 발생: " + apiUrl);
			return 3;
		} else if(e.getCause() instanceof java.net.ConnectException
				|| e.getCause() instanceof java.net.UnknownHostException) {
			logger.error("|!!! HEALTHCHECK INFO : FAILED !!!| ※ API 서버 다운 발생: " + apiUrl);
			return 2;
		} else {
			logger.error("|!!! HEALTHCHECK INFO : FAILED !!!| ※ API 서버 장애 발생: " + apiUrl);
			//logger.error("|!!! HEALTHCHECK INFO : FAILED !!!| ※ API 서버 장애 발생: " + apiUrl, e);//==> 상세 정보 확인 필요 시 전환
			return 1;
		}
	}

}
