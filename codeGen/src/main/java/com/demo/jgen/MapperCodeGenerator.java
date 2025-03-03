package com.demo.jgen;

import io.swagger.v3.oas.models.media.Schema;
import java.io.IOException;

/*
 * Mapper 코드 생성기
 */
public class MapperCodeGenerator implements BaseCodeGenerator {

    @Override
    public void generateCode(String packageName, String resourceName, String packageDir, Schema<?> schema) throws IOException {
        String mapperCode = """
                package %s;

                import org.apache.ibatis.annotations.Mapper;
                import org.apache.ibatis.annotations.Param;
                import java.util.List;
                import java.util.Map;

                @Mapper
                public interface %sMapper {

                    List<%sDto> findAll();

                    List<%sDto> findByPage(@Param("offset") int offset, @Param("limit") int limit);

                    %sDto findById(Long id);

                    void insert(%sDto dto);

                    void update(%sDto dto);

                    void deleteById(Long id);
                }
                """.formatted(
                	packageName, resourceName, // 패키지명, 인터페이스명
                	resourceName, // findAll()
                	resourceName, // findByPage()
                	resourceName, // findById()
                	resourceName, // insert()
                	resourceName); // update()

        writeToFile(packageDir + resourceName + "Mapper.java", mapperCode);
    }
}
