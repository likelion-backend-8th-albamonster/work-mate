package com.example.workmate.controller.account;

import com.example.workmate.dto.account.AccountDto;
import com.example.workmate.service.account.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/profile/{id}")
@RequiredArgsConstructor
public class ProfileController {
    private final AccountService service;

    // profile 정보 가져오기
    @GetMapping
    public AccountDto readOneAccount(@PathVariable("id") Long id) {
       return service.readOneAccount(id);
    }

    // shop에 아르바이트 신청
    @PostMapping("/submit")
    public String submit(
            @PathVariable("id") Long id,
            @RequestParam("name") String name
    ) {
        return String.format("%d Status: %s", id, service.submit(id,name).toString());
    }

    @PostMapping("/accept/{accountShopId}")
    public String accept(
            @PathVariable("id") Long id,
            @PathVariable("accountShopId") Long accountShopId,
            @RequestParam("flag") boolean flag
    ) {
        return String.format("%d Status: %s", id, service.accept(id, accountShopId, flag));
    }
}

