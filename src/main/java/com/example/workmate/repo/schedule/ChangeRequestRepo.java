package com.example.workmate.repo.schedule;

import com.example.workmate.entity.schedule.ChangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChangeRequestRepo extends JpaRepository<ChangeRequest, Long> {
    List<ChangeRequest> findAllByShop_Id(Long shopId);
    List<ChangeRequest> findAllByAccount_IdAndShop_Id(Long accountId, Long shopId);
}
