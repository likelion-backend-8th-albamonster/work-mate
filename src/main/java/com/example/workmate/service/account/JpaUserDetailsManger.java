package com.example.workmate.service.account;

import com.example.workmate.entity.account.Account;
import com.example.workmate.entity.account.Authority;
import com.example.workmate.entity.account.CustomAccountDetails;
import com.example.workmate.repo.AccountRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.stream.Collectors;

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
                .email(account.getEmail())
                .authority(account.getAuthority())
                .build();
    }

    @Override
    public void createUser(UserDetails user) {
        if (userExists(user.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        try {
            CustomAccountDetails accountDetails = (CustomAccountDetails) user;
            Account newAccount = Account.builder()
                    .username(accountDetails.getUsername())
                    .password(accountDetails.getPassword())
                    .authority(accountDetails.getAuthority())
                    .build();
            log.info("authority: {}", accountDetails.getAuthorities());
            accountRepo.save(newAccount);
        } catch (Exception e) {
            log.error("Failed Cast to: {}", CustomAccountDetails.class);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
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
