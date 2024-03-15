package com.example.workmate.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;

@Component
public class ScheduleUtil {
    @Bean
    public LocalDateTime GetLastDayOfMonth(){
        LocalDateTime now = LocalDateTime.now();
        YearMonth month = YearMonth.from(now);
        return month.atEndOfMonth().atTime(LocalTime.MAX);
    }
}
