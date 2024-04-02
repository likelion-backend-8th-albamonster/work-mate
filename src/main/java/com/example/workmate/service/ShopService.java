package com.example.workmate.service;

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
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

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

    // 아르바이트 요청 명단 불러오기
    public List<AccountShopDto> getAccountShopsByShopId(Long shopId) {
        List<AccountShop> accountShops = accountShopRepo.findByShop_Id(shopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return accountShops.stream()
                .map(AccountShopDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 아르바이트 요청 명단에서 Account name 불러오기
    public List<String> getAccountNameByAccountShop(Long shopId) {
        List<AccountShop> accountShops = accountShopRepo.findByShop_Id(shopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return accountShops.stream()
                .map(AccountShop::getAccount)
                .map(Account::getName)
                .toList();
    }

    // 아르바이트 요청 명단에서 아르바이트 상태 불러오기
    public List<AccountStatus> getAccountStatus(Long shopId) {
        List<AccountShop> accountShops = accountShopRepo.findByShop_Id(shopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return accountShops.stream()
                .map(AccountShop::getStatus)
                .toList();
    }

    // Shop에서 아르바이트생으로 등록
    public String accept(Long shopId, Long accountShopId) {
        Shop shop = shopRepo.findById(shopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        AccountShop target = accountShopRepo.findById(accountShopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        log.info("account: {}", authFacade.getAuth().getName());
        Account account = accountRepo.findByUsername(authFacade.getAuth().getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!account.getAuthority().equals(Authority.ROLE_BUSINESS_USER)
        && !account.getAuthority().equals(Authority.ROLE_ADMIN)) {
            log.error("매장의 관리자만 접근 가능합니다.");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        // 등록 승락
        target.setStatus(AccountStatus.ACCEPT);
        target.setShop(Shop.builder()
                .id(shop.getId())
                .name(shop.getName())
                .address(shop.getAddress())
                .build());
        accountShopRepo.save(target);
        return target.getStatus().getStatus();
    }

    // 아르바이트 요청 거절
    public void deleteAccountShop(Long shopId, Long accountShopId) {
        Shop shop = shopRepo.findById(shopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        AccountShop target = accountShopRepo.findById(accountShopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        log.info("account: {}", authFacade.getAuth().getName());
        Account account = accountRepo.findByUsername(authFacade.getAuth().getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!account.getAuthority().equals(Authority.ROLE_BUSINESS_USER)
                && !account.getAuthority().equals(Authority.ROLE_ADMIN)) {
            log.error("매장의 관리자만 접근 가능합니다.");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        target.setStatus(AccountStatus.REJECT);
        log.info("Status: {}", target.getStatus().getStatus());
        accountShopRepo.delete(target);
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
