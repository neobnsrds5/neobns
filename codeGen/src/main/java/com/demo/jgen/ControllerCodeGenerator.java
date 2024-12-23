package com.demo.jgen;

import io.swagger.v3.oas.models.media.Schema;
import java.io.IOException;

/*
 * 컨트롤러 코드 생성기
 */
public class ControllerCodeGenerator implements BaseCodeGenerator {

    @Override
    public void generateCode(String packageName, String resourceName, String packageDir, Schema<?> schema) throws IOException {
        String controllerCode = """
                package %s;

                import org.springframework.web.bind.annotation.*;
                import org.springframework.beans.factory.annotation.Autowired;
                import java.util.List;
                import java.util.Map;

                @RestController
                @RequestMapping("/%s")
                public class %sController {

                    @Autowired
                    private %sService service;

                    @GetMapping
                    public List<%sDto> findAll() {
                        return service.findAll();
                    }

                    @GetMapping("/page")
                    public List<%sDto> findByPage(@RequestParam("page") int page,
                                                  @RequestParam("size") int size) {
                        return service.findByPage(page, size);
                    }

                    @GetMapping("/{id}")
                    public %sDto findById(@PathVariable("id") Long id) {
                        return service.findById(id);
                    }

                    @PostMapping
                    public void create(@RequestBody %sDto dto) {
                        service.create(dto);
                    }

                    @PutMapping("/{id}")
                    public void update(@PathVariable("id") Long id, @RequestBody %sDto dto) {
                        service.update(id, dto);
                    }

                    @DeleteMapping("/{id}")
                    public void delete(@PathVariable("id") Long id) {
                        service.delete(id);
                    }
                }
                """.formatted(
            		packageName, // 패키지명
            		resourceName.toLowerCase(), // @RequestMapping("/%s")
            		resourceName, resourceName, // 클래스명, Service
            		resourceName, // findAll()
            		resourceName, // findByPage()
            		resourceName, // findById()
            		resourceName, // create()
            		resourceName); // update()

        writeToFile(packageDir + resourceName + "Controller.java", controllerCode);
    }
}
