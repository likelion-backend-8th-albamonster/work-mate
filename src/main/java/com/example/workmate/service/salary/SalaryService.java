package com.example.workmate.service.salary;

import com.example.workmate.component.ScheduleUtil;
import com.example.workmate.dto.salary.SalaryDto;
import com.example.workmate.entity.AccountShop;
import com.example.workmate.entity.Shop;
import com.example.workmate.entity.account.Account;
import com.example.workmate.entity.account.Authority;
import com.example.workmate.entity.salary.Salary;
import com.example.workmate.entity.schedule.WorkTime;
import com.example.workmate.facade.AuthenticationFacade;
import com.example.workmate.repo.AccountRepo;
import com.example.workmate.repo.AccountShopRepo;
import com.example.workmate.repo.ShopRepo;
import com.example.workmate.repo.salary.SalaryRepo;
import com.example.workmate.repo.schedule.WorkTimeRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SalaryService {
    private final SalaryRepo salaryRepo;
    private final WorkTimeRepo workTimeRepo;
    private final AuthenticationFacade authFacade;
    private final AccountRepo accountRepo;
    private final AccountShopRepo accountShopRepo;
    private final ShopRepo shopRepo;
    private final ScheduleUtil scheduleUtil;
    private final int HourlyRate = 10000;


    // 이전 월급이 있으면 정산한 뒤 새로 만들고 없으면 새로 만듬
    public SalaryDto create(Long shopId, Long accountId, SalaryDto dto){
//        Account account = checkMember(shopId);
//        checkManagerOrAdmin(account);

        // 해당 만드려는 타깃이 현재 매장에 속해있는지
        AccountShop accountShop = accountShopRepo.findByShop_IdAndAccount_id(shopId,accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Account salaryAccount = accountRepo.findById(accountId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Shop shop = shopRepo.findById(shopId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // 매장사람의 이전 월급내용을 찾아온다.
        Salary salary = salaryRepo.findTopByAccount_IdAndShop_IdOrderBySalaryDateDesc(
            accountId, shopId);

        // 새로 만들 때 이번 달근무표에 올라와있는 만큼을 참고해서 만듬, 매월 1일 ~ 말일 기준, 시급은 만원이라고 가정
        // 근무표에 없으면 오류가 난다.
        LocalDate date = dto.getSalaryDate();
        YearMonth yearMonth = YearMonth.from(date);

        LocalDateTime startDay = LocalDate.of(date.getYear(), date.getMonth(), 1).atStartOfDay();
        LocalDateTime endDay = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);

        List<WorkTime> workTimes = workTimeRepo
                .findAllByAccount_IdAndShop_IdAndWorkStartTimeBetweenOrderByWorkStartTimeAsc(
                        accountId, shopId, startDay, endDay);
        if(workTimes.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        long albaMinute = 0L;
        for(WorkTime workTime : workTimes){
            albaMinute = albaMinute + ChronoUnit.MINUTES.between(
                    workTime.getWorkEndTime(),workTime.getWorkStartTime());
        }
        int totalSalary = (int)(HourlyRate * albaMinute / 60);

        if(salary != null) {
            // 이전 내용이 있으면 정산하고 새로 만들기
            salary.setStatus(Salary.Status.DONE);
            salaryRepo.save(salary);
        }

        Salary newSalary = Salary.builder()
                .account(salaryAccount)
                .shop(shop)
                .salaryDate(date)
                .totalSalary(totalSalary)
                .build();

        return SalaryDto.fromEntity(salaryRepo.save(newSalary));
    }
    // 매장의 정산내역들 다 보기

    // 해당 매장 직원인지 확인하기
    public Account checkMember(Long shopId){
        String username = authFacade.getAuth().getName();
        Account account = accountRepo.findByUsername(username).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Optional<AccountShop> optionalAccountShop =
                accountShopRepo.findByShop_IdAndAccount_id(shopId, account.getId());
        if (optionalAccountShop.isEmpty())
            throw new IllegalArgumentException();
        return account;
    }

    // 해당 직원의 매니저 이상 권한 확인하기
    public void checkManagerOrAdmin(Account account){
        if(account.getAuthority() == Authority.ROLE_USER)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
}
