package com.example.workmate.service;

import com.example.workmate.config.ScheduleUtil;
import com.example.workmate.repo.WorkTimeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final WorkTimeRepo workTimeRepo;
    private final ScheduleUtil scheduleUtil;

    public List<WorkTimeRepo> monthlySchedule(Long shopId){
        // 현재 날짜와 해당 달의 마지막 날짜 구하기
        LocalDateTime lastDay = scheduleUtil.GetLastDayOfMonth();
        return null;
    }
}
