package com.neobns.wiremock_service.admin.service;

import org.springframework.stereotype.Service;

import com.neobns.wiremock_service.admin.dto.AdminApiDTO;
import com.neobns.wiremock_service.admin.mapper.AdminMapper;
import java.util.List;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {
	
	private final AdminMapper adminMapper;

	
	// 검색 + 페이징 처리된 API 목록 가져오기
    public List<AdminApiDTO> findApisByPage(int page, int size, String apiName) {
        int offset = (page - 1) * size; // 페이지네이션을 위한 오프셋 계산
        return adminMapper.findAll(apiName, size, offset);
    }

    // 검색된 API 개수 가져오기
    public int countApis(String apiName) {
        return adminMapper.countByApiName(apiName);
    }
}
