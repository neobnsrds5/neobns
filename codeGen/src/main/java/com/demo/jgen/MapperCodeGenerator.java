package com.demo.jgen;

import io.swagger.v3.oas.models.media.Schema;
import java.io.IOException;

public class MapperCodeGenerator implements BaseCodeGenerator {

    @Override
    public void generateCode(String packageName, String resourceName, String packageDir, Schema schema) throws IOException {
        String mapperCode = """
                package %s;

                import org.apache.ibatis.annotations.Mapper;
                import java.util.List;
                import java.util.Map;

                @Mapper
                public interface %sMapper {

                    List<%sDto> findAll(Map<String, Object> filters);

                    List<%sDto> findByPage(Map<String, Object> filters, int offset, int limit);

                    %sDto findById(Long id);

                    void insert(%sDto dto);

                    void update(%sDto dto);

                    void deleteById(Long id);
                }
                """.formatted(packageName, resourceName, resourceName, resourceName, resourceName, resourceName, resourceName);

        writeToFile(packageDir + resourceName + "Mapper.java", mapperCode);
    }
}
