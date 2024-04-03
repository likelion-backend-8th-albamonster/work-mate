package com.example.workmate.dto.community;

import com.example.workmate.entity.community.Comment;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private Long accountId;
    private Long articleId;
    private Long shopArticleId;
    private String accountName;
    private String content;
    private String commentAuthority;
    private LocalDateTime commentWriteTime;

    public static CommentDto fromEntity(Comment entity) {
        return CommentDto.builder()
                .id(entity.getId())
                .content(entity.getContent())
                .articleId(entity.getArticle().getId())
                .shopArticleId(entity.getArticle().getShopArticleId())
                .accountId(entity.getAccount().getId())
                .accountName(entity.getAccount().getName())
                .commentAuthority(String.valueOf(entity.getAccount().getAuthority()))
                .commentWriteTime(entity.getCommentWriteTime())
                .build();
    }
}
