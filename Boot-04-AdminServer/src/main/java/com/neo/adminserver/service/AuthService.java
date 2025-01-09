package com.neo.adminserver.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.neo.adminserver.dto.FwkUserDTO;
import com.neo.adminserver.mapper.db2.AuthMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final AuthMapper authMapper;

	public List<FwkUserDTO> findSById(String username) {
		return authMapper.findById(username);
	}
	public void updateUserSession(String username, String lastSession, String dtime) {
		authMapper.updateUserSession(username, lastSession, dtime);
	}
	public void insertUser(FwkUserDTO user) {
		authMapper.insertUser(user);
	}
	
	public void isLogin(String currentSessionId , String newSessionId) {
		
	}
	

}
