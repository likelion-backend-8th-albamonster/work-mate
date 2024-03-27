package com.example.workmate.entity.account;

import lombok.Getter;

@Getter
public enum Authority {
    ROLE_INACTIVE_USER("비활성유저"),
    ROLE_USER("아르바이트생"),
    ROLE_BUSINESS_USER("매니저"),
    ROLE_ADMIN("관리자");

    private String authority;

    Authority(String authority) {
        this.authority = authority;
    }
}
