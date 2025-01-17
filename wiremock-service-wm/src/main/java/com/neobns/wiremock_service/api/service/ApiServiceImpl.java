package com.neobns.wiremock_service.api.service;

import java.io.IOException;
import java.net.http.HttpHeaders;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.apache.hc.core5.http.HttpEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
		int statusCode = 0;	//0: 정상, 1: 장애, 2: 다운, 3: 지연
		
		try {
			ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
			saveStubToWireMock(apiUrl, response);
			// 응답 상태 코드 확인
			if(response.getStatusCode() == HttpStatus.OK) {
				statusCode = validateJson(response);
			} else { 
				statusCode = 1;
			}
			
		} catch(Exception e) {
			statusCode = handleException(apiUrl, e);
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
	            response.getHeaders().getContentType().toString().contains("application/json")) {
	            return 0; // 정상
	        }

	        // Content-Type이 없거나 JSON이 아니더라도 Body를 JSON으로 파싱 시도
	        ObjectMapper objectMapper = new ObjectMapper();
	        objectMapper.readTree(response.getBody());
	        return 0; // 정상
	    } catch (Exception e) {
	        return 1; // JSON 파싱 오류
	    }
	}
	
	private boolean isStubExists(String apiUrl) {
	    String wireMockAdminUrl = "http://localhost:8080/__admin/mappings";

	    try {
	        // WireMock Admin API 호출
	        ResponseEntity<String> response = restTemplate.getForEntity(wireMockAdminUrl, String.class);
	        ObjectMapper objectMapper = new ObjectMapper();
	        JsonNode mappings = objectMapper.readTree(response.getBody()).get("mappings");

	        // Stub 목록에서 URL 매칭 확인
	        for (JsonNode mapping : mappings) {
	            String stubUrl = mapping.get("request").get("url").asText();
	            if (stubUrl.equals(apiUrl)) {
	                return true; // Stub이 이미 존재
	            }
	        }
	    } catch (Exception e) {
	        throw new RuntimeException("Failed to check stub existence: " + e.getMessage());
	    }

	    return false; // Stub이 존재하지 않음
	}
	
	private String saveBodyToFile(String apiUrl, String bodyContent) {
	    String sanitizedUrl = apiUrl.replaceAll("[^a-zA-Z0-9]", "_");
	    String fileName = sanitizedUrl + ".json";

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
	
	private void saveStubToWireMock(String apiUrl, ResponseEntity<String> response) {
	    // Stub이 이미 존재하면 저장하지 않음
	    if (isStubExists(apiUrl)) {
	        System.out.println("Stub already exists for URL: " + apiUrl);
	        return;
	    }

	    String bodyFileName = saveBodyToFile(apiUrl, response.getBody());

	    String stubJson = generateStubJson(
	        apiUrl,
	        response.getStatusCodeValue(),
	        response.getHeaders().getContentType() != null
	            ? response.getHeaders().getContentType().toString()
	            : "application/json",
	        bodyFileName
	    );

	    String wireMockAdminUrl = "http://localhost:8080/__admin/mappings";
	    HttpHeaders headers = new HttpHeaders();
	    headers.set("Content-Type", "application/json");
	    HttpEntity<String> requestEntity = new HttpEntity<>(stubJson, headers);
	    
	    restTemplate.postForEntity(wireMockAdminUrl, requestEntity, String.class);
	}
	
	private String generateStubJson(String apiUrl, int statusCode, String contentType, String bodyFileName) {
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
	
	@Override
	public void deleteApi(int id) {
		ApiVO apiVO = apiDao.findById(id);
		if(apiVO == null) {
			throw new IllegalArgumentException("삭제할 API가 존재하지 않습니다.");
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
