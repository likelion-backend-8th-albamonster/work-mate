package com.example.workmate.service.community;

import com.example.workmate.dto.community.ArticleDto;
import com.example.workmate.entity.Article;
import com.example.workmate.entity.Board;
import com.example.workmate.entity.Shop;
import com.example.workmate.entity.account.Account;
import com.example.workmate.repo.AccountRepo;
import com.example.workmate.repo.ShopRepo;
import com.example.workmate.repo.community.ArticleRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepo articleRepo;
    private final ShopRepo shopRepo;
    private final AccountRepo accountRepo;

    public ArticleDto create(
            ArticleDto articleDto
    ) {
        Shop shop = shopRepo.findById(articleDto.getShopId())
                .orElseThrow(() -> new EntityNotFoundException("Shop not found with id: " + articleDto.getShopId()));
        Account account = null;
        if (articleDto.getAccountId() != null) {
            account = accountRepo.findById(articleDto.getAccountId())
                    .orElseThrow(() -> new EntityNotFoundException("Account not found with id: " + articleDto.getAccountId()));
        }
        Article article = articleRepo.save(Article.builder()
                .title(articleDto.getTitle())
                .content(articleDto.getContent())
                .board(articleDto.getBoard())
                .articleWriteTime(LocalDateTime.now())
                .account(account)
                .shop(shop)
                .build());
        return ArticleDto.fromEntity(article);
    }

    public Page<ArticleDto> readPage(
            Long shopId,
            Pageable pageable
    ) {
        return articleRepo.findAllByShopId(shopId, pageable)
                .map(ArticleDto::fromEntity);
    }

    public Page<ArticleDto> readPageByBoard(
            Board board,
            Long shopId,
            Pageable pageable
    ) {
        return articleRepo.findByBoardAndShopId(board, shopId, pageable)
                .map(ArticleDto::fromEntity);
    }

    public ArticleDto readOne(
            Long shopId,
            Long articleId
    ) {
        return ArticleDto.fromEntity(articleRepo.findByIdAndShopId(articleId, shopId)
                .orElseThrow());
    }

    public ArticleDto update(
            Long shopId,
            Long articleId,
            ArticleDto articleDto
    ) {
        Article article = articleRepo.findByIdAndShopId(articleId, shopId)
                .orElseThrow();
        if (article.getAccount() != null && article.getAccount().getId().equals(articleDto.getAccountId())) {
            article.setTitle(articleDto.getTitle());
            article.setContent(articleDto.getContent());
        } else {
            throw new IllegalStateException("권한이 없습니다.");
        }
        return ArticleDto.fromEntity(articleRepo.save(article));
    }

    public void delete(
            Long shopId,
            Long articleId,
            ArticleDto articleDto
    ) {
        Article article = articleRepo.findByIdAndShopId(articleId, shopId)
                .orElseThrow();
        if (article.getAccount() != null && article.getAccount().getId().equals(articleDto.getAccountId())) {
            articleRepo.delete(article);
        } else {
            throw new IllegalStateException("권한이 없습니다.");
        }
    }
}


