package com.example.workmate.controller.schedule;

import com.example.workmate.component.ScheduleUtil;
import com.example.workmate.dto.WorkTimeDto;
import com.example.workmate.dto.schedule.ChangeRequestDto;
import com.example.workmate.dto.schedule.ScheduleDto;
import com.example.workmate.service.schedule.ScheduleDataService;
import com.example.workmate.service.schedule.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/schedule")
public class ScheduleController {
    private final ScheduleService scheduleService;
    private final ScheduleDataService scheduleDataService;
    private final ScheduleUtil scheduleUtil;
    //스케쥴 보는 테스트
    @ResponseBody
    @PostMapping("/make")
    public String make(){
        // 사람, 상점, accountShop 만들기
        scheduleDataService.accountShop();
        // 사람의 한 달 동안의 랜덤시간 근무 만들기
        scheduleDataService.makeWorkTime(2L);
        scheduleDataService.makeWorkTime(3L);
        return "done";
    }

    // 근무표 보기 만드는거 테스트중
    @RequestMapping("/schedule")
    public String month(Model model){
        LocalDate now = LocalDate.now();
        Long shopId = 1L;
        ScheduleDto dto = ScheduleDto.builder()
                .year(now.getYear())
                .month(now.getMonthValue())
                .day(now.getDayOfMonth())
                .build();
        List<WorkTimeDto> schedules = scheduleService.viewMonth(shopId, dto);
        model.addAttribute("schedules", schedules);
        model.addAttribute("calender",scheduleUtil.makeCalender(dto));
        return "monthly-schedule";
    }

    // 근무 만들기
    @PostMapping("/create")
    public WorkTimeDto create(
            @RequestParam WorkTimeDto dto
    ){
        return scheduleService.create(dto);
    }

    // 근무 수정하기
    @ResponseBody
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
    @ResponseBody
    @GetMapping("/read/{shopId}")
    public Page<WorkTimeDto> readPage(
            Pageable pageable,
            @PathVariable("shopId")
            Long shopId
    ){
        return scheduleService.readPage(shopId, pageable);
    }

    //근무 하나 보기
    @ResponseBody
    @GetMapping("/read-one/{workTimeId}")
    public WorkTimeDto readOne(
            @PathVariable("workTimeId")
            Long workTimeId
    ){
        return scheduleService.readOne(workTimeId);
    }
    // 근무 지우기
    @ResponseBody
    @DeleteMapping("/delete/{workTimeId}")
    public WorkTimeDto delete(
            @PathVariable("workTimeId")
            Long workTimeId
    ){
        return scheduleService.delete(workTimeId);
    }

    // 한달 씩으로 보기.
    @ResponseBody
    @GetMapping("view-month/{shopId}")
    public List<WorkTimeDto> viewMonth(
            @PathVariable("shopId")
            Long shopId,
            @RequestBody
            ScheduleDto month
    ){
        List<WorkTimeDto> workTimeDtos = scheduleService.viewMonth(shopId, month);
        return workTimeDtos;
    }

    // 시작 기간과 끝 기간을 정해서 보기 0번은 시작, 1번은 끝
    @ResponseBody
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
    @ResponseBody
    @PostMapping("create-change")
    public ChangeRequestDto changeRequest(
            @RequestBody
            ChangeRequestDto dto
    ){
        return scheduleService.createChange(dto);
    }
    // 근무표 변경요청 보기, 아직 제안 중인 것만
    @ResponseBody
    @GetMapping("view-change-all")
    public List<ChangeRequestDto> viewChangeAll(
            @RequestParam("shopId")
            Long shopId
    ){
        return scheduleService.readChangeAll(shopId);
    }

    // 근무 교체 승인
    @ResponseBody
    @PutMapping ("confirm-change")
    public ChangeRequestDto confirmChange(
            @RequestParam("changeRequestId")
            Long changeRequestId
    ){
        return scheduleService.confirmChange(changeRequestId);
    }

    //근무 교체 거절
    @ResponseBody
    @PutMapping("decline-change")
    public ChangeRequestDto declineChange(
            @RequestParam("changeRequestId")
            Long changeRequestId
    ){
        return scheduleService.confirmChange(changeRequestId);
    }
}
