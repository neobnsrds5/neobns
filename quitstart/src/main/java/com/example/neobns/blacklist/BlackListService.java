package com.example.neobns.blacklist;

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
		// 게이트웨이 등을 통해 ip가 변경되었더라도 원래의 ip를 가져옴
		String clientIp = request.getHeader("X-Forwarded-For");
		if (clientIp != null && !clientIp.isEmpty() && "unknown".equalsIgnoreCase(clientIp)) {
			return clientIp.split(",")[0].trim();
		}
		// "X-Real-IP" 헤더에서 ip 주소를 가져옴
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
