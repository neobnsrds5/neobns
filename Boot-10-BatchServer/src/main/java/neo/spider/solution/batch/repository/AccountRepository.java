package neo.spider.solution.batch.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import neo.spider.solution.batch.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

}
