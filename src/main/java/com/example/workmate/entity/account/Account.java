package com.example.workmate.entity.account;

import com.example.workmate.entity.AccountShop;
import com.example.workmate.entity.attendance.Attendance;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String username;    // 유저 아이디
    @Column(nullable = false)
    private String password;    // 비밀번호
    @Column(nullable = false)
    private String name;        // 유저 이름
    @Column(nullable = false)
    private String email;       // 이메일
    private String businessNumber;  // 사업자 등록번호
    @Enumerated(EnumType.STRING)
    private Authority authority;    // 권한

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AccountShop> accountShops;

    //TODO 관계 추가
    private Long salaryId;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Attendance> attendances;
    // TODO 관계 추가
    private Long ArticleId;
}
