package com.example.workmate.controller.schedule;

import com.example.workmate.dto.WorkTimeDto;
import com.example.workmate.dto.schedule.ChangeRequestDto;
import com.example.workmate.dto.schedule.ScheduleDto;
import com.example.workmate.service.schedule.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;
    // 한달 씩으로 보기
    // dto에는 year, month값만 있고 day는 0이라는 가정
    @GetMapping("view-month/{shopId}")
    public List<WorkTimeDto> viewMonth(
            @PathVariable("shopId")
            Long shopId,
            @RequestBody
            ScheduleDto month
    ){
        return scheduleService.viewMonth(shopId, month);
    }

    // 시작 기간과 끝 기간을 정해서 보기
    // dto에 모든 값이 있다는 가정
    @GetMapping("view-period/{shopId}")
    public List<WorkTimeDto> viewPeriod(
            @PathVariable("shopId")
            Long shopId,
            @RequestBody
            ScheduleDto start,
            @RequestBody
            ScheduleDto end
    ){
        return scheduleService.viewPeriod(shopId, start, end);
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
    @PutMapping ("confirm-change")
    public ChangeRequestDto confirmChange(
            @RequestParam("shopId")
            Long shopId,
            @RequestParam("changeRequestId")
            Long changeRequestId
    ){
        return scheduleService.confirmChange(shopId,changeRequestId);
    }
    @PutMapping("decline-change")
    public ChangeRequestDto declineChange(
            @RequestParam("shopId")
            Long shopId,
            @RequestParam("changeRequestId")
            Long changeRequestId
    ){
        return scheduleService.confirmChange(shopId,changeRequestId);
    }
}
