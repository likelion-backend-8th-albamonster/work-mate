package com.example.workmate.service.schedule;


import com.example.workmate.entity.AccountShop;
import com.example.workmate.entity.Shop;
import com.example.workmate.entity.WorkRole;
import com.example.workmate.entity.WorkTime;
import com.example.workmate.entity.account.Account;
import com.example.workmate.entity.account.Authority;
import com.example.workmate.repo.AccountRepo;
import com.example.workmate.repo.AccountShopRepo;
import com.example.workmate.repo.ShopRepo;
import com.example.workmate.repo.WorkTimeRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ScheduleDataService {
    private final WorkTimeRepo workTimeRepo;
    private final ShopRepo shopRepo;
    private final AccountRepo accountRepo;
    private final AccountShopRepo accountShopRepo;
    private final PasswordEncoder passwordEncoder;

    public ScheduleDataService(
            WorkTimeRepo workTimeRepo,
            ShopRepo shopRepo,
            AccountRepo accountRepo,
            AccountShopRepo accountShopRepo,
            PasswordEncoder passwordEncoder
    ) {
        this.workTimeRepo = workTimeRepo;
        this.shopRepo = shopRepo;
        this.accountRepo = accountRepo;
        this.accountShopRepo = accountShopRepo;
        this.passwordEncoder = passwordEncoder;

        this.accountRepo.saveAll(List.of(
                Account.builder()
                        .name("yang1")
                        .username("yang1")
                        .password(passwordEncoder.encode("1111"))
                        .email("yang1")
                        .authority(Authority.ROLE_USER)
                        .mailAuth(true)
                        .build(),
                Account.builder()
                        .name("yang2")
                        .username("yang2")
                        .password(passwordEncoder.encode("1111"))
                        .email("yang2")
                        .authority(Authority.ROLE_USER)
                        .mailAuth(true)
                        .build(),
                Account.builder()
                        .name("yangAdmin")
                        .username("yangAdmin")
                        .password(passwordEncoder.encode("1111"))
                        .email("yangAdmi")
                        .authority(Authority.ROLE_ADMIN)
                        .mailAuth(true)
                        .build()
        ));
        this.shopRepo.saveAll(List.of(
                Shop.builder()
                        .name("yangshop1")
                        .address("yangshop1")
                        .build()
        ));
    }
    public void accountShop() {
        Account account1 = accountRepo.findById(2L).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Shop shop = shopRepo.findById(1L).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        AccountShop accountShop1 = AccountShop.builder()
                .account(account1)
                .shop(shop)
                .build();
        accountShopRepo.save(accountShop1);

        Account account2 = accountRepo.findById(3L).orElseThrow(
                ()->new ResponseStatusException(HttpStatus.NOT_FOUND));

        AccountShop accountShop2 = AccountShop.builder()
                .account(account2)
                .shop(shop)
                .build();
        accountShopRepo.save(accountShop2);

        Account account3 = accountRepo.findById(4L).orElseThrow(
                ()->new ResponseStatusException(HttpStatus.NOT_FOUND));

        AccountShop accountShop3 = AccountShop.builder()
                .account(account3)
                .shop(shop)
                .build();
        accountShopRepo.save(accountShop3);
    }

    // 근무만들기
    public void makeWorkTime(Long accountId){
        Account account1 = accountRepo.findById(accountId).orElseThrow(
                ()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        Shop shop = shopRepo.findById(1L).orElseThrow(
                ()->new ResponseStatusException(HttpStatus.NOT_FOUND));

        LocalDate now = LocalDate.now();
        LocalDate endDay = now.withDayOfMonth(now.lengthOfMonth());
        int intEndDay = endDay.getDayOfMonth();
        List<WorkTime> workTimes1 = new ArrayList<>();

        for (int i = 0; i < intEndDay; i++) {
            int startHour = (int)(Math.random() * 24);
            LocalDateTime startTime = LocalDateTime.of(
                    now.getYear(),
                    now.getMonth(),
                    i + 1,
                    startHour,
                    0
            );
            LocalDateTime endTime = startTime.plusHours(4);
            workTimes1.add(WorkTime.builder()
                            .account(account1)
                            .shop(shop)
                            .workStartTime(startTime)
                            .workEndTime(endTime)
                            .workRole(WorkRole.ROLE_HALL)
                    .build());
        }
        workTimeRepo.saveAll(workTimes1);
        log.info("userid: {} count: {}",account1.getId(), workTimes1.size());
    }
}
