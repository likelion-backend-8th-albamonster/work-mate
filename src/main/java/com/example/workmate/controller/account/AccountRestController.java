package com.example.workmate.controller.account;

import com.example.workmate.jwt.JwtTokenUtils;
import com.example.workmate.jwt.dto.JwtRequestDto;
import com.example.workmate.jwt.dto.JwtResponseDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<JwtResponseDto> login(
            @RequestBody JwtRequestDto dto,
            HttpServletResponse response
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

        String token = jwtTokenUtils.generateToken(userDetails);
        log.info("token: {}", token);

        // 쿠키에 토큰 저장
        Cookie cookie = new Cookie("jwtToken", token);
        cookie.setMaxAge(24 * 60 * 60); // 쿠키의 만료 시간 설정 (예: 24시간)
        cookie.setPath("/"); // 쿠키의 경로 설정
        cookie.setDomain("localhost");
        cookie.setSecure(false);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        JwtResponseDto responseDto = new JwtResponseDto();
        responseDto.setToken(token);

        return ResponseEntity.ok().body(responseDto);
    }
}
