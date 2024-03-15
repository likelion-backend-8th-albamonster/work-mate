package com.example.workmate.entity;
import com.example.workmate.entity.account.Account;
import com.example.workmate.entity.account.Authority;
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
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @Setter
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private WorkRole workRole = WorkRole.ROLE_HALL;

    private LocalDateTime workStartTime;
    private LocalDateTime workEndTime;
}
