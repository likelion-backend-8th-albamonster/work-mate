package com.example.workmate.controller.account;

import com.example.workmate.dto.account.AccountDto;
import com.example.workmate.dto.account.AccountShopDto;
import com.example.workmate.dto.shop.ShopDto;
import com.example.workmate.entity.account.AccountStatus;
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

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileRestController {
    private final UserDetailsManager manager;
    private final PasswordEncoder passwordEncoder;
    private final AccountService service;
    private final MailService mailService;

    @GetMapping
    public AccountDto readOneAccount() {
       return service.readOneAccount();
    }

    // profile 정보 가져오기
    @GetMapping("/{id}")
    public AccountDto readOneAccount(@PathVariable("id") Long id) {
        return service.readOneAccount(id);
    }

    // 정보 업데이트
    @PostMapping("/{id}/update")
    public AccountDto updateAccount(@PathVariable("id") Long id, AccountDto dto) {
        return service.updateAccount(id, dto);
    }

    // 이메일 코드를 보낸다.
    @PostMapping("/email-check")
    public void checkEmail(
            @RequestParam("username") String username,
            @RequestParam("email") String email
    ) {
        mailService.send(username, email);
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

    // 아르바이트 요청 명단 불러오기
    @GetMapping("/{id}/account-shop")
    public List<AccountShopDto> getAccountShopByAccountId(@PathVariable("id") Long id) {
        return service.getAccountShopByAccountId(id);
    }

    // 아르바이트 요청 명단에서 Shop name 불러오기
    @GetMapping("/{id}/account-shop/shop-name")
    public List<String> getShopNameByAccountShop(@PathVariable("id") Long id) {
        return service.getShopNameByAccountShop(id);
    }

    // 아르바이트 요청 명단에서 아르바이트 상태 불러오기
    @GetMapping("/{id}/account-shop/account-status")
    public List<AccountStatus> getAccountStatus(@PathVariable("id") Long id) {
        return service.getAccountStatus(id);
    }

    // shop에 아르바이트 신청
    @PostMapping("/submit")
    public AccountShopDto submit(
            @RequestBody ShopDto dto
    ) {
        return service.submit(dto.getName());
    }
}

