package com.example.workmate.service;

import com.example.workmate.dto.AttendanceDto;
import com.example.workmate.entity.Attendance;
import com.example.workmate.entity.Status;
import com.example.workmate.entity.account.Account;
import com.example.workmate.repo.AccountRepo;
import com.example.workmate.repo.AttendanceRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

//출퇴근관리서비스
@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepo attendanceRepo;
    private final AccountRepo accountRepo;

    //출근요청
    //이미 기록된 시간이 있는 경우 출근 등록 거부
    @Transactional
    public AttendanceDto checkIn(Long userId){
        //사용자 확인
        Account account = accountRepo.findById(userId)
                .orElseThrow(
                        ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자 정보를 확인해주세요")
                );

        //오늘 날짜에 이미 기록된 출근이 있는지 확인
        //db에서 사용자 id로 출근시간 가장 최근 데이터 꺼내오기
        // 오늘 날짜의 출근기록을 가져올 수 있도록 리팩토링
        Optional<Attendance> attendanceCk = attendanceRepo.findByAccountOrderByCheckInTime(account);
        if (attendanceCk.isPresent()){
            //현재시간 받기
            LocalDate today = LocalDate.now();
            //DB에서 꺼내온 시간
            LocalDate attendanceToday = attendanceCk.get().getCheckInTime().toLocalDate();
            //같은 날짜라면
            if (today.isEqual(attendanceToday)){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "중복 출근 요청입니다.");
            }
        }

        //저장
        Attendance attendance = Attendance.builder()
                .account(account)
                //.checkInTime(LocalDateTime.now())
                .checkInTime(LocalDateTime.of(2024,3,14,13,10,11))
                .status(Status.OK)
                .build();
        return AttendanceDto.fromEntity(attendanceRepo.save(attendance));

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
