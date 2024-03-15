package com.example.workmate.repo.schedule;

import com.example.workmate.entity.schedule.ChangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChangeRequestRepo extends JpaRepository<ChangeRequest, Long> {
    List<ChangeRequest> findAllByShop_Id(Long shopId);
    Optional<ChangeRequest> findByAccount_Id(Long accountId);
}
