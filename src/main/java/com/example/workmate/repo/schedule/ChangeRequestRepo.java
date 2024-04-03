package com.example.workmate.repo.schedule;

import com.example.workmate.entity.schedule.ChangeRequest;
import com.example.workmate.entity.schedule.WorkTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChangeRequestRepo extends JpaRepository<ChangeRequest, Long> {
    List<ChangeRequest> findAllByShop_Id(Long shopId);
    List<ChangeRequest> findAllByAccount_IdAndShop_Id(Long accountId, Long shopId);
    Page<ChangeRequest> findAllByShop_IdAndStatus(Long shopId, ChangeRequest.Status status, Pageable pageable);
}
