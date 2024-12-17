package com.example.neobns.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.neobns.dto.FwkErrorHisDto;
import com.example.neobns.oraclemapper.OracleMapper;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ShowErrorController {
	
	private final OracleMapper mapper;
	
	@GetMapping("/alertError")
	public String index() {
		return "index2";
	}
	
	@GetMapping("/showErrorInOracle")
	@ResponseBody
	public List<FwkErrorHisDto> getErrors(){
		return mapper.getRecords(0L);
	}
}
