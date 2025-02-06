package neo.spider.sol.batchServer.mapper;

import org.apache.ibatis.annotations.Mapper;

import neo.spider.sol.batchServer.dto.AccountDTO;

@Mapper
public interface AccountMapper {

	void addAccount(AccountDTO dto);

	void updateAccount(AccountDTO dto);

	AccountDTO findAccountById(long id);
}
