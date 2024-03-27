package com.example.workmate.service.account;

import com.example.workmate.dto.account.AccountDto;
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

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepo accountRepo;
    private final ShopRepo shopRepo;
    private final AccountShopRepo accountShopRepo;
    private final AuthenticationFacade authFacade;
    private final MailService mailService;

    // 유저 정보 가져오기
    public AccountDto readOneAccount(Long id) {
        Account account = getAccount(id);

        log.info("auth user: {}", authFacade.getAuth().getName());
        log.info("page username: {}", account.getUsername());

        // 토큰으로 접근 시도한 유저와, 페이지의 유저가 다른경우 예외
        if (!authFacade.getAuth().getName().equals(account.getUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return AccountDto.fromEntity(account);
    }

    // 아르바이트 요청
    public AccountStatus submit(Long accountId, String name) {
        AccountShop newAccountShop = new AccountShop();

        // 아르바이트를 요청할 사용자 정보, 매장 정보 불러오기
        Account account = getAccount(accountId);
        Shop shop = getShopByName(name);

        log.info("auth user: {}", authFacade.getAuth().getName());
        log.info("page username: {}", account.getUsername());

        // 토큰으로 접근 시도한 유저와, 페이지의 유저가 다른경우 예외
        if (!authFacade.getAuth().getName().equals(account.getUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        newAccountShop.setShop(Shop.builder()
                .id(shop.getId())
                .name(shop.getName())
                .address(shop.getAddress())
                .build());

        newAccountShop.setStatus(AccountStatus.SUBMITTED);
        newAccountShop.setAccount(Account.builder()
                .id(account.getId())
                .username(account.getUsername())
                .name(account.getName())
                .email(account.getEmail())
                .build());

        accountShopRepo.save(newAccountShop);

        return newAccountShop.getStatus();
    }

    // Shop에서 아르바이트생으로 등록
    public String accept(Long accountId, Long accountShopId, boolean flag) {
        Account account = getAccount(accountId);
        AccountShop target = getAccountShop(accountShopId);
        Shop shop = getShop(target.getShop().getId());

        log.info("auth user: {}", authFacade.getAuth().getName());
        log.info("page username: {}", account.getUsername());

        // 토큰으로 접근 시도한 유저와, 페이지의 유저가 다른경우 예외
        if (!authFacade.getAuth().getName().equals(account.getUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        if (!account.getAuthority().equals(Authority.ROLE_BUSINESS_USER)) {
            log.error("매장의 관리자만 접근 가능합니다.");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        // 등록 승락
        if (flag) {
            target.setStatus(AccountStatus.ACCEPT);
            target.setShop(Shop.builder()
                    .id(shop.getId())
                    .name(shop.getName())
                    .address(shop.getAddress())
                    .build());
            accountShopRepo.save(target);
            return target.getStatus().getStatus();
        }

        // 등록 거절
        else {
            target.setStatus(AccountStatus.REJECT);
            log.info("Status: {}", target.getStatus());
            accountShopRepo.delete(target);
            return "등록 거절";
        }

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
    private Shop getShopByName(String name) {
        Optional<Shop> optionalShop = shopRepo.findByName(name);
        if (optionalShop.isEmpty()) {
            log.error("매장을 찾을 수 없습니다.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return optionalShop.get();
    }

    // Id로 매장 불러오기
    private Shop getShop(Long id) {
        Optional<Shop> optionalShop = shopRepo.findById(id);
        if (optionalShop.isEmpty()) {
            log.error("매장을 찾을 수 없습니다.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return optionalShop.get();
    }

    // AccountShop 불러오기
    private AccountShop getAccountShop(Long id) {
        Optional<AccountShop> optionalAccountShop = accountShopRepo.findById(id);
        if (optionalAccountShop.isEmpty()) {
            log.error("요청을 찾을 수 없습니다.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return optionalAccountShop.get();
    }
}
