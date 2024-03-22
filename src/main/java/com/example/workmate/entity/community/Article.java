package com.example.workmate.entity.community;

import com.example.workmate.entity.Shop;
import com.example.workmate.entity.account.Account;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
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
    private Long shopArticleId;
    @Setter
    private String title;
    @Setter
    private String content;
    @Setter
    @Column(name = "article_write_time")
    private LocalDateTime articleWriteTime;
    @Setter
    private String password;

    @Setter
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Board board = Board.NOTICE;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @Setter
    @OneToMany(mappedBy = "article", fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();
}
