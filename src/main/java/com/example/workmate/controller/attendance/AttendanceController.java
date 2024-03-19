package com.example.workmate.controller.attendance;

import com.example.workmate.dto.attendance.AttendanceDto;
import com.example.workmate.dto.ncpdto.PointDto;
import com.example.workmate.repo.AccountRepo;
import com.example.workmate.repo.ShopRepo;
import com.example.workmate.service.AttendanceService;
import com.example.workmate.service.account.AccountService;
import com.example.workmate.service.ncpservice.NaviService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService attendanceService;
    private final NaviService naviService;
    private final AccountService accountService;
    private final AccountRepo accountRepo;
    private final ShopRepo shopRepo;
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
        model.addAttribute("account", accountRepo.findById(accountId)
                .orElseThrow(
                        ()->new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "사용자 정보를 확인해주세요")));
        //매장 정보
        model.addAttribute("shop", shopRepo.findById(shopId)
                .orElseThrow(
                        ()->new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "매장 정보를 확인해주세요")));

        model.addAttribute("userIp", "194.114.136.73");
        //오늘 날짜에 이미 기록된 출근이 있는지 확인
        boolean isExist = attendanceService.isExistTodayCheckIn(accountId, shopId);
        model.addAttribute("isExist", isExist);
        //출퇴근 데이터가 존재할때
        if (isExist){
            model.addAttribute("attendance", attendanceService.readOne(accountId, shopId));
        }
        return "attendance";
    }

    //출근요청
    //NCP api를 통해 사용자의 위치가 매장 근처인지 확인
    //이미 기록된 시간이 있는 경우 출근 등록 거부
    @PostMapping("/checkIn/{userId}/{shopId}")
    public String checkIn(
            @PathVariable("userId")
            Long userId,
            @PathVariable("shopId")
            Long shopId,
            @RequestParam("userLat")
            double userLat,
            @RequestParam("userLng")
            double userLng,
            @RequestParam("shopAddress")
            String shopAddress,
            //출근정보
            Model model
    ){
        //사용자 좌표
        PointDto userPointDto = new PointDto(userLat, userLng);
        //사용자가 매장 위치에 있는지 확인
        if (naviService.checkTwoPoint(userPointDto, shopAddress)){
            //오늘 날짜에 이미 기록된 출근이 있는지 확인
            boolean isExist = attendanceService.isExistTodayCheckIn(userId, shopId);
            //이미 출근정보가 있다면
            if (isExist){
                //"중복 출근 요청입니다." alert창으로 나타내기
            }
            //사용자 출근 정보 저장
            AttendanceDto dto = attendanceService.checkIn(userId, shopId);
            //출근 확인되었습니다 alert창으로 나타내기
        }
        //매장위치에 있지 않으면
        else {
            //"현재 위치를 확인해주세요" alert창으로 나타내기
        }
        return String.format("redirect:/attendance/%d/%d", userId, shopId);
    }

    //퇴근요청
    //퇴근시간이 자신의 근무종료시간보다 늦다면 추가정산이 이루어진다.
    @PostMapping("/checkOut/{userId}/{shopId}")
    public String checkOut(
            @PathVariable("userId")
            Long userId,
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
            Model model
    ){
        //사용자 좌표
        PointDto userPointDto = new PointDto(userLat, userLng);
        //사용자가 매장 위치에 있는지 확인
        if (naviService.checkTwoPoint(userPointDto, shopAddress)){
            //사용자 퇴근 정보 저장
            attendanceService.checkOut(attendanceId);
            //퇴근 확인되었습니다 alert창으로 나타내기
            
        }
        //매장위치에 있지 않으면
        else {
            //"현재 위치를 확인해주세요" alert창으로 나타내기
        }
        return String.format("redirect:/attendance/%d/%d", userId, shopId);
    }

    //쉬는시간요청
    @PostMapping("/restIn")
    public String restIn(){
        attendanceService.restIn();
        return "home";
    }
    //쉬는시간종료요청
    @PostMapping("/restOut")
    public String restOut(){
        attendanceService.restOut();
        return "home";
    }
    //출퇴근 기록 보기(아르바이트생/관리자)
    //pagenation
    @GetMapping("/showLog")
    public String showLog(
            Integer pageNumber,
            Integer pageSize
    ){
        Page<AttendanceDto> page = attendanceService.showLog(pageNumber,pageSize);
        return "home";
    }

    //출퇴근 수정(관리자)
    //모든 아르바이트생의 출퇴근 기록 수정 가능
    //Status를 수정하여, 정상출근 / 지각 / 조퇴 상태 변경
    @PutMapping("/update")
    public String update(){
        attendanceService.udpate();
        return "home";
    }
}
