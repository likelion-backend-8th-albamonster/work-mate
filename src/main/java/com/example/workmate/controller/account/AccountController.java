package com.example.workmate.controller.account;

import com.example.workmate.entity.account.Authority;
import com.example.workmate.entity.account.CustomAccountDetails;
import com.example.workmate.facade.AuthenticationFacade;
import com.example.workmate.service.account.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {
    private final UserDetailsManager manager;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationFacade authFacade;
    private final MailService mailService;

    @GetMapping("/home")
    public String home() {
        log.info(SecurityContextHolder.getContext().getAuthentication().getName());
        log.info(authFacade.getAuth().getName());

        return "account/index";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "account/login-form";
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

    @PostMapping("check-code")
    public String checkCode(
            @RequestParam("username")
            String username,
            @RequestParam("password")
            String password,
            @RequestParam("code")
            String code
    ){
        if (!manager.userExists(username)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        UserDetails userDetails = manager.loadUserByUsername(username);
        log.info("username: {}", userDetails.getUsername());
        log.info("password: {}", userDetails.getPassword());

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            log.error("비밀번호가 일치하지 않습니다.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        return mailService.checkCode(username, code);
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
