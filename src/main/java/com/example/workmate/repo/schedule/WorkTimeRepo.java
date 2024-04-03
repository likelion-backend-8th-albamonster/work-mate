package com.example.workmate.repo.schedule;

import com.example.workmate.entity.schedule.WorkTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WorkTimeRepo extends JpaRepository<WorkTime, Long> {
    // shopId와 기간의 시작일, 끝 일을 넣어주면 기간 내에 샵에서 근무하는 리스트가 나온다
    // startTime을 between으로 넣은 이유는 해당 해당 기간 내에 시작하지만 기간 이후에 끝나는일도
    // 검색해서 해당 기간에 넘는 시간은 잘라내고 쓰기 위해서이다.
    // ex) 27일까지 검색했는데, 27일에 시작해서 28일이 넘어서 끝나는 심야알바같은 경우.
    List<WorkTime> findAllByShop_IdAndWorkStartTimeBetweenOrderByWorkStartTimeAsc(
            Long ShopId, LocalDateTime start, LocalDateTime end
    );
    List<WorkTime> findAllByAccount_IdAndShop_IdAndWorkStartTimeBetweenOrderByWorkStartTimeAsc(
            Long accountId, Long ShopId, LocalDateTime start, LocalDateTime end
    );
    Optional<WorkTime> findByAccount_Id(Long accountId);
    Page<WorkTime> findAllByShop_Id(Long shopId, Pageable pageable);
}
