package com.example.neobns.controller;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.neobns.batch.SubBatch1;
import com.example.neobns.dto.AccountDTO;
import com.example.neobns.dto.ErrorLogDTO;
import com.example.neobns.dto.ItemDto;
import com.example.neobns.dto.ResponseDto;
import com.example.neobns.dto.TransferDTO;
import com.example.neobns.entity.Account;
import com.example.neobns.service.ErrorService;
import com.example.neobns.service.QuickService;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class QuickController {

	private final QuickService quickService;
	private final ErrorService errorService;
	private static final Logger logger = LoggerFactory.getLogger("========Log Test=========");
	
	// transfer 호출
	@PostMapping("/transfer")
	public ResponseEntity<String> callTransfer(@RequestBody TransferDTO dto){
		log.info("이체 시작하기 위해 메인 서비스에서 호출 from : {}, to : {}, money : {}", dto.getFromAccount(), dto.getToAccount(), dto.getMoney());
		String result = quickService.initiateTransfer(dto.getFromAccount(), dto.getToAccount(), dto.getMoney());
		log.info("이체 서비스를 통해 이체 완료");
		return ResponseEntity.ok(result);
	}

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
	
	// ${} sql injection을 위한 api
	@GetMapping("/item/sql/injection")
	public ItemDto getItemSqlInjection(@RequestParam("id") String id) {
		ItemDto itemDto = quickService.getItemByIdWithSqlInjection(id);
		log.info("name: {}", itemDto.getName());
		return itemDto;
	}
	// ${} sql injection을 위한 api
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
	
	@GetMapping("/testforsocket")
	public ErrorLogDTO getError() {
		return errorService.getError();
	}
	
	// 강제로 에러를 발생시키고 싶을 때 호출한다.
	@GetMapping("/errorBreak")
	public String errorBreak() {
		throw new ArrayIndexOutOfBoundsException("from errorBreak");
	}
	
	
	
	// 로그 변경 확인
	@GetMapping("/changeLogLevel")
	public void changeLogLevel() {
		for (int i = 0; i < 10; i++) {
			logger.info("로그테스트 실행 중");
			logger.debug("로그테스트 실행 중");
		}
	}
	
	

}
