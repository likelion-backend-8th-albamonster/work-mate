package com.example.workmate.component;

import com.example.workmate.dto.schedule.ScheduleDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class ScheduleUtil {

    //flag가 true면 해당 달의 시작일을 넘겨줌
    public LocalDate getDay(Boolean flag, ScheduleDto dto){
        if((dto.getDay() == 0)){
            LocalDate localDate = LocalDate.of(dto.getYear(), dto.getMonth(), 1);
            YearMonth month = YearMonth.from(localDate);
            if (flag)
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

    // 캘린더 한달 칸 수 만들어주기
    public List<Integer> makeCalender(ScheduleDto dto){
        LocalDate localDate = LocalDate.of(dto.getYear(), dto.getMonth(), 1);
        YearMonth month = YearMonth.from(localDate);

        int endOfDay = month.atEndOfMonth().getDayOfMonth();
        int dayOfWeek = getDayOfWeek(dto);
        List<Integer> days = new ArrayList<>();
        int allWeek = (dayOfWeek + endOfDay) / 7 + 1;
        log.info("allWeek * 7 - dayOfWeek - 1: {}",(allWeek * 7 - dayOfWeek - 1));
        for (int i = 0; i < (allWeek * 7); i++) {
            if((i >= dayOfWeek)&&(i < endOfDay + dayOfWeek)){
                days.add(i - dayOfWeek + 1);
            }
            else
                days.add(-1);
        }
        log.info(days.toString());
        return days;
    }

    public List<LocalDate> listSchedule(LocalDate startDate, LocalDate endDate){
        LocalDate date = LocalDate.ofEpochDay(endDate.toEpochDay() - startDate.toEpochDay());
        int intDate = date.getDayOfYear();

        log.info("intDate: {}",intDate);
        List<LocalDate> listSchedule = new ArrayList<>();
        for (int i = 0; i < intDate; i++) {
            listSchedule.add(startDate.plusDays(i));
            log.info(listSchedule.get(i).toString());
        }
        return listSchedule;
    }
}
