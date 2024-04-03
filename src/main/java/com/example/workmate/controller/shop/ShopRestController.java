package com.example.workmate.controller.shop;

import com.example.workmate.dto.account.AccountShopDto;
import com.example.workmate.dto.shop.ShopDto;
import com.example.workmate.entity.account.AccountStatus;
import com.example.workmate.service.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/shop")
@RequiredArgsConstructor
public class ShopRestController {
    private final ShopService service;

    // CREATE Shop
    @PostMapping("/create")
    public ShopDto createShop(@RequestBody ShopDto dto) {
        return service.createShop(dto);
    }

    // READ All Shop
    @GetMapping("/read-all")
    public List<ShopDto> readAllShop() {
        return service.readAllShop();
    }

    // READ ONE Shop By ID
    @GetMapping("/{id}")
    public ShopDto readOneShop(@PathVariable("id") Long id) {
        return service.readOneShop(id);
    }

    // READ ONE Shop By Name
    @GetMapping("/read-one")
    public ShopDto readOneShopByName(@RequestParam("name") String name) {
        return service.readOneShopByName(name);
    }

    // UPDATE Shop
    @PostMapping("/{id}/update")
    public ShopDto updateShop(@PathVariable("id") Long id,@RequestBody ShopDto dto) {
        return service.updateShop(id, dto);
    }

    // DELETE Shop
    @DeleteMapping("/{id}/delete")
    public String deleteShop(@PathVariable("id") Long id) {
        service.deleteShop(id);
        return "delete shop";
    }

    // 아르바이트 요청 명단 불러오기
    @GetMapping("/{id}/shop-account")
    public List<AccountShopDto> getAccountByAccountShop(@PathVariable("id") Long id) {
        return service.getAccountShopsByShopId(id);
    }

    // 아르바이트 요청 명단에서 Account name 불러오기
    @GetMapping("/{id}/shop-account/account-name")
    public List<String> getAccountNameByAccountShop(@PathVariable("id") Long id) {
        return service.getAccountNameByAccountShop(id);
    }

    // 아르바이트 요청 명단에서 아르바이트 상태 불러오기
    @GetMapping("/{id}/shop-account/account-status")
    public List<AccountStatus> getAccountStatus(@PathVariable("id") Long id) {
        return service.getAccountStatus(id);
    }

    // 아르바이트 요청 승낙
    @PostMapping("/{id}/shop-account/accept/{accountShopId}")
    public String accept(
            @PathVariable("id") Long shopId,
            @PathVariable("accountShopId") Long accountShopId
    ) {
        return String.format("Status: %s", service.accept(shopId, accountShopId));
    }

    // 아르바이트 요청 거절
    @DeleteMapping("/{id}/shop-account/delete/{accountShopId}")
    public void delete(
            @PathVariable("id") Long shopId,
            @PathVariable("accountShopId") Long accountShopId
    ) {
        service.deleteAccountShop(shopId, accountShopId);
    }

}
