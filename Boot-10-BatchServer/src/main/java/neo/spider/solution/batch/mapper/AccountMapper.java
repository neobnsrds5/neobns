package neo.spider.solution.batch.mapper;

import org.apache.ibatis.annotations.Mapper;

import neo.spider.solution.batch.dto.AccountDTO;

@Mapper
public interface AccountMapper {

	void addAccount(AccountDTO dto);

	void updateAccount(AccountDTO dto);

	AccountDTO findAccountById(long id);
}
