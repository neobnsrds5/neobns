package com.example.demo.item;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/item")
public class ItemController {

    @Autowired
    private ItemService service;

    @GetMapping
    public List<ItemDto> findAll() {
        return service.findAll();
    }

    @GetMapping("/page")
    public List<ItemDto> findByPage(@RequestParam("id") Map<String, Object> filters,
                                  @RequestParam("page") int page,
                                  @RequestParam("size") int size) {
        return service.findByPage(filters, page, size);
    }

    @GetMapping("/{id}")
    public ItemDto findById(@PathVariable("id") Long id) {
        return service.findById(id);
    }

    @PostMapping
    public void create(@RequestBody ItemDto dto) {
        service.create(dto);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable("id") Long id, @RequestBody ItemDto dto) {
        service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        service.delete(id);
    }
}
