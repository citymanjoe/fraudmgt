package com.telcoilng.fraudmgt.repository;

import com.telcoilng.fraudmgt.entity.Accounts;
import com.telcoilng.fraudmgt.entity.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transactions, Long> {
}
