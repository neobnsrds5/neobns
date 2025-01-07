package com.demo.jgen;

import io.swagger.v3.oas.models.media.Schema;

import java.io.IOException;

/*
 * Batch를 위한 데이터베이스 Configuration 코드 생성기
 */
public class DbConfigGenerator implements BaseCodeGenerator {

    @Override
    public void generateCode(String packageName, String resourceName, String packageDir, Schema<?> schema) throws IOException {
        String dbConfigCode = """
				package %s;
				
				import javax.sql.DataSource;
				
				import org.springframework.context.annotation.Bean;
				import org.springframework.context.annotation.Configuration;
				import org.springframework.boot.jdbc.DataSourceBuilder;
				
				@Configuration
				public class DBConfig {
				
					@Bean(name = "dataDBSource")
					public DataSource dataDBSource() {
						return DataSourceBuilder.create().driverClassName("com.mysql.cj.jdbc.Driver") // 드라이버 클래스
								.url("jdbc:mysql://localhost:3306/db2") // Data DB URL
								.username("root") // 사용자 이름
								.password("1234") // 비밀번호
								.build();
					}
				
					@Bean(name = "targetDBSource")
					public DataSource targetDBSource() {
						return DataSourceBuilder.create().driverClassName("com.mysql.cj.jdbc.Driver") // 드라이버 클래스
								.url("jdbc:mysql://localhost:3306/db3") // Target DB URL
								.username("root") // 사용자 이름
								.password("1234") // 비밀번호
								.build();
					}
				
				}
                """.formatted(packageName);

        writeToFile(packageDir + "DBConfig.java", dbConfigCode);
    }
}
