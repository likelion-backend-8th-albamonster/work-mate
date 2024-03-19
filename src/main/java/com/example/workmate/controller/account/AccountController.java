package com.example.workmate.controller.account;

import com.example.workmate.entity.account.Authority;
import com.example.workmate.entity.account.CustomAccountDetails;
import com.example.workmate.facade.AuthenticationFacade;
import com.example.workmate.service.account.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService service;
    private final UserDetailsManager manager;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationFacade authFacade;

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

    @PostMapping("/login")
    public String login(
            @RequestParam("username") String username,
            @RequestParam("password") String password
    ) {
        if (service.checkLogin(username, password)) {

            return "my-profile";
        }
        return "redirect:/account/login";
    }

    @GetMapping("/my-profile")
    public String myProfile(
            Authentication authentication,
            Model model
    ) {
        model.addAttribute("username", authentication.getName());
        log.info(authentication.getName());
        log.info(((CustomAccountDetails) authentication.getPrincipal()).getPassword());
        log.info(((CustomAccountDetails) authentication.getPrincipal()).getEmail());
        return "my-profile";
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
                    .authority(Authority.ROLE_USER)
                    .build());
        }
        return "redirect:/account/login";
    }

    @GetMapping("/business-register")
    public String businessRegisterForm() {
        return "business-register-form";
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
                    .authority(Authority.ROLE_BUSINESS_USER)
                    .build());
        }
        return "redirect:/account/login";
    }


}
