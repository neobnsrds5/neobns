package com.neobns.wiremock_service.config.wiremock;

import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseTransformer;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.Response;

public class TransformStub extends ResponseTransformer{
	
	private static final String VALID_KEY = "data"; // param
	private static final String URL_KEY = "url";
	private static final String BODY_DATA_KEY = "data"; // body
		
	@Override
	public Response transform(Request request, Response response, FileSource files, Parameters parameters) {
		
		try {
			Response finalResponse = null;
			String requestBody = request.getBodyAsString();
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode json;
	        
	        // 6. Header 
	        String headerValue = request.containsHeader("X-Custom-Header")
	                ? request.getHeader("X-Custom-Header")
	                : "null";
			
	        switch (request.getMethod().getName().toUpperCase()) {
				case "GET": {
					String paramKey = request.queryParameter(VALID_KEY).isPresent() ? request.queryParameter(VALID_KEY).firstValue() : "default";
	
					// key 값 유효성 검사
			        if (paramKey == null) {
			            // key 값이 없거나 유효하지 않은 경우 에러 반환
			        	finalResponse = Response.response()
			                    .status(400)
			                    .body("{\"error\": \"Invalid or missing key\"}")
			                    .build();
			        }
			        
			        SaveStub.saveStub(request.getUrl(), request.getMethod().toString(), paramKey, 200);
			        finalResponse = Response.response()
			                .but()
			                .status(200)
			                .body(paramKey)
			                .build();
					break;
				}
				case "POST":{
					json = objectMapper.readTree(requestBody);
					
					String responseData = null;
					// HTTP Method 처리
			        String method = json.has("method") ? json.get("method").asText().toUpperCase() : request.getMethod().toString();
			        
			        // url
			        String url = json.has(URL_KEY) ? json.get(URL_KEY).asText() : request.getUrl();
					// 설정한 응답 데이터
					responseData = json.has(BODY_DATA_KEY) ? json.get(BODY_DATA_KEY).toString() : "{\"message\": \"Default response\"}";
					
					SaveStub.saveStub(url, method, responseData, 200);
					finalResponse = Response.response()
			                .but()
			                .status(200)
			                .body(responseData)
			                .build();
					break;
				}
				case "DELETE":
				case "PATCH":
				case "PUT":{
					// 추가 해야 할 곳
					break;
				}
				default:
					// 처리되지 않은 메서드
			        finalResponse = Response.response()
			                .status(405)
			                .body("{\"error\": \"Method not allowed\"}")
			                .build();
			        break;
			}
	        return finalResponse;
	        
//			// Query 파라미터 확인
//			if("GET".equalsIgnoreCase(request.getMethod().getName())) {
//				String paramKey = request.queryParameter(VALID_KEY).isPresent() ? request.queryParameter(VALID_KEY).firstValue() : "default";
//
//				// key 값 유효성 검사
//		        if (paramKey == null) {
//		            // key 값이 없거나 유효하지 않은 경우 에러 반환
//		        	finalResponse = Response.response()
//		                    .status(400)
//		                    .body("{\"error\": \"Invalid or missing key\"}")
//		                    .build();
//		        }
//		        
//		        SaveStub.saveStub(request.getUrl(), request.getMethod().toString(), paramKey, 200);
//		        finalResponse = Response.response()
//		                .but()
//		                .status(200)
//		                .body(paramKey)
//		                .build();
//		       
//		        
//			} else if("POST".equalsIgnoreCase(request.getMethod().getName())) {
//				
//				json = objectMapper.readTree(requestBody);
//				
//				String responseData = null;
//				// HTTP Method 처리
//		        String method = json.has("method") ? json.get("method").asText().toUpperCase() : "GET";
//		        
//		        // url
//		        String url = json.has(URL_KEY) ? json.get(URL_KEY).asText() : "/default";
//				// 설정한 응답 데이터
//				responseData = json.has(BODY_DATA_KEY) ? json.get(BODY_DATA_KEY).toString() : "{\"message\": \"Default response\"}";
//				
//				SaveStub.saveStub(url, method, responseData, 200);
//				finalResponse = Response.response()
//		                .but()
//		                .status(200)
//		                .body(responseData)
//		                .build();
//			} else if("DELETE".equalsIgnoreCase(request.getMethod().getName())) {
//				json = objectMapper.readTree(requestBody);
//				
//				
//			}
//
//			return finalResponse != null ? finalResponse : Response.response()
//		            .status(500)
//		            .body("{\"error\": \"Unhandled case\"}")
//		            .build();
			
			
		}catch(Exception e ) {
			return Response.response()
	                .but()
	                .body("Json Validation Error")
	                .build();
		}

//            registerStub(method, url, queryParamValue, responseBody);           	

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "trans";
	}
	@Override
	public boolean applyGlobally() {
		return false;
	}
	
//	// Stub 등록 메서드
//    private void registerStub(String method, String url, String queryParamValue, String bodyFileName) {
//        switch (method) {
//            case "GET":
//            	stubFor(get(urlPathEqualTo(url))
//                        .withQueryParam("type", equalTo(queryParamValue))
//                        .willReturn(aResponse()
//                                .withStatus(200)
//                                .withHeader("Content-Type", "application/json")
//                                .withBodyFile(bodyFileName)));
//                break;
//            case "POST":
//            	stubFor(post(urlPathEqualTo(url))
//                        .withQueryParam("type", equalTo(queryParamValue))
//                        .willReturn(aResponse()
//                                .withStatus(200)
//                                .withHeader("Content-Type", "application/json")
//                                .withBodyFile(bodyFileName)));
//                break;
//            case "PUT":
//            	stubFor(put(urlPathEqualTo(url))
//                        .withQueryParam("type", equalTo(queryParamValue))
//                        .willReturn(aResponse()
//                                .withStatus(200)
//                                .withHeader("Content-Type", "application/json")
//                                .withBodyFile(bodyFileName)));
//                break;
//            case "DELETE":
//            	stubFor(delete(urlPathEqualTo(url))
//                        .withQueryParam("type", equalTo(queryParamValue))
//                        .willReturn(aResponse()
//                                .withStatus(200)
//                                .withHeader("Content-Type", "application/json")
//                                .withBodyFile(bodyFileName)));
//                break;
//            default:
//                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
//        }
//    }
	

}
