package com.example.workmate.entity;

import lombok.Getter;

//출근 상태 enum
@Getter
public enum Status {
    //정상출근 / 지각 / 조퇴
    OK("정상출근"),
    LATE("지각"),
    EARLY("조퇴");

    private String status;
    Status(String status) { this.status = status; }

}
