package com.example.workmate.dto.shop;

import com.example.workmate.entity.AccountShop;
import com.example.workmate.entity.Shop;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ShopDto {
    private Long id;
    private String name;
    private String address;
    private List<Long> accountShopsId;

    public static ShopDto fromEntity(Shop shop) {
        ShopDtoBuilder builder = ShopDto.builder()
                .id(shop.getId())
                .name(shop.getName())
                .address(shop.getAddress());

        if (shop.getAccountShops() != null) {
            builder.accountShopsId(shop.getAccountShops().stream().map(AccountShop::getId).toList());
        } else {
            builder.accountShopsId(Collections.emptyList());
        }

        return builder.build();
    }
}
