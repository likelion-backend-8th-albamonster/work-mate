package com.example.workmate.service.schedule;

import com.example.workmate.config.ScheduleUtil;
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
import com.example.workmate.repo.AccountShopRepo;
import com.example.workmate.repo.ShopRepo;
import com.example.workmate.repo.WorkTimeRepo;
import com.example.workmate.repo.schedule.ChangeRequestRepo;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class ScheduleService {
    private final WorkTimeRepo workTimeRepo;
    private final ScheduleUtil scheduleUtil;
    private final AuthenticationFacade authFacade;
    private final ShopRepo shopRepo;
    private final AccountShopRepo accountShopRepo;
    private final ChangeRequestRepo changeRequestRepo;

    // 처음 근무표 확인으로 들어왔을 때
    // 한달 해당 매장의 근무표 불러오기
    public List<WorkTimeDto> viewMonth(Long shopId,ScheduleDto dto){
        checkMember(shopId);

        // 시작일과 마지막날 구하기
        LocalDateTime startDay = scheduleUtil.GetDay(false,dto).atStartOfDay();
        LocalDateTime endDay = scheduleUtil.GetDay(true, dto).atTime(LocalTime.MAX);


        List<WorkTime> workTimes = workTimeRepo
                .findAllByShop_IdAndWorkStartTimeGreaterThanAndWorkEndTimeLessThanEqual(
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
    // 처음 근무표 확인으로 들어왔을 때
    // 정한 기간의 근무표를 보기
    public List<WorkTimeDto> viewPeriod(
            Long shopId,
            ScheduleDto start,
            ScheduleDto end
    ){
        checkMember(shopId);

        // 시작일과 마지막날 구하기
        LocalDateTime startDay = scheduleUtil.GetDay(false, start).atStartOfDay();
        LocalDateTime endDay = scheduleUtil.GetDay(false, end).atTime(LocalTime.MAX);
        List<WorkTime> workTimes = workTimeRepo
                .findAllByShop_IdAndWorkStartTimeGreaterThanAndWorkEndTimeLessThanEqual(
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
        // 인증 먼저
        checkMember(dto.getShopId());
        // 이미 다른 변경요청을 하고있으면 승인되거나 거절될 때 까지 다시 하지 못한다.
        Optional<ChangeRequest> OptionalRequest = changeRequestRepo.findByAccount_Id(dto.getAccountId());
        if (OptionalRequest.isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        Shop shop = shopRepo.findById(dto.getShopId()).orElseThrow(
                ()-> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
        WorkTime workTime = workTimeRepo.findById(dto.getWorkTimeId()).orElseThrow(
                ()-> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
        return ChangeRequestDto.fromEntity(changeRequestRepo.save(ChangeRequest.builder()
                        //.account(account)
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
    // 근무표 변경요청 승인하기, 관리자만 가능
    public ChangeRequestDto confirmChange(Long shopId, Long changeRequestId){
//        authFacade.extractUser 완성 전에는 인증 없이 갑니다.
//        Account account = checkMember(shopId);
//        checkManagerOrAdmin(account);
        ChangeRequest changeRequest = changeRequestRepo.findById(changeRequestId).orElseThrow(
                ()-> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
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
    public ChangeRequestDto declineChange(Long shopId, Long changeRequestId){
//        Account account = checkMember(shopId);
//        checkManagerOrAdmin(account);
        ChangeRequest changeRequest = changeRequestRepo.findById(changeRequestId).orElseThrow(
                ()-> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
        changeRequest.setStatus(ChangeRequest.Status.DECLINED);

        changeRequestRepo.save(changeRequest);
        return ChangeRequestDto.fromEntity(changeRequest);
    }


    // 해당 매장 직원인지 확인하기
    public Account checkMember(Long shopId){
//        Account account = authFacade.extractUser();
//        Optional<AccountShop> optionalAccountShop =
//                accountShopRepo.findByShop_IdAndAccount_id(shopId, account.getId());
//        if (optionalAccountShop.isEmpty())
//            throw new IllegalArgumentException();
//        return account;
        return null;
    }

    public void checkManagerOrAdmin(Account account){
        if(account.getAuthority() == Authority.ROLE_USER)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
}
