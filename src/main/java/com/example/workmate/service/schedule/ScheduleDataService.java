package com.example.workmate.service.schedule;


import com.example.workmate.entity.AccountShop;
import com.example.workmate.entity.Shop;
import com.example.workmate.entity.account.AccountStatus;
import com.example.workmate.entity.schedule.WorkRole;
import com.example.workmate.entity.schedule.WorkTime;
import com.example.workmate.entity.account.Account;
import com.example.workmate.entity.account.Authority;
import com.example.workmate.repo.AccountRepo;
import com.example.workmate.repo.AccountShopRepo;
import com.example.workmate.repo.ShopRepo;
import com.example.workmate.repo.schedule.WorkTimeRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

//        if(accountRepo.count() == 1){
//            this.accountRepo.saveAll(List.of(
//                    Account.builder()
//                            .name("yang1")
//                            .username("yang1")
//                            .password(passwordEncoder.encode("1111"))
//                            .email("yang1")
//                            .authority(Authority.ROLE_USER)
//                            .mailAuth(true)
//                            .build(),
//                    Account.builder()
//                            .name("yang2")
//                            .username("yang2")
//                            .password(passwordEncoder.encode("1111"))
//                            .email("yang2")
//                            .authority(Authority.ROLE_USER)
//                            .mailAuth(true)
//                            .build(),
//                    Account.builder()
//                            .name("yangAdmin")
//                            .username("yangAdmin")
//                            .password(passwordEncoder.encode("1111"))
//                            .email("yangAdmi")
//                            .authority(Authority.ROLE_ADMIN)
//                            .mailAuth(true)
//                            .build()
//            ));
//        }
//        if(shopRepo.count() == 0){
//            this.shopRepo.saveAll(List.of(
//                    Shop.builder()
//                            .name("yangshop1")
//                            .address("yangshop1")
//                            .build()
//            ));
//        }
    }
    public void accountShop(Long accountId, Long shopId) {
        Account account1 = accountRepo.findById(accountId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Shop shop = shopRepo.findById(shopId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        AccountShop accountShop1 = AccountShop.builder()
                .account(account1)
                .shop(shop)
                .status(AccountStatus.ACCEPT)
                .build();
        accountShopRepo.save(accountShop1);
    }

    public void makeWorkTime(Long accountId, Long shopId, int year, int month){
        Account account1 = accountRepo.findById(accountId).orElseThrow(
                ()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        Shop shop = shopRepo.findById(shopId).orElseThrow(
                ()->new ResponseStatusException(HttpStatus.NOT_FOUND));

        LocalDate startDay = LocalDate.of(year,month,1);
        LocalDate endDay = startDay.withDayOfMonth(startDay.lengthOfMonth());
        int intEndDay = endDay.getDayOfMonth();
        List<WorkTime> workTimes1 = new ArrayList<>();

        for (int i = 0; i < intEndDay; i++) {
            int startHour = (int)(Math.random() * 24);
            LocalDateTime startTime = LocalDateTime.of(
                    startDay.getYear(),
                    startDay.getMonthValue(),
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
