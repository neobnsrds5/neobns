package com.example.neobns.controller;

import com.example.neobns.dto.TransferDTO;
import com.example.neobns.service.QuickService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class QuickController {

    private final QuickService quickService;
    
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
