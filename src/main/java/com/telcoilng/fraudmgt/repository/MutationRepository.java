package com.telcoilng.fraudmgt.repository;

import com.telcoilng.fraudmgt.entity.Accounts;
import com.telcoilng.fraudmgt.entity.Mutation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MutationRepository extends JpaRepository<Mutation, Long> {
    Page<Mutation> findByAccount(Accounts r, Pageable page);
}
