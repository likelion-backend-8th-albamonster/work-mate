package com.example.workmate.entity;

import com.example.workmate.entity.account.Account;
import jakarta.persistence.*;

@Entity
public class AccountShop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;
}
