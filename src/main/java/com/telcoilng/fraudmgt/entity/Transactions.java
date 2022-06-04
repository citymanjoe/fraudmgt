package com.telcoilng.fraudmgt.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "m_transaction")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString()
public class Transactions {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false)
    private Long id;

    @NotNull
    @NotEmpty
    @Size(min = 3, max = 10)
    @Column(nullable = false)
    private String creditAccountNo;

    @NotNull
    @NotEmpty
    @Size(min = 3, max = 10)
    @Column(nullable = false)
    private String debitAccountNo;

    @NotNull
    private BigDecimal transactionAmount = BigDecimal.ZERO;

    @NotNull
    @Column(name = "transactionDate", nullable = false)
    private LocalDate transactionDate = LocalDate.now();

    @NotNull
    @NotEmpty
    @Size(min = 3, max = 255)
    @Column(nullable = false)
    private String transactionNarration;

    @NotNull
    @NotEmpty
    @Size(min = 3, max = 5)
    @Column(nullable = false)
    private String transactionCurrencyCode;
}
