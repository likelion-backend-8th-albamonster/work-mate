package com.example.workmate.service.account;

import com.example.workmate.entity.account.Account;
import com.example.workmate.entity.account.Authority;
import com.example.workmate.entity.account.CustomAccountDetails;
import com.example.workmate.repo.AccountRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Slf4j
@Service
public class JpaUserDetailsManger implements UserDetailsManager {
    private final AccountRepo accountRepo;

    public JpaUserDetailsManger(AccountRepo accountRepo, PasswordEncoder passwordEncoder) {
        this.accountRepo = accountRepo;
        if (!userExists("admin")) {
            createUser(CustomAccountDetails.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("password"))
                    .name("admin")
                    .email("admin")
                    .authority(Authority.ROLE_ADMIN)
                    .build());
        }
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Account> optionalAccount = accountRepo.findByUsername(username);
        if (optionalAccount.isEmpty())
            throw new UsernameNotFoundException(username);

        Account account = optionalAccount.get();
        return CustomAccountDetails.builder()
                .username(account.getUsername())
                .password(account.getPassword())
                .name(account.getName())
                .email(account.getEmail())
                .businessNumber(account.getBusinessNumber())
                .authority(account.getAuthority())
                .build();
    }

    @Override
    public void createUser(UserDetails user) {
        if (userExists(user.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (user instanceof CustomAccountDetails accountDetails) {
            Account newAccount = Account.builder()
                    .username(accountDetails.getUsername())
                    .password(accountDetails.getPassword())
                    .name(accountDetails.getName())
                    .email(accountDetails.getEmail())
                    .businessNumber(accountDetails.getBusinessNumber())
                    .authority(accountDetails.getAuthority())
                    .build();
            log.info("authority: {}", accountDetails.getAuthorities());
            accountRepo.save(newAccount);
        } else {
            throw new IllegalArgumentException("Unsupported UserDetails type");
        }
    }

    @Override
    public void updateUser(UserDetails user) {

    }

    @Override
    public void deleteUser(String username) {

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }

    @Override
    public boolean userExists(String username) {
        return accountRepo.existsByUsername(username);
    }
}
