package com.example.workmate.service.schedule;

import com.example.workmate.component.ScheduleUtil;
import com.example.workmate.dto.WorkTimeDto;
import com.example.workmate.dto.schedule.ChangeRequestDto;
import com.example.workmate.dto.schedule.ScheduleDto;
import com.example.workmate.entity.AccountShop;
import com.example.workmate.entity.Shop;
import com.example.workmate.entity.WorkTime;
import com.example.workmate.entity.account.Account;
import com.example.workmate.entity.account.Authority;
import com.example.workmate.entity.schedule.ChangeRequest;
import com.example.workmate.facade.AuthenticationFacade;
import com.example.workmate.repo.AccountRepo;
import com.example.workmate.repo.AccountShopRepo;
import com.example.workmate.repo.ShopRepo;
import com.example.workmate.repo.WorkTimeRepo;
import com.example.workmate.repo.schedule.ChangeRequestRepo;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleService {
    private final WorkTimeRepo workTimeRepo;
    private final ScheduleUtil scheduleUtil;
    private final AuthenticationFacade authFacade;
    private final ShopRepo shopRepo;
    private final AccountShopRepo accountShopRepo;
    private final ChangeRequestRepo changeRequestRepo;
    private final AccountRepo accountRepo;

    // 근무 넣기
    public WorkTimeDto create(WorkTimeDto dto){
        // 해당 매장 매니저 이상만 가능
        Account account = checkMember(dto.getShopId());
        checkManagerOrAdmin(account);

        Account albaAccount = accountRepo.findById(dto.getAccountId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
        Shop shop = shopRepo.findById(dto.getShopId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
        WorkTime workTime = WorkTime.builder()
                .account(albaAccount)
                .shop(shop)
                .workRole(dto.getWorkRole())
                .workStartTime(dto.getWorkStartTime())
                .workEndTime(dto.getWorkEndTime())
                .build();

        return WorkTimeDto.fromEntity(workTimeRepo.save(workTime));
    }

    // 근무 수정하기
    public WorkTimeDto update(Long workTimeId, WorkTimeDto dto){
        // 해당 매장 매니저 이상만 가능
        Account account = checkMember(dto.getShopId());
        checkManagerOrAdmin(account);

        WorkTime workTime = workTimeRepo.findById(workTimeId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        workTime.updateTime(
                dto.getWorkStartTime(),
                dto.getWorkEndTime(),
                dto.getWorkRole()
        );

        return WorkTimeDto.fromEntity(workTimeRepo.save(workTime));
    }

    // 근무 모두 보기
    public Page<WorkTimeDto> readPage(Long shopId, Pageable pageable){
        // 해당 매장 근무자만 가능
        checkMember(shopId);

        return workTimeRepo.findAllByShop_Id(shopId, pageable)
                .map(WorkTimeDto::fromEntity);
    }

    // 근무 하나 보기
    public WorkTimeDto readOne(Long workTimeId){
        WorkTime workTime = workTimeRepo.findById(workTimeId).orElseThrow(
                ()-> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
        // 해당 매장 근무자만 가능
        checkMember(workTime.getShop().getId());

        return WorkTimeDto.fromEntity(workTime);
    }

    // 근무 지우기
    public WorkTimeDto delete(Long workTimeId){
        // 해당 근무가 존재하는지
        WorkTime workTime = workTimeRepo.findById(workTimeId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        // 해당 매장 매니저 이상만 가능
        Account account = checkMember(workTime.getShop().getId());
        checkManagerOrAdmin(account);

        WorkTimeDto workTimeDto = WorkTimeDto.fromEntity(workTime);
        workTimeRepo.delete(workTime);
        return workTimeDto;
    }

    // 한달 해당 매장의 근무표 불러오기
    public List<WorkTimeDto> viewMonth(Long shopId,ScheduleDto dto){
        // 테스트중이라 확인하는거 임시로 자름
        // checkMember(shopId);

        // 시작일과 마지막날 구하기
        dto.setDay(0);
        LocalDateTime startDay = scheduleUtil.getDay(true,dto).atStartOfDay();
        LocalDateTime endDay = scheduleUtil.getDay(false, dto).atTime(LocalTime.MAX);

        int dayOfWeek = scheduleUtil.getDayOfWeek(dto);
        log.info("dayOfWeek : {}",dayOfWeek);
        log.info("dayOfWeek + endDay : {}",dayOfWeek + endDay.getDayOfMonth());
        log.info("startDay: {}, endDay: {}",startDay,endDay);
        List<WorkTime> workTimes = workTimeRepo
                .findAllByShop_IdAndWorkStartTimeBetweenOrderByWorkStartTimeAsc(
                        shopId, startDay, endDay
                );
        if (workTimes.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        List<WorkTimeDto> dtos = new ArrayList<>();
        for (WorkTime workTime : workTimes){
            dtos.add(WorkTimeDto.fromEntity(workTime));
        }

        log.info("worTimes size: {}",workTimes.size());
        log.info("dto size: {}",dtos.size());

        return dtos;
    }
    // 처음 근무표 확인으로 들어왔을 때 정한 기간의 근무표를 보기
    public List<WorkTimeDto> viewPeriod(
            Long shopId,
            ScheduleDto start,
            ScheduleDto end
    ){
        checkMember(shopId);

        // 시작일과 마지막날 구하기
        LocalDateTime startDay = scheduleUtil.getDay(false, start).atStartOfDay();
        LocalDateTime endDay = scheduleUtil.getDay(false, end).atTime(LocalTime.MAX);
        List<WorkTime> workTimes = workTimeRepo
                .findAllByShop_IdAndWorkStartTimeBetweenOrderByWorkStartTimeAsc(
                        shopId, startDay, endDay
                );
        if (workTimes.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        List<WorkTimeDto> dtos = new ArrayList<>();
        for (WorkTime workTime : workTimes){
            dtos.add(WorkTimeDto.fromEntity(workTime));
        }
        return dtos;
    }

    // 근무표 변경요청
    public ChangeRequestDto createChange(ChangeRequestDto dto){

        Account account = checkMember(dto.getShopId());
        // 이미 해당 매장에서 다른 변경요청을 하고있으면 승인되거나 거절될 때 까지 다시 하지 못한다.
        List<ChangeRequest> changeRequests =
                changeRequestRepo.findAllByAccount_IdAndShop_Id(
                dto.getAccountId(), dto.getShopId()
        );

        if(!changeRequests.isEmpty()){
            for (ChangeRequest request : changeRequests){
                if(request.getStatus().equals(ChangeRequest.Status.OFFERED))
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        }

        Shop shop = shopRepo.findById(dto.getShopId()).orElseThrow(
                ()-> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
        WorkTime workTime = workTimeRepo.findById(dto.getWorkTimeId()).orElseThrow(
                ()-> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
        return ChangeRequestDto.fromEntity(changeRequestRepo.save(ChangeRequest.builder()
                .account(account)
                .shop(shop)
                .workTime(workTime)
                .cancelReason("")
                .build()));
    }
    // 근무표 변경요청 조회하기
    public List<ChangeRequestDto> readChangeAll(Long shopId){
        checkMember(shopId);
        List<ChangeRequest> changeRequests = changeRequestRepo.findAllByShop_Id(shopId);
        if (changeRequests.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        List<ChangeRequestDto> dtos = new ArrayList<>();
        for (ChangeRequest request : changeRequests){
            if(request.getStatus() == ChangeRequest.Status.OFFERED){
                dtos.add(ChangeRequestDto.fromEntity(request));
            }
        }
        return dtos;
    }
    @Transactional
    // 근무표 변경요청 승인하기
    public ChangeRequestDto confirmChange(Long changeRequestId){
        ChangeRequest changeRequest = changeRequestRepo.findById(changeRequestId).orElseThrow(
                ()-> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        //해당 매장의 매니저 이상 가능
        Account account = checkMember(changeRequest.getShop().getId());
        checkManagerOrAdmin(account);

        changeRequest.setStatus(ChangeRequest.Status.CONFIRMED);

        WorkTime changeTime1 = workTimeRepo.findById(changeRequest.getWorkTime().getId()).orElseThrow(
                ()-> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
        WorkTime changeTime2 = workTimeRepo.findByAccount_Id(changeRequest.getAccount().getId()).orElseThrow(
                ()-> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        LocalDateTime changeStartTime = changeTime1.getWorkStartTime();
        LocalDateTime changeEndTime = changeTime1.getWorkEndTime();

        changeTime1.changeTime(
                changeTime2.getWorkStartTime(),
                changeTime2.getWorkEndTime()
        );
        changeTime2.changeTime(
                changeStartTime,
                changeEndTime
        );
        changeRequestRepo.save(changeRequest);
        workTimeRepo.saveAll(List.of(changeTime1,changeTime2));
        return ChangeRequestDto.fromEntity(changeRequest);
    }

    @Transactional
    // 근무표 변경요청 거절하기, 관리자만 가능
    public ChangeRequestDto declineChange(Long changeRequestId){
        ChangeRequest changeRequest = changeRequestRepo.findById(changeRequestId).orElseThrow(
                ()-> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
        //해당 매장의 매니저 이상 가능
        Account account = checkMember(changeRequest.getShop().getId());
        checkManagerOrAdmin(account);

        changeRequest.setStatus(ChangeRequest.Status.DECLINED);

        changeRequestRepo.save(changeRequest);
        return ChangeRequestDto.fromEntity(changeRequest);
    }

    // 해당 매장 직원인지 확인하기
    public Account checkMember(Long shopId){
        String username = authFacade.getAuth().getName();
        Account account = accountRepo.findByUsername(username).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
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
