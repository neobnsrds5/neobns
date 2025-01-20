package com.neobns.wiremock_service.api.controller.api;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.neobns.wiremock_service.api.dto.ChangeModeRequest;
import com.neobns.wiremock_service.api.service.ApiService;
import com.neobns.wiremock_service.api.vo.ApiVO;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiApiController {
	
	private final ApiService apiService;
	
	private final WireMockServer wireMockServer;
	
	@PostMapping("/toggle-mode")
	public ResponseEntity<String> toggleApiMode(@RequestBody Integer id) {
		try {
			apiService.toggleResponseStatusById(id);
	        return ResponseEntity.status(HttpStatus.OK).body("Successfully updated the response status.");
	    } catch (Exception e) {
	    	e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update response status.");
	    }
	}
	
	@PostMapping("/change-mode-selected")
	public ResponseEntity<String> changeMode(@RequestBody ChangeModeRequest request) {
		try {
			apiService.changeModeByIds(request.getIds(), request.isTargetMode());
	        return ResponseEntity.status(HttpStatus.OK).body("Successfully updated the response status.");
	    } catch (Exception e) {
	    	e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update response status.");
	    }
	}
	
	@PostMapping("/health-check")
	public ResponseEntity<String> healthCheck(@RequestBody Integer id) {
		try {
			apiService.performHealthCheck(id);
	        return ResponseEntity.status(HttpStatus.OK).body("Successfully performed Health Check.");
	    } catch (Exception e) {
	    	e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to perform Health Check.");
	    }
	}
	
	@GetMapping("/execute/{id}")
	public void executeApi(@PathVariable int id, HttpServletResponse response) throws IOException {
		ApiVO apiVO = apiService.getApi(id);
		if(apiVO == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
	        response.setContentType("application/json");
	        response.getWriter().write("{\"error\": \"API 정보가 없습니다.\"}");
	        return;
		}
		
		apiVO = apiService.performHealthCheck(id);
		String apiUrl = apiVO.getApiUrl();
		boolean isHealthy = apiVO.getLastCheckedStatus() == 0;
		boolean isMockMode = !apiVO.getResponseStatus();
				
		if(isHealthy) {	//서버 정상 시 실서버/대응답 DB 상태에 따라 처리
			if (isMockMode) {
	            // 대응답 처리 - WireMock Stub 요청
				String stubUUID = apiService.isStubExists(apiUrl); // UUID로 Stub 조회
				String mockApiUrl = "http://localhost:" + wireMockServer.port() + "/__admin/mappings/" + stubUUID;
				System.out.println("MOCK : " + mockApiUrl);
	            ResponseEntity<String> mockResponse = apiService.getStubResponse(mockApiUrl); // Stub 데이터를 가져옴
	            
	            response.setStatus(HttpServletResponse.SC_OK);
	            response.setContentType("application/json; charset=UTF-8");
	            // Stub 데이터를 JSON 형태로 반환
	            response.getWriter().write(mockResponse.getBody());
	        } else {
	        	// 실서버로 리다이렉트
	        	response.sendRedirect(apiUrl);
	        }														
		} else {		//서버 장애 시 공통 Stub 처리
			if(isMockMode) {
				// 상태에 따른 Stub 반환
				String transApiName = apiVO.getApiName().replace(" ", "");
		        String stubUrl = "http://localhost:" + wireMockServer.port() +"/mock/stub/" + transApiName+ "/" + apiVO.getLastCheckedStatus();
		        try {
		            System.out.println("Requesting Stub URL: {}"+ stubUrl);
		            ResponseEntity<String> stubResponse = apiService.getStubResponse(stubUrl);
		            response.setStatus(stubResponse.getStatusCode().value());
		            response.setContentType("application/json; charset=UTF-8");
		            response.getWriter().write(stubResponse.getBody());
		        } catch (HttpClientErrorException e) {
		        	System.out.println("HTTP Error while requesting Stub URL: "+stubUrl+", Status: " + e.getStatusCode()+ ", Body: " + e.getResponseBodyAsString());
		            response.setStatus(e.getStatusCode().value());
		            response.setContentType("application/json; charset=UTF-8");
		            response.getWriter().write("{\"error\": \"Fallback Stub 요청 실패\"}");
		        } catch (Exception e) {
		        	System.out.println("Unexpected error while requesting Stub URL: " + stubUrl +" e :" + e);
		            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		            response.setContentType("application/json; charset=UTF-8");
		            response.getWriter().write("{\"error\": \"Fallback Stub 요청 실패\"}");
		        }
			}
		}
	}
	
	@PostMapping("/add")
	public ResponseEntity<String> addApi(@RequestBody Map<String, String> apiData) {
	    try {
	    	String apiName = apiData.get("apiName");
	        String apiUrl = apiData.get("apiUrl");
	       
	        apiService.saveNewApi(apiName, apiUrl);
	        return ResponseEntity.status(HttpStatus.CREATED).body("API successfully added.");
	    }catch(IllegalArgumentException e) {
	    	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add API.");
	    }
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> deleteApi(@PathVariable int id){
		
		try {
			apiService.deleteApi(id);
	        return ResponseEntity.status(HttpStatus.OK).body("API successfully deleted.");
	    } catch (Exception e) {
	    	e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete API");
	    }
		
	}
	
}
