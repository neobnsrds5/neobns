package neo.spider.sol.admin.batchServer.controller;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import neo.spider.sol.admin.batchServer.service.FileMaintenanceService;

@RestController
@RequiredArgsConstructor
public class BatchController {
	private final JobLauncher jobLauncher;
	private final JobRegistry jobRegistry;
	private final FileMaintenanceService fileMaintenanceService;
	private final String quickPath = "../logs/application.log";
	private final String gatewayPath = "../logs/gateway-application.log";
	private final String accountsPath = "../logs/accounts-application.log";

	@GetMapping("/batch/dbtoapi/{value}")
	public String firstApi(@PathVariable("value") String value) throws Exception {

		String uniqVal = value + System.currentTimeMillis();

		JobParameters jobParameters = new JobParametersBuilder().addString("dbtoapi", uniqVal).toJobParameters();

		jobLauncher.run(jobRegistry.getJob("dbToApiJob"), jobParameters);
		return "OK";
	}

	@GetMapping("/batch/dbtodb/{value}")
	public String dbtodbTest(@PathVariable("value") String value) throws Exception {

		String uniqVal = value + System.currentTimeMillis();

		JobParameters jobParameters = new JobParametersBuilder().addString("dbtodb", uniqVal).toJobParameters();
		jobLauncher.run(jobRegistry.getJob("dbCopyJob"), jobParameters);
		return "OK";
	}

	@GetMapping("/batch/filetodb/{value}")
	public String fileToDbTest(@PathVariable("value") String value) {

		String uniqVal = value + System.currentTimeMillis();

		JobParameters jobParameters = new JobParametersBuilder().addString("filetodb", uniqVal).toJobParameters();

		try {
			jobLauncher.run(jobRegistry.getJob("fileToDBJob"), jobParameters);
			return "OK";
		} catch (Exception e) {
			e.printStackTrace();
			return "FAIL";
		}
	}

	@GetMapping("/batch/logtodb/{value}")
	public String logToDbTest(@PathVariable("value") String value) {

		String uniqVal = value + System.currentTimeMillis();
		String filePath;

		if (value.equalsIgnoreCase("quick")) {
			filePath = quickPath;
		} else if (value.equalsIgnoreCase("gate")) {
			filePath = gatewayPath;
		} else if (value.equalsIgnoreCase("accounts")) {
			filePath = accountsPath;
		} else {
			return "FAIL";
		}

		JobParameters jobParameters = new JobParametersBuilder().addString("logtodb", uniqVal)
				.addString("filePath", filePath).toJobParameters();

		try {

			jobLauncher.run(jobRegistry.getJob("logToDBJob"), jobParameters);
			fileMaintenanceService.cleanupLogFile(filePath);
			return "OK";
		} catch (Exception e) {
			e.printStackTrace();
			return "FAIL";
		}
	}

	@GetMapping("/batch/parentbatch/{value}")
	public String parentBatchTest(@PathVariable("value") String value) {

		String uniqVal = value + System.currentTimeMillis();

		JobParameters jobParameters = new JobParametersBuilder().addString("parentBatch", uniqVal).toJobParameters();

		try {
			jobLauncher.run(jobRegistry.getJob("parentBatchJob"), jobParameters);
			return "OK";
		} catch (Exception e) {
			return "FAIL";
		}
	}

}
