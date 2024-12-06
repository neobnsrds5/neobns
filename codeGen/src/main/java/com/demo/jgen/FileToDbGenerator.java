package com.demo.jgen;

import io.swagger.v3.oas.models.media.Schema;

import java.io.IOException;
import java.util.Map;

public class FileToDbGenerator implements BaseCodeGenerator {

    @Override
    public void generateCode(String packageName, String resourceName, String packageDir, Schema schema) throws IOException {
        // 생성할 파일 내용
        String content = generateClassContent(packageName, resourceName, schema);

        // 파일로 작성
        writeToFile(packageDir + resourceName + "FileToDbBatch.java", content);
    }

    private String generateClassContent(String packageName, String className, Schema schema) {
        StringBuilder tokenizerBuilder = new StringBuilder();
        StringBuilder sqlBuilder = new StringBuilder();
        Map<String, Schema> properties = schema.getProperties();

        // CSV 토큰 생성
        for (Map.Entry<String, Schema> entry : properties.entrySet()) {
            String fieldName = entry.getKey();
            tokenizerBuilder.append("\"").append(fieldName).append("\", ");
        }
        tokenizerBuilder.setLength(tokenizerBuilder.length() - 2); // 마지막 콤마 제거

        // SQL 필드 생성
        sqlBuilder.append("INSERT INTO ").append(className.toLowerCase())
                .append(" (").append(String.join(", ", properties.keySet()))
                .append(") VALUES (:").append(String.join(", :", properties.keySet())).append(")");

        return "package " + packageName + ";\n\n" +
                "import javax.sql.DataSource;\n" +
                "import org.springframework.batch.core.Job;\n" +
                "import org.springframework.batch.core.Step;\n" +
                "import org.springframework.batch.core.job.builder.JobBuilder;\n" +
                "import org.springframework.batch.core.repository.JobRepository;\n" +
                "import org.springframework.batch.core.step.builder.StepBuilder;\n" +
                "import org.springframework.batch.item.file.FlatFileItemReader;\n" +
                "import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;\n" +
                "import org.springframework.batch.item.file.mapping.DefaultLineMapper;\n" +
                "import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;\n" +
                "import org.springframework.batch.item.database.JdbcBatchItemWriter;\n" +
                "import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;\n" +
                "import org.springframework.beans.factory.annotation.Qualifier;\n" +
                "import org.springframework.context.annotation.Bean;\n" +
                "import org.springframework.context.annotation.Configuration;\n" +
                "import org.springframework.core.io.FileSystemResource;\n" +
                "import org.springframework.transaction.PlatformTransactionManager;\n" +
                "import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;\n" +
                "import org.springframework.core.task.TaskExecutor;\n\n" +
                "@Configuration\n" +
                "public class " + className + "FileToDbBatch {\n\n" +
                "\tprivate final DataSource dataSource;\n" +
                "\tprivate final JobRepository jobRepository;\n" +
                "\tprivate final PlatformTransactionManager transactionManager;\n\n" +
                "\tpublic " + className + "FileToDbBatch(@Qualifier(\"targetDataSource\") DataSource dataSource,\n" +
                "\t\t\tJobRepository jobRepository,\n" +
                "\t\t\tPlatformTransactionManager transactionManager) {\n" +
                "\t\tthis.dataSource = dataSource;\n" +
                "\t\tthis.jobRepository = jobRepository;\n" +
                "\t\tthis.transactionManager = transactionManager;\n" +
                "\t}\n\n" +
                "\t@Bean\n" +
                "\tpublic Job fileToDbJob() {\n" +
                "\t\treturn new JobBuilder(\"fileToDbJob\", jobRepository)\n" +
                "\t\t\t.start(fileToDbStep())\n" +
                "\t\t\t.build();\n" +
                "\t}\n\n" +
                "\t@Bean\n" +
                "\tpublic Step fileToDbStep() {\n" +
                "\t\treturn new StepBuilder(\"fileToDbStep\", jobRepository)\n" +
                "\t\t\t.<" + className + "Dto, " + className + "Dto>chunk(100, transactionManager)\n" +
                "\t\t\t.reader(fileReader())\n" +
                "\t\t\t.writer(fileToDbWriter())\n" +
                "\t\t\t.taskExecutor(taskExecutor())\n" +
                "\t\t\t.build();\n" +
                "\t}\n\n" +
                "\t@Bean\n" +
                "\tpublic FlatFileItemReader<" +className +"Dto> fileReader() {\n" +
                "\t\tFlatFileItemReader<" +className + "Dto> reader = new FlatFileItemReader<>();\n" +
                "\t\treader.setResource(new FileSystemResource(\"파일 경로\"));\n" +
                "\t\treader.setLinesToSkip(1);\n" +
                "\t\tDefaultLineMapper<" + className + "Dtp> lineMapper = new DefaultLineMapper<>();\n" +
                "\t\tDelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();\n" +
                "\t\ttokenizer.setNames(" + tokenizerBuilder.toString() + ");\n" +
                "\t\tlineMapper.setLineTokenizer(tokenizer);\n" +
                "\t\tBeanWrapperFieldSetMapper<" + className + "Dto> fieldSetMapper = new BeanWrapperFieldSetMapper<>();\n" +
                "\t\tfieldSetMapper.setTargetType(" + className +  "Dto.class);\n" +
                "\t\tlineMapper.setFieldSetMapper(fieldSetMapper);\n" +
                "\t\treader.setLineMapper(lineMapper);\n" +
                "\t\treturn reader;\n" +
                "\t}\n\n" +
                "\t@Bean\n" +
                "\tpublic JdbcBatchItemWriter<" + className + "Dto> fileToDbWriter() {\n" +
                "\t\treturn new JdbcBatchItemWriterBuilder<Object>()\n" +
                "\t\t\t.dataSource(dataSource)\n" +
                "\t\t\t.sql(\"" + sqlBuilder.toString() + "\")\n" +
                "\t\t\t.beanMapped()\n" +
                "\t\t\t.build();\n" +
                "\t}\n\n" +
                "\t@Bean\n" +
                "\tpublic TaskExecutor taskExecutor() {\n" +
                "\t\tThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();\n" +
                "\t\texecutor.setCorePoolSize(4);\n" +
                "\t\texecutor.setMaxPoolSize(8);\n" +
                "\t\texecutor.setQueueCapacity(50);\n" +
                "\t\texecutor.setThreadNamePrefix(\"fileToDbTask\");\n" +
                "\t\texecutor.initialize();\n" +
                "\t\treturn executor;\n" +
                "\t}\n" +
                "}";
    }
}
