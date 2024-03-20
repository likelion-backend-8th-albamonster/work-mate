package com.example.workmate.entity.attendance;

import lombok.Getter;

//출근 상태 enum
@Getter
public enum Status {
    //출근 / 퇴근
    IN("출근"),
    OUT("퇴근");
    private String status;
    Status(String status) { this.status = status; }

}
