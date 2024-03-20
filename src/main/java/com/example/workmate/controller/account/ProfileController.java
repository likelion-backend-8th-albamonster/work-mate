package com.example.workmate.controller.account;

import com.example.workmate.entity.account.Account;
import com.example.workmate.repo.AccountRepo;
import com.example.workmate.service.account.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final AccountService accountService;
    private final AccountRepo accountRepo;

    // 사용자 프로필 페이지
    @GetMapping("/{id}")
    public String readOneAccount(@PathVariable("id") Long id) {
        Account account = accountService.readOneAccount(id);

        log.info(account.getName());
        log.info(account.getAccountShops().toString());

        return "my-profile";
    }
}
