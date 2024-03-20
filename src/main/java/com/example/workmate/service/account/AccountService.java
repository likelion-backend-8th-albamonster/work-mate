package com.example.workmate.service.account;

import com.example.workmate.entity.account.Account;
import com.example.workmate.facade.AuthenticationFacade;
import com.example.workmate.repo.AccountRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepo accountRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationFacade authFacade;

    // 유저 정보 가져오기
    public Account readOneAccount(Long id) {
        Optional<Account> optionalAccount = accountRepo.findById(id);
        if (optionalAccount.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Account account = optionalAccount.get();

        log.info(account.toString());
        return account;
    }
}
