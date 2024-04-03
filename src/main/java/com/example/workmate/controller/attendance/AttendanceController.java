package com.example.workmate.controller.attendance;

import com.example.workmate.dto.attendance.AttendanceDto;
import com.example.workmate.dto.attendance.AttendanceLogDto;
import com.example.workmate.dto.ncpdto.PointDto;
import com.example.workmate.entity.account.Account;
import com.example.workmate.entity.attendance.Status;
import com.example.workmate.facade.AuthenticationFacade;
import com.example.workmate.repo.AccountRepo;
import com.example.workmate.repo.ShopRepo;
import com.example.workmate.service.ShopService;
import com.example.workmate.service.account.AccountService;
import com.example.workmate.service.attendance.AttendanceService;
import com.example.workmate.service.ncpservice.NaviService;
import com.example.workmate.service.schedule.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Slf4j
@Controller
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService attendanceService;
    private final NaviService naviService;
    private final AccountRepo accountRepo;
    private final ShopRepo shopRepo;
    private final ScheduleService scheduleService;

    //출근요청페이지
    //요청 시 사용자 정보 / 매장정보 / 출근 정보를 확인
    @GetMapping("/{accountId}/{shopId}")
    public String attendance(
            @PathVariable("accountId")
            Long accountId,
            @PathVariable("shopId")
            Long shopId,
            //사용자의 ip와 매장 주소가 model에 들어가야 함
            Model model
    ){

        //사용자정보 확인
        Account account = accountRepo.findById(accountId)
                .orElseThrow(
                        ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자 정보를 확인해주세요")
                );
        //사용자 동일 여부 체크
        attendanceService.checkSameAccount(account);
        model.addAttribute("account", account);

        //해당매장에 다니는 사람인지 체크
        scheduleService.checkMember(shopId);

        //매장 정보
        model.addAttribute("shop", shopRepo.findById(shopId)
                .orElseThrow(
                        ()->new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "매장 정보를 확인해주세요")));

        //오늘 날짜에 이미 기록된 출근이 있는지 확인
        boolean isExist = attendanceService.isExistTodayCheckIn(accountId, shopId);
        model.addAttribute("isExist", isExist);
        //출퇴근 데이터가 존재할때
        if (isExist){
            model.addAttribute("attendance", attendanceService.readOne(accountId, shopId));
        }
        return "attendance/attendance";
    }

    //출근요청
    //NCP api를 통해 사용자의 위치가 매장 근처인지 확인
    //이미 기록된 시간이 있는 경우 출근 등록 거부
    @PostMapping("/checkIn/{accountId}/{shopId}")
    public String checkIn(
            @PathVariable("accountId")
            Long accountId,
            @PathVariable("shopId")
            Long shopId,
            @RequestParam("userLat")
            double userLat,
            @RequestParam("userLng")
            double userLng,
            @RequestParam("shopAddress")
            String shopAddress,
            //리다이렉트 값을 보내기 위한 변수
            RedirectAttributes redirectAttributes
    ){
        //사용자정보 확인
        Account account = accountRepo.findById(accountId)
                .orElseThrow(
                        ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자 정보를 확인해주세요")
                );
        //사용자 동일 여부 체크
        attendanceService.checkSameAccount(account);
        //해당매장에 다니는 사람인지 체크
        scheduleService.checkMember(shopId);

        //사용자 좌표
        PointDto userPointDto = new PointDto(userLat, userLng);
        //사용자가 매장 위치에 있는지 확인
        if (naviService.checkTwoPoint(userPointDto, shopAddress)){
            //오늘 날짜에 이미 기록된 출근이 있는지 확인
            boolean isExist = attendanceService.isExistTodayCheckIn(accountId, shopId);
            //이미 출근정보가 있다면
            if (isExist){
                //"중복 출근 요청입니다." alert창으로 나타내기
                redirectAttributes.addFlashAttribute("msg", "중복 출근 요청입니다.");
            }
            //사용자 출근 정보 저장
            AttendanceDto dto = attendanceService.checkIn(accountId, shopId);
            //출근 확인되었습니다 alert창으로 나타내기
            redirectAttributes.addFlashAttribute("msg", "출근 확인되었습니다.");
        }
        //매장위치에 있지 않으면
        else {
            redirectAttributes.addFlashAttribute("msg", "현재 위치를 확인해주세요.");
        }
        return String.format("redirect:/attendance/%d/%d", accountId, shopId);
    }

    //퇴근요청
    @PostMapping("/checkOut/{accountId}/{shopId}")
    public String checkOut(
            @PathVariable("accountId")
            Long accountId,
            @PathVariable("shopId")
            Long shopId,
            @RequestParam("userLat")
            double userLat,
            @RequestParam("userLng")
            double userLng,
            @RequestParam("shopAddress")
            String shopAddress,
            //출퇴근정보
            @RequestParam("attendanceId")
            Long attendanceId,
            //리다이렉트 값을 보내기 위한 변수
            RedirectAttributes redirectAttributes
    ){
        //사용자정보 확인
        Account account = accountRepo.findById(accountId)
                .orElseThrow(
                        ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자 정보를 확인해주세요")
                );
        //사용자 동일 여부 체크
        attendanceService.checkSameAccount(account);

        //해당매장에 다니는 사람인지 체크
        scheduleService.checkMember(shopId);
        //사용자 좌표
        PointDto userPointDto = new PointDto(userLat, userLng);
        //사용자가 매장 위치에 있는지 확인
        if (naviService.checkTwoPoint(userPointDto, shopAddress)){
            //사용자 퇴근 정보 저장
            attendanceService.checkOut(attendanceId);
            //퇴근 확인되었습니다 alert창으로 나타내기
            redirectAttributes.addFlashAttribute("msg", "퇴근 확인되었습니다.");
        }
        //매장위치에 있지 않으면
        else {
            //"현재 위치를 확인해주세요" alert창으로 나타내기
            redirectAttributes.addFlashAttribute("msg", "현재 위치를 확인해주세요.");
        }
        return String.format("redirect:/attendance/%d/%d", accountId, shopId);
    }

    //쉬는시간요청
    //출근상태에만 쉬는시간 요청이 가능
    @PostMapping("/restIn/{accountId}/{shopId}")
    public String restIn(
            @PathVariable("accountId")
            Long accountId,
            @PathVariable("shopId")
            Long shopId,
            @RequestParam("userLat2")
            double userLat,
            @RequestParam("userLng2")
            double userLng,
            @RequestParam("shopAddress")
            String shopAddress,
            //출퇴근정보
            @RequestParam("attendanceId")
            Long attendanceId,
            //리다이렉트 값을 보내기 위한 변수
            RedirectAttributes redirectAttributes
    ){
        //사용자정보 확인
        Account account = accountRepo.findById(accountId)
                .orElseThrow(
                        ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자 정보를 확인해주세요")
                );
        //사용자 동일 여부 체크
        attendanceService.checkSameAccount(account);
        //해당매장에 다니는 사람인지 체크
        scheduleService.checkMember(shopId);
        //사용자 좌표
        PointDto userPointDto = new PointDto(userLat, userLng);
        //사용자가 매장 위치에 있는지 확인
        if (naviService.checkTwoPoint(userPointDto, shopAddress)){
            //휴식시간 정보 저장
            attendanceService.restIn(attendanceId);
            //출근 확인되었습니다 alert창으로 나타내기
            redirectAttributes.addFlashAttribute("msg", "휴식시작. 리프레시하세요!");
        }
        //매장위치에 있지 않으면
        else {
            redirectAttributes.addFlashAttribute("msg", "현재 위치를 확인해주세요.");
        }
        return String.format("redirect:/attendance/%d/%d", accountId, shopId);
    }
    //쉬는시간종료요청
    //출근상태에만 요청이 가능
    @PostMapping("/restOut/{accountId}/{shopId}")
    public String restOut(
            @PathVariable("accountId")
            Long accountId,
            @PathVariable("shopId")
            Long shopId,
            @RequestParam("userLat2")
            double userLat,
            @RequestParam("userLng2")
            double userLng,
            @RequestParam("shopAddress")
            String shopAddress,
            //출퇴근정보
            @RequestParam("attendanceId")
            Long attendanceId,
            //리다이렉트 값을 보내기 위한 변수
            RedirectAttributes redirectAttributes
    ){
        //사용자정보 확인
        Account account = accountRepo.findById(accountId)
                .orElseThrow(
                        ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자 정보를 확인해주세요")
                );
        //사용자 동일 여부 체크
        attendanceService.checkSameAccount(account);
        //해당매장에 다니는 사람인지 체크
        scheduleService.checkMember(shopId);
        //사용자 좌표
        PointDto userPointDto = new PointDto(userLat, userLng);
        //사용자가 매장 위치에 있는지 확인
        if (naviService.checkTwoPoint(userPointDto, shopAddress)){
            //휴식종료 정보 저장
            attendanceService.restOut(attendanceId);
            //출근 확인되었습니다 alert창으로 나타내기
            redirectAttributes.addFlashAttribute("msg", "휴식종료. 열심히 일합시다!");
        }
        //매장위치에 있지 않으면
        else {
            redirectAttributes.addFlashAttribute("msg", "현재 위치를 확인해주세요.");
        }
        return String.format("redirect:/attendance/%d/%d", accountId, shopId);
    }

    //출퇴근 기록 보기(아르바이트생/관리자)
    //pagenation
    @GetMapping("/showLog/{accountId}")
    public String showLog(
            @PathVariable("accountId")
            Long accountId,
            @RequestParam(value = "shopId", defaultValue = "0", required = false)
            Long shopId,
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false)
            Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false)
            Integer pageSize,
            @RequestParam(value = "sortType", defaultValue = "checkInTime",required = false)
            String sortType,
            Model model
    ){
        //페이징
        Page<AttendanceLogDto> attendanceLogList;
        //사용자정보 확인
        Account account = accountRepo.findById(accountId)
                .orElseThrow(
                        ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자 정보를 확인해주세요")
                );
        //사용자 동일 여부 체크
        attendanceService.checkSameAccount(account);
        //매장 id가 주어지지 않을 때
        if (shopId == 0){
            //한 유저의 모든 매장 출근 데이터 가져오기
            attendanceLogList
                    = attendanceService.showLogAll(pageNumber,pageSize,sortType, accountId, account.getAuthority());
        }
        //매장 id가 주어지고, 아르바이트생일 때
        else {
            //한 유저의 한 매장 출근 데이터 가져오기
            attendanceLogList
                    = attendanceService.showLog(pageNumber,pageSize,sortType, accountId, shopId, account.getAuthority());
        }

        //사용자
        model.addAttribute("account", account);
        //출퇴근상태
        model.addAttribute("statusList", Status.values());
        //출근기록
        model.addAttribute("attendanceLogList", attendanceLogList);
        //매장명
        model.addAttribute("shopList",
                attendanceService.readOneAccountShopList(accountId));
        //shop id
        model.addAttribute("shopId", shopId);
        //사용자 권한
        model.addAttribute("auth", account.getAuthority());
        return "attendance/attendanceLog";
    }

    //출퇴근 기록 검색 페이지
    //pagenation
    @GetMapping("/showLog/search/{accountId}")
    public String showLogSearch(
            @PathVariable("accountId")
            Long accountId,
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false)
            Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false)
            Integer pageSize,
            @RequestParam(value = "searchDuration", defaultValue = "allDay", required = true)
            String searchDuration,
            @RequestParam(value = "searchWord", defaultValue = "", required = true)
            String searchWord,
            @RequestParam(value = "searchType", defaultValue = "shopName", required = true)
            String searchType,
            @RequestParam(value = "sortType", defaultValue = "checkInTime",required = false)
            String sortType,
            Model model
    ){
        //사용자정보 확인
        Account account = accountRepo.findById(accountId)
                .orElseThrow(
                        ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자 정보를 확인해주세요")
                );
        //사용자 동일 여부 체크
        attendanceService.checkSameAccount(account);

        //검색 서비스
        Page<AttendanceLogDto> attendanceLogList
                = attendanceService.showLogSearch(
                        accountId,pageNumber,pageSize, sortType,
                        searchDuration,searchWord,searchType, account);
        //사용자
        model.addAttribute("account", account);
        //출퇴근상태
        model.addAttribute("statusList", Status.values());
        //출근기록
        model.addAttribute("attendanceLogList", attendanceLogList);
        //매장명
        model.addAttribute("shopList",
                attendanceService.readOneAccountShopList(accountId));
        //사용자 권한
        model.addAttribute("auth", account.getAuthority());

        //검색기간/검색타입/검색단어
        model.addAttribute("searchDuration", searchDuration);
        model.addAttribute("searchType", searchType);
        model.addAttribute("searchWord", searchWord);
        return "attendance/attendanceSearch";
    }

    //출퇴근 수정(관리자)
    //모든 아르바이트생의 출퇴근 기록 수정 가능
    //출근데이터의 Status를 수정하여 출근 / 쉬는시간 / 쉬는시간 종료 / 퇴큰 상태를 변경
    @PostMapping("/update/{accountId}")
    public String update(
            @PathVariable("accountId")
            Long accountId,
            @RequestParam("shopId")
            Long shopId,
            @RequestParam("attendanceId")
            Long attendanceId,
            @RequestParam("status")
            String status,
//            //수정할 출근기록들
//            @RequestParam("updateDto")
//            List<AttendanceLogUpdateDto> updateDto,
            //리다이렉트용
            RedirectAttributes redirectAttributes,
            Model model
    ){
        //사용자
        Account account = accountRepo.findById(accountId)
                .orElseThrow(
                        ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자 정보를 확인해주세요.")
                );
        //사용자 동일 여부 체크
        attendanceService.checkSameAccount(account);
        //사용자 권한 확인. 아르바이트생이면 안됨.
        scheduleService.checkManagerOrAdmin(account);
        //해당 매장의 관리자가 맞는지 체크
        scheduleService.checkMember(shopId);
//        List<AttendanceLogUpdateDto> updateDto
//                = new ArrayList<>();
//        AttendanceLogUpdateDto dto1 = AttendanceLogUpdateDto.builder()
//                .attendanceId(1L)
//                .status(Status.REST_OUT)
//                .build();
//        AttendanceLogUpdateDto dto2 = AttendanceLogUpdateDto.builder()
//                .attendanceId(5L)
//                .status(Status.OUT)
//                .build();
//
//        updateDto.add(dto1);
//        updateDto.add(dto2);
        //하나하나 update
        attendanceService.updateLog(attendanceId, status);
        //일괄 update
        //attendanceService.updateLogAll(shopId,updateDto);
        redirectAttributes.addFlashAttribute("msg", "수정되었습니다! ^0^");
        return String.format("redirect:/attendance/showLog/%d", accountId);

    }
}
