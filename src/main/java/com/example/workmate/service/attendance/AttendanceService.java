package com.example.workmate.service.attendance;

import com.example.workmate.dto.attendance.AttendanceDto;
import com.example.workmate.entity.attendance.Attendance;
import com.example.workmate.entity.Shop;
import com.example.workmate.entity.attendance.Status;
import com.example.workmate.entity.account.Account;
import com.example.workmate.repo.AccountRepo;
import com.example.workmate.repo.attendance.AttendanceRepo;
import com.example.workmate.repo.ShopRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//출퇴근관리서비스
@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepo attendanceRepo;
    private final AccountRepo accountRepo;
    private final ShopRepo shopRepo;

    //출근요청
    //이미 기록된 시간이 있는 경우 출근 등록 거부
    @Transactional
    public AttendanceDto checkIn(Long accountId, Long shopId){
        //사용자 확인
        Account account = accountRepo.findById(accountId)
                .orElseThrow(
                        ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자 정보를 확인해주세요")
                );
        //매장 확인
        Shop shop = shopRepo.findById(shopId)
                .orElseThrow(
                        ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "매장 정보를 확인해주세요")
                );

        //오늘 날짜에 이미 기록된 출근이 있는지 확인
        //db에서 사용자 id와 매장 id로 가장 최근 데이터 하나 꺼내오기
        if (isExistTodayCheckIn(accountId, shopId)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "중복 출근 요청입니다.");
        }

        //저장
        Attendance attendance = Attendance.builder()
                .account(account)
                .shop(shop)
                .checkInTime(LocalDateTime.now())
                //.checkInTime(LocalDateTime.of(2024,3,20,1,30))
                .status(Status.IN)
                .build();
        return AttendanceDto.fromEntity(attendanceRepo.save(attendance));

    }

    //퇴근요청
    //front단에서 출근데이터 존재 확인
    //퇴근시간이 자신의 근무종료시간보다 늦다면 추가정산이 이루어진다.
    @Transactional
    public AttendanceDto checkOut(Long attendanceId){
        Attendance attendance = attendanceRepo.findById(attendanceId)
                .orElseThrow(
                        ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "출퇴근 정보를 확인해주세요")
                );
        //출근 데이터 값이 이미 퇴근상태일때
        if (Status.OUT.equals(attendance.getStatus())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "중복퇴근 요청입니다.");
        }
        //오늘 날짜의 출근 데이터 status 수정
        attendance.setStatus(Status.OUT);
        attendance.setCheckOutTime(LocalDateTime.now());
        return AttendanceDto.fromEntity(attendanceRepo.save(attendance));
    }
    //쉬는시간요청
    @Transactional
    public void restIn(Long attendanceId){
        Attendance attendance = attendanceRepo.findById(attendanceId)
                .orElseThrow(
                        ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "출퇴근 정보를 확인해주세요")
                );
        //이미 휴식상태일때
        if (Status.REST_IN.equals(attendance.getStatus())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "중복 휴식 요청입니다.");
        }
        //오늘 날짜의 출근 데이터 status 수정
        attendance.setStatus(Status.REST_IN);
        attendanceRepo.save(attendance);
    }
    //쉬는시간종료요청
    @Transactional
    public void restOut(Long attendanceId){
        Attendance attendance = attendanceRepo.findById(attendanceId)
                .orElseThrow(
                        ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "출퇴근 정보를 확인해주세요")
                );
        //이미 휴식상태일때
        if (Status.REST_OUT.equals(attendance.getStatus())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "중복 휴식종료 요청입니다.");
        }
        //오늘 날짜의 출근 데이터 status 수정
        attendance.setStatus(Status.REST_OUT);
        attendanceRepo.save(attendance);
    }

    //오늘 날짜에 이미 기록된 출근이 있는지 확인
    public boolean isExistTodayCheckIn(Long accountId, Long shopId){
        //사용자 확인
        Account account = accountRepo.findById(accountId)
                .orElseThrow(
                        ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자 정보를 확인해주세요")
                );
        //매장 확인
        Shop shop = shopRepo.findById(shopId)
                .orElseThrow(
                        ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "매장 정보를 확인해주세요")
                );
        //체크용
        boolean isExist = false;
        //오늘 날짜에 이미 기록된 출근이 있는지 확인
        //db에서 사용자 id와 매장 id로 가장 최근 데이터 하나 꺼내오기
        Optional<Attendance> attendanceCk
                = attendanceRepo.findTopByAccountAndShopOrderByCheckInTimeDesc(account, shop);
        if (attendanceCk.isPresent()){
            //오늘날짜 받기
            LocalDate today = LocalDate.now();
            //DB에서 꺼내온 시간을 날짜형태로 변경
            LocalDate attendanceToday = attendanceCk.get().getCheckInTime().toLocalDate();
            //같은 날짜인지 비교
            if (today.isEqual(attendanceToday)){
                //throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "중복 출근 요청입니다.");
                isExist = true;
            }
        }
        return isExist;
    }

    //한 사용자의 모든 출퇴근 기록
    public Page<AttendanceDto> showLogAll(
            Integer pageNumber,
            Integer pageSize,
            Long accountId
    ){
        //사용자 확인
        Account account = accountRepo.findById(accountId)
                .orElseThrow(
                        ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자 정보를 확인해주세요")
                );
        Pageable pageable = PageRequest.of(pageNumber,pageSize,
                Sort.by("id").descending());

        //한 계정에 대한 것만 가져온다.
        //TODO querydsl로 변경
        // inner join 사용
        Page<Attendance> attendancePage
                = attendanceRepo.findAllByAccount(pageable, account);

        return new PageImpl<>(
                attendancePage.stream().map(AttendanceDto::fromEntity).toList(),
                pageable,
                attendancePage.getSize()
        );
    }

    //출퇴근 기록 보기
    //권한을 확인하여. 관리자와 아르바이트생은 서로다른 결과를 return
    //아르바이트생은 자기 자신의 출퇴근 기록만 확인 가능
    //관리자는 모든 아르바이트생의 출퇴근 기록 확인 가능
    public Page<AttendanceDto> showLog(
            Integer pageNumber,
            Integer pageSize,
            Long accountId,
            Long shopId
    ){
        //사용자 확인
        Account account = accountRepo.findById(accountId)
                .orElseThrow(
                        ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자 정보를 확인해주세요")
                );
        //매장 확인
        Shop shop = shopRepo.findById(shopId)
                .orElseThrow(
                        ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "매장 정보를 확인해주세요")
                );

        Pageable pageable = PageRequest.of(pageNumber,pageSize,
                Sort.by("id").descending());

        //TODO querydsl로 변경
        // inner join 사용
        //한 계정에 대한 것만 가져온다.
        Page<Attendance> attendancePage
                = attendanceRepo.findAllByAccountAndShop(pageable, account, shop);

        Page<AttendanceDto> attendanceDtoPage = new PageImpl<>(
                attendancePage.stream().map(AttendanceDto::fromEntity).toList(),
                pageable,
                attendancePage.getSize()
        );

        return attendanceDtoPage;
    }

    //한 사용자가 다니는 매장리스트
    public List<Shop> readOneAccountShopList(Long accountId){
        //사용자 확인
        Account account = accountRepo.findById(accountId)
                .orElseThrow(
                        ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자 정보를 확인해주세요")
                );

        List<Long> accountShopIdList = new ArrayList<>();
        //한 계정에 대한 모든 출퇴근 정보
        List<Attendance> attendanceList
                = attendanceRepo.findAllByAccount(account);
        //id 넣기
        for(Attendance attendance : attendanceList){
            accountShopIdList.add(attendance.getShop().getId());
        }

        return shopRepo.findAllById(accountShopIdList);
    }


    //출퇴근 수정(관리자)
    //모든 아르바이트생의 출퇴근 기록 수정 가능
    //Status를 수정하여, 정상출근 / 지각 / 조퇴 상태 변경
    public void udpate(Long accountId, Long shopId, Long adminId, Long attendanceId, String status){
        //수정 진행하는 관리자가 권한을 가지고 있는가

        //수정할 사용자 확인
        Account account = accountRepo.findById(accountId)
                .orElseThrow(
                        ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자 정보를 확인해주세요")
                );
        //매장 확인
        Shop shop = shopRepo.findById(shopId)
                .orElseThrow(
                        ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "매장 정보를 확인해주세요")
                );

        //수정할 사용자의 매장 직원이 맞는가
            //attendance_shop에서 adminId로 data 가져오기
            //가져온 데이터의 attendanceShopId가 shopId와 일치하는지 체크

        //사용자의 출퇴근 데이터 가져오기
        Attendance attendance = attendanceRepo.findById(attendanceId)
                .orElseThrow(
                        ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "출퇴근 정보를 확인해주세요")
                );
        //출퇴근 데이터의 status 값을, 받아온 status값으로 변경
        //enum에 존재하는 값일 때
        attendance.setStatus(Status.valueOf(status));
        attendanceRepo.save(attendance);
    }

    //하나 찾기
    public AttendanceDto readOne(Long accountId, Long shopId){
        //사용자 확인
        Account account = accountRepo.findById(accountId)
                .orElseThrow(
                        ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자 정보를 확인해주세요")
                );
        //매장 확인
        Shop shop = shopRepo.findById(shopId)
                .orElseThrow(
                        ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "매장 정보를 확인해주세요")
                );
        Attendance attendance
                = attendanceRepo.findTopByAccountAndShopOrderByCheckInTimeDesc(account, shop)
                .orElseThrow(
                        ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "출퇴근 정보를 확인해주세요")
                );
        return AttendanceDto.fromEntity(attendance);
    }

    //TODO 검색기능


}
