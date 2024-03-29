package com.example.workmate.service.community;

import com.example.workmate.dto.community.CommentDto;
import com.example.workmate.entity.community.Article;
import com.example.workmate.entity.community.Comment;
import com.example.workmate.entity.account.Account;
import com.example.workmate.repo.AccountRepo;
import com.example.workmate.repo.community.ArticleRepo;
import com.example.workmate.repo.community.CommentRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final ArticleRepo articleRepo;
    private final CommentRepo commentRepo;
    private final AccountRepo accountRepo;
    private final ArticleService articleService;

    public CommentDto create(
            Long shopId,
            Long shopArticleId,
            CommentDto commentDto
    ) {
        // 게시글 가져오기
        Article article = articleRepo.findByShopArticleIdAndShopId(shopArticleId, shopId)
                .orElseThrow();

        // 사용자 accountId 가져오기
        Long accountId = articleService.getAccountId();
        Account account = accountRepo.findById(accountId)
                .orElseThrow(() -> new IllegalStateException("계정 정보를 찾을 수 없습니다."));

        // DTO 저장
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
        // 코멘트 가져오기
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow();

        // 현재 접속한 accountId 가져오기
        Long accountId = articleService.getAccountId();

        // 현재 유저와 comment에서 가져온 accountId 비교하기
        if (comment.getAccount().getId().equals(accountId)) {
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
        // 코멘트 가져오기
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new IllegalStateException ("댓글이 없습니다."));

        // 현재 접속한 accountId 가져오기
        Long accountId = articleService.getAccountId();

        // 현재 유저와 comment에서 가져온 accountId 비교하기
        if (comment.getAccount().getId().equals(accountId)
                || articleService.checkAccessRights()) {
            commentRepo.delete(comment);
        } else {
            throw new IllegalStateException("권한이 없습니다.");
        }
    }
}
