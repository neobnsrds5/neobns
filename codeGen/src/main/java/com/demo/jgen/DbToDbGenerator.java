package com.demo.jgen;

import io.swagger.v3.oas.models.media.Schema;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

public class DbToDbGenerator implements BaseCodeGenerator {

    @Override
    public void generateCode(String packageName, String resourceName, String packageDir, Schema schema) throws IOException {

        // 생성할 파일 내용 생성
        String content = generateClassContent(packageName, resourceName, schema);

        writeToFile(packageDir + resourceName + "DbToDbBatch.java", content);
    }

    private String generateClassContent(String packageName, String className, Schema schema) {

        StringBuilder mapBuilder = new StringBuilder();
        StringBuilder sqlBuilder = new StringBuilder();
        StringBuilder paramBuilder = new StringBuilder();
        Map<String, Schema> properties = schema.getProperties();
        String tableName = className.toLowerCase();

        for (Map.Entry<String, Schema> entry : properties.entrySet()) {
            String fieldName = entry.getKey();
            if (fieldName.toLowerCase().equals("id"))
                continue;
            String getMethod = mapSchemaTypeToJavaType(entry.getValue().getType(), entry.getValue().getFormat());
            mapBuilder.append("\t\t\tmap.put(")
                    .append("\"").append(fieldName).append("\", rs.get")
                    .append(getMethod).append("(")
                    .append("\"").append(fieldName).append("\"));\n");
        }
        sqlBuilder.append("INSERT INTO ").append(tableName).append(" (")
                .append(String.join(", ", properties.keySet().stream()
                        .filter(field -> !field.equalsIgnoreCase("id"))
                        .toList()))
                .append(") VALUES (:")
                .append(String.join(", :", properties.keySet().stream()
                        .filter(field -> !field.equalsIgnoreCase("id"))
                        .toList())).append(")");
        for (Map.Entry<String, Schema> entry : properties.entrySet()) {
            String fieldName = entry.getKey();
            if (fieldName.toLowerCase().equals("id"))
                continue;
            String getMethod = mapSchemaTypeToJavaType(entry.getValue().getType(), entry.getValue().getFormat());
            paramBuilder.append("\t\t\t\tparams.addValue(")
                    .append("\"").append(fieldName).append("\", item.get")

                    .append("\"").append(fieldName).append("\"));\n");
        }

        return "package " + packageName + ";\n\n" +
                "import java.util.HashMap;\n" +
                "import java.util.Map;\n\n" +
                "import javax.sql.DataSource;\n\n" +
                "import org.springframework.batch.core.Job;\n" +
                "import org.springframework.batch.core.Step;\n" +
                "import org.springframework.batch.core.job.builder.JobBuilder;\n" +
                "import org.springframework.batch.core.repository.JobRepository;\n" +
                "import org.springframework.batch.core.step.builder.StepBuilder;\n" +
                "import org.springframework.batch.item.database.JdbcBatchItemWriter;\n" +
                "import org.springframework.batch.item.database.JdbcPagingItemReader;\n" +
                "import org.springframework.batch.item.database.PagingQueryProvider;\n" +
                "import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;\n" +
                "import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;\n" +
                "import org.springframework.beans.factory.annotation.Qualifier;\n" +
                "import org.springframework.context.annotation.Bean;\n" +
                "import org.springframework.context.annotation.Configuration;\n" +
                "import org.springframework.core.task.TaskExecutor;\n" +
                "import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;\n" +
                "import org.springframework.transaction.PlatformTransactionManager;\n\n" +
                "@Configuration\n" +
                "public class " + className + "DbToDbBatch {\n\n" +
                "\tprivate final DataSource realSource;\n" +
                "\tprivate final DataSource targetSource;\n\n" +
                "\tprivate final JobRepository jobRepository;\n" +
                "\tprivate final PlatformTransactionManager transactionManager;\n\n" +
                "\tpublic " + className + "(@Qualifier(\"dataDBSource\") DataSource realSource,\n" +
                "\t\t\t@Qualifier(\"targetDataSource\") DataSource targetSource,\n" +
                "\t\t\tJobRepository jobRepository,\n" +
                "\t\t\tPlatformTransactionManager transactionManager) {\n" +
                "\t\tthis.realSource = realSource;\n" +
                "\t\tthis.targetSource = targetSource;\n" +
                "\t\tthis.jobRepository = jobRepository;\n" +
                "\t\tthis.transactionManager = transactionManager;\n" +
                "\t}\n\n" +
                "\t@Bean\n" +
                "\tpublic JdbcPagingItemReader<Map<String, Object>> reader() throws Exception {\n" +
                "\t\tJdbcPagingItemReader<Map<String, Object>> reader = new JdbcPagingItemReader<>();\n" +
                "\t\treader.setDataSource(realSource);\n" +
                "\t\treader.setName(\"pagingReader\");\n" +
                "\t\treader.setQueryProvider(queryProvider());\n" +
                "\t\treader.setRowMapper((rs, rowNum) -> {\n" +
                "\t\t\tMap<String, Object> map = new HashMap<>();\n" +
                mapBuilder.toString() + "\n" +
                "\t\t\treturn map;\n" +
                "\t\t});\n" +
                "\t\treader.setPageSize(10);\n" +
                "\t\treturn reader;\n" +
                "\t}\n\n" +
                "\t@Bean\n" +
                "\tpublic PagingQueryProvider queryProvider() throws Exception {\n" +
                "\t\tSqlPagingQueryProviderFactoryBean factory = new SqlPagingQueryProviderFactoryBean();\n" +
                "\t\tfactory.setDataSource(realSource);\n" +
                "\t\tfactory.setSelectClause(\"SELECT *\");\n" +
                "\t\tfactory.setFromClause(\"FROM " + tableName + "\");\n" +
                "\t\tfactory.setSortKey(\"id\");\n" +
                "\t\treturn factory.getObject();\n" +
                "\t}\n\n" +
                "\t@Bean\n" +
                "\tpublic JdbcBatchItemWriter<Map<String, Object>> writer() {\n" +
                "\t\treturn new JdbcBatchItemWriterBuilder<Map<String, Object>>()\n" +
                "\t\t\t.dataSource(targetSource)\n" +
                "\t\t\t.sql(" + sqlBuilder.toString() + ");\n" +
                "\t\t\t.itemSqlParameterSourceProvider(item -> {\n" +
                "\t\t\t\tMapSqlParameterSource params = new MapSqlParameterSource();\n" +
                paramBuilder.toString() +
                "\t\t\t\treturn params;\n" +
                "\t\t\t})\n" +
                "\t\t\t.build();\n" +
                "\t}\n\n" +
                "\t@Bean\n" +
                "\tpublic Step step() throws Exception {\n" +
                "\t\treturn new StepBuilder(\"dbCopyStep\", jobRepository)\n" +
                "\t\t\t.<Map<String, Object>, Map<String, Object>>chunk(100, transactionManager)\n" +
                "\t\t\t.reader(reader())\n" +
                "\t\t\t.writer(writer())\n" +
                "\t\t\t.taskExecutor(taskExecutor())\n" +
                "\t\t\t.build();\n" +
                "\t}\n\n" +
                "\t@Bean\n" +
                "\tpublic Job job() throws Exception {\n" +
                "\t\treturn new JobBuilder(\"dbCopyJob\", jobRepository)\n" +
                "\t\t\t.start(step())\n" +
                "\t\t\t.build();\n" +
                "\t}\n\n" +
                "\t@Bean\n" +
                "\tpublic TaskExecutor taskExecutor() {\n" +
                "\t\tThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();\n" +
                "\t\texecutor.setCorePoolSize(4);\n" +
                "\t\texecutor.setMaxPoolSize(8);\n" +
                "\t\texecutor.setQueueCapacity(50);\n" +
                "\t\texecutor.setThreadNamePrefix(\"dbCopyTask\");\n" +
                "\t\texecutor.initialize();\n" +
                "\t\treturn executor;\n" +
                "\t}\n\n" +
                "}";
    }


    private String mapSchemaTypeToJavaType(String schemaType, String schemaFormat) {
        if (schemaType == null) {
            return "Object";
        }

        switch (schemaType) {
            case "string":
                if ("date".equals(schemaFormat)) {
                    return "java.time.LocalDate";
                } else if ("date-time".equals(schemaFormat)) {
                    return "java.time.LocalDateTime";
                } else if ("uuid".equals(schemaFormat)) {
                    return "java.util.UUID";
                } else {
                    return "String";
                }

            case "integer":
                if ("int32".equals(schemaFormat)) {
                    return "Integer";
                } else if ("int64".equals(schemaFormat)) {
                    return "Long";
                } else {
                    return "Integer"; // Default to Integer for unspecified formats
                }

            case "number":
                if ("float".equals(schemaFormat)) {
                    return "Float";
                } else if ("double".equals(schemaFormat)) {
                    return "Double";
                } else {
                    return "Double"; // Default to Double for unspecified formats
                }

            case "boolean":
                return "Boolean";

            case "array":
                return "List"; // Could be extended to include generics if items are provided

            case "object":
                return "Map"; // Could be extended for specific object definitions

            default:
                return "Object"; // Fallback for unknown types
        }
    }

}
