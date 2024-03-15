package com.example.workmate.entity;

import com.example.workmate.entity.account.Account;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String address;

    @OneToMany(mappedBy = "shop")
    private List<AccountShop> accountShops;
}
