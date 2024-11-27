package com.example.neobns.controller;

import com.example.neobns.dto.ItemDto;
import com.example.neobns.dto.ResponseDto;
import com.example.neobns.service.QuickService;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class QuickController {

    private final QuickService quickService;
    
    @GetMapping("/")
    public String main() {
    	return "메인 어플리케이션 홈페이지";
    }

    @GetMapping("/dummy")
    @Timed(value = "quick.dummyone", longTask = true)
    public String dummy() throws InterruptedException{
    	Thread.sleep(300);
        log.info("dummy");
        return "{}";
    }

    @GetMapping("/dummy2")
    @Timed(value = "quick.dummytwo", longTask = true)
    public String dummy2(){
        log.info("dummy2");
        return "quitstart의 dummy2";
    }
    @GetMapping("/member")
    public String getMember(@RequestParam("empNo") String empNo, @RequestParam("year") int year){
        log.info("empNo : {}, year : {}", empNo, year);
        return "ok";
    }

    @GetMapping("/company/{id}")
    public String getCompany(@PathVariable("id") String id){
        log.info("id : {}", id);
        return "ok";
    }

    @PostMapping("/item")
    public ResponseDto registerItem(@RequestBody ItemDto item){
        log.info("item : {}", item);
        boolean b = quickService.registerItem(item);
        ResponseDto response = new ResponseDto();
        if (b){
            response.setMessage("ok");
        } else {
            response.setMessage("fail");
        }
        return response;
    }

    @GetMapping("/item")
    public ItemDto getItem(@RequestParam("id") String id){
        ItemDto itemDto = quickService.getItemById(id);
        log.info("name: {}", itemDto.getName());
        return itemDto;
    }

    @GetMapping("/findAll")
    public List<ItemDto> getAll(){
        List<ItemDto> results = quickService.getAll();
        for (ItemDto item : results){
            System.out.println(item.getId()+ " " + item.getName());
        }
        return results;
    }
}
