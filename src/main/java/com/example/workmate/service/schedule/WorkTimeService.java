package com.example.workmate.service.schedule;

import com.example.workmate.dto.WorkTimeDto;
import com.example.workmate.entity.WorkTime;
import com.example.workmate.entity.account.Account;
import com.example.workmate.entity.account.Authority;
import com.example.workmate.repo.WorkTimeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class WorkTimeService {
    private final WorkTimeRepo workTimeRepo;

//    public WorkTimeDto create(Long shopId){
//        /*//checkMember(shopId);
//
//        WorkTime workTime = WorkTime.builder()
//
//                .build()
//        return WorkTimeDto.builder().build();*/
//    }

    // 해당 매장 직원인지 확인하기
    public Account checkMember(Long shopId){
//        Account account = authFacade.extractUser();
//        Optional<AccountShop> optionalAccountShop =
//                accountShopRepo.findByShop_IdAndAccount_id(shopId, account.getId());
//        if (optionalAccountShop.isEmpty())
//            throw new IllegalArgumentException();
//        return account;
        return null;
    }

    public void checkManagerOrAdmin(Account account){
        if(account.getAuthority() == Authority.ROLE_USER)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
}
