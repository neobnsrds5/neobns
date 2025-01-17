package com.neobns.wiremock_service.api.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.jayway.jsonpath.JsonPath;

public class JsonPathCheck {

	 private static final Path MAPPINGS_DIR = Paths.get("src", "main", "resources", "wiremock", "mappings");

	 public static int checkFileState(String fileName, int id) {
	        try {
	            // 파일 이름 패턴
	            String delayFileName = id + "-" + fileName + "-delay-mapping.json";
	            String badFileName = id + "-" + fileName + "-bad-mapping.json";

	            // 파일 경로
	            Path delayFilePath = MAPPINGS_DIR.resolve(delayFileName);
	            Path badFilePath = MAPPINGS_DIR.resolve(badFileName);
	            // delay 파일 확인
	            if (Files.exists(delayFilePath)) {
	                if (isIdPresentInJson(delayFilePath)) {
	                    return 3; // delay 상태
	                }
	            }

	            // bad 파일 확인
	            if (Files.exists(badFilePath)) {
	                if (isIdPresentInJson(badFilePath)) {
	                    return 2; // bad 상태
	                }
	            }

	            // 매칭되지 않음
	            return 0;
	        } catch (Exception e) {
	            e.printStackTrace();
	            return 0;
	        }
	    }
	 
    // JSON 파일에서 특정 값 가져오기 및 {id} 존재 여부 확인
    public static boolean isIdPresentInJson(Path filePath) {
        try {
            String jsonContent = Files.readString(filePath);

            // JSONPath로 데이터 검색
            String jsonPathExpression = "$..*"; // 모든 값을 탐색
            Object result = JsonPath.parse(jsonContent).read(jsonPathExpression);

            // /mock/stub/delay/ or /mock/stub/bad/가 포함되어 있는지 확인
            if (result != null) {
                String resultString = result.toString();
                return resultString.contains("\\/mock\\/stub\\/delay\\/") ||
                		resultString.contains("\\/mock\\/stub\\/bad\\/");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }
	
}
