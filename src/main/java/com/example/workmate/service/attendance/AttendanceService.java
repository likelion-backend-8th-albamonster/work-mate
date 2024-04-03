package com.example.workmate.service.attendance;

import com.example.workmate.dto.attendance.AttendanceDto;
import com.example.workmate.dto.attendance.AttendanceLogDto;
import com.example.workmate.dto.attendance.AttendanceLogUpdateDto;
import com.example.workmate.entity.AccountShop;
import com.example.workmate.entity.Shop;
import com.example.workmate.entity.account.Account;
import com.example.workmate.entity.account.Authority;
import com.example.workmate.entity.attendance.Attendance;
import com.example.workmate.entity.attendance.Status;
import com.example.workmate.facade.AuthenticationFacade;
import com.example.workmate.repo.AccountRepo;
import com.example.workmate.repo.AccountShopRepo;
import com.example.workmate.repo.ShopRepo;
import com.example.workmate.repo.attendance.AttendanceRepo;
import com.example.workmate.repo.attendance.AttendanceRepoDsl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final AttendanceRepoDsl attendanceRepoDsl;
    private final AccountShopRepo accountShopRepo;
    private final AuthenticationFacade authFacade;

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
    public Page<AttendanceLogDto> showLogAll(
            Integer pageNumber,
            Integer pageSize,
            String sortType,
            Long accountId,
            Authority authority
    ){
        Pageable pageable = PageRequest.of(pageNumber,pageSize,
                Sort.by(sortType).descending());

        Page<AttendanceLogDto> attendanceLogDtoPage;
        //권한 확인
        //관리자라면
        if (authority != Authority.ROLE_USER){
            //한 계정에 대한 모든 accountShop 데이터 가져오기
            Optional<List<AccountShop>> optionalAccountShopList
                    = accountShopRepo.findAllByAccount_id(accountId);
            //데이터 존재 시
            if (optionalAccountShopList.isPresent()){
                List<AccountShop> accountShopList
                        = optionalAccountShopList.orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "계정과 매장 정보를 확인해주세요")
                );
                List<Long> accountShopIdList = new ArrayList<>();
                //id 추출
                for(AccountShop accountShop : accountShopList){
                    accountShopIdList.add(accountShop.getShop().getId());
                }
                //데이터 가져오기(관리자용)
                attendanceLogDtoPage
                        = attendanceRepoDsl.readUserAttendanceLogForAdmin(accountShopIdList, pageable);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "계정과 매장 정보를 확인해주세요");
            }


        } else {
            //일반 사용자라면
            attendanceLogDtoPage
                    = attendanceRepoDsl.readUserAttendanceLog(accountId, pageable);
        }

        return attendanceLogDtoPage;
    }

    //한 사용자의 모든 출퇴근 검색 기록
    public Page<AttendanceLogDto> showLogAllForSearch(
            Integer pageNumber,
            Integer pageSize,
            String sortType,
            Long accountId,
            Authority authority,
            String searchDuration,
            String searchWord,
            String searchType
    ){
        Pageable pageable = PageRequest.of(pageNumber,pageSize,
                Sort.by(sortType).descending());

        Page<AttendanceLogDto> attendanceLogDtoPage;

        //기간 확인
        LocalDateTime thisTime = LocalDateTime.now();
        LocalDateTime searchTime;

        if ("allDay".equals(searchDuration)){
            searchTime = null;
        } else if("oneMonth".equals(searchDuration)){
            searchTime = thisTime.minusMonths(1);
        }else if("oneWeek".equals(searchDuration)){
            searchTime = thisTime.minusWeeks(1);
        }else if("oneDay".equals(searchDuration)){
            searchTime = thisTime.minusDays(1);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "검색기간을 확인해주세요");
        }

        //권한 확인
        //관리자라면
        if (authority != Authority.ROLE_USER){
            //한 계정에 대한 모든 accountShop 데이터 가져오기
            Optional<List<AccountShop>> optionalAccountShopList
                    = accountShopRepo.findAllByAccount_id(accountId);
            //데이터 존재 시
            if (optionalAccountShopList.isPresent()){
                List<AccountShop> accountShopList
                        = optionalAccountShopList.orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "계정과 매장 정보를 확인해주세요")
                );
                List<Long> accountShopIdList = new ArrayList<>();
                //id 추출
                for(AccountShop accountShop : accountShopList){
                    accountShopIdList.add(accountShop.getShop().getId());
                }
                //데이터 가져오기(관리자용)
                attendanceLogDtoPage
                        = attendanceRepoDsl
                            .readUserAttendanceLogForAdminForSearch(
                                    accountShopIdList, pageable,
                                    thisTime, searchTime,
                                    searchWord, searchType);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "계정과 매장 정보를 확인해주세요");
            }


        } else {
            //일반 사용자라면
            attendanceLogDtoPage
                    = attendanceRepoDsl
                        .readUserAttendanceLogForSearch(
                                accountId, pageable,
                                thisTime, searchTime,
                                searchWord, searchType);
        }

        return attendanceLogDtoPage;
    }

    //출퇴근
    public Page<AttendanceLogDto> showLog(
            Integer pageNumber,
            Integer pageSize,
            String sortType,
            Long accountId,
            Long shopId,
            Authority authority
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
                Sort.by(sortType).descending());

        //한 계정의 한 매장에 대한 것만 가져온다.
        Page<AttendanceLogDto> attendanceLogDtoPage
                = attendanceRepoDsl.readUserOneShopAttendanceLog(accountId, shopId, pageable, authority);

        return attendanceLogDtoPage;
    }

    //출퇴근 검색기록
    public Page<AttendanceLogDto> showLogForSearch(
            Integer pageNumber,
            Integer pageSize,
            Long accountId,
            Long shopId,
            Authority authority,
            String searchDuration,
            String searchWord,
            String searchType
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

        //한 계정의 한 매장에 대한 것만 가져온다.
        Page<AttendanceLogDto> attendanceLogDtoPage
                = attendanceRepoDsl.readUserOneShopAttendanceLog(accountId, shopId, pageable, authority);

        return attendanceLogDtoPage;
    }

    //한 매장의 모든 출근 리스트
    public Page<AttendanceLogDto> showOneShopLog(
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
        //한 계정의 한 매장에 대한 것만 가져온다.
        //querydsl로 가져온다.
        Page<AttendanceLogDto> attendanceLogDtoPage
                = attendanceRepoDsl.readOneShopAttendanceLog(shopId, pageable);

        return attendanceLogDtoPage;
    }

    //한 사용자가 다니는 매장리스트
    public List<Shop> readOneAccountShopList(Long accountId){
        //사용자 확인
        Account account = accountRepo.findById(accountId)
                .orElseThrow(
                        ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자 정보를 확인해주세요")
                );
        List<Long> accountShopIdList = new ArrayList<>();

        //accountShop에서 사용자가 다니는 매장리스트 추출
        Optional<List<AccountShop>> optionalAccountShopList
                = accountShopRepo.findAllByAccount_id(accountId);
        //데이터 존재 시
        if (optionalAccountShopList.isPresent()){
            List<AccountShop> accountShopList
                    = optionalAccountShopList.orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "계정과 매장 정보를 확인해주세요")
            );
            //id 추출
            for(AccountShop accountShop : accountShopList){
                accountShopIdList.add(accountShop.getShop().getId());
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "계정과 매장 정보를 확인해주세요");
        }

        return shopRepo.findAllById(accountShopIdList);
    }




    //출퇴근 수정(관리자)
    //모든 아르바이트생의 출퇴근 기록 수정 가능
    //Status를 수정하여, 정상출근 / 지각 / 조퇴 상태 변경
    @Transactional
    public void updateLogAll(
            Long shopId,
        List<AttendanceLogUpdateDto> updateDto

    ){
        attendanceRepoDsl.udpateAttendanceList(shopId, updateDto);
    }

    @Transactional
    public void updateLog(Long attendanceId, String status){
        Attendance attendance = attendanceRepo.findById(attendanceId)
                .orElseThrow(
                        ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "출퇴근 정보를 확인해주세요")
                );

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


    //검색기능
    public Page<AttendanceLogDto> showLogSearch(
            Long accountId,
            Integer pageNumber,
            Integer pageSize,
            String sortType,
            String searchDuration,
            String searchWord,
            String searchType,
            Account account
    ){
        Page<AttendanceLogDto> attendanceLogList;
        //한 유저의 모든 매장 출근 검색 데이터 가져오기
        attendanceLogList
                = showLogAllForSearch(
                        pageNumber,pageSize,sortType,
                        accountId, account.getAuthority(),
                        searchDuration,searchWord,searchType);

        return attendanceLogList;
    }
    
    //접근요청한 사용자명이 jwt 안의 사용자명과 일치하는가
    public void checkSameAccount(Account account){
        if (!authFacade.getAuth().getName().equals(account.getUsername())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "사용자 정보가 일치하지 않습니다.");
        }
    }

}
