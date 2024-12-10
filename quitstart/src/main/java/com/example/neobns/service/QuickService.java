package com.example.neobns.service;

import com.example.neobns.dto.AccountDTO;
import com.example.neobns.dto.ItemDto;
import com.example.neobns.entity.Account;
import com.example.neobns.mapper.QuickMapper;
import com.example.neobns.repository.AccountRepository;
import com.example.neobns.repository.TransferRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuickService {

	private final QuickMapper mapper;
	private final AccountRepository accountRepository;
	private final TransferRepository transferRepository;
	private final CacheManager cacheManager;

	@CachePut(value = "item:id", key = "#itemDto.id")
	public boolean registerItem(ItemDto itemDto) {
		mapper.addItem(itemDto);
		return true;
	}

	@Cacheable(value = "item:id", key = "#id")
	public ItemDto getItemById(String id) {
		ItemDto itemDto = mapper.findById(id);
		return itemDto;
	}
	
	@Cacheable(value = "item:id")
	public List<ItemDto> getAll() {
		List<ItemDto> results = mapper.findAll();
		return results;
	}

//	@CachePut(value = "account:id", key = "#dto.id")
	public void addAccount(AccountDTO dto) {
		// TODO Auto-generated method stub
		mapper.addAccount(dto);
	}
	
	@Scheduled(fixedRate = 100_000)
	@CacheEvict(value = "account:id", allEntries = true)
	public void clearAllAccountCache() {
		System.out.println("All account cash evicted. fixedRate = 10000");
		
	}
	
	@CachePut(value = "account:id", key = "#account.id")
	public Account addUpdateAccountJPA(Account account) {
		Account returnAccount = accountRepository.save(account);
		return returnAccount;
		
	}

	@Cacheable(value = "account:id", key = "#id")
	public Account getAccountByIdJPA(long id) {
		return accountRepository.findById(id).get();
	}
}
