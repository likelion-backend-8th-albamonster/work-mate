package com.example.workmate.controller.schedule;

import com.example.workmate.dto.WorkTimeDto;
import com.example.workmate.dto.schedule.ChangeRequestDto;
import com.example.workmate.dto.schedule.ScheduleDto;
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
@RequestMapping("schedule")
public class ScheduleController {
    private final ScheduleService scheduleService;
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
    @GetMapping("view-month/{shopId}")
    public List<WorkTimeDto> viewMonth(
            @PathVariable("shopId")
            Long shopId,
            @RequestBody
            ScheduleDto month
    ){
        return scheduleService.viewMonth(shopId, month);
    }

    // 시작 기간과 끝 기간을 정해서 보기 0번은 시작, 1번은 끝
    @GetMapping("view-period/{shopId}")
    public List<WorkTimeDto> viewPeriod(
            @PathVariable("shopId")
            Long shopId,
            @RequestBody
            List<ScheduleDto> dtos
    ){
        return scheduleService.viewPeriod(shopId, dtos.get(0), dtos.get(1));
    }

    // 근무표 변경요청만들기
    @PostMapping("create-change")
    public ChangeRequestDto changeRequest(
            @RequestBody
            ChangeRequestDto dto
    ){
        return scheduleService.createChange(dto);
    }
    // 근무표 변경요청 보기, 아직 제안 중인 것만
    @GetMapping("view-change-all")
    public List<ChangeRequestDto> viewChangeAll(
            @RequestParam("shopId")
            Long shopId
    ){
        return scheduleService.readChangeAll(shopId);
    }

    // 근무 교체 승인
    @PutMapping ("confirm-change")
    public ChangeRequestDto confirmChange(
            @RequestParam("changeRequestId")
            Long changeRequestId
    ){
        return scheduleService.confirmChange(changeRequestId);
    }

    //근무 교체 거절
    @PutMapping("decline-change")
    public ChangeRequestDto declineChange(
            @RequestParam("changeRequestId")
            Long changeRequestId
    ){
        return scheduleService.confirmChange(changeRequestId);
    }
}
