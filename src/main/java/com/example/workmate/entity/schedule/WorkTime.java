package com.example.workmate.entity.schedule;
import com.example.workmate.entity.Shop;
import com.example.workmate.entity.account.Account;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class WorkTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @Setter
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private WorkRole workRole = WorkRole.ROLE_HALL;

    private LocalDateTime workStartTime;
    private LocalDateTime workEndTime;

    public void updateTime(
            LocalDateTime workStartTime,
            LocalDateTime workEndTime,
            WorkRole workRole
    ){
        this.workStartTime = workStartTime;
        this.workEndTime = workEndTime;
        this.workRole = workRole;
    }

    public void changeTime(
            LocalDateTime workStartTime,
            LocalDateTime workEndTime
    ){
        this.workStartTime = workStartTime;
        this.workEndTime = workEndTime;
    }
}
