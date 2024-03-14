package com.example.workmate.controller;

import com.example.workmate.dto.AttendanceDto;
import com.example.workmate.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService service;

    //출근요청
    //NCP api를 통해 사용자의 위치를 확인
    //이미 기록된 시간이 있는 경우 출근 등록 거부
    @PostMapping("/checkIn")
    public String checkIn(){
        service.checkIn();
        return "home";
    }

    //퇴근요청
    //퇴근시간이 자신의 근무종료시간보다 늦다면 추가정산이 이루어진다.
    @PostMapping("/checkIn")
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
