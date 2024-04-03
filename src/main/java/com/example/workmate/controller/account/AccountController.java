package com.example.workmate.controller.account;

import com.example.workmate.entity.account.Authority;
import com.example.workmate.entity.account.CustomAccountDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {
    private final UserDetailsManager manager;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginForm() {
        return "account/login-form";
    }

    @GetMapping("/logout")
    public String logout() {
        return "account/logout";
    }

    @GetMapping("/oauth")
    public String oauth() {
        return "account/oauth-login";
    }
    // 회원가입 화면
    @GetMapping("/register")
    public String registerForm() {
        return "account/register";
    }

    @GetMapping("/users-register")
    public String userRegisterForm() {
        return "account/user-register-form";
    }

    @PostMapping("/users-register")
    public String userSignUpRequest(
            @RequestParam("username")
            String username,
            @RequestParam("password")
            String password,
            @RequestParam("password-check")
            String passwordCheck,
            @RequestParam("name")
            String name,
            @RequestParam("email")
            String email
    ) {
        if (password.equals(passwordCheck)) {
            manager.createUser(CustomAccountDetails.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .name(name)
                    .email(email)
                    .authority(Authority.ROLE_INACTIVE_USER)
                    .build());
        }
        return "redirect:/account/login";
    }

    @GetMapping("/business-register")
    public String businessRegisterForm() {
        return "account/business-register-form";
    }

    @PostMapping("/business-register")
    public String businessSignUpRequest(
            @RequestParam("username")
            String username,
            @RequestParam("password")
            String password,
            @RequestParam("password-check")
            String passwordCheck,
            @RequestParam("name")
            String name,
            @RequestParam("email")
            String email,
            @RequestParam("business-number")
            String businessNumber
    ) {
        if (password.equals(passwordCheck)) {
            manager.createUser(CustomAccountDetails.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .name(name)
                    .email(email)
                    .businessNumber(businessNumber)
                    .authority(Authority.ROLE_INACTIVE_USER)
                    .build());
        }
        return "redirect:/account/login";
    }
}
