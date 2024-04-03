package com.example.workmate.entity.schedule;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

@Getter
public enum WorkRole {
    // 임의로 일단 정해놨습니다
    ROLE_KITCHEN("주방"),
    ROLE_MANAGER("매니저"),
    ROLE_HALL("홀"),
    ROLE_COOK("요리사"),
    ROLE_ADMIN("관리자"),
    ROLE_COUNTER("카운터");

    private String workRole;

    WorkRole(String workRole) {
        this.workRole = workRole;
    }
}
