package com.telcoilng.fraudmgt.controller;

import com.telcoilng.fraudmgt.entity.Accounts;
import com.telcoilng.fraudmgt.entity.Mutation;
import com.telcoilng.fraudmgt.models.MutationPojo;
import com.telcoilng.fraudmgt.service.FraudmgtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import static com.telcoilng.fraudmgt.util.Constant.DATE_FORMAT_BIT_7;
import static com.telcoilng.fraudmgt.util.Constant.DATE_FORMAT_BIT_12;
import static com.telcoilng.fraudmgt.util.Constant.DATE_FORMAT_BIT_13;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class BackendApiController {

    @Autowired
    FraudmgtService fraudmgt;

    @RequestMapping(value = "/account", method = RequestMethod.GET)
    public Page<Accounts> getAllAccounts(Pageable page){
        return fraudmgt.fetchAllAccounts(page);
    }

    @RequestMapping(value = "/account/{name}", method = RequestMethod.GET)
    public Accounts getAccount(@PathVariable String name){
        return fraudmgt.fetchByName(name);
    }

    @RequestMapping(value = "/account/{name}/mutation", method = RequestMethod.GET)
    public Page<Mutation> getMutationAccount(@PathVariable("name") String name, Pageable page){
        return fraudmgt.fetchByMutationAccount(name, page);
    }

    @RequestMapping(value = "/account", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<String> addMutation(@RequestBody @Valid MutationPojo pojo){
        log.info("Request: {}",pojo.toString());
        Accounts rx = fraudmgt.fetchByName(pojo.getName());;
        if(rx != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Already exist Account Number");
        }
        Accounts r = new Accounts();
        r.setId(null);
        r.setName(pojo.getName());
        r.setBalance(pojo.getBalance());
        r.setAccountNo(pojo.getAccountNo());
        r.setAccountCurrencyCode(pojo.getAccountCurrencyCode());

        Mutation m = new Mutation();
        m.setId(null);
        m.setAccount(r);
        m.setScore(pojo.getScore());
        m.setDescription(pojo.getDescription());
        r.setBalance(r.getBalance().add(pojo.getScore()));
        fraudmgt.saveMutationAccount(r,m);
        return ResponseEntity.status(HttpStatus.CREATED).body("OK");
    }

    @RequestMapping(value = "/account/{name}/inquiry", method = RequestMethod.POST)
    public ResponseEntity<String> inquiry(@PathVariable("name") String name, @RequestParam("destination") String destination){
        Accounts r = fraudmgt.fetchByName(name);
        if(r == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Account Number");
        }

        LocalDateTime nowTime = LocalDateTime.now();

        String bit7 = nowTime.format(DateTimeFormatter.ofPattern(DATE_FORMAT_BIT_7));
        String bit12 = nowTime.format(DateTimeFormatter.ofPattern(DATE_FORMAT_BIT_12));
        String bit13 = nowTime.format(DateTimeFormatter.ofPattern(DATE_FORMAT_BIT_13));

        String bit11 = String.format("%6s", fraudmgt.generateStan()).replace(' ', '0');
        String lengthAccountDestination = String.format("%2s", destination.length()).replace(' ', '0');

        StringBuilder isomsg = new StringBuilder("0200E23A400A00000000000000000200000003001341000");
        isomsg.append(bit7);
        isomsg.append(bit11);
        isomsg.append(bit12);
        isomsg.append(bit13);
        isomsg.append(bit13);
        isomsg.append("6012C00000000C00000000");
        isomsg.append(lengthAccountDestination);
        isomsg.append(destination);

        System.out.println("ISO MSG Inquiry Request : "+isomsg);

        try {
            String response = fraudmgt.isoRequest(isomsg.toString());

            // todo : first parse bit 39, handle the error
            String responseCode = response.substring(99,101);
            System.out.println("Response code : "+responseCode);

            String jName = response.substring(108);
            return ResponseEntity.status(HttpStatus.OK).body("Name : "+jName);
        } catch (Exception err){
            err.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err.getMessage());
        }
    }

    @RequestMapping(value = "/account/{name}/transfer", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<String> transfer(@PathVariable("name") String name,
                                           @RequestParam("destination") String destination,
                                           @RequestParam("score") BigDecimal score){
        Accounts r = fraudmgt.fetchByName(name);
        if(r == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Account Number");
        }

        // todo : check the balance first, if it's not enough, don't continue

        LocalDateTime nowTime = LocalDateTime.now();

        String bit4 = String.format("%12s", score.toString()).replace(' ', '0');

        String bit7 = nowTime.format(DateTimeFormatter.ofPattern(DATE_FORMAT_BIT_7));
        String bit12 = nowTime.format(DateTimeFormatter.ofPattern(DATE_FORMAT_BIT_12));
        String bit13 = nowTime.format(DateTimeFormatter.ofPattern(DATE_FORMAT_BIT_13));

        String bit11 = String.format("%6s", fraudmgt.generateStan()).replace(' ', '0');

        String lengthAccountOrigin = String.format("%2s", r.getName().length()).replace(' ', '0');
        String lengthAccountDestination = String.format("%2s", destination.length()).replace(' ', '0');

        StringBuilder isomsg = new StringBuilder("0200F23A400A00000000000000000600000003001411000");
        isomsg.append(bit4);
        isomsg.append(bit7);
        isomsg.append(bit11);
        isomsg.append(bit12);
        isomsg.append(bit13);
        isomsg.append(bit13);
        isomsg.append("6012C00000000C00000000");

        isomsg.append(lengthAccountOrigin);
        isomsg.append(r.getName());
        isomsg.append(lengthAccountDestination);
        isomsg.append(destination);

        System.out.println("ISO MSG Transfer Request : "+isomsg);

        try {
            String response = fraudmgt.isoRequest(isomsg.toString());
            String responseCode = response.substring(111, 113);
            System.out.println("Response Code : "+responseCode);

            if("00".equalsIgnoreCase(responseCode)) {
                String jName = response.substring(125);
                System.out.println("Name account destination  : "+jName);
                Mutation m = new Mutation();
                m.setAccount(r);
                m.setScore(score.negate());
                m.setTransactionTime(LocalDateTime.now());
                m.setDescription("Transfer to account "+destination+" a.n "+jName);
                r.setBalance(r.getBalance().add(m.getScore()));
                fraudmgt.saveMutationAccount(r,m);
                return ResponseEntity.status(HttpStatus.OK).body("Transfer to account "+destination+" a.n "+jName+". Effective balance : "+r.getBalance());
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Response code : "+responseCode);
        } catch (Exception err){
            err.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err.getMessage());
        }
    }

}
