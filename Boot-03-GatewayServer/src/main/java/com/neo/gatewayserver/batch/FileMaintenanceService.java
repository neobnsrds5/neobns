package com.neo.gatewayserver.batch;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;

@Service
public class FileMaintenanceService {
	
	public void cleanupLogFile(String filePath) {
		
		try(FileWriter fileWriter = new FileWriter(filePath, false);) {
			
			fileWriter.write("");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	public long findFileLinesCount(String filePath) {

		long count = 0;
		Path file = Paths.get(filePath);
		try {
			count = Files.lines(file).count();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return count;

	}

}
