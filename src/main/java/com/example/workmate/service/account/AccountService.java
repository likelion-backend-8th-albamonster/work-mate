package com.example.workmate.service.account;

import com.example.workmate.dto.account.AccountDto;
import com.example.workmate.entity.account.Account;
import com.example.workmate.entity.account.Authority;
import com.example.workmate.facade.AuthenticationFacade;
import com.example.workmate.repo.AccountRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepo accountRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationFacade authFacade;

    // 일반 회원가입
    public void userCreate(AccountDto dto) {
        // 사용자 id가 중복되어있는지 확인
        if (accountRepo.existsByUsername(dto.getUsername())) {
            log.error("Username already exists");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        //이메일이 중복되어 있는지 확인
        if (accountRepo.existsByEmail(dto.getEmail())) {
            log.error("Email already exists");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Account newAccount = Account.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .authority(Authority.ROLE_USER)
                .build();

        AccountDto.fromEntity(accountRepo.save(newAccount));
    }

    // 사업자 회원가입
    public void businessCreate(AccountDto dto) {
        // 사용자 id가 중복되어있는지 확인
        if (accountRepo.existsByUsername(dto.getUsername())) {
            log.error("Username already exists");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        //이메일이 중복되어 있는지 확인
        if (accountRepo.existsByEmail(dto.getEmail())) {
            log.error("Email already exists");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Account newAccount = Account.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .businessNumber(dto.getBusinessNumber())
                .authority(Authority.ROLE_BUSINESS_USER)
                .build();
        AccountDto.fromEntity(accountRepo.save(newAccount));
    }

}
