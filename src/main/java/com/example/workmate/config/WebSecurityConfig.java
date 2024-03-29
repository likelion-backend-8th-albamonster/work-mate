package com.example.workmate.config;

import com.example.workmate.entity.account.Authority;
import com.example.workmate.jwt.JwtTokenFilter;
import com.example.workmate.jwt.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtTokenUtils jwtTokenUtils;
    private final UserDetailsManager manager;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeHttpRequests ->
                        authorizeHttpRequests
                                .requestMatchers(
                                        "/token/issue",
                                        "/token/validate",
                                        "/account/home"
                                )
                                .permitAll()

                                .requestMatchers(
                                        "/account/login",
                                        "/account/users-register",
                                        "/account/business-register"
                                )
                                .anonymous()

                                .requestMatchers("/account/my-profile")
                                .hasAnyRole(Authority.ROLE_USER.getAuthority(), Authority.ROLE_BUSINESS_USER.getAuthority(), Authority.ROLE_ADMIN.getAuthority())
                )
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(new JwtTokenFilter(jwtTokenUtils, manager),
                        AuthorizationFilter.class)
        ;
        return http.build();
    }
}
