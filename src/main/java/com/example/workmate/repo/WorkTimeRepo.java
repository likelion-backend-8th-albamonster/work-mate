package com.example.workmate.repo;

import com.example.workmate.entity.WorkTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WorkTimeRepo extends JpaRepository<WorkTime, Long> {
    // shopId와 기간의 시작일, 끝 일을 넣어주면 기간 내에 샵에서 근무하는 리스트가 나온다
    List<WorkTime> findAllByShop_IdAndWorkStartTimeGreaterThanAndWorkEndTimeLessThanEqual(
            Long ShopId, LocalDateTime start, LocalDateTime end
    );
    Optional<WorkTime> findByAccount_Id(Long accountId);
}
