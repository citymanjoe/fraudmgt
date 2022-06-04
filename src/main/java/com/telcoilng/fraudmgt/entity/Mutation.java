package com.telcoilng.fraudmgt.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "m_mutation")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString()
public class Mutation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, nullable = false)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_account", nullable = false)
    private Accounts account;

    @NotNull
    @Column(name = "transactionTime", nullable = false)
    private LocalDateTime transactionTime = LocalDateTime.now();

    @NotNull
    @Column(nullable = false)
    private BigDecimal score;

    @NotNull
    @NotEmpty
    @Size(min = 3, max = 255)
    private String description;
}
