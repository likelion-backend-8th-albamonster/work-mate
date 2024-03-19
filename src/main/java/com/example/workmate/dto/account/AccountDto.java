package com.example.workmate.dto.account;

import com.example.workmate.entity.AccountShop;
import com.example.workmate.entity.Shop;
import com.example.workmate.entity.account.Account;
import com.example.workmate.entity.account.Authority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
public class AccountDto {
    private Long id;
    private String username;
    private String password;
    private String name;
    private String email;
    private String businessNumber;
    @Setter
    private Authority authority;
    private List<AccountShop> accountShops;

    public static AccountDto fromEntity(Account account) {
        return AccountDto.builder()
                .id(account.getId())
                .username(account.getUsername())
                .password(account.getPassword())
                .name(account.getName())
                .email(account.getEmail())
                .businessNumber(account.getBusinessNumber())
                .accountShops(account.getAccountShops())
                .authority(account.getAuthority())
                .build();
    }
}
