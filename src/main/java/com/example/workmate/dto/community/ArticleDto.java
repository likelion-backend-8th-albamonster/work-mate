package com.example.workmate.dto.community;

import com.example.workmate.entity.community.Article;
import com.example.workmate.entity.community.Board;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDto {
    private Long id;
    private Long accountId;
    private Long shopArticleId;
    private Long shopId;
    private Board board;
    private List<CommentDto> comments;
    private String title;
    private String content;
    private String accountName;
    private String password;
    private String authorAuthority;
    private LocalDateTime articleWriteTime;

    public static ArticleDto fromEntity(Article entity) {
        List<CommentDto> commentDtos = Optional.ofNullable(entity.getComments())
                .orElse(Collections.emptyList())
                .stream()
                .map(CommentDto::fromEntity)
                .collect(Collectors.toList());

        return ArticleDto.builder()
                .id(entity.getId())
                .shopArticleId(entity.getShopArticleId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .board(entity.getBoard())
                .comments(commentDtos)
                .accountId(entity.getAccount() != null ? entity.getAccount().getId() : null) //임시
                .accountName(entity.getAccount() != null ? entity.getAccount().getName() : "익명") //임시
                .shopId(entity.getShop().getId())
                .password(entity.getPassword())
                .authorAuthority(String.valueOf(entity.getAccount().getAuthority()))
                .articleWriteTime(entity.getArticleWriteTime())
                .build();
    }
}