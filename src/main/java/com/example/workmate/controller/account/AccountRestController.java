package com.example.workmate.controller.account;

import com.example.workmate.jwt.JwtTokenUtils;
import com.example.workmate.jwt.dto.JwtRequestDto;
import com.example.workmate.jwt.dto.JwtResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountRestController {
    private final UserDetailsManager manager;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;

    @PostMapping("/login")
    public JwtResponseDto login(
            @RequestBody JwtRequestDto dto
    ) {
        if (!manager.userExists(dto.getUsername())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        UserDetails userDetails = manager.loadUserByUsername(dto.getUsername());
        log.info("username: {}", userDetails.getUsername());
        log.info("password: {}", userDetails.getPassword());

        if (!passwordEncoder.matches(dto.getPassword(), userDetails.getPassword())) {
            log.error("비밀번호가 일치하지 않습니다.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        JwtResponseDto response = new JwtResponseDto();
        response.setToken(jwtTokenUtils.generateToken(userDetails));
        log.info("token: {}", response.getToken());

        return response;
    }
}
