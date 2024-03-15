package com.example.workmate.repo;

import com.example.workmate.entity.Attendance;
import com.example.workmate.entity.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttendanceRepo extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByAccount(Account account);
    Optional<Attendance> findByAccountOrderByCheckInTime(Account account);
    boolean existsByAccount(Account account);

}
