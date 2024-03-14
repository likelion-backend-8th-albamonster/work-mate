package com.example.workmate.entity;

import jakarta.persistence.*;


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