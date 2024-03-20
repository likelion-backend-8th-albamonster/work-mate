package com.example.workmate.dto.community;

import com.example.workmate.entity.Comment;
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
    private String accountName;
    private String content;
    private LocalDateTime commentWriteTime;

    public static CommentDto fromEntity(Comment entity) {
        return CommentDto.builder()
                .id(entity.getId())
                .content(entity.getContent())
                .articleId(entity.getArticle().getId())
                .accountId(entity.getAccount() != null ? entity.getAccount().getId() : null)
                .accountName(entity.getAccount() != null ? entity.getAccount().getUsername() : null)
                .commentWriteTime(entity.getCommentWriteTime())
                .build();
    }
}
