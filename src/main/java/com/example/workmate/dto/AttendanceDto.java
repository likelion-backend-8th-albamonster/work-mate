package com.example.workmate.dto;

import com.example.workmate.entity.Attendance;
import com.example.workmate.entity.Status;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDto {
    private Long id;
    //사용자id
    private Long accountId;
    //출근시간
    private LocalDateTime checkInTime;
    //퇴근시간
    private LocalDateTime checkOutTime;
    //출근상태
    @Enumerated(EnumType.STRING)
    private Status status;

    public static AttendanceDto fromEntity(Attendance entity){
        return AttendanceDto.builder()
                .id(entity.getId())
                .accountId(entity.getAccount().getId())
                .checkInTime(entity.getCheckInTime())
                .checkOutTime(entity.getCheckOutTime())
                .status(entity.getStatus())
                .build();
    }
}
