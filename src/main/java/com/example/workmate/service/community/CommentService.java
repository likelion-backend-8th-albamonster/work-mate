package com.example.workmate.service.community;

import com.example.workmate.dto.community.CommentDto;
import com.example.workmate.entity.Article;
import com.example.workmate.entity.Comment;
import com.example.workmate.entity.account.Account;
import com.example.workmate.repo.AccountRepo;
import com.example.workmate.repo.community.ArticleRepo;
import com.example.workmate.repo.community.CommentRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final ArticleRepo articleRepo;
    private final CommentRepo commentRepo;
    private final AccountRepo accountRepo;

    public CommentDto create(
            Long shopId,
            Long shopArticleId,
            CommentDto commentDto
    ) {
        Article article = articleRepo.findByShopArticleIdAndShopId(shopArticleId, shopId)
                .orElseThrow();
        Account account = null;
        if (commentDto.getAccountId() != null) {
            account = accountRepo.findById(commentDto.getAccountId())
                    .orElseThrow();
        }
        Comment comment = commentRepo.save(Comment.builder()
                .account(account)
                .article(article)
                .content(commentDto.getContent())
                .commentWriteTime(LocalDateTime.now())
                .build());
        return CommentDto.fromEntity(commentRepo.save(comment));
    }

    public CommentDto update(
            Long commentId,
            CommentDto commentDto
    ) {
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow();
        if (comment.getAccount() != null && comment.getAccount().getId().equals(commentDto.getAccountId())) {
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
        if (comment.getAccount() != null &&comment.getAccount().getId().equals(commentDto.getAccountId())) {
            commentRepo.delete(comment);
        } else {
            throw new IllegalStateException("권한이 없습니다.");
        }
    }
}
