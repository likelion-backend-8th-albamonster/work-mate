package com.example.workmate.dto.community;

import com.example.workmate.entity.Article;
import com.example.workmate.entity.Board;
import com.example.workmate.entity.account.Account;
import lombok.*;

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
    private Account accountId;
    private Long shopId;
    private Board board;
    private List<CommentDto> comments;
    private String title;
    private String content;
    //
    public static ArticleDto fromEntity(Article entity) {
        List<CommentDto> commentDtos = Optional.ofNullable(entity.getComments())
                .orElse(Collections.emptyList())
                .stream()
                .map(CommentDto::fromEntity)
                .collect(Collectors.toList());

        return ArticleDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .board(entity.getBoard())
                .comments(commentDtos)
                .accountId(entity.getAccount())
                .shopId(entity.getShop().getId())
                .build();
    }
}