package com.neo.adminserver.service;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import org.apache.ibatis.annotations.Case;
import org.springframework.stereotype.Service;

import com.neo.adminserver.dto.LogDTO;
import com.neo.adminserver.mapper.LogMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogService {

	private final LogMapper logMapper;

	public List<LogDTO> findSlowByPage() {
		return logMapper.findSlowByPage();
	}

	public List<LogDTO> findErrorByPage() {
		return logMapper.findErrorByPage();
	}

	public List<LogDTO> findByTraceId(String traceId) {
		return logMapper.findByTraceId(traceId);
	}

	
	public String buildPlantUML(String traceID, List<LogDTO> logList) {

		for (int i = 0; i <= logList.size() - 1; i++) {

			if (logList.get(i).getCallerClass().contains("http://localhost:8000/")) {
				logList.get(i).setCallerClass("user");
			} else if (logList.get(i).getCallerClass().contains("com.example.neobns")) {

				int lastDot = logList.get(i).getCallerClass().lastIndexOf(".");
				String editedClass = logList.get(i).getCallerClass().substring(lastDot + 1);

				logList.get(i).setCallerClass(editedClass);
			}
		}

		System.out.println("converted log list : " + logList.toString());

		StringBuilder builder = new StringBuilder();

		for (int i = 0; i <= logList.size() - 1; i++) {
			String addedString;

			if (i <= logList.size() - 2) {

				if (logList.get(i).getCallerClass().equals("SQL")) {

					addedString = logList.get(i).getCallerClass() + " -> " + logList.get(i + 1).getCallerClass()
							+ " : <font color=red> " + logList.get(i).getCallerMethod();

				} else {

					addedString = logList.get(i).getCallerClass() + " -> " + logList.get(i + 1).getCallerClass() + " : "
							+ logList.get(i).getCallerMethod();

				}

			} else {
				addedString = logList.get(i).getCallerClass() + " -> " + logList.get(0).getCallerClass() + " : "
						+ logList.get(i).getCallerMethod();
			}

			builder.append(addedString).append(System.lineSeparator());

		}

		String url = builder.toString();

		System.out.println("made string : " + url);

		return url;
	}
}
