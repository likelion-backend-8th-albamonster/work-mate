package com.example.workmate.repo;

import com.example.workmate.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendanceRepo extends JpaRepository<Attendance, Long> {
}
