package com.example.workmate.service;

import com.example.workmate.dto.account.AccountDto;
import com.example.workmate.dto.account.AccountShopDto;
import com.example.workmate.dto.shop.ShopDto;
import com.example.workmate.entity.AccountShop;
import com.example.workmate.entity.Shop;
import com.example.workmate.entity.account.Account;
import com.example.workmate.entity.account.AccountStatus;
import com.example.workmate.entity.account.Authority;
import com.example.workmate.facade.AuthenticationFacade;
import com.example.workmate.repo.AccountRepo;
import com.example.workmate.repo.AccountShopRepo;
import com.example.workmate.repo.ShopRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopService {
    private final ShopRepo shopRepo;
    private final AuthenticationFacade authFacade;
    private final AccountRepo accountRepo;
    private final AccountShopRepo accountShopRepo;

    // CREATE Shop
    public ShopDto createShop(ShopDto dto) {
        Account account = authFacade.getAccount();
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

    public Map<String, AccountStatus>  getAccountByAccountShop(Long shopId) {
        Account account = accountRepo.findByUsername(authFacade.getAuth().getName())
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (isAuthority(account.getAuthority())) {
            log.info("권한이 없습니다.");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        Optional<List<AccountShop>> optionalAccountShopList
                = accountShopRepo.findAllByShop_id(shopId);
        if (optionalAccountShopList.isEmpty()) {
            log.info("아르바이트 명단이 없습니다.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        List<AccountShop> accountShopList = optionalAccountShopList.get();

        List<String> names = accountShopList.stream()
                .map(AccountShop::getAccount)
                .map(Account::getName)
                .toList();

        List<AccountStatus> statuses = accountShopList.stream()
                .map(AccountShop::getStatus)
                .toList();

        Map<String, AccountStatus> accountStatusMap = new HashMap<>();
        for (int i = 0; i < names.size(); i++) {
            accountStatusMap.put(names.get(i), statuses.get(i));
        }
        return accountStatusMap;
    }


    // Check Authority
    private boolean checkAuthority(Account account) {
        // 비활성유저거나 일반 유저일 경우 true
        return account.getAuthority().equals(Authority.ROLE_INACTIVE_USER)
                || account.getAuthority().equals(Authority.ROLE_USER);
    }

    private boolean isAuthority(Authority authority) {
        // 비활성 유저인 경우 true
        return authority.equals(Authority.ROLE_INACTIVE_USER) ||
                authority.equals(Authority.ROLE_USER);
    }
}
