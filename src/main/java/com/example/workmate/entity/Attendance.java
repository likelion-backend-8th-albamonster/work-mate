package com.example.workmate.entity;

import com.example.workmate.entity.account.Account;
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
    //사용자
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
    //출근시간
    private LocalDateTime checkInTime;
    //퇴근시간
    private LocalDateTime checkOutTime;

    //출근상태
    @Enumerated(EnumType.STRING)
    private Status status;
}
