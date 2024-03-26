package com.example.workmate.config;

import com.example.workmate.component.OAuth2SuccessHandler;
import com.example.workmate.jwt.JwtTokenFilter;
import com.example.workmate.jwt.JwtTokenUtils;
import com.example.workmate.service.account.OAuth2UserServiceImpl;
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
    private final OAuth2UserServiceImpl oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeHttpRequests ->
                        authorizeHttpRequests
                                .requestMatchers(
                                        "/token/issue",
                                        "/token/validate",
                                        "/account/home",

                                        // 메일 코드 확인
                                        "/account/check-code",
                                        //근무표 관련 테스트중
                                        "/schedule/**",
                                        "/schedule/view-month/{shopId}"
                                )
                                .permitAll()

                                .requestMatchers(
                                        "/account/login",
                                        "/account/register",
                                        "/account/users-register",
                                        "/account/business-register"
                                )
                                .anonymous()

                                // 권한 설정 필요
                                .requestMatchers("/profile/{id}/**")
                                .authenticated()

                                // 매장 생성 테스트용
                                .requestMatchers("/shop/**")
                                .permitAll()
                )
                .oauth2Login(oauth2Login -> oauth2Login
                        .loginPage("/account/login")
                        .successHandler(oAuth2SuccessHandler)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService))

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
