package com.example.workmate.repo;

import com.example.workmate.entity.WorkTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkTimeRepo extends JpaRepository<WorkTime, Long> {
}
