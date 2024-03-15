package com.example.workmate.controller.schedule;

import com.example.workmate.dto.WorkTimeDto;
import com.example.workmate.service.schedule.WorkTimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("worktime")
public class WorkTimeController {
    private final WorkTimeService workTimeService;

    @PostMapping("create")
    public WorkTimeDto create(
            @RequestParam("shopId")
            Long shopId
    ){
        workTimeService.create(shopId);
    }
}
