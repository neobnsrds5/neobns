package neo.spider.sol.batchServer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import neo.spider.sol.batchServer.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

}
