package com.example.workmate.dto.account;

import com.example.workmate.entity.AccountShop;
import com.example.workmate.entity.Shop;
import com.example.workmate.entity.account.Account;
import com.example.workmate.entity.account.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AccountShopDto {
    private Long id;
    private AccountStatus status;
    private Long accountId;
    private Long shopId;

    public static AccountShopDto fromEntity(AccountShop accountShop) {
        return AccountShopDto.builder()
                .id(accountShop.getId())
                .status(accountShop.getStatus())
                .accountId(accountShop.getAccount().getId())
                .shopId(accountShop.getShop().getId())
                .build();
    }
}
