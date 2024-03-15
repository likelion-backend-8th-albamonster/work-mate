package com.example.workmate.controller.account;

import com.example.workmate.dto.account.AccountDto;
import com.example.workmate.entity.account.CustomAccountDetails;
import com.example.workmate.facade.AuthenticationFacade;
import com.example.workmate.service.account.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationFacade authFacade;
    private final ResourceLoader resourceLoader;

    @GetMapping("/home")
    public String home() {
        return "index";
    }

    // 회원가입 화면
    @GetMapping("/register")
    public String registerForm() {
        return "register-form";
    }

    @PostMapping("/register")
    public String signUpRequest(
            @RequestParam("username")
            String username,
            @RequestParam("password")
            String password,
            @RequestParam("password-check")
            String passwordCheck
    ) {
        if (password.equals(passwordCheck)) {
            accountService.create(AccountDto.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .build());

        }
        return "redirect:/account/login";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login-form";
    }
}
