package com.example.workmate.entity;

import lombok.Getter;

//출근 상태 enum
@Getter
public enum Status {
    //정상출근 / 지각 / 조퇴
    IN("출근"),
    LATE("지각"),
    EARLY_OUT("조퇴"),
    OUT("퇴근");
    private String status;
    Status(String status) { this.status = status; }

}
