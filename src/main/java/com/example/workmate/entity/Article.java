package com.example.workmate.entity;

import com.example.workmate.entity.account.Account;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String title;
    @Setter
    private String content;
    // 사장님-근무자 비밀글 기능
//    @Setter
//    private String password;

    @Setter
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Board board = Board.NOTICE;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account accountId;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shopId;

    @Setter
    @OneToMany(mappedBy = "articleId", fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();
}
