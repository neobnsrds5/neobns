package com.demo.jgen;

import io.swagger.v3.oas.models.media.Schema;
import java.io.IOException;

public class ServiceCodeGenerator implements BaseCodeGenerator {

    @Override
    public void generateCode(String packageName, String resourceName, String packageDir, Schema schema) throws IOException {
        String serviceCode = """
                package %s;

                import org.springframework.stereotype.Service;
                import org.springframework.beans.factory.annotation.Autowired;
                import org.springframework.transaction.annotation.Transactional;
                import java.util.List;
                import java.util.Map;

                @Service
                public class %sService {

                    @Autowired
                    private %sMapper mapper;

                    @Transactional(readOnly = true)
                    public List<%sDto> findAll(Map<String, Object> filters) {
                        return mapper.findAll(filters);
                    }

                    @Transactional(readOnly = true)
                    public List<%sDto> findByPage(Map<String, Object> filters, int page, int size) {
                        int offset = (page - 1) * size;
                        return mapper.findByPage(filters, offset, size);
                    }

                    @Transactional(readOnly = true)
                    public %sDto findById(Long id) {
                        return mapper.findById(id);
                    }

                    @Transactional
                    public void create(%sDto dto) {
                        mapper.insert(dto);
                    }

                    @Transactional
                    public void update(Long id, %sDto dto) {
                        mapper.update(dto);
                    }

                    @Transactional
                    public void delete(Long id) {
                        mapper.deleteById(id);
                    }
                }
                """.formatted(packageName, resourceName, resourceName, resourceName, resourceName, resourceName, resourceName, resourceName);

        writeToFile(packageDir + resourceName + "Service.java", serviceCode);
    }
}
