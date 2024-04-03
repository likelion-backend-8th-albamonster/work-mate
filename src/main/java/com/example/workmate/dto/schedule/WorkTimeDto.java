package com.example.workmate.dto.schedule;

import com.example.workmate.entity.schedule.WorkRole;
import com.example.workmate.entity.schedule.WorkTime;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Getter
@Slf4j
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class WorkTimeDto {
    private Long id;
    private String name;
    private Long accountId;
    private Long shopId;

    private LocalDateTime workStartTime;
    private LocalDateTime workEndTime;
    private WorkRole workRole;

    public static WorkTimeDto fromEntity(WorkTime entity){
        WorkTimeDtoBuilder builder = WorkTimeDto.builder()
                .id(entity.getId())
                .name(entity.getAccount().getUsername())
                .workStartTime(entity.getWorkStartTime())
                .workEndTime(entity.getWorkEndTime())
                .workRole(entity.getWorkRole());

        return builder.build();
    }
}
