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
		
		ArrayList<LogDTO> newList = (ArrayList<LogDTO>) ((ArrayList<LogDTO>) logList).clone();

		for (int i = 0; i <= newList.size() - 1; i++) {

			if (newList.get(i).getCallerClass().contains("http://")) {
				String newMethod = newList.get(i).getCallerClass() + " : " + newList.get(i).getCallerMethod();
				newList.get(i).setCallerClass("user");
				newList.get(i).setCallerMethod(newMethod);
			} else if (newList.get(i).getCallerClass().contains("com.example.neobns")) {

				int lastDot = newList.get(i).getCallerClass().lastIndexOf(".");
				String editedClass = newList.get(i).getCallerClass().substring(lastDot + 1);

				newList.get(i).setCallerClass(editedClass);
			}
		}

		System.out.println("converted log list : " + newList.toString());

		StringBuilder builder = new StringBuilder();

		for (int i = 0; i <= newList.size() - 1; i++) {
			String addedString;

			if (i <= newList.size() - 2) {

				if (newList.get(i).getCallerClass().equals("SQL")) {

					addedString = newList.get(i).getCallerClass() + " -> " + newList.get(i + 1).getCallerClass()
							+ " : <font color=red> " + newList.get(i).getCallerMethod();

				} else {

					addedString = newList.get(i).getCallerClass() + " -> " + newList.get(i + 1).getCallerClass() + " : "
							+ newList.get(i).getCallerMethod();

				}

			} else {
				addedString = newList.get(i).getCallerClass() + " -> " + newList.get(0).getCallerClass() + " : "
						+ newList.get(i).getCallerMethod();
			}

			builder.append(addedString).append(System.lineSeparator());

		}

		String url = builder.toString();

		System.out.println("made string : " + url);

		return url;
	}
}
