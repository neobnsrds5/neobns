package com.example.neobns.dto;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogDTO {

	private String timestmp;
	private String loggerName;
	private String levelString;
	private String callerClass;
	private String callerMethod;
	private String query;
	private String uri;
	private String traceId;
	private String userId;
	private String ipAddress;
	private String device;
	private String executeResult;
	private String serverId = "BT20";

	// FWK_ERROR_HIS 로 매핑
	public String mapErrorCode() {

		String result = "";

		if (executeResult == null) {
			result = "BEE00001"; // backend etc
		} else if (callerClass.equals("SQL")) {

			if (executeResult.contains("IllegalArgumentException")) {
				result = "BET00001"; // backend transaction
			} else {
				result = "BET00002";
			}

		} else {

			result = "BEO00001"; // backend other

		}

		return result;

	}

	public String generateErrorSerNo() throws Exception {

		String formattedTime = convertTimestamp();

		return (formattedTime + serverId + traceId).substring(0,26);
	}

	public String convertTimestamp() throws Exception {

		String formattedTime = new SimpleDateFormat("yyyyMMddHHmmss")
				.format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(timestmp));

		return formattedTime;
	}

	public String convertUrl() {

		String returnUrl = "";
		if (uri != null) {
			returnUrl = returnUrl.replaceFirst("/", "");
		}

		return returnUrl;
	}

	public FwkErrorHisDto convertToHisDto() throws Exception {

		FwkErrorHisDto fwkErrorHisDto = new FwkErrorHisDto(mapErrorCode(), generateErrorSerNo(), userId, executeResult,
				convertTimestamp(), convertUrl(), executeResult, serverId);

		return fwkErrorHisDto;
	}

}
