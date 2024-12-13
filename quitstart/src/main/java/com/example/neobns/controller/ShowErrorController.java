package com.example.neobns.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ShowErrorController {
	@GetMapping("/alertError")
	public String index() {
		return "index";
	}
}
