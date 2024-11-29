package com.example.demo.transfer;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/transfer")
public class TransferController {

    @Autowired
    private TransferService service;

    @GetMapping
    public List<TransferDto> findAll() {
        return service.findAll();
    }

    @GetMapping("/page")
    public List<TransferDto> findByPage(@RequestParam("id") Map<String, Object> filters,
                                  @RequestParam("page") int page,
                                  @RequestParam("size") int size) {
        return service.findByPage(filters, page, size);
    }

    @GetMapping("/{id}")
    public TransferDto findById(@PathVariable("id") Long id) {
        return service.findById(id);
    }

    @PostMapping
    public void create(@RequestBody TransferDto dto) {
        service.create(dto);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable("id") Long id, @RequestBody TransferDto dto) {
        service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        service.delete(id);
    }
}
