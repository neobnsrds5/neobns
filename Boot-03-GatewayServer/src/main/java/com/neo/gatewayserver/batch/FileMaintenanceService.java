package com.neo.gatewayserver.batch;

import java.io.FileWriter;
import java.io.IOException;

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

}
