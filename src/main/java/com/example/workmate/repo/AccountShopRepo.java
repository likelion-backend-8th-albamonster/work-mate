package com.example.workmate.repo;

import com.example.workmate.entity.AccountShop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountShopRepo extends JpaRepository <AccountShop, Long> {
    Optional<AccountShop> findByShop_IdAndAccount_id(Long shopId, Long accountId);
    //한 계정에 대한 모든 accountShop 데이터
    Optional<List<AccountShop>> findAllByAccount_id(Long accountId);
    Optional <List<AccountShop>> findByShop_Id(Long shopId);
    Optional<List<AccountShop>> findByAccount_Id(Long accountId);
    boolean existsByShop_IdAndAccount_Id(Long shop_id, Long account_id);
}
