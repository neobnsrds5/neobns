package com.demo.jgen;

import io.swagger.v3.oas.models.media.Schema;
import java.io.IOException;

/*
 * 서비스 코드 생성기
 */
public class ServiceCodeGenerator implements BaseCodeGenerator {

    @Override
    public void generateCode(String packageName, String resourceName, String packageDir, Schema<?> schema) throws IOException {
        String serviceCode = """
                package %s;

                import org.springframework.stereotype.Service;
                import org.springframework.beans.factory.annotation.Autowired;
                import java.util.List;
                import java.util.Map;

                @Service
                public class %sService {

                    @Autowired
                    private %sMapper mapper;

                    public List<%sDto> findAll() {
                        return mapper.findAll();
                    }

                    public List<%sDto> findByPage(int page, int size) {
                        int offset = (page - 1) * size;
                        return mapper.findByPage(offset, size);
                    }

                    public %sDto findById(Long id) {
                        return mapper.findById(id);
                    }

                    public void create(%sDto dto) {
                        mapper.insert(dto);
                    }

                    public void update(Long id, %sDto dto) {
                        mapper.update(dto);
                    }

                    public void delete(Long id) {
                        mapper.deleteById(id);
                    }
                }
                """.formatted(
            		packageName, resourceName, resourceName, // 패키지명, 클래스명, Mapper
            		resourceName, // findAll()
            		resourceName, // findByPage()
            		resourceName, // findById()
            		resourceName, // create
            		resourceName); // update

        writeToFile(packageDir + resourceName + "Service.java", serviceCode);
    }
}
