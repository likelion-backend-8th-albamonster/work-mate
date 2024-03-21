package com.example.workmate.repo;

import com.example.workmate.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShopRepo extends JpaRepository<Shop, Long> {
    Optional<Shop> findByNameContaining(String name);
    Optional<Shop> findByName(String name);
}
