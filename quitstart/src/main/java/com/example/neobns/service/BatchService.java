package com.example.neobns.service;

import java.util.List;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import com.example.neobns.batch.SpiderTransaction;
import com.example.neobns.dto.AccountDTO;

@Service
public class BatchService {

	@SpiderTransaction(propagation = Propagation.REQUIRED, timeout = 1)
	public void writeAccounts(Chunk<? extends AccountDTO> items, JdbcBatchItemWriter<AccountDTO> writer)
			throws Exception {
		
		writer.write(items);
		Thread.sleep(2000);

	}

}
