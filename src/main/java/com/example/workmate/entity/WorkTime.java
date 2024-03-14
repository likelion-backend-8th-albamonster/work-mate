package com.example.workmate.entity;
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

    @ManyToOne
    @JoinColumn(name = "accountId")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "shopId")
    private Shop shop;

    //private List<workRole> workRoles;
    private LocalDateTime workStartTime;
    private LocalDateTime workEndTime;
}
