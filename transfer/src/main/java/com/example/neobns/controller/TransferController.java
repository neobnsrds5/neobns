package com.example.neobns.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.neobns.dto.TransferDTO;
import com.example.neobns.service.QuickService;
import com.example.neobns.service.TransferService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class TransferController {
	
	private final TransferService transferService;
	private final QuickService quickService;

	@PostMapping("/transfer/ex")
	public ResponseEntity<String> transfer(@RequestBody TransferDTO request){
		log.info("이체 서비스에서 이체 기능 호출 ");
		if(request.getMoney()>0) {
			transferService.insertTransferDetail(request);
			System.out.println("transferService.insertTransferDetail(request)");
			return ResponseEntity.ok("Transfer succesful: " + request.getMoney() + " to " + request.getToAccount());
		}else {
			System.out.println("Invalid transfer amount");
			return ResponseEntity.badRequest().body("Invalid transfer amount");
		}
	}
	
    @GetMapping("/")
    public String main() {
    	return "이체 어플리케이션 메인 페이지";
    }

    @GetMapping("/dummy")
    public String dummy(){
        return "{}";
    }

    @GetMapping("/dummy2")
    public String dummy2(){
        return "transfer의 dummy2";
    }

    @PostMapping("/transfer")
    public boolean transferBatchTest(@RequestBody TransferDTO item) {
    	System.out.print("from: " + item.getFromAccount() + ", to: " + item.getToAccount() + ", money: " + item.getMoney());
    	return true;
    }
    
    @PostMapping("/check-changes")
    public Map<String, String> checkForChanges() {
        return quickService.checkForChanges();
    }
    
    @GetMapping("/properties")
    public Map<String, String> getProperties() {
        return quickService.loadCurrentProperties();
    }

}
