package com.example.workmate.entity;

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

    public enum Board {
        NOTICE, // 공지사항
        REQUEST, // 요청사항
        SUGGESTIONS, // 건의사항
        FREE // 자유게시판
    }

//    @Setter
//    @ManyToOne(fetch = FetchType.LAZY)
//    private Account accountId;

    @Setter
    @OneToMany(mappedBy = "article", fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();
}
