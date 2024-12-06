package com.demo.jgen;

import io.swagger.v3.oas.models.media.Schema;

import java.io.IOException;

public class ScheduleGenerator implements BaseCodeGenerator {

    @Override
    public void generateCode(String packageName, String resourceName, String packageDir, Schema schema) throws IOException {
        String serviceCode = """
                package %s;

                import java.text.SimpleDateFormat;
                import java.util.Date;
                
                import org.springframework.batch.core.JobParameters;
                import org.springframework.batch.core.JobParametersBuilder;
                import org.springframework.batch.core.configuration.JobRegistry;
                import org.springframework.batch.core.launch.JobLauncher;
                import org.springframework.context.annotation.Configuration;
                import org.springframework.scheduling.annotation.Scheduled;

                import lombok.RequiredArgsConstructor;
                
                @Configuration
                @RequiredArgsConstructor
                public class ToRestSchedule {

                    private final JobLauncher jobLauncher;
                    private final JobRegistry jobRegistry;
                    
                    @Scheduled(cron = "* * 0 * * * ", zone = "Asia/Seoul")
                    public void runTransferSchedule() throws Exception {
                     	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
                     	String date = dateFormat.format(new Date());
                     		
                     	JobParameters jobParameters = new JobParametersBuilder()
                     			.addString("date", date)
                     			.toJobParameters();
                     		
                        jobLauncher.run(jobRegistry.getJob("수행할 Job이름"), jobParameters);
                    }
                    
                }
                """;

        writeToFile(packageDir + "ToRestSchedule.java", serviceCode);
    }
}
