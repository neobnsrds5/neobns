package neo.spider.sol.admin.batchServer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import neo.spider.sol.admin.batchServer.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

}
