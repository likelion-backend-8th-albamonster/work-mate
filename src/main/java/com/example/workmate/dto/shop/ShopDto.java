package com.example.workmate.dto.shop;

import com.example.workmate.entity.AccountShop;
import com.example.workmate.entity.Shop;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ShopDto {
    private Long id;
    private String name;
    private String address;
    private List<AccountShop> accountShops;

    public static ShopDto fromEntity(Shop shop) {
        return ShopDto.builder()
                .id(shop.getId())
                .name(shop.getName())
                .address(shop.getAddress())
                .accountShops(shop.getAccountShops())
                .build();
    }
}
