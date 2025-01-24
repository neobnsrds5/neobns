package com.neobns.wiremock_service.config.wiremock;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SaveStub {
	
	private static final String MAPPINGS_DIR = "src/main/resources/wiremock/mappings";
    private static final String FILES_DIR = "src/main/resources/wiremock/__files";

    public static void saveStub(String url, String method, String responseBody, int statusCode) throws IOException {
        // 디렉토리 생성
        new File(MAPPINGS_DIR).mkdirs();
        new File(FILES_DIR).mkdirs();

        // 파일 이름 생성
        String splitUrl = url.split("\\?")[0];
        String fileName = splitUrl.replaceAll("[^a-zA-Z0-9]", "_") + "_" + System.currentTimeMillis();

        // 응답 데이터 저장
        File responseFile = new File(FILES_DIR, fileName + ".json");
        try (FileWriter responseWriter = new FileWriter(responseFile)) {
            responseWriter.write(responseBody);
        }

        // 매핑 데이터 저장
        File mappingFile = new File(MAPPINGS_DIR, fileName + ".json");
        String mappingJson = String.format(
            "{\n" +
                    "  \"request\": {\n" +
                    "    \"url\": \"%s\",\n" +
                    "    \"method\": \"%s\"\n" +
                    "  },\n" +
                    "  \"response\": {\n" +
                    "    \"status\": %d,\n" +
                    "    \"bodyFileName\": \"%s.json\"\n" +
                    "  }\n" +
                    "}",
            url, method, statusCode, fileName);

        try (FileWriter mappingWriter = new FileWriter(mappingFile)) {
            mappingWriter.write(mappingJson);
        }

        System.out.println("Stub saved: " + fileName);
    }
}
