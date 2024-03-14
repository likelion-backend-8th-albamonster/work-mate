package com.example.workmate.dto;

import com.example.workmate.entity.Article;
import com.example.workmate.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDto {
    private Long id;
    private Long accountId;
    private Long shopId;
    private Article.Board board;
    private final List<CommentDto> comments = new ArrayList<>();
    private String title;
    private String content;

    public static ArticleDto fromEntity(Article entity) {
        ArticleDto dto = new ArticleDto();
        dto.id = entity.getId();
        dto.title = entity.getTitle();
        dto.content = entity.getContent().replace("\n", "<br>");
        dto.board = entity.getBoard();
        for (Comment comment: entity.getComments()) {
            dto.comments.add(CommentDto.fromEntity(comment));
        }
        return dto;
    }
}
