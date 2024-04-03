package com.example.workmate.dto.attendance;

import com.example.workmate.entity.attendance.Attendance;
import com.example.workmate.entity.attendance.Status;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Tuple;
import lombok.*;

import java.time.LocalDateTime;
//출근기록페이지의 데이터를 나타내기 위한 DTO
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceLogDto {
    //출석id
    private Long attendanceId;
    //사용자id
    private Long accountId;
    //매장 id
    private Long shopId;
    //출근시간
    private LocalDateTime checkInTime;
    //퇴근시간
    private LocalDateTime checkOutTime;
    //출근상태
    @Enumerated(EnumType.STRING)
    private Status status;
    //매장명
    private String shopName;
    //사용자 name
    private String name;


}
