package com.example.workmate.controller.account;

import com.example.workmate.dto.account.AccountDto;
import com.example.workmate.dto.account.AccountShopDto;
import com.example.workmate.dto.shop.ShopDto;
import com.example.workmate.entity.account.Account;
import com.example.workmate.entity.account.AccountStatus;
import com.example.workmate.facade.AuthenticationFacade;
import com.example.workmate.repo.AccountRepo;
import com.example.workmate.service.account.AccountService;
import com.example.workmate.service.account.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileRestController {
    private final AccountRepo accountRepo;
    private final AccountService service;
    private final MailService mailService;
    private final AuthenticationFacade authFacade;

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
    @PostMapping("/update")
    public AccountDto updateAccount(@RequestBody AccountDto dto) {
        return service.updateAccount(dto);
    }

    // 이메일 코드를 보낸다.
    @PostMapping("/email-check")
    public ResponseEntity<String> checkEmail(
            @RequestParam("username") String username,
            @RequestParam("email") String email
    ) {
        if (!mailService.checkInfo(username, email)) {
            log.error("아이디 또는 이메일이 일치하지 않습니다.");
            return ResponseEntity.badRequest().body("아이디 또는 이메일이 일치하지 않습니다.");
        }
        mailService.send(username, email);
        return ResponseEntity.ok("이메일을 성공적으로 보냈습니다.");
    }

    // 이메일 코드 일치하는지 체크
    @PostMapping("/check-code")
    public ResponseEntity<String> checkCode(
            @RequestParam("username") String username,
            @RequestParam("code") String code
    ) {
        Account account = accountRepo.findByUsername(authFacade.getAuth().getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        log.info("username: {}", account.getUsername());

        // 입력한 username과 auth username이 일치하는지 확인
        if (!username.equals(account.getUsername())) {
            log.error("아이디가 일치하지 않습니다.");
            return ResponseEntity.badRequest().body("아이디가 일치하지 않습니다.");
        }

        if (!mailService.checkMailAuth(username)) {
            log.error("잘못된 코드입니다.");
            return ResponseEntity.notFound().build();
        }

        if (!mailService.checkTimeLimit5L(username)) {
            log.error("시간 만료입니다.");
            return ResponseEntity.status(HttpStatus.GONE).body("인증 시간이 만료되었습니다.");
        }

        if (!mailService.checkCode(username, code)) {
            return ResponseEntity.badRequest().body("코드를 확인하세요.");
        }
        return ResponseEntity.ok("인증되었습니다.");
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

