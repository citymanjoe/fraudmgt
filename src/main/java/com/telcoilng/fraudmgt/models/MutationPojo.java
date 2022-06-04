package com.telcoilng.fraudmgt.models;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString
public class MutationPojo {

    private BigDecimal score;
    private String accountNo;
    private String name;
    private BigDecimal balance;
    private String accountCurrencyCode;
    private String description;
}
