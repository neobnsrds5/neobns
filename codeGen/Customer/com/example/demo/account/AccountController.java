package com.example.demo.account;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService service;

    @GetMapping
    public List<AccountDto> findAll() {
        return service.findAll();
    }

    @GetMapping("/page")
    public List<AccountDto> findByPage(@RequestParam("id") Map<String, Object> filters,
                                  @RequestParam("page") int page,
                                  @RequestParam("size") int size) {
        return service.findByPage(filters, page, size);
    }

    @GetMapping("/{id}")
    public AccountDto findById(@PathVariable("id") Long id) {
        return service.findById(id);
    }

    @PostMapping
    public void create(@RequestBody AccountDto dto) {
        service.create(dto);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable("id") Long id, @RequestBody AccountDto dto) {
        service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        service.delete(id);
    }
}
