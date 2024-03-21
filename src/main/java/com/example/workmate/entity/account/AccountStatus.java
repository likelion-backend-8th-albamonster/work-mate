package com.example.workmate.entity.account;

import lombok.Getter;

@Getter
public enum AccountStatus {
    SUBMITTED("승인 요청"),
    ACCEPT("승인"),
    REJECT("거절");

    private final String status;

    AccountStatus(String status) {
        this.status = status;
    }
}
