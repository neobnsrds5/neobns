package com.neobns.accounts.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter @ToString @AllArgsConstructor @NoArgsConstructor
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_account_number", referencedColumnName = "account_number", nullable = false)
    private Accounts fromAccount;

    @ManyToOne
    @JoinColumn(name = "to_account_number", referencedColumnName = "account_number", nullable = false)
    private Accounts toAccount;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private LocalDateTime transferDate;

}
