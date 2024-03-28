package com.example.workmate.controller.account;

import com.example.workmate.dto.account.AccountDto;
import com.example.workmate.dto.account.AccountShopDto;
import com.example.workmate.dto.shop.ShopDto;
import com.example.workmate.service.account.AccountService;
import com.example.workmate.service.account.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileRestController {
    private final UserDetailsManager manager;
    private final PasswordEncoder passwordEncoder;
    private final AccountService service;
    private final MailService mailService;

    // profile 정보 가져오기
    @GetMapping
    public AccountDto readOneAccount() {
       return service.readOneAccount();
    }

    @GetMapping("/{id}")
    public String readOneAccountShop(@PathVariable("id") Long id) {
        AccountShopDto accountShopDto = AccountShopDto.fromEntity(service.getAccountShop(id));
        return service.ShopName(accountShopDto.getId());
    }

    // 정보 업데이트
    @PostMapping("/{id}/update")
    public AccountDto updateAccount(@PathVariable("id") Long id, AccountDto dto) {
        return service.updateAccount(id, dto);
    }

    // 이메일 코드를 보낸다.
    @PostMapping("/email-check")
    public String checkEmail(
            @RequestParam("username") String username,
            @RequestParam("email") String email
    ) {
        mailService.send(username, email);
        return "send code";
    }

    // 이메일 코드 일치하는지 체크
    @PostMapping("/check-code")
    public String checkCode(
            @RequestParam("password") String password,
            @RequestParam("code") String code
    ) {
        AccountDto accountDto = service.readOneAccount();

        UserDetails userDetails = manager.loadUserByUsername(accountDto.getUsername());
        log.info("username: {}", userDetails.getUsername());
        log.info("password: {}", userDetails.getPassword());

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            log.error("비밀번호가 일치하지 않습니다.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        return mailService.checkCode(accountDto.getUsername(), code);
    }

    // shop에 아르바이트 신청
    @PostMapping("/submit")
    public String submit(
            @RequestParam("name") String name
    ) {
        return String.format("Status: %s",service.submit(name).toString());
    }

    @PostMapping("/accept/{accountShopId}")
    public String accept(
            @PathVariable("accountShopId") Long accountShopId,
            @RequestParam("flag") boolean flag
    ) {
        return String.format("Status: %s", service.accept(accountShopId, flag));
    }
}

