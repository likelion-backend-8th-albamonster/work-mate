package com.example.workmate.repo;

import com.example.workmate.entity.Attendance;
import com.example.workmate.entity.Shop;
import com.example.workmate.entity.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttendanceRepo extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByAccount(Account account);
    Optional<Attendance> findTopByAccountAndShopOrderByCheckInTimeDesc(Account account, Shop shop);
    boolean existsByAccount(Account account);

}
