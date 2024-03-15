package com.example.workmate.controller;

import com.example.workmate.dto.AttendanceDto;
import com.example.workmate.service.AttendanceService;
import com.example.workmate.service.ncpservice.NaviService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService service;
    private final NaviService naviService;
    //출근요청페이지
    @GetMapping
    public String attendance(
            //사용자의 ip와 매장 주소가 model에 들어가야 함
    ){
        return "home";
    }

    //출근요청
    //NCP api를 통해 사용자의 위치가 매장 근처인지 확인
    //이미 기록된 시간이 있는 경우 출근 등록 거부
    @PostMapping("/checkIn/{userId}")
    public String checkIn(
            @PathVariable("userId")
            Long userId,
            @RequestParam("userIp")
            String userIp,
            @RequestParam("shopAddress")
            String shopAddress,
            //출근정보
            Model model
    ){
        //사용자가 매장 위치에 있는지 확인
        if (naviService.checkTwoPoint(userIp, shopAddress)){
            //사용자 출근 정보 저장
            AttendanceDto dto = service.checkIn(userId);
            //저장된 값을 모델에 담기
            
        }
        //매장위치에 있지 않으면 분기
        return "home";
    }

    //퇴근요청
    //퇴근시간이 자신의 근무종료시간보다 늦다면 추가정산이 이루어진다.
    @PostMapping("/checkOut")
    public String checkOut(){
        service.checkOut();
        return "home";
    }

    //쉬는시간요청
    @PostMapping("/restIn")
    public String restIn(){
        service.restIn();
        return "home";
    }
    //쉬는시간종료요청
    @PostMapping("/restOut")
    public String restOut(){
        service.restOut();
        return "home";
    }
    //출퇴근 기록 보기(아르바이트생/관리자)
    //pagenation
    @GetMapping("/showLog")
    public String showLog(
            Integer pageNumber,
            Integer pageSize
    ){
        Page<AttendanceDto> page = service.showLog(pageNumber,pageSize);
        return "home";
    }

    //출퇴근 수정(관리자)
    //모든 아르바이트생의 출퇴근 기록 수정 가능
    //Status를 수정하여, 정상출근 / 지각 / 조퇴 상태 변경
    @PutMapping("/update")
    public String update(){
        service.udpate();
        return "home";
    }
}
