package com.neobns.wiremock_service.api.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neobns.wiremock_service.api.dao.ApiDao;
import com.neobns.wiremock_service.api.util.JsonPathCheck;
import com.neobns.wiremock_service.api.util.JsonValidation;
import com.neobns.wiremock_service.api.vo.ApiVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApiServiceImpl implements ApiService {
	
	private final JsonValidation jsonValidation;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final ApiDao apiDao;
	private final RestTemplate restTemplate;
	private final Path filesDir = Paths.get("src", "main", "resources", "wiremock", "__files");
	private final Path mappingsDir = Paths.get("src", "main", "resources", "wiremock", "mappings");
	private static final String COMMON_DELAY_RESPONSE = "common-delay-response.json";
	private static final String COMMON_BAD_RESPONSE = "common-bad-response.json";
	
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
	public void saveNewApi(String apiName, String apiUrl, 
            String normalMappings, String normalFiles,
            String delayMappings, String delayFiles,
            String errorMappings, String errorFiles) {
		
		validateApiFiles(normalMappings, normalFiles);
	    validateApiFiles(delayMappings, delayFiles);
	    validateApiFiles(errorMappings, errorFiles);
	    
		try {
			
			// 1. DB에 API 정보 저장
			ApiVO apiVO = new ApiVO();
			apiVO.setApiName(apiName);
			apiVO.setApiUrl(apiUrl);
			apiDao.saveApi(apiVO);
			
			int generatedId = apiVO.getId();
			
		    // 상태별 파일 저장
	        saveApiFiles(mappingsDir, filesDir, apiName, "", normalMappings, normalFiles, generatedId);
	        
	        if(!delayMappings.contains(COMMON_DELAY_RESPONSE)) {
	        	saveApiFiles(mappingsDir, filesDir, apiName, "-delay", delayMappings, delayFiles, generatedId);
	        }
	        if(!errorMappings.contains(COMMON_BAD_RESPONSE)) {
	        	saveApiFiles(mappingsDir, filesDir, apiName, "-bad", errorMappings, errorFiles, generatedId);
	        }
	        
	
		} catch (Exception e) {
	        logger.error("Error while creating mappings or __files for API: " + apiName, e);
	        throw new RuntimeException("Failed to create mappings or __files for API: " + apiName, e);
	    }
		
	}
	
	@Override
	public int jsonCheck(String fileName, int id) {
		return JsonPathCheck.checkFileState(fileName, id);
	}
	
	private void saveApiFiles(Path mappingsDir, Path filesDir, String apiName, String suffix, 
            String mappings, String files, int id) throws IOException {
		
		String processedMappings = mappings;
		if (mappings.contains("{id}")) {
	        processedMappings = processedMappings.replace("{id}", String.valueOf(id));
	    }
	    processedMappings = processedMappings.replace("{apiName}", apiName);

	    
		// 파일 경로 설정
		Path mappingsPath = mappingsDir.resolve(id + "-" + apiName + suffix + "-mapping.json");
		Path filesPath = filesDir.resolve(id + "-" + apiName + suffix + "-response.json");
		
		// 파일 저장
		Files.writeString(mappingsPath, processedMappings, StandardCharsets.UTF_8);
		Files.writeString(filesPath, files, StandardCharsets.UTF_8);
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
		int statusCode = 0;	//0: 정상, 1-3: 비정상 
		
		try {
			ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
			//장애 판별 로직: Content-Type이 json인지 검사하여 판별(Content-type이 없거나 html이라도 json 결과값이 파싱되면 정상 처리)
			if(response.getStatusCode() == HttpStatus.OK) {
				if(response.getHeaders().getContentType() != null 
						&& response.getHeaders().getContentType().toString().contains("application/json")) {
					statusCode = 0;
				} else {
					ObjectMapper objectMapper = new ObjectMapper();
					try {
						objectMapper.readTree(response.getBody());
						statusCode = 0;
					} catch(Exception e) {
						statusCode = 1;
					}
				}
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
	
	@Override
	public void deleteApi(int id) {
		ApiVO apiVO = apiDao.findById(id);
		if(apiVO == null) {
			throw new IllegalArgumentException("삭제할 API가 존재하지 않습니다.");
		}
		deleteApiFiles(apiVO.getApiName(), id);
		apiDao.deleteById(id);
	}
	
	@Override
	public void updateApi(int id, String apiName, String apiUrl, 
	        String normalMappings, String normalFiles, 
	        String delayMappings, String delayFiles, 
	        String errorMappings, String errorFiles) {
		
		validateApiFiles(normalMappings, normalFiles);
	    validateApiFiles(delayMappings, delayFiles);
	    validateApiFiles(errorMappings, errorFiles);
	    
		try {
	        // 1. DB에서 기존 API 조회 및 수정
	        ApiVO apiVO = apiDao.findById(id);
	        if (apiVO == null) {
	            throw new IllegalArgumentException("수정할 API가 존재하지 않습니다.");
	        }
	        
	        String oldApiName = apiVO.getApiName();
	        
	        apiVO.setApiName(apiName);
	        apiVO.setApiUrl(apiUrl);
	        apiDao.updateApi(apiVO);

	        if(!oldApiName.equals(apiName)) {
	        	deleteApiFiles(oldApiName, apiVO.getId());
	        }
	        String normalCheck = id + "-" + oldApiName + "-mapping.json";
	        normalMappings = normalMappings.replace(normalCheck, apiName);
	        
	        String delayCheck = id + "-" + oldApiName + "-mapping.json";
	        delayMappings = delayMappings.replace(delayCheck, apiName);
	        
	        String badCheck = id + "-" + oldApiName + "-mapping.json";
	        errorMappings = errorMappings.replace(badCheck, apiName);
	        
	        System.out.println("delayMappings : " + delayMappings);
	        System.out.println("errorMappings : " + errorMappings);

	        // 상태별 파일 저장
	        saveApiFiles(mappingsDir, filesDir, apiName, "", normalMappings, normalFiles, id);
	        Path delayMapping = mappingsDir.resolve(id + "-"+ oldApiName + "-delay-mapping.json");
	        Path badMapping = mappingsDir.resolve(id + "-"+ oldApiName + "-bad-mapping.json");
	        
	        if(!Files.exists(delayMapping) || !delayMappings.contains(COMMON_DELAY_RESPONSE)) {
	        	saveApiFiles(mappingsDir, filesDir, apiName, "-delay", delayMappings, delayFiles, id);
	        }
	        if(!Files.exists(badMapping) || !errorMappings.contains(COMMON_BAD_RESPONSE)) {
	        	saveApiFiles(mappingsDir, filesDir, apiName, "-bad", errorMappings, errorFiles, id);
	        }       

	        logger.info("API '{}' updated successfully.", apiName);
	    } catch (Exception e) {
	        logger.error("Error while updating API: " + apiName, e);
	        throw new RuntimeException("Failed to update API: " + apiName, e);
	    }
	}
	
	// 파일 삭제
	private void deleteApiFiles(String apiName, int id) {
		try {
	        // 기존 파일 경로 설정

			Path mappingsPath = mappingsDir.resolve(id + "-" + apiName + "-mapping.json");
	        Path filesPath = filesDir.resolve(id + "-" + apiName + "-response.json");     

	        Path delayMappingPath = mappingsDir.resolve(id + "-"+ apiName + "-delay-mapping.json");
	        Path delayFilesPath = filesDir.resolve(id + "-"+ apiName + "-delay-response.json");
	        
	        Path badMappingPath = mappingsDir.resolve(id + "-"+ apiName + "-bad-mapping.json");
	        Path badFilesPath = filesDir.resolve(id + "-"+ apiName + "-bad-response.json");
	        
	        // delay, bad 있을 경우 삭제
	        if(Files.exists(delayMappingPath)) {
	        	Files.deleteIfExists(delayMappingPath);
	        }
	        if(Files.exists(delayFilesPath)) {
	        	Files.deleteIfExists(delayFilesPath);
	        }
	        if(Files.exists(badMappingPath)) {
	        	Files.deleteIfExists(badMappingPath);
	        }
	        if(Files.exists(badFilesPath)) {
	        	Files.deleteIfExists(badFilesPath);
	        }
	        
	        // 파일 삭제
	        Files.deleteIfExists(mappingsPath);
	        Files.deleteIfExists(filesPath);

	        logger.info("Deleted old API files for '{}'", apiName);
	    } catch (IOException e) {
	        logger.error("Failed to delete old API files for '{}'", apiName, e);
	    }
		
	}

	@Override
	public Map<String, Object> loadApi(int id) {
		try {
	        // DB에서 API 정보 가져오기
	        ApiVO apiVO = apiDao.findById(id);
	        if (apiVO == null) {
	            return null;
	        }

	        // 파일 경로 설정
	        Path mappingsPath = mappingsDir.resolve(id + "-" + apiVO.getApiName() + "-mapping.json");
	        Path filesPath = filesDir.resolve(id + "-" + apiVO.getApiName() + "-response.json");     

	        Path delayMappingPath = mappingsDir.resolve(id + "-"+ apiVO.getApiName() + "-delay-mapping.json");
	        Path delayFilesPath = filesDir.resolve(id + "-"+ apiVO.getApiName() + "-delay-response.json");
	        
	        Path badMappingPath = mappingsDir.resolve(id + "-"+ apiVO.getApiName() + "-bad-mapping.json");
	        Path badFilesPath = filesDir.resolve(id + "-"+ apiVO.getApiName() + "-bad-response.json");
	        
	        // 파일 읽기
	        String mappingsContent = Files.exists(mappingsPath) ? Files.readString(mappingsPath, StandardCharsets.UTF_8) : "{}";
	        String filesContent = Files.exists(filesPath) ? Files.readString(filesPath, StandardCharsets.UTF_8) : "{}";
	        String delayMappingsContent = Files.exists(delayMappingPath) ? 
	        		Files.readString(delayMappingPath, StandardCharsets.UTF_8) : Files.readString(mappingsDir.resolve(Paths.get("stub", "delay-stub.json")), StandardCharsets.UTF_8); ;
	        String delayFilesContent = Files.exists(delayFilesPath) ? 
	        		Files.readString(delayFilesPath, StandardCharsets.UTF_8) : Files.readString(filesDir.resolve(COMMON_DELAY_RESPONSE));
	        String badMappingContent = Files.exists(badMappingPath) ? 
	        		Files.readString(badMappingPath, StandardCharsets.UTF_8) : Files.readString(mappingsDir.resolve(Paths.get("stub", "bad-stub.json")), StandardCharsets.UTF_8); 
	        String badFilesContent = Files.exists(badFilesPath) ? 
	        		Files.readString(badFilesPath, StandardCharsets.UTF_8) : Files.readString(filesDir.resolve(COMMON_BAD_RESPONSE));

	        // 응답 데이터 생성
	        Map<String, Object> response = Map.of(
	                "apiName", apiVO.getApiName(),
	                "apiUrl", apiVO.getApiUrl(),
	                "normalMappings", mappingsContent,
	                "normalFiles", filesContent,
	                "delayMappings", delayMappingsContent,
	                "delayFiles", delayFilesContent,
	                "errorMappings", badMappingContent,
	                "errorFiles", badFilesContent
	        );

	        return response;
	    } catch (IOException e) {
	        e.printStackTrace();
	        return null;
	    }
	}
	
	private void validateApiFiles(String mappings, String files) {

		if (mappings != null && !mappings.isBlank()) {
	        jsonValidation.validateMappingJson(mappings);
	    }
		if (files != null && !files.isBlank()) {
	        jsonValidation.validateJson(files);
	    }
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
