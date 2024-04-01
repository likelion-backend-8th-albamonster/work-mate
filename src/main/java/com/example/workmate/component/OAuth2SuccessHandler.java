package com.example.workmate.component;

import com.example.workmate.entity.account.Authority;
import com.example.workmate.entity.account.CustomAccountDetails;
import com.example.workmate.jwt.JwtTokenUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
// OAuth2UserServiceImpl이 성공적으로 OAuth2 과정을 마무리 했을 때, 넘겨받은 사용자 정보를 바탕으로 JWT를 생성,
// 클라이언트한테 JWT를 전달
public class OAuth2SuccessHandler
        // 인증에 성공했을 때 특정 URL로 리다이렉트 하고 싶은 경우 활용 가능한 SuccessHandler
        extends SimpleUrlAuthenticationSuccessHandler {
    // JWT 발급을 위해 JwtTokenUtils
    private final JwtTokenUtils tokenUtils;
    // 사용자 정보 등록을 위해 UserDetailsManager
    private final UserDetailsManager userDetailsManager;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication
    )
            throws IOException, ServletException
    {
        // OAuth2UserServiceImpl의 반환값이 할당된다.
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        log.info(oAuth2User.toString());

        // 넘겨받은 정보를 바탕으로 사용자 정보를 준비
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String username = String.format("%s", email);
        String providerId = oAuth2User.getAttribute("id");

        // 처음으로 이 소셜 로그인으로 로그인을 시도했다.
        if (!userDetailsManager.userExists(username)) {
            // 새 계정을 만들어야 한다.
            userDetailsManager.createUser(CustomAccountDetails.builder()
                    .username(username)
                    .name(name)
                    .email(email)
                    .password(providerId)
                    .authority(Authority.ROLE_USER)
                    .mailAuth(true)
                    .build());
        }

        // 데이터베이스에서 사용자 계정 회수
        UserDetails details
                = userDetailsManager.loadUserByUsername(username);
        // JWT 생성
        String token = tokenUtils.generateToken(details);
        log.info("Token: {}", token);

        Cookie cookie = new Cookie("jwtToken", token);
        cookie.setHttpOnly(false);
        cookie.setPath("/");
        response.addCookie(cookie);

        // 어디로 리다이렉트 할지 지정
        String targetUrl = "http://localhost:8080/account/oauth";
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
