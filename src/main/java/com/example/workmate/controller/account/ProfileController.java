package com.example.workmate.controller.account;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ProfileController {

    @GetMapping("/my-profile")
    public String myProfile() {
        return "account/my-profile";
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
