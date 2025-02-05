package com.neobns.wiremock_service.api.controller.api;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.neobns.wiremock_service.api.dto.ApiDTO;
import com.neobns.wiremock_service.api.dto.ChangeModeRequest;
import com.neobns.wiremock_service.api.service.ApiService;

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
	public void executeApi(@PathVariable int id, HttpServletResponse response) throws IOException, URISyntaxException {
		ApiDTO apiDTO = apiService.getApi(id);
		URI uri = new URI(apiDTO.getApiUrl());
		String apiUri = uri.getPath();
		if(uri.getQuery() != null) {
			apiUri = apiUri + "?" + uri.getQuery();
		}
		
		apiDTO = apiService.performHealthCheck(id);
		String apiUrl = apiDTO.getApiUrl();
		boolean isHealthy = apiDTO.getLastCheckedStatus() == 0; // 0 이면 서버 정상 1 이면 비정상
		boolean isMockMode = !apiDTO.getResponseStatus();
		
		String redirectUrl;
		
//		if(isHealthy) {	//서버 정상 시 실서버/대응답 DB 상태에 따라 처리
//			// 서버 정상
//			if(isMockMode) { // 실서버,대응답 설정 값
//				//apiService.getBodyFileName(apiUrl);
//				redirectUrl = "http://localhost:" + wireMockServer.port() + apiUri;	//대응답
//			}
//			else redirectUrl = apiUrl;															//실서버
//		} else {		//서버 장애 시 공통 Stub 처리
//			switch(apiDTO.getLastCheckedStatus()) {
//				case 1:	//장애
//				case 2:	//다운
//					redirectUrl = "http://localhost:" + wireMockServer.port() + "/mock/stub/bad";
//					break;
//				case 3:	//지연
//					redirectUrl = "http://localhost:" + wireMockServer.port() + "/mock/stub/delay";
//					break;
//				default:
//					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//	                response.setContentType("application/json");
//	                response.getWriter().write("{\"error\": \"서버 장애 상태를 처리할 수 없습니다.\"}");
//	                return;
//			}
//		}
		
		if(isMockMode) { // 실서버,대응답 설정 값
			redirectUrl = "http://localhost:" + wireMockServer.port() + apiUri;	//대응답
		}else {
			redirectUrl = apiUrl;												//실서버
		}
		
		response.sendRedirect(redirectUrl);
	}
	
	@PostMapping("/add")
	public ResponseEntity<String> addApi(@RequestBody ApiDTO apiDTO) {
	   try {
		   apiService.saveNewApi(apiDTO);
	        return ResponseEntity.status(HttpStatus.CREATED).body("{\"ok\" : \"API 등록 성공\"}");
	   } catch (Exception e) {
	    	e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\" : \"API 등록 실패\"}");
	    }
        	
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> deleteApi(@PathVariable int id){
		
		try {
			apiService.deleteApi(id);
	        return ResponseEntity.status(HttpStatus.OK).body("{\"ok\" : \"API 삭제 성공\"}");
	    } catch (Exception e) {
	    	e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\" : \"API 삭제 실패\"}");
	    }
		
	}
	
	@PutMapping("/update/{id}")
	public ResponseEntity<String> updateMockApi(@PathVariable int id, @RequestBody ApiDTO apiDto) {
        try {
        	apiService.updateApi(id, apiDto);
            return ResponseEntity.status(HttpStatus.OK).body("{\"ok\" : \"API 수정 성공\"}");
		} catch (Exception e) {
			e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\" : \"API 수정 실패\"}");
		}
		
    }
	
	@GetMapping("/get/{id}")
	 public ResponseEntity<Map<String, Object>> getMockApiById(@PathVariable int id) {
        ApiDTO apiDto = apiService.getApi(id); 
        Map<String, Object> response = apiService.getMockData(id);
        if (apiDto == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(response);
    }
	
	
}
