package com.example.workmate.entity.account;

import com.example.workmate.entity.AccountShop;
import com.example.workmate.entity.Attendance;
import com.example.workmate.entity.Shop;
import com.example.workmate.entity.account.Authority;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;    // 유저 아이디
    private String password;    // 비밀번호
    private String email;   // 이메일
    @Enumerated(EnumType.STRING)
    private Authority authority;    // 권한

    @OneToMany(mappedBy = "account")
    private List<AccountShop> accountShops;

    //TODO 관계 추가
    private Long salaryId;

    @OneToMany(mappedBy = "account")
    private List<Attendance> attendances;
    // TODO 관계 추가
    private Long ArticleId;
}
