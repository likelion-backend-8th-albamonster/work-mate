package com.example.workmate.service.community;

import com.example.workmate.dto.community.ArticleDto;
import com.example.workmate.entity.Article;
import com.example.workmate.entity.Board;
import com.example.workmate.repo.community.ArticleRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;



@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepo articleRepo;

    public ArticleDto create(
            ArticleDto dto
    ) {
        Article article = articleRepo.save(Article.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .board(dto.getBoard())
                .account(dto.getAccountId())
                .shop(dto.getShopId())
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
        if (article.getAccount().equals(articleDto.getAccountId())) {
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
        if (article.getAccount().equals(articleDto.getAccountId())) {
            articleRepo.delete(article);
        } else {
            throw new IllegalStateException("권한이 없습니다.");
        }
    }
}

