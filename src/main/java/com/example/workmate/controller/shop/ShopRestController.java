package com.example.workmate.controller.shop;

import com.example.workmate.dto.account.AccountDto;
import com.example.workmate.dto.account.AccountShopDto;
import com.example.workmate.dto.shop.ShopDto;
import com.example.workmate.entity.AccountShop;
import com.example.workmate.entity.account.AccountStatus;
import com.example.workmate.service.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    public AccountDto getAccountByAccountShop(@PathVariable("id") Long id) {
        return service.getAccountByAccountShop(id);
    }
}