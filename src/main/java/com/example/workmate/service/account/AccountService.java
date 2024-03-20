package com.example.workmate.service.account;

import com.example.workmate.dto.account.AccountDto;
import com.example.workmate.entity.account.Account;
import com.example.workmate.facade.AuthenticationFacade;
import com.example.workmate.repo.AccountRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepo accountRepo;
    private final PasswordEncoder passwordEncoder;
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

    // 아르바이트 매장 추가하기


    private Account getAccount(Long id) {
        Optional<Account> optionalAccount = accountRepo.findById(id);
        if (optionalAccount.isEmpty()) {
            log.error("사용자를 찾을 수 없습니다.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return optionalAccount.get();
    }


}
