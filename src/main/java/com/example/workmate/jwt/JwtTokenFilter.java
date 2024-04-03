package com.example.workmate.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtTokenUtils jwtTokenUtils;
    private final UserDetailsManager manager;
    private String cookieToken;

    public JwtTokenFilter(
            JwtTokenUtils jwtTokenUtils,
            UserDetailsManager manager
    ) {
        this.jwtTokenUtils = jwtTokenUtils;
        this.manager = manager;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        log.debug("try jwt filter");
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for(Cookie c : cookies){
                if (c.getName().equals("jwtToken")){
                    log.info("cookie value: {}", c.getValue());
                    cookieToken = c.getValue();
                }
            }
        }

        // 1. Authorization 헤더를 회수
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("authHeader: {}",authHeader);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.split(" ")[1];
            if (jwtTokenUtils.validate(token)) {
                SecurityContext context = SecurityContextHolder.createEmptyContext();

                String username = jwtTokenUtils
                        .parseClaims(token)
                        .getSubject();

                UserDetails userDetails = manager.loadUserByUsername(username);
                for (GrantedAuthority authority :userDetails.getAuthorities()) {
                    log.info("authority: {}", authority.getAuthority());
                }

                // 인증 정보 생성
                AbstractAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                token,
                                userDetails.getAuthorities()
                        );
                // 인증 정보 등록
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);
                log.info("set security context with jwt");
            }
            else {
                log.warn("jwt validation failed");
            }
        }
        else if(cookieToken != null){
            if (jwtTokenUtils.validate(cookieToken)) {
                SecurityContext context = SecurityContextHolder.createEmptyContext();

                String username = jwtTokenUtils
                        .parseClaims(cookieToken)
                        .getSubject();


                UserDetails userDetails = manager.loadUserByUsername(username);
                for (GrantedAuthority authority :userDetails.getAuthorities()) {
                    log.info("authority: {}", authority.getAuthority());
                }

                // 인증 정보 생성
                AbstractAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                cookieToken,
                                userDetails.getAuthorities()
                        );
                // 인증 정보 등록
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);
                log.info("set security context with jwt");
            }
            else {
                log.warn("jwt validation failed");
            }
        }
        // 다음 필터 호출
        filterChain.doFilter(request, response);
    }
}