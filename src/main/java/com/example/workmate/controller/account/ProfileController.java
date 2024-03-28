package com.example.workmate.controller.account;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {
    @GetMapping("/my-profile")
    public String myProfile() {
        return "account/my-profile";
    }
}
