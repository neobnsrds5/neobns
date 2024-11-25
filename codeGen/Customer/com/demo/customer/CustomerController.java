package com.demo.customer;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerService service;

    @GetMapping
    public List<CustomerDto> findAll(@RequestParam("id") Map<String, Object> filters) {
        return service.findAll(filters);
    }

    @GetMapping("/page")
    public List<CustomerDto> findByPage(@RequestParam("id") Map<String, Object> filters,
                                  @RequestParam("page") int page,
                                  @RequestParam("size") int size) {
        return service.findByPage(filters, page, size);
    }

    @GetMapping("/{id}")
    public CustomerDto findById(@PathVariable("id") Long id) {
        return service.findById(id);
    }

    @PostMapping
    public void create(@RequestBody CustomerDto dto) {
        service.create(dto);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable("id") Long id, @RequestBody CustomerDto dto) {
        service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        service.delete(id);
    }
}
