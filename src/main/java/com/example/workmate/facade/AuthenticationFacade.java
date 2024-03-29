package com.example.workmate.facade;

import com.example.workmate.entity.account.Account;
import com.example.workmate.entity.account.CustomAccountDetails;
import com.example.workmate.repo.AccountRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.net.http.HttpHeaders;
import java.security.Principal;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationFacade {
    private final AccountRepo accountRepo;

    public Authentication getAuth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public Account getAccount() {
        Authentication authentication = getAuth();
        log.info("auth = {}", authentication);
        if (authentication.getPrincipal() instanceof CustomAccountDetails) {
            CustomAccountDetails customAccountDetails = (CustomAccountDetails) authentication.getPrincipal();
            return accountRepo.findByUsername(customAccountDetails.getUsername())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        }else {
            String name = authentication.getName();
            log.info("name = {}", name);
            Account a =  accountRepo.findByEmail(name)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));


            log.info("a = {}", a);
            return a;
        }
    }
}
