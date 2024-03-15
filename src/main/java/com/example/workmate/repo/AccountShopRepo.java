package com.example.workmate.repo;

import com.example.workmate.entity.AccountShop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountShopRepo extends JpaRepository <AccountShop, Long> {
    Optional<AccountShop> findByShop_IdAndAccount_id(Long shopId, Long accountId);
}
