package com.example.workmate.dto;

import com.example.workmate.entity.Article;
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
    private Long accountId;
    private Long shopId;
    private Article.Board board;
    private List<CommentDto> comments;
    private String title;
    private String content;

    public static ArticleDto fromEntity(Article entity) {
        return ArticleDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent().replace("\n", "<br>"))
                .board(entity.getBoard())
                .comments(entity.getComments().stream()
                        .map(CommentDto::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }
}