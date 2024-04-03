package com.example.workmate.controller.salary;

import com.example.workmate.dto.salary.SalaryDto;
import com.example.workmate.service.salary.SalaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;


@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/salary")
public class SalaryController {
    private final SalaryService salaryService;

    // creat
    @ResponseBody
    @PostMapping("create")
    public SalaryDto create(
            @RequestBody
            SalaryDto dto
    ){
        return salaryService.create(dto);
    }

    // 관리자나, 매니저가 정산 안됐을 때 만 지울 수 있다.
    @ResponseBody
    @DeleteMapping("delete/{salaryId}")
    public SalaryDto delete(
            @PathVariable("salaryId")
            Long salaryId
    ){
        return salaryService.deleteSalary(salaryId);
    }

    // 관리자나 매니저가 매장 월급내역 검색할 떄 필요
    @ResponseBody
    @PostMapping("salary-shop/{shopId}")
    public List<SalaryDto> readShopAll(
            @PathVariable("shopId")
            Long shopId
    ){
        return salaryService.readShopSalaryAll(shopId);
    }

    // 알바가 일하는 매장 월급내역 볼 떄 필요
    @ResponseBody
    @PostMapping("my-salary/{shopId}")
    public List<SalaryDto> SalaryAll(
            @PathVariable("shopId")
            Long shopId
    ){
        return salaryService.mySalaryAll(shopId);
    }

    // 알바가 자신이 일하는 모든 매장의 월급내역 볼 떄 필요
    @ResponseBody
    @PostMapping("my-salaries")
    public List<List<SalaryDto>> mySettleAll(){
        return salaryService.readEachShopSalary();
    }

    // 관리자나 매니저가 알바 하나 월급 정산하기
    @ResponseBody
    @PostMapping("settle-one/{shopId}")
    public SalaryDto settleOne(
            @PathVariable("shopId")
            Long shopId,
            @RequestParam
            Long salaryId
    ){
        return salaryService.settleOne(salaryId,shopId);
    }

    // 매장의 정산 안된 모든 월급을 정산하기.
    @ResponseBody
    @PostMapping("settle-all/{shopId}")
    public List<SalaryDto> settleAll(
            @PathVariable("shopId")
            Long shopId
    ){
        return salaryService.settleAll(shopId);
    }
}

