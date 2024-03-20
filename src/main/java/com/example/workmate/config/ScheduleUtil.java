package com.example.workmate.config;

import com.example.workmate.dto.WorkTimeDto;
import com.example.workmate.dto.schedule.ScheduleDto;
import com.example.workmate.entity.WorkTime;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Component
public class ScheduleUtil {

    //flag가 true면 해당 달의 시작일을 넘겨줌
    public LocalDate getDay(Boolean flag, ScheduleDto dto){
        if((dto.getDay() == 0)){
            LocalDate localDate = LocalDate.of(dto.getYear(), dto.getMonth(), 1);
            YearMonth month = YearMonth.from(localDate);
            if (flag == true)
                return month.atEndOfMonth();
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

        for (int i = 0; i < dayOfWeek + endOfDay; i++) {
            if(i > dayOfWeek){
                days.add(i - dayOfWeek + 1);
            }
            else
                days.add(null);
        }
        return days;
    }
}
