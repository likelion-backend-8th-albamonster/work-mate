package com.example.workmate.entity.Account;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String username;        // 아이디
    private String password;        // 비밀번호
    @Column(nullable = false, unique = true)
    private String email;           // 이메일
    @Enumerated(EnumType.STRING)
    private Authority authority;    // 권한
}
