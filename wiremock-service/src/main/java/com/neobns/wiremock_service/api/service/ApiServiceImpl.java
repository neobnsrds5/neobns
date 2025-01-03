package com.neobns.wiremock_service.api.service;

import java.time.LocalDateTime;
import java.util.List;
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
