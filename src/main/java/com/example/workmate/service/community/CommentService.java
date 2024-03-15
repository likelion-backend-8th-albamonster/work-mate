package com.example.workmate.service.community;

import com.example.workmate.dto.account.AccountDto;
import com.example.workmate.dto.community.CommentDto;
import com.example.workmate.entity.Article;
import com.example.workmate.entity.Comment;
import com.example.workmate.repo.community.ArticleRepository;
import com.example.workmate.repo.community.CommentRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final ArticleRepository articleRepo;
    private final CommentRepo commentRepo;

    public CommentDto create(
            Long articleId,
            CommentDto dto
    ) {
        Article article = articleRepo.findById(articleId)
                .orElseThrow();

        Comment comment = commentRepo.save(Comment.builder()
                .content(dto.getContent())
                .article(dto.getArticleId())
                .account(dto.getAccountId())
                .build());
        return CommentDto.fromEntity(commentRepo.save(comment));
    }

    public CommentDto update(
        Long commentId,
        AccountDto accountId,
        CommentDto commentDto
    ) {
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow();
        if (comment.getAccount().equals(commentDto.getAccountId())) {
            comment.setContent(commentDto.getContent());
        } else {
            throw new IllegalStateException("권한이 없습니다.");
        }
        return CommentDto.fromEntity(commentRepo.save(comment));
    }

    public void delete(
        Long commentId,
        CommentDto commentDto
    ) {
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow();
        if (comment.getAccount().equals(commentDto.getAccountId())) {
            commentRepo.delete(comment);
        } else {
            throw new IllegalStateException("권한이 없습니다.");
        }
    }
}
