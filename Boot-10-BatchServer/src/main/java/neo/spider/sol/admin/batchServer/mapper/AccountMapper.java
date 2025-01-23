package neo.spider.sol.admin.batchServer.mapper;

import org.apache.ibatis.annotations.Mapper;

import neo.spider.sol.admin.batchServer.dto.AccountDTO;

@Mapper
public interface AccountMapper {

	void addAccount(AccountDTO dto);

	void updateAccount(AccountDTO dto);

	AccountDTO findAccountById(long id);
}
