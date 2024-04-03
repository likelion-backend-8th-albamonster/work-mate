package com.example.workmate.controller.account;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ProfileController {
    @GetMapping
    public String home() {
        return "account/index";
    }

    @GetMapping("/my-profile")
    public String myProfile() {
        return "account/my-profile";
    }

    @GetMapping("/my-profile/update")
    public String profileUpdate() {
        return "account/update";
    }

    @GetMapping("/email-check")
    public String emailCheck() {
        return "account/check-email";
    }

    @GetMapping("/check-code")
    public String checkCode() {
        return "account/check-code";
    }
}
