package com.example.workmate.repo;

import com.example.workmate.entity.WorkTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface WorkTimeRepo extends JpaRepository<WorkTime, Long> {
    List<WorkTime> findAllByShop_IdAndWorkStartTimeGreaterThanAndWorkEndTimeLessThanEqual(
            Long ShopId, LocalDateTime start, LocalDateTime end
    );
}
