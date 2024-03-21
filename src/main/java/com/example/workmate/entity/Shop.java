package com.example.workmate.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Setter
    private String name;
    @Setter
    private String address;

    @Setter
    @OneToMany(mappedBy = "shop")
    private List<AccountShop> accountShops;
}
