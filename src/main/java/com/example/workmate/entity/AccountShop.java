package com.example.workmate.entity;

import com.example.workmate.entity.account.Account;
import com.example.workmate.entity.account.AccountStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class AccountShop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @Setter
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Setter
    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;
}
