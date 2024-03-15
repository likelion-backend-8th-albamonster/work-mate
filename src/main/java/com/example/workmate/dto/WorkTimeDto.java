package com.example.workmate.dto;

import com.example.workmate.entity.WorkRole;
import com.example.workmate.entity.WorkTime;

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
    private Long name;
    private Long accountId;
    private Long shopId;

    private LocalDateTime workStartTime;
    private LocalDateTime workEndTime;
    private WorkRole workRole;

    public WorkTimeDto fromEntity(WorkTime entity){
        WorkTimeDtoBuilder builder = WorkTimeDto.builder()
                .id(entity.getId())
                .workStartTime(entity.getWorkStartTime())
                .workEndTime(entity.getWorkEndTime())
                .workRole(entity.getWorkRole());

        return builder.build();
    }
}
