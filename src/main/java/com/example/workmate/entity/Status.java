package com.example.workmate.entity;

import lombok.Getter;

//출근 상태 enum
@Getter
public enum Status {
    //정상출근 / 지각 / 조퇴
    OK, LATE, EARLY

}
