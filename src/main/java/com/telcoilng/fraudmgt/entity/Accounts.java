package com.telcoilng.fraudmgt.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Entity
@Table(name = "m_account")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString()
public class Accounts {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false)
    private Long id;

    @NotNull
    @NotEmpty
    @Size(min = 3, max = 10)
    @Column(unique = true, nullable = false)
    private String accountNo;

    @NotNull
    @NotEmpty
    @Size(min = 3, max = 255)
    @Column(unique = true, nullable = false)
    private String name;

    @NotNull
    private BigDecimal balance = BigDecimal.ZERO;

    @NotNull
    @NotEmpty
    @Size(min = 3, max = 5)
    @Column(nullable = false)
    private String accountCurrencyCode;
}
