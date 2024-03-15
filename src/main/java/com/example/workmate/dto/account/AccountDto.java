package com.example.workmate.dto.account;

import com.example.workmate.entity.account.Account;
import com.example.workmate.entity.account.Authority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AccountDto {
    private Long id;
    private String username;
    private String password;
    private String email;
    private Authority authority;

    public static AccountDto fromEntity(Account account) {
        return AccountDto.builder()
                .id(account.getId())
                .username(account.getUsername())
                .password(account.getPassword())
                .authority(account.getAuthority())
                .build();
    }
}
