package com.example.workmate.service;

import com.example.workmate.dto.shop.ShopDto;
import com.example.workmate.entity.Shop;
import com.example.workmate.entity.account.Account;
import com.example.workmate.entity.account.Authority;
import com.example.workmate.facade.AuthenticationFacade;
import com.example.workmate.repo.ShopRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopService {
    private final ShopRepo shopRepo;
    private final AuthenticationFacade authenticationFacade;

    // CREATE Shop
    public ShopDto createShop(ShopDto dto) {
        Account account = authenticationFacade.getAccount();
        log.info("account: {}", account.getUsername());

        if (checkAuthority(account)) {
            log.error("권한이 없습니다.");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        Shop newShop = Shop.builder()
                .name(dto.getName())
                .address(dto.getAddress())
                .build();

        return ShopDto.fromEntity(shopRepo.save(newShop));
    }

    // READ All Shop
    public List<ShopDto> readAllShop() {
        List<Shop> shopList = shopRepo.findAll();
        return shopList.stream()
                .map(ShopDto::fromEntity)
                .toList();
    }

    // READ ONE Shop by Id
    public ShopDto readOneShop(Long id) {
        Shop shop = shopRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ShopDto.fromEntity(shop);
    }

    // READ ONE Shop by Name
    public ShopDto readOneShopByName(String name) {
        Shop shop = shopRepo.findByNameContaining(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ShopDto.fromEntity(shop);
    }

    // UPDATE Shop
    public ShopDto updateShop(Long id, ShopDto dto) {
        Shop target = shopRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        target.setName(dto.getName());
        target.setAddress(dto.getAddress());

        return ShopDto.fromEntity(shopRepo.save(target));
    }

    // DELETE Shop
    public void deleteShop(Long id) {
        Shop target = shopRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        shopRepo.delete(target);
    }

    // Check Authority
    public boolean checkAuthority(Account account) {
        // 비활성유저거나 일반 유저일 경우 true
        return account.getAuthority().equals(Authority.ROLE_INACTIVE_USER)
                || account.getAuthority().equals(Authority.ROLE_USER);
    }
}
