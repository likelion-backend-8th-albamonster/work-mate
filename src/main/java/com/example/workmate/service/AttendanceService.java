package com.example.workmate.service;

import com.example.workmate.dto.AttendanceDto;
import com.example.workmate.entity.Attendance;
import com.example.workmate.repo.AttendanceRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

//출퇴근관리서비스
@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepo attendanceRepo;

    //출근요청
    //NCP api를 통해 사용자의 위치를 확인
    //이미 기록된 시간이 있는 경우 출근 등록 거부
    public void checkIn(){

    }
    //퇴근요청
    //퇴근시간이 자신의 근무종료시간보다 늦다면 추가정산이 이루어진다.
    public void checkOut(){

    }
    //쉬는시간요청
    public void restIn(){

    }
    //쉬는시간종료요청
    public void restOut(){

    }

    //출퇴근 기록 보기
    //권한을 확인하여. 관리자와 아르바이트생은 서로다른 결과를 return
    //아르바이트생은 자기 자신의 출퇴근 기록만 확인 가능
    //관리자는 모든 아르바이트생의 출퇴근 기록 확인 가능
    public Page<AttendanceDto> showLog(
            Integer pageNumber,
            Integer pageSize
    ){
        Pageable pageable = PageRequest.of(pageNumber,pageSize,
                Sort.by("id").descending());

        //이부분에 대한
        Page<Attendance> attendancePage
                = attendanceRepo.findAll(pageable);
        return new PageImpl<>(
                attendancePage.stream().map(AttendanceDto::fromEntity).toList(),
                pageable,
                attendancePage.getSize()
        );
    }

    //출퇴근 수정(관리자)
    //모든 아르바이트생의 출퇴근 기록 수정 가능
    //Status를 수정하여, 정상출근 / 지각 / 조퇴 상태 변경
    public void udpate(){

    }

    //TODO 검색기능


}
