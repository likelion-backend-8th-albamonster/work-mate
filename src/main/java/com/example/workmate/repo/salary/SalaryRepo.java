package com.example.workmate.repo.salary;

import com.example.workmate.entity.salary.Salary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface  SalaryRepo extends JpaRepository<Salary, Long> {
    Salary findTopByAccount_IdAndShop_IdOrderByIdDesc(Long accountId, Long shopId);
    List<Salary> findByShop_IdOrderByIdDesc(Long shopId);
    List<Salary> findAllByShop_IdOrderByIdDesc(Long shopId);
    List<Salary> findByShop_IdAndAccount_IdOrderByIdDesc(Long shopId, Long accountId);
    List<Salary> findAllByShop_IdAndStatusOrderByIdDesc(Long shopId, Salary.Status status);
}
