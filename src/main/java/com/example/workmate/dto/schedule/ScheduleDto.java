package com.example.workmate.dto.schedule;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleDto {
    private Integer year;
    private Integer month;
    @Setter
    private Integer day;
}
