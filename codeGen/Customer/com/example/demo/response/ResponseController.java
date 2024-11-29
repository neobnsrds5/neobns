package com.example.demo.response;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/response")
public class ResponseController {

    @Autowired
    private ResponseService service;

    @GetMapping
    public List<ResponseDto> findAll() {
        return service.findAll();
    }

    @GetMapping("/page")
    public List<ResponseDto> findByPage(@RequestParam("id") Map<String, Object> filters,
                                  @RequestParam("page") int page,
                                  @RequestParam("size") int size) {
        return service.findByPage(filters, page, size);
    }

    @GetMapping("/{id}")
    public ResponseDto findById(@PathVariable("id") Long id) {
        return service.findById(id);
    }

    @PostMapping
    public void create(@RequestBody ResponseDto dto) {
        service.create(dto);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable("id") Long id, @RequestBody ResponseDto dto) {
        service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        service.delete(id);
    }
}
