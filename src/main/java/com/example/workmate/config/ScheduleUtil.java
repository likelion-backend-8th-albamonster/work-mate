package com.example.workmate.config;

import com.example.workmate.dto.schedule.ScheduleDto;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Component
public class ScheduleUtil {

    //flag가 true면 해당 달의 시작일을 넘겨줌
    public LocalDate GetDay(Boolean flag, ScheduleDto dto){
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

}
