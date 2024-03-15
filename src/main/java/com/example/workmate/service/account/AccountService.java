package com.example.workmate.service.account;

import com.example.workmate.dto.account.AccountDto;
import com.example.workmate.entity.account.Account;
import com.example.workmate.entity.account.Authority;
import com.example.workmate.facade.AuthenticationFacade;
import com.example.workmate.repo.AccountRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepo accountRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationFacade authFacade;

    // 회원가입
    public void create(AccountDto dto) {
        Account newAccount = Account.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .authority(Authority.ROLE_USER)
                .build();

        AccountDto.fromEntity(accountRepo.save(newAccount));
    }
}
