package com.neobns.admin.flowadmin.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neobns.admin.flowadmin.dto.ConfigDto;
import com.neobns.admin.flowadmin.dto.SearchDto;
import com.neobns.admin.flowadmin.dto.SearchResultDto;
import com.neobns.admin.flowadmin.service.ControlService;
import com.neobns.admin.flowadmin.service.MessagePublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@Controller
public class ControlController {

    private final ControlService controlService;
    private final MessagePublisher messagePublisher;
    private final ObjectMapper objectMapper;

    public ControlController(ControlService controlService, MessagePublisher messagePublisher, ObjectMapper objectMapper) {
        this.controlService = controlService;
        this.messagePublisher = messagePublisher;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/")
    public String findPage(Model model,
                           @ModelAttribute SearchDto dto,
                           @RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "10") int size){

        List<SearchResultDto> results = controlService.find(dto, page, size);
        int total = (results.isEmpty())? 0 : results.get(0).getResultCount();
        int totalPage = (total / size) + (total % size == 0 ? 0 : 1);
        int dix = (page/10)*10;
        int start = dix+1;
        int end = dix+10;
        end = Math.min(end, totalPage);
        int[] range = IntStream.range(start, end+1).toArray();

        model.addAttribute("results", results);
        model.addAttribute("page", page);
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("size", size);
        model.addAttribute("param", dto);
        model.addAttribute("range", range);

        return "main";
    }

    @GetMapping("/r")
    public String r(){
        return "r";
    }

    @GetMapping("/new")
    public String newPage(){
        return "new";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute ConfigDto dto){
        try {
            int result = controlService.create(dto);
        } catch (RuntimeException e){
            System.out.println(e.getMessage());
        }
        return "redirect:/";
    }

    @PostMapping("/deleteAll")
    public String deleteAll(@RequestParam List<Long> deleteIds){
        if (deleteIds == null || deleteIds.isEmpty()){
            return "redirect:/";
        }
        try {
            int result = controlService.deleteByIds(deleteIds);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "redirect:/";
    }

    @GetMapping("/edit")
    public String updatePage(@RequestParam Long id, Model model){
        try{
            ConfigDto result = controlService.findById(id);
            model.addAttribute("id", id);
            model.addAttribute("config", result);
        } catch (Exception e){
            System.out.println(e.getMessage());
            return "redirect:/";
        }
        return "update";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute ConfigDto dto){
        try{
            int result = controlService.update(dto);
        } catch (Exception e){
            System.out.println(e.getMessage());
            return "redirect:/edit?id="+ dto.getId();
        }
        return "redirect:/";
    }
}
