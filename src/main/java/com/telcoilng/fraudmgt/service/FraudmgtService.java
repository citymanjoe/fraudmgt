package com.telcoilng.fraudmgt.service;

import com.telcoilng.fraudmgt.entity.Accounts;
import com.telcoilng.fraudmgt.entity.Mutation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FraudmgtService {

    Page<Accounts> fetchAllAccounts(Pageable page);

    Accounts fetchByName(String name);

    Page<Mutation> fetchByMutationAccount(String name, Pageable page);

    void saveMutationAccount(Accounts account, Mutation mutta);

    Integer generateStan();

    String isoRequest(String message) throws Exception;
}
