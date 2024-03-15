package com.example.workmate.dto;

import com.example.workmate.entity.Article;
import com.example.workmate.entity.Board;
import com.example.workmate.entity.Shop;
import com.example.workmate.entity.account.Account;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDto {
    private Long id;
    private Account accountId;
    private Shop shopId;
    private Board board;
    private List<CommentDto> comments;
    private String title;
    private String content;

    public static ArticleDto fromEntity(Article entity) {
        return ArticleDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .board(entity.getBoard())
                .comments(entity.getComments().stream()
                        .map(CommentDto::fromEntity)
                        .collect(Collectors.toList()))
                .accountId(entity.getAccountId())
                .shopId(entity.getShopId())
                .build();
    }
}