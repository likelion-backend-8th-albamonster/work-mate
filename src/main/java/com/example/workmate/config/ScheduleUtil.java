package com.example.workmate.config;

import com.example.workmate.dto.WorkTimeDto;
import com.example.workmate.dto.schedule.ScheduleDto;
import com.example.workmate.entity.WorkTime;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Component
@Slf4j
public class ScheduleUtil {

    //flag가 true면 해당 달의 시작일을 넘겨줌
    public LocalDate getDay(Boolean flag, ScheduleDto dto){
        if((dto.getDay() == 0)){
            LocalDate localDate = LocalDate.of(dto.getYear(), dto.getMonth(), 1);
            YearMonth month = YearMonth.from(localDate);
            if (flag == true)
                return localDate;
            return month.atEndOfMonth();
        }
        LocalDate localDate = LocalDate.of(dto.getYear(), dto.getMonth(), dto.getDay());
        return localDate;
    }

    public int getDayOfWeek(ScheduleDto dto){
        LocalDate localDate = LocalDate.of(dto.getYear(), dto.getMonth(), 1);
        return (localDate.getDayOfWeek().getValue())%7;
    }

    public List<Integer> makeCalender(ScheduleDto dto){
        LocalDate localDate = LocalDate.of(dto.getYear(), dto.getMonth(), 1);
        YearMonth month = YearMonth.from(localDate);

        int endOfDay = month.atEndOfMonth().getDayOfMonth();
        int dayOfWeek = getDayOfWeek(dto);
        List<Integer> days = new ArrayList<>();

        log.info("dayOfweek: {}",dayOfWeek);
        log.info("endOfDay: {}",endOfDay);

        int allWeek = (dayOfWeek + endOfDay) / 7 + 1;
        for (int i = 0; i < (allWeek * 7); i++) {
            if((i >= dayOfWeek)&&(i < allWeek * 7 - dayOfWeek - 1)){
                days.add(i - dayOfWeek + 1);
            }
            else
                days.add(-1);
        }
        log.info(days.toString());
        return days;
    }
}
