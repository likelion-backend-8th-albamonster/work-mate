package com.example.workmate.dto.salary;

import com.example.workmate.entity.salary.Salary;
import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SalaryDto {
    private Long id;
    private Long accountId;
    private Long shopId;
    private Integer salaryYear;
    private Integer salaryMonth;
    private Integer totalSalary;
    private Salary.Status status;

    public static SalaryDto fromEntity(Salary entity){
        return SalaryDto.builder()
                .id(entity.getId())
                .accountId(entity.getAccount().getId())
                .shopId(entity.getShop().getId())
                .salaryYear(entity.getSalaryYear())
                .salaryMonth(entity.getSalaryMonth())
                .totalSalary(entity.getTotalSalary())
                .status(entity.getStatus())
                .build();
    }
}
