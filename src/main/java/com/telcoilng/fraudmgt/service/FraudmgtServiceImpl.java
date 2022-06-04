package com.telcoilng.fraudmgt.service;

import com.telcoilng.fraudmgt.entity.Accounts;
import com.telcoilng.fraudmgt.entity.Mutation;
import com.telcoilng.fraudmgt.repository.AccountRepository;
import com.telcoilng.fraudmgt.repository.MutationRepository;
import com.telcoilng.fraudmgt.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

@Service
@Slf4j
public class FraudmgtServiceImpl implements FraudmgtService {

    @Value("${isoServer.host}") String host;
    @Value("${isoServer.port}") Integer port;

    @Autowired
    AccountRepository accountRepo;

    @Autowired
    MutationRepository mutationRepo;

    @Autowired
    TransactionRepository transactionRepo;

    @Override
    public Page<Accounts> fetchAllAccounts(Pageable page) {
        return accountRepo.findAll(page);
    }

    @Override
    public Accounts fetchByName(String name) {
        return accountRepo.findByName(name);
    }

    @Override
    public Page<Mutation> fetchByMutationAccount(String name, Pageable page) {
        Accounts account = accountRepo.findByName(name);
        if(account == null){
            return null;
        }
        return mutationRepo.findByAccount(account,page);
    }

    @Override
    public void saveMutationAccount(Accounts account, Mutation mutta) {
        log.info("Account Info: {}", account.toString());
        log.info("Mutation Info: {}", mutta.toString());
        Accounts acct = accountRepo.save(account);
        log.info("Account ID: ", acct.getId());
        mutta.setAccount(acct);
        mutationRepo.save(mutta);
    }

    @Override
    public Integer generateStan(){
        return new Random().nextInt(999999);
    }

    @Override
    public String isoRequest(String message) throws Exception {
        String length = String.format("%4s", message.length()).replace(' ', '0');

        String responseData;
        try (Socket clientSocket = new Socket(host, port)) {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            System.out.println("Sending : "+length+message);
            out.println(length + message);
            char[] responseLengthChar = new char[4];
            in.read(responseLengthChar);
            String responseLengthStr = new String(responseLengthChar);
            System.out.println("Response length : "+responseLengthStr);
            Integer responseLength = Integer.valueOf(responseLengthStr);
            char[] responseDataChar = new char[responseLength];
            in.read(responseDataChar);
            responseData = new String(responseDataChar);
            System.out.println("Response Data : "+responseData);
            out.close();
            in.close();
        }

        return responseData;
    }
}
