package com.example.workmate.controller.schedule;

import com.example.workmate.component.ScheduleUtil;
import com.example.workmate.dto.schedule.ChangeRequestDto;
import com.example.workmate.dto.schedule.ScheduleRequestDto;
import com.example.workmate.dto.schedule.ScheduleDto;
import com.example.workmate.dto.schedule.WorkTimeDto;
import com.example.workmate.service.schedule.ScheduleDataService;
import com.example.workmate.service.schedule.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedule")
public class ScheduleRestController {
    private final ScheduleService scheduleService;
    private final ScheduleDataService scheduleDataService;
    private final ScheduleUtil scheduleUtil;

    // 사람, 상점, accountShop 만들기
    @PostMapping("/account-shop")
    public String accountshop(
            @RequestParam
            Long accountId,
            @RequestParam
            Long shopId
    ){
        scheduleDataService.accountShop(accountId,shopId);
        return "done";
    }

    // 한 사람의 한 달 동안의 랜덤시간 근무 만들기
    @PostMapping("/make")
    public String make(
            @RequestParam
            Long accountId,
            @RequestParam
            Long shopId,
            @RequestParam
            int year,
            @RequestParam
            int month
    ){
        scheduleDataService.makeWorkTime(accountId,shopId,year,month);
        return "done";
    }

    // 근무 만들기
    @PostMapping("/create")
    public WorkTimeDto create(
            @RequestParam WorkTimeDto dto
    ){
        return scheduleService.create(dto);
    }

    // 근무 수정하기
    @GetMapping("/update/{workTimeId}")
    public WorkTimeDto update(
            @RequestParam WorkTimeDto dto,
            @PathVariable("workTimeId")
            Long workTimeId
            // git check
    ){
        return scheduleService.update(workTimeId, dto);
    }

    // 근무 모두 보기
    @GetMapping("/read/{shopId}")
    public Page<WorkTimeDto> readPage(
            Pageable pageable,
            @PathVariable("shopId")
            Long shopId
    ){
        return scheduleService.readPage(shopId, pageable);
    }

    //근무 하나 보기
    @GetMapping("/read-one/{workTimeId}")
    public WorkTimeDto readOne(
            @PathVariable("workTimeId")
            Long workTimeId
    ){
        return scheduleService.readOne(workTimeId);
    }
    // 근무 지우기
    @DeleteMapping("/delete/{workTimeId}")
    public WorkTimeDto delete(
            @PathVariable("workTimeId")
            Long workTimeId
    ){
        return scheduleService.delete(workTimeId);
    }

    // 한달 씩으로 보기.
    @GetMapping("/view-month/{shopId}")
    public List<WorkTimeDto> viewMonth(
            @PathVariable("shopId")
            Long shopId,
            @RequestBody
            ScheduleDto month
    ){
        return scheduleService.viewMonth(shopId, month);
    }

    // 시작 기간과 끝 기간을 정해서 보기 0번은 시작, 1번은 끝
    @GetMapping("/view-period/{shopId}")
    public List<WorkTimeDto> viewPeriod(
            @PathVariable("shopId")
            Long shopId,
            @RequestBody
            ScheduleRequestDto dto
    ){
        return scheduleService.viewPeriod(shopId,dto.getStartDate(), dto.getEndDate());
    }

    // 근무표 변경요청만들기
    @PostMapping("/create-change")
    public ChangeRequestDto changeRequest(
            @RequestBody
            ChangeRequestDto dto
    ){
        return scheduleService.createChange(dto);
    }
    // 근무표 변경요청 보기, 아직 제안 중인 것만
    @GetMapping("/read-change/{shopId}")
    public List<ChangeRequestDto> readChangeAll(
            @PathVariable("shopId")
            Long shopId
    ){
        return scheduleService.readChangeAll(shopId);
    }

    // 근무 교체 승인
    @PutMapping ("/confirm-change/{changeRequestId}")
    public ChangeRequestDto confirmChange(
            @PathVariable
            Long changeRequestId
    ){
        return scheduleService.confirmChange(changeRequestId);
    }

    //근무 교체 거절
    @PutMapping("/decline-change/{changeRequestId}")
    public ChangeRequestDto declineChange(
            @PathVariable
            Long changeRequestId
    ){
        return scheduleService.declineChange(changeRequestId);
    }
}
