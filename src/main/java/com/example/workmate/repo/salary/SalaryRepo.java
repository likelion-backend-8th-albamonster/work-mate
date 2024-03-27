package com.example.workmate.repo.salary;

import com.example.workmate.entity.salary.Salary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface  SalaryRepo extends JpaRepository<Salary, Long> {
    Salary findTopByAccount_IdAndShop_IdOrderBySalaryDateDesc(Long accountId, Long shopId);
}
