package com.example.workmate.repo;

import com.example.workmate.entity.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepo extends JpaRepository<Account, Long> {
}
