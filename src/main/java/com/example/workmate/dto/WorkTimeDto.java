package com.example.workmate.dto;


import com.example.workmate.entity.account.Account;
import com.example.workmate.entity.Shop;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class WorkTimeDto {
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
