package com.example.workmate.service.account;

import com.example.workmate.dto.account.AccountDto;
import com.example.workmate.entity.account.Account;
import com.example.workmate.entity.account.Authority;
import com.example.workmate.entity.account.CustomAccountDetails;
import com.example.workmate.facade.AuthenticationFacade;
import com.example.workmate.repo.AccountRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    private final UserDetailsManager manager;
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

        dto.setAuthority(Authority.ROLE_USER);
        UserDetails newAccount = buildUserDetails(dto);
        manager.createUser(newAccount);
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

        dto.setAuthority(Authority.ROLE_BUSINESS_USER);
        UserDetails newAccount = buildUserDetails(dto);
        manager.createUser(newAccount);
    }

    // AccountDto를 UserDetails로 변환하는 메서드
    private UserDetails buildUserDetails(AccountDto dto) {
        return CustomAccountDetails.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword())) // 패스워드 인코딩 필요
                .email(dto.getEmail())
                .businessNumber(dto.getBusinessNumber())
                .authority(dto.getAuthority())
                .build();
    }
}
