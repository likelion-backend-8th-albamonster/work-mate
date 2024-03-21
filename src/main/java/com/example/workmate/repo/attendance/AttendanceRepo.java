package com.example.workmate.repo.attendance;

import com.example.workmate.entity.attendance.Attendance;
import com.example.workmate.entity.Shop;
import com.example.workmate.entity.account.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepo extends JpaRepository<Attendance, Long> {
    //한 계정에 대한 하나의 출퇴근 정보 하나
    Attendance findTopByAccountOrderByIdDesc(Account account);
    Optional<Attendance> findTopByAccountAndShopOrderByCheckInTimeDesc(Account account, Shop shop);
    boolean existsByAccount(Account account);
   
    
    //한 계정에 대한 모든 출퇴근 정보
    List<Attendance> findAllByAccount(Account account);
    ////한 계정에 대한 모든 촐퇴근 정보 페이징
    Page<Attendance> findAllByAccount(Pageable pageable, Account account);
    //한 계정의 한 매장에 출퇴근 정보 페이징
    Page<Attendance> findAllByAccountAndShop(Pageable pageable, Account account, Shop shop);
}
