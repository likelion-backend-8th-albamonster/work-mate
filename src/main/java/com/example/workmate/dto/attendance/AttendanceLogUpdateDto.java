package com.example.workmate.dto.attendance;

import com.example.workmate.entity.attendance.Status;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

//출근기록수정용 DTO
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceLogUpdateDto {
    //출석id
    private Long attendanceId;
    //출근상태
    @Enumerated(EnumType.STRING)
    private Status status;
}
