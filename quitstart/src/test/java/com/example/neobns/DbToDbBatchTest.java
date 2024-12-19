package com.example.neobns;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.example.neobns.config.TargetDbConfig;
import com.example.neobns.dto.AccountDTO;
import com.example.neobns.mapper.TargetMapper;

@SpringBootTest
@Import(TargetDbConfig.class)
public class DbToDbBatchTest {
	
	@Autowired
	private JobLauncher jobLauncher;
	@Autowired
	private JobRegistry jobRegistry;
	
	@Autowired
	private TargetMapper targetMapper;
	
	@BeforeEach
	public void before() {
		
		//db3 account 조회
		
		List<AccountDTO> result1 = targetMapper.findAllAccounts();
		System.out.println();
		System.out.println("===============================db3 : before db to db==================================");
		System.out.println();
		for (int i = 0; i < result1.size(); i++) {
			
			System.out.println(result1.get(i).toString());
			
		}
		System.out.println();
	}
	
	@AfterEach
	public void after() {
		//db3 account 조회
		
		List<AccountDTO> result2 = targetMapper.findAllAccounts();
		System.out.println();
		System.out.println("===============================db3 : after db to db==================================");
		System.out.println();
		for (int i = 0; i < result2.size(); i++) {
			
			System.out.println(result2.get(i).toString());
			
		}
		System.out.println();
	}
	
	@Test
	public void dbToDb() throws Exception{
		System.out.println();
		System.out.println("===============================db to db 시작==================================");
		System.out.println();
		MDC.put("userId", "testBatch");
		String uniqVal = String.valueOf(System.currentTimeMillis());
		JobParameters jobParameters = new JobParametersBuilder().addString("dbtodb", uniqVal).toJobParameters();
		jobLauncher.run(jobRegistry.getJob("dbCopyJob"), jobParameters);	
		MDC.remove("userId");
		System.out.println();
		System.out.println("===============================db to db 끝==================================");
		System.out.println();
	}

}
