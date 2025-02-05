package com.neobns.wiremock_service.admin.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import com.neobns.wiremock_service.api.dto.ApiDTO;
import com.neobns.wiremock_service.api.service.ApiService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AdminController {

	private final ApiService apiService;
	
	@GetMapping("/admin")
	public String showAdmin(Model model,
			@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
			@ModelAttribute ApiDTO paramDto) {
		List<ApiDTO> apiList = apiService.getAllApis();
		model.addAttribute("apiList", apiList);
		return "admin";
	}
	
}
