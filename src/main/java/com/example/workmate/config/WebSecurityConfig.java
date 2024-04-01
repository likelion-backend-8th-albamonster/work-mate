package com.example.workmate.config;

import com.example.workmate.component.OAuth2SuccessHandler;
import com.example.workmate.entity.account.Authority;
import com.example.workmate.jwt.JwtTokenFilter;
import com.example.workmate.jwt.JwtTokenUtils;
import com.example.workmate.service.account.OAuth2UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
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
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeHttpRequests ->
                        authorizeHttpRequests
                                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                                .requestMatchers(
                                        "/token/issue",
                                        "/token/validate",
                                        "/account/home",
                                        "/account/login",

                                        // 계정 html
                                        "/my-profile/**",
                                        "/email-check",
                                        "/shop",
                                        "/shop/{id}",
                                        "/shop/{shopId}/shop-account",
                                        "/shop/create",
                                        "/account/logout",

                                        // 메일 코드 확인
                                        "/account/check-code",
                                        //근무표 관련 테스트중
                                        "/schedule/**"

                                )
                                .permitAll()

                                .requestMatchers(
                                        "/account/register",
                                        "/account/users-register",
                                        "/account/business-register"
                                )
                                .anonymous()

                                .requestMatchers(
                                        "/account/oauth",
                                        "/profile",
                                        "/profile/{id}",
                                        "/profile/{id}/update",
                                        "/profile/email-check",
                                        "/profile/check-code",
                                        "/profile/submit")
                                .authenticated()

                                // 매장 생성 테스트용
                                .requestMatchers(
                                        "/api/shop/crete",
                                        "/api/shop/{id}",
                                        "/api/shop/{id}/update",
                                        "/api/shop/{id}/delete",
                                        "/api/shop/{id}/shop-account",
                                        "/api/shop/{id}/shop-account/account-name",
                                        "/api/shop/{id}/shop-account/account-status",
                                        "/api/shop/{shopId}/shop-account/accept/{accountShopId}",
                                        "/{id}/shop-account/account-name")
                                .authenticated()
                )
                .oauth2Login(oauth2Login -> oauth2Login
                        .loginPage("/account/login")
                        .permitAll()
                        .successHandler(oAuth2SuccessHandler)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService))

                )
                .logout(logout -> logout
                        .logoutUrl("/account/logout")
                        .logoutSuccessUrl("/account/home")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID", "jwtToken")
                )
//                .sessionManagement(sessionManagement ->
//                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                )
                .addFilterBefore(new JwtTokenFilter(jwtTokenUtils, manager),
                        AuthorizationFilter.class)
        ;
        return http.build();
    }
}
