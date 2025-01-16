package com.neobns.wiremock_service.api.util;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JsonValidation {
	
	private final ObjectMapper objectMapper = new ObjectMapper();

	public void validateMappingJson(String mappings) {
		try {
			validateJson(mappings);
			
	        JsonNode mappingNode = objectMapper.readTree(mappings);

	        // 필수 필드 검증
	        if (!mappingNode.has("request") || !mappingNode.has("response")) {
	            throw new IllegalArgumentException("Missing 'request' or 'response' field in mapping JSON.");
	        }

	        // Request 검증
	        JsonNode requestNode = mappingNode.get("request");
	        if (!requestNode.has("method") || !requestNode.get("method").isTextual()) {
	            throw new IllegalArgumentException("Invalid or missing 'method' field.");
	        }
	        String method = requestNode.get("method").asText();
	        if (!method.matches("GET|POST|PUT|DELETE|OPTIONS|ANY")) {
	            throw new IllegalArgumentException("Invalid HTTP method: " + method);
	        }
	        if (requestNode.has("url")) {
	            if (!requestNode.get("url").isTextual()) {
	                throw new IllegalArgumentException("Invalid 'url' field: must be a string.");
	            }
	        }
	        else if (requestNode.has("urlPattern")) {
	            if (!requestNode.get("urlPattern").isTextual()) {
	                throw new IllegalArgumentException("Invalid 'urlPattern' field: must be a string.");
	            }
	        } else {
	            throw new IllegalArgumentException("Missing 'url' or 'urlPattern' field in request.");
	        }

	        // Response 검증
	        JsonNode responseNode = mappingNode.get("response");
	        if (!responseNode.has("status") || !responseNode.get("status").isInt()) {
	            throw new IllegalArgumentException("Invalid or missing 'status' field.");
	        }
	        int status = responseNode.get("status").asInt();
	        if (status < 100 || status > 599) {
	            throw new IllegalArgumentException("Invalid HTTP status code: " + status);
	        }
	        if (!responseNode.has("bodyFileName") || !responseNode.get("bodyFileName").isTextual()) {
	            throw new IllegalArgumentException("Invalid or missing 'bodyFileName' field.");
	        }
	        if(!responseNode.has("headers") || !requestNode.get("headers").isTextual()) {
	        	throw new IllegalArgumentException("Invalid or missing 'headers' field.");
	        }

	        // 파일 참조 검증
	        String bodyFileName = responseNode.get("bodyFileName").asText();
	        
	        if (!bodyFileName.endsWith(".json")) {
	            throw new IllegalArgumentException("Invalid 'bodyFileName': must end with .json");
	        }
	        
	        
	    } catch (Exception e) {
	        throw new IllegalArgumentException("Invalid mapping JSON: " + e.getMessage());
	    }
	}
	
	public void validateJson(String files) {
		try {
            objectMapper.readTree(files); // JSON 파싱 시도
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON format: " + e.getMessage());
        }
	}
	
}
