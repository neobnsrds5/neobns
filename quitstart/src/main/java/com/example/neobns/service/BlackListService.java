package com.example.neobns.service;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import kotlin.ranges.IntRange;

@Service
public class BlackListService {

	private final JdbcTemplate spiderTemplate;

	public BlackListService(JdbcTemplate spiderTemplate) {
		super();
		this.spiderTemplate = spiderTemplate;
	}

	public boolean isBlackListed(String ip) {

		ip = ip.trim();
		String sql = "SELECT COUNT(*) FROM FWK_BLACKLIST WHERE BLACK_LIST_IP = ?";
		Integer count = spiderTemplate.queryForObject(sql, Integer.class, ip);

		return (count != null && count > 0);

	}

	public String getRealIp(HttpServletRequest request) {

		String clientIp = request.getHeader("X-Forwarded-For");
		if (clientIp != null && !clientIp.isEmpty() && "unknown".equalsIgnoreCase(clientIp)) {
			return clientIp.split(",")[0].trim();
		}

		clientIp = request.getHeader("X-Real-IP");
		if (clientIp != null && !clientIp.isEmpty() && "unknown".equalsIgnoreCase(clientIp)) {
			return clientIp.trim();
		}

		return request.getRemoteAddr();
	}

	public List<String> getBlackList() {

		String sql = "SELECT BLACK_LIST_IP FROM FWK_BLACKLIST";
		List<String> blackList = spiderTemplate.queryForList(sql, String.class);
		return blackList;

	}

}
