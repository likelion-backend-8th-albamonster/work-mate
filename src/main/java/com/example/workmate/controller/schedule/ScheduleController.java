package com.example.workmate.controller.schedule;

import com.example.workmate.component.ScheduleUtil;
import com.example.workmate.dto.account.AccountDto;
import com.example.workmate.dto.schedule.ScheduleRequestDto;
import com.example.workmate.dto.schedule.WorkTimeDto;
import com.example.workmate.dto.schedule.ChangeRequestDto;
import com.example.workmate.dto.schedule.ScheduleDto;
import com.example.workmate.dto.shop.ShopDto;
import com.example.workmate.service.ShopService;
import com.example.workmate.service.account.AccountService;
import com.example.workmate.service.schedule.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/schedule")
@CrossOrigin
public class ScheduleController {
    private final ScheduleService scheduleService;
    private final ScheduleUtil scheduleUtil;
    private final ShopService shopService;
    private final AccountService accountService;

    // 근무표 메인페이지
    @RequestMapping("/{shopId}")
    public String mainSchedule(
            Model model,
            @PathVariable("shopId")
            Long shopId,
            Authentication auth
    ){
        LocalDate now = LocalDate.now();
        ScheduleDto dto = ScheduleDto.builder()
                .year(now.getYear())
                .month(now.getMonthValue())
                .day(now.getDayOfMonth())
                .build();

        if (auth != null){
            log.info("auth: {}",auth.getName());
        }
        else
            log.info("auth is null");
        ScheduleRequestDto scheduleRequestDto = new ScheduleRequestDto();
        List<WorkTimeDto> schedules = scheduleService.viewMonth(shopId, dto);
        ShopDto shopDto = shopService.readOneShop(shopId);
        AccountDto accountDto = accountService.readOneAccount();

        if (auth != null){
            model.addAttribute("authority",auth.getAuthorities().toString());
            model.addAttribute("username",auth.getName());
        }else{
            model.addAttribute("authority", "anonymous");
            model.addAttribute("username",null);
        }
        model.addAttribute("accountId", accountDto.getId());
        model.addAttribute("scheduleRequestDto", scheduleRequestDto);
        model.addAttribute("schedules", schedules);
        model.addAttribute("shop", shopDto);
        model.addAttribute("calender",scheduleUtil.makeCalender(dto));
        return "schedule/monthly-schedule";
    }

    // 기간 동안의 근무표리스트 보기
    @PostMapping("/list-schedule/{shopId}")
    public String findSchedule(
            Model model,
            @PathVariable("shopId")
            Long shopId,
            @RequestParam("startDate")
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate startDate,
            @RequestParam("endDate")
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate endDate
    ){
        List<WorkTimeDto> schedules = scheduleService.viewPeriod(shopId,startDate,endDate);
        List<LocalDate> listSchedules = scheduleUtil.listSchedule(startDate,endDate);
        model.addAttribute("schedules", schedules);
        model.addAttribute("shopId",shopId);
        model.addAttribute("listSchedules",listSchedules);
        return "schedule/list-schedule";
    }

    // 근무 교체할 시간 고르기
    @GetMapping("/change-worktime/{shopId}")
    public String changeSchedule(
            Model model,
            @PathVariable("shopId")
            Long shopId
    ){
        ChangeRequestDto dto = new ChangeRequestDto();
        LocalDate date = LocalDate.now();
        // 현재의 시간보다 미래의 시간만 교체신청 할 수 있다.
        model.addAttribute("shopId",shopId);
        model.addAttribute("changeRequestDto", dto);

        return "schedule/change-worktime";
    }

    // 근무표 관리
    @GetMapping ("/manage-schedule/{shopId}")
    public String manageSchedule(
            @PathVariable("shopId")
            Long shopId
    ){
        return "schedule/manage-schedule";
    }

    // 근무교체 보기
    @RequestMapping("/view-change-worktime/{shopId}")
    public String viewChangeWorkTime(
            Model model,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable,
            @PathVariable("shopId")
            Long shopId
    ){
        Page<ChangeRequestDto> schedulePage = scheduleService.readChangePage(shopId,pageable);
        AccountDto accountDto = accountService.readOneAccount();

        ScheduleRequestDto dto = new ScheduleRequestDto();
        model.addAttribute("scheduleRequestDto",dto);
        model.addAttribute("schedules",schedulePage);
        model.addAttribute("accountDto",accountDto);
        model.addAttribute("shopId",shopId);

        return "schedule/view-change-worktime";
    }
    @PostMapping ("/confirm-change/{shopId}")
    public String confirmChange(
            @PathVariable("shopId")
            Long shopId,
            @RequestParam("id")
            Long changeRequestId
    ){
        scheduleService.confirmChange(changeRequestId);
        return String.format("redirect:/schedule/view-change-worktime/%d",shopId);
    }

    //근무 교체 거절
    @PostMapping("/decline-change/{shopId}")
    public String declineChange(
            @PathVariable("shopId")
            Long shopId,
            @RequestParam("id")
            Long changeRequestId
    ){
        scheduleService.declineChange(changeRequestId);
        return String.format("redirect:/schedule/view-change-worktime/%d",shopId);
    }
}
