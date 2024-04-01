package com.example.workmate.controller.shop;

import com.example.workmate.dto.account.AccountShopDto;
import com.example.workmate.dto.shop.ShopDto;
import com.example.workmate.service.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/shop")
@RequiredArgsConstructor
public class ShopController {
    private final ShopService shopService;

    // CREATE Shop Form
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("shop", model);
        return "shop/create";
    }

    // READ All Shop
    @GetMapping
    public String readAllShop(Model model) {
        List<ShopDto> shops = shopService.readAllShop();
        model.addAttribute("shops", shops);
        return "shop/list";
    }

    // READ ONE Shop By ID
    @GetMapping("/{id}")
    public String readOneShop(@PathVariable("id") Long id, Model model) {
        ShopDto shop = shopService.readOneShop(id);
        model.addAttribute("shop", shop);
        return "shop/detail";
    }

    // READ ONE Shop By Name
    @GetMapping("/search")
    public String readOneShopByName(@RequestParam("name") String name, Model model) {
        ShopDto shop = shopService.readOneShopByName(name);
        model.addAttribute("shop", shop);
        return "shop/detail";
    }

    // UPDATE Shop Form
    @GetMapping("/{id}/update")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        ShopDto shop = shopService.readOneShop(id);
        model.addAttribute("shop", shop);
        return "shop/update";
    }

    // UPDATE Shop
    @PostMapping("/{id}/update")
    public String updateShop(@PathVariable("id") Long id, @ModelAttribute ShopDto dto) {
        shopService.updateShop(id, dto);
        return "redirect:/shop";
    }

    // DELETE Shop
    @PostMapping("/{id}/delete")
    public String deleteShop(@PathVariable("id") Long id) {
        shopService.deleteShop(id);
        return "redirect:/shop";
    }

    // 아르바이트 요청 명단 불러오기
    @GetMapping("/{id}/shop-account")
    public String getAccountByAccountShop(@PathVariable("id") Long id, Model model) {
        List<AccountShopDto> accountShopList = shopService.getAccountShopsByShopId(id);
        model.addAttribute("accountShopList", accountShopList);
        return "shop/account-shop-list"; // 해당 뷰의 이름 반환
    }
}
