package com.telcoilng.fraudmgt.repository;

import com.telcoilng.fraudmgt.entity.Accounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Accounts, Long> {
    Accounts findByName(String name);
}
