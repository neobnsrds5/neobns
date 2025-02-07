package com.neobns.admin.flowcontrol.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public ResponseEntity<String> test(){
        return ResponseEntity.ok("success");
    }

    @GetMapping("/wait")
    public ResponseEntity<String> testWait() throws InterruptedException {
        Thread.sleep(5000);
        return ResponseEntity.ok("success");
    }

    @GetMapping("/wait/deep")
    public ResponseEntity<String> testWaitDeep() throws InterruptedException {
        Thread.sleep(5000);
        return ResponseEntity.ok("success in deeper url");
    }

    @GetMapping("/test/inside")
    public ResponseEntity<String> testInside() {
        return ResponseEntity.ok("inside");
    }

    @GetMapping("/plus")
    public ResponseEntity<String> testPlus() {

        return ResponseEntity.ok("plus");
    }
}
