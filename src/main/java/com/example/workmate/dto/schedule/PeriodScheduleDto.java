package com.example.workmate.dto.schedule;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Slf4j
@Builder
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PeriodScheduleDto {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
}
