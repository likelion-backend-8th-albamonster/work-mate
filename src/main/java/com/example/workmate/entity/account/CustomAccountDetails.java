package com.example.workmate.entity.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomAccountDetails implements UserDetails {
    private Long id;
    private String username;
    private String password;
    private String name;
    private String email;
    private String businessNumber;
    private boolean mailAuth;
    private Authority authority;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (authority != null) {
            String role = authority.getAuthority();
            return Collections.singleton(new SimpleGrantedAuthority(role));
        } else {
            return Collections.emptySet();
        }
    }
    private Account account;

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
