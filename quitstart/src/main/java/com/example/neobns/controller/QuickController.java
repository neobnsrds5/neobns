package com.example.neobns.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.example.neobns.dto.AccountDTO;
import com.example.neobns.dto.ItemDto;
import com.example.neobns.dto.ResponseDto;
import com.example.neobns.entity.Account;
import com.example.neobns.service.QuickService;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class QuickController {

	private final QuickService quickService;

	@GetMapping("/")
	public String main() {
		return "메인 페이지입니다";
	}

	@GetMapping("/dummy")
	@Timed(value = "quick.dummyone", longTask = true)
	public String dummy() throws InterruptedException {
		Thread.sleep(300);
		log.info("dummy");
		return "{}";
	}

	@GetMapping("/dummy2")
	@Timed(value = "quick.dummytwo", longTask = true)
	public String dummy2() {
		log.info("dummy2");
		return "quitstart의 dummy2";
	}

	@GetMapping("/member")
	public String getMember(@RequestParam("empNo") String empNo, @RequestParam("year") int year) {
		log.info("empNo : {}, year : {}", empNo, year);
		return "ok";
	}

	@GetMapping("/company/{id}")
	public String getCompany(@PathVariable("id") String id) {
		log.info("id : {}", id);
		return "ok";
	}

	@PostMapping("/item")
	public ResponseDto registerItem(@RequestBody ItemDto item) {
		log.info("item : {}", item);
		boolean b = quickService.registerItem(item);
		ResponseDto response = new ResponseDto();
		if (b) {
			response.setMessage("ok");
		} else {
			response.setMessage("fail");
		}
		return response;
	}

	@GetMapping("/item")
	public ItemDto getItem(@RequestParam("id") String id) {
		ItemDto itemDto = quickService.getItemById(id);
		log.info("name: {}", itemDto.getName());
		return itemDto;
	}

	@GetMapping("/findAll")
	public List<ItemDto> getAll() {
		List<ItemDto> results = quickService.getAll();
		return results;
	}

	@PostMapping("/addAccount")
	public ResponseEntity<String> addAccount(@RequestBody AccountDTO dto) {
		quickService.addAccount(dto);

		return ResponseEntity.ok("OK");
	}

	@PostMapping("/addAccountJPA")
	public ResponseEntity<String> addAccountJPA(@RequestBody Account account) {
		quickService.addUpdateAccountJPA(account);

		return ResponseEntity.ok("OK");
	}

	@GetMapping("/getAccountJPA")
	public Account getAccountJPA(@RequestParam("id") long id) {
		Account account = quickService.getAccountByIdJPA(id);
		log.info("name: {}", account.getName());
		return account;
	}

	@PutMapping("/updateAccountJPA")
	public ResponseEntity<String> updateAccountJPA(@RequestBody Account account) {

		quickService.addUpdateAccountJPA(account);

		return ResponseEntity.ok("OK");
	}

}
