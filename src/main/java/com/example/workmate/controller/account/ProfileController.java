package com.example.workmate.controller.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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

}
