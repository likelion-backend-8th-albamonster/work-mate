package com.example.workmate.dto;

import com.example.workmate.entity.Comment;
import com.example.workmate.entity.account.Account;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private String content;
    private Account accountId;

    public static CommentDto fromEntity(Comment entity) {
        return CommentDto.builder()
                .id(entity.getId())
                .content(entity.getContent())
                .accountId(entity.getAccountId())
                .build();
    }
}
