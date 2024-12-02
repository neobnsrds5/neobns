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
		log.info("Service....");
		System.out.println("clearing and updating the cache");
		mapper.addItem(itemDto);
		return true;
	}

	@Cacheable(value = "item:id", key = "#id")
	public ItemDto getItemById(String id) {
		System.out.println("getItemById : " + id + " 실행됨~");
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
//
//	@CachePut(value = "account:id", key = "#dto.id")
//	public AccountDTO updateAccount(AccountDTO dto) {
//		// TODO Auto-generated method stub
//		System.out.println("update account service : " + dto);
//		mapper.updateAccount(dto);
//		return dto;
//	}
//
//	@Cacheable(value = "account:id", key = "#id")
//	public AccountDTO getAccountById(long id) {
//		System.out.println("getAccountById : " + id + " 실행됨~");
//		AccountDTO accountDTO = mapper.findAccountById(id);
//		return accountDTO;
//	}
	
	@Scheduled(fixedRate = 100_000)
	@CacheEvict(value = "account:id", allEntries = true)
	public void clearAllAccountCache() {
		System.out.println("All account cash evicted. fixedRate = 10000");
		
	}
	
	@CachePut(value = "account:id", key = "#account.id")
	public Account addUpdateAccountJPA(Account account) {
		Account returnAccount = accountRepository.save(account);
		System.out.println("return account : " + returnAccount);
		return returnAccount;
		
	}

	@Cacheable(value = "account:id", key = "#id")
	public Account getAccountByIdJPA(long id) {
		return accountRepository.findById(id).get();
	}
}
