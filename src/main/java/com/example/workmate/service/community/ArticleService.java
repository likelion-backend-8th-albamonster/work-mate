package com.example.workmate.service.community;

import com.example.workmate.dto.community.ArticleDto;
import com.example.workmate.entity.Shop;
import com.example.workmate.entity.account.Account;
import com.example.workmate.entity.community.Article;
import com.example.workmate.entity.community.Board;
import com.example.workmate.repo.AccountRepo;
import com.example.workmate.repo.ShopRepo;
import com.example.workmate.repo.community.ArticleRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    // 게시글 작성
    public ArticleDto create(
            ArticleDto articleDto
    ) {
        Shop shop = shopRepo.findById(articleDto.getShopId())
                .orElseThrow();

        Account account = null; // 테스트용
        if (articleDto.getAccountId() != null) {
            account = accountRepo.findById(articleDto.getAccountId())
                    .orElseThrow();
        }

        Long lastShopArticleId = articleRepo.findLastShopArticleIdByShop(shop.getId())
                .orElse(0L) + 1;
        Article article = articleRepo.save(Article.builder()
                .title(articleDto.getTitle())
                .content(articleDto.getContent())
                .board(articleDto.getBoard())
                .password(articleDto.getPassword())
                .articleWriteTime(LocalDateTime.now())
                .shopArticleId(lastShopArticleId)
                .account(account)
                .shop(shop)
                .build());
        return ArticleDto.fromEntity(article);
    }

    // 게시글 목록 읽기
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

    // 게시글 한개 읽기
    public ArticleDto readOne(
            Long shopId,
            Long shopArticleId
    ) {
        return ArticleDto.fromEntity(articleRepo.findByShopArticleIdAndShopId(shopArticleId, shopId)
                .orElseThrow());
    }

    // 게시글 수정하기
    public ArticleDto update(
            Long shopId,
            Long shopArticleId,
            ArticleDto articleDto
    ) {
        Article article = articleRepo.findByShopArticleIdAndShopId(shopArticleId, shopId)
                .orElseThrow();
        if (article.getAccount() != null && article.getAccount().getId().equals(articleDto.getAccountId())) {
            article.setTitle(articleDto.getTitle());
            article.setContent(articleDto.getContent());
        } else {
            throw new IllegalStateException("권한이 없습니다.");
        }
        return ArticleDto.fromEntity(articleRepo.save(article));
    }

    // 게시글 삭제하기
    public void delete(
            Long shopId,
            Long shopArticleId,
            ArticleDto articleDto
    ) {
        Article article = articleRepo.findByShopArticleIdAndShopId(shopArticleId, shopId)
                .orElseThrow();
        if (article.getAccount() != null && article.getAccount().getId().equals(articleDto.getAccountId())) {
            articleRepo.delete(article);
        } else {
            throw new IllegalStateException("권한이 없습니다.");
        }
    }

    // 게시글 검색하기
    public Page<ArticleDto> search(
            String type,
            String keyword,
            Pageable pageable
    ) {
        return articleRepo.findByKeyewordContaining(type, keyword, pageable)
                .map(ArticleDto::fromEntity);
    }

    // 게시판 별 게시글 검색하기
    public Page<ArticleDto> searchWithBoard(
            String type,
            String keyword,
            Board board,
            Pageable pageable
    ) {
        return articleRepo.findByKeyewordContainingAndBoard(type, keyword, board, pageable)
                .map(ArticleDto::fromEntity);
    }


    // 공지사항 게시글 최신순 3개 읽어들이기
    public Page<ArticleDto> findNoticeArticles(Long shopId, Pageable pageable) {
        return articleRepo.findByShopIdAndBoardOrderByIdDesc(shopId, Board.NOTICE, PageRequest.of(0, 3))
                .map(ArticleDto::fromEntity);
    }

    //비밀게시판 패스워드 체크하기
    public boolean checkPassword(Long shopArticleId, Long shopId, String password) {
        Article article = articleRepo.findByShopArticleIdAndShopId(shopArticleId, shopId)
                .orElseThrow();

        return article.getPassword().equals(password);
    }
}

