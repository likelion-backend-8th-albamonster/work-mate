package com.example.workmate.dto.account;

import com.example.workmate.entity.account.Account;
import com.example.workmate.entity.account.Authority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@AllArgsConstructor
public class AccountDto {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String businessNumber;
    @Setter
    private Authority authority;

    public static AccountDto fromEntity(Account account) {
        return AccountDto.builder()
                .id(account.getId())
                .username(account.getUsername())
                .password(account.getPassword())
                .businessNumber(account.getBusinessNumber())
                .authority(account.getAuthority())
                .build();
    }
}
