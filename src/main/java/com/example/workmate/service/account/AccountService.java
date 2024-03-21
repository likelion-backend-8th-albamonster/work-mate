package com.example.workmate.service.account;

import com.example.workmate.dto.account.AccountDto;
import com.example.workmate.dto.account.AccountShopDto;
import com.example.workmate.entity.AccountShop;
import com.example.workmate.entity.Shop;
import com.example.workmate.entity.account.Account;
import com.example.workmate.entity.account.AccountStatus;
import com.example.workmate.facade.AuthenticationFacade;
import com.example.workmate.repo.AccountRepo;
import com.example.workmate.repo.AccountShopRepo;
import com.example.workmate.repo.ShopRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepo accountRepo;
    private final ShopRepo shopRepo;
    private final AccountShopRepo accountShopRepo;
    private final AuthenticationFacade authFacade;

    // 유저 정보 가져오기
    public AccountDto readOneAccount(Long id) {
        Account account = getAccount(id);

        log.info("auth user: {}", authFacade.getAuth().getName());
        log.info("page username: {}", account.getUsername());

        // 토큰으로 접근 시도한 유저와, 페이지의 유저가 다른경우 예외
        if (authFacade.getAuth().getName().equals(account.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return AccountDto.fromEntity(account);
    }

    // 아르바이트 요청
    public AccountShopDto submit(Long id, String name) {
        AccountShop newAccountShop = new AccountShop();

        // 아르바이트를 요청할 사용자 정보, 매장 정보 불러오기
        Account account = getAccount(id);
        Shop shop = getShop(name);

        newAccountShop.setAccount(account);
        newAccountShop.setShop(shop);
        newAccountShop.setStatus(AccountStatus.SUBMITTED);

//        List<AccountShop> accountlist = account.getAccountShops();
//        accountlist.add(newAccountShop);
//
//        List<AccountShop> shopList = shop.getAccountShops();
//        shopList.add(newAccountShop);
//
//        account.setAccountShops(accountlist);
//        shop.setAccountShops(shopList);
//        accountRepo.save(account);
//        shopRepo.save(shop);

        return AccountShopDto.fromEntity(accountShopRepo.save(newAccountShop));
    }


    // 사용자 불러오기
    private Account getAccount(Long id) {
        Optional<Account> optionalAccount = accountRepo.findById(id);
        if (optionalAccount.isEmpty()) {
            log.error("사용자를 찾을 수 없습니다.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return optionalAccount.get();
    }

    // 이름으로 매장 불러오기
    private Shop getShop(String name) {
        Optional<Shop> optionalShop = shopRepo.findByName(name);
        if (optionalShop.isEmpty()) {
            log.error("매장을 찾을 수 없습니다.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return optionalShop.get();
    }
}
