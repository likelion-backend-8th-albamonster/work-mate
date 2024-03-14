package com.example.workmate.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

//출근테이블
@Builder
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //TODO accountId 추가
    
    //출근시간
    private LocalDateTime checkInTime;
    //퇴근시간
    private LocalDateTime checkOutTime;

    //출근상태
    @Enumerated(EnumType.STRING)
    private Status status;


}
