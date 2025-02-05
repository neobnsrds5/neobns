package com.neobns.wiremock_service.config.wiremock.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.json.JSONObject;
import org.json.JSONTokener;

public class JsonUtil {
	
	public JSONObject jsonObject;
	private File file;
	
	public JSONObject getJson() {
		return jsonObject;
	}
	
	public JsonUtil setJson(String fileName) {
		
		file = new File(System.getProperty("user.dir") + "/src/main/resources/wiremock/__files/" + fileName);
		
		if (!file.exists()) {
            throw new RuntimeException("JSON 파일을 찾을 수 없습니다: " + file.getPath());
        }

        try (InputStream inputStream = new FileInputStream(file)) {
            JSONTokener jsonTokener = new JSONTokener(inputStream);
            jsonObject = new JSONObject(jsonTokener);
        } catch (Exception e) {
            throw new RuntimeException("JSON 파일을 읽는 중 오류 발생", e);
        }	
		
		return this;
	}
	

}
