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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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

    @Transactional
    public SalaryDto create(SalaryDto dto){
        Account account = checkMember(dto.getShopId());
        checkManagerOrAdmin(account);

        // 해당 만드려는 타깃이 현재 매장에 속해있는지
        AccountShop accountShop = accountShopRepo.findByShop_IdAndAccount_id(
                dto.getShopId(),dto.getAccountId()).orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Account salaryAccount = accountRepo.findById(dto.getAccountId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Shop shop = shopRepo.findById(dto.getShopId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // 매장사람의 이전 월급내용을 찾아온다.
        Salary salary = salaryRepo.findTopByAccount_IdAndShop_IdOrderByIdDesc(
                dto.getAccountId(), dto.getShopId());

        // 새로 만들 때 이번 달근무표에 올라와있는 만큼을 참고해서 만듬, 매월 1일 ~ 말일 기준, 시급은 만원이라고 가정
        // 근무표에 없으면 오류가 난다.
        LocalDate date = LocalDate.of(dto.getSalaryYear(),dto.getSalaryMonth(),1);
        YearMonth yearMonth = YearMonth.from(date);

        LocalDateTime startDay = LocalDate.of(date.getYear(), date.getMonth(), 1).atStartOfDay();
        LocalDateTime endDay = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);

        List<WorkTime> workTimes = workTimeRepo
                .findAllByAccount_IdAndShop_IdAndWorkStartTimeBetweenOrderByWorkStartTimeAsc(
                        dto.getAccountId(), dto.getShopId(), startDay, endDay);
        if(workTimes.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        long albaMinute = 0L;
        for(WorkTime workTime : workTimes){
            albaMinute = albaMinute + ChronoUnit.MINUTES.between(
                    workTime.getWorkStartTime(),workTime.getWorkEndTime());
        }
        int totalSalary = (int)(HourlyRate * albaMinute / 60);

        if(salary != null) {
            // 이전달이 정산 안돼있으면 정산하고 새로 만들기
            if(salary.getStatus().equals(Salary.Status.BEFORE)){
                salary.setStatus(Salary.Status.DONE);
                salaryRepo.save(salary);
            }
        }

        Salary newSalary = Salary.builder()
                .account(salaryAccount)
                .shop(shop)
                .salaryYear(dto.getSalaryYear())
                .salaryMonth(dto.getSalaryMonth())
                .totalSalary(totalSalary)
                .build();

        return SalaryDto.fromEntity(salaryRepo.save(newSalary));
    }

    // 월급 지우기, 정산 안된 내역만 지울 수 있다.
    public SalaryDto deleteSalary(Long salaryId){
        Salary salary = salaryRepo.findById(salaryId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Account account = checkMember(salary.getShop().getId());
        checkManagerOrAdmin(account);

        if(salary.getStatus().equals(Salary.Status.DONE)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        SalaryDto dto = SalaryDto.fromEntity(salary);
        salaryRepo.deleteById(salaryId);

        return dto;
    }

    // 매장의 정산내역들 다 보기
    public List<SalaryDto> readShopSalaryAll(Long shopId){
        Account account = checkMember(shopId);
        checkManagerOrAdmin(account);

        List<Salary> salaries = salaryRepo
                .findByShop_IdOrderByIdDesc(shopId);

        if(salaries.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        List<SalaryDto> dtos = new ArrayList<>();
        for (Salary salary : salaries){
            dtos.add(SalaryDto.fromEntity(salary));
        }
        return dtos;
    }

    // 한 유저의 한 매장 정산내역 보기
    public List<SalaryDto> mySalaryAll(Long shopId){
        Account account = checkMember(shopId);

        List<Salary> salaries = salaryRepo
                .findByShop_IdAndAccount_IdOrderByIdDesc(shopId,account.getId());

        if(salaries.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        List<SalaryDto> dtos = new ArrayList<>();
        for (Salary salary : salaries){
            dtos.add(SalaryDto.fromEntity(salary));
        }
        return dtos;
    }

    // 사용자가 매장마다의 정산내역을 보기
    public List<List<SalaryDto>> readEachShopSalary(){
        String username = authFacade.getAuth().getName();
        Account account = accountRepo.findByUsername(username).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Optional<List<AccountShop>> optionalList = accountShopRepo.findAllByAccount_id(account.getId());
        if (optionalList.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        List<AccountShop> accountShops = optionalList.get();
        List<Long> shopIds = new ArrayList<>();
        for (AccountShop accountShop : accountShops){
            shopIds.add(accountShop.getId());
        }
        List<Salary> salaries = new ArrayList<>();
        for (Long shopId : shopIds){
            List<Salary> salaryFind = salaryRepo.findAllByShop_IdOrderByIdDesc(shopId);
            if(!salaryFind.isEmpty()){
                salaries.addAll(salaryFind);
            }
        }

        if (salaries.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        log.info("salaries.size: {}", salaries.size());
        List<List<SalaryDto>> listDtos = new ArrayList<>();

        for (AccountShop accountShop : accountShops) {
            List<SalaryDto> dtos = new ArrayList<>();
            for (Salary salary : salaries){
                if(accountShop.getShop().getId().equals(salary.getShop().getId())){
                    log.info("ok");
                    dtos.add(SalaryDto.fromEntity(salary));
                    log.info(dtos.get(0).getId().toString());
                    log.info(dtos.get(0).getTotalSalary().toString());
                }
            }
            listDtos.add(dtos);
            log.info(listDtos.get(0).get(0).getId().toString());
            log.info(listDtos.get(0).get(0).getTotalSalary().toString());
        }
        return listDtos;
    }

    @Transactional
    // 월급id로 정산하기
    public SalaryDto settleOne(Long salaryId, Long shopId){
        Account account = checkMember(shopId);
        checkManagerOrAdmin(account);

        Salary salary = salaryRepo.findById(salaryId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (salary.getStatus().equals(Salary.Status.DONE)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        salary.setStatus(Salary.Status.DONE);
        return SalaryDto.fromEntity(salaryRepo.save(salary));
    }

    @Transactional
    // 해당 매장 정산안된 월급 모두 정산하기
    public List<SalaryDto> settleAll(Long shopId){
        Account account = checkMember(shopId);
        checkManagerOrAdmin(account);

        List<Salary> salaries = salaryRepo.
                findAllByShop_IdAndStatusOrderByIdDesc(shopId, Salary.Status.BEFORE);
        if (salaries.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        List<SalaryDto> dtos = new ArrayList<>();
        for (Salary salary : salaries){
            salary.setStatus(Salary.Status.DONE);
            dtos.add(SalaryDto.fromEntity(salary));
        }
        salaryRepo.saveAll(salaries);
        return dtos;
    }

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
