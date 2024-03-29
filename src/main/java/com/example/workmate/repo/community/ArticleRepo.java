package com.example.workmate.repo.community;

import com.example.workmate.entity.community.Article;
import com.example.workmate.entity.community.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ArticleRepo extends JpaRepository<Article, Long> {

    // 매장별 게시글 전체 읽기
    Page<Article> findAllByShopId(Long shopId, Pageable pageable);

    // 게시판별 게시글 전체 읽기
    Page<Article> findByBoardAndShopId(Board board, Long shopId, Pageable pageable);
    // 매장별 게시글 읽기
    Optional<Article> findByShopArticleIdAndShopId(Long shopArticleId, Long shopId);

    // 키워드로 게시글 찾기
    @Query(
            "SELECT a " +
                    "FROM Article a " +
                    "WHERE (:type = 'title' AND a.title LIKE %:keyword%) " +
                    "OR (:type = 'content' AND a.content LIKE %:keyword%)"
    )
    Page<Article> findByKeyewordContaining(
            @Param("type")
            String type,
            @Param("keyword")
            String keyword,
            Pageable pageable
    );

    // 게시판별 키워드로 게시글 찾기
    @Query(
            "SELECT a " +
                    "FROM Article a " +
                    "WHERE ((:type = 'title' AND a.title LIKE %:keyword%) " +
                    "OR (:type = 'content' AND a.content LIKE %:keyword%)) " +
                    "AND a.board = :board"
    )
    Page<Article> findByKeywordContainingAndBoard(
            @Param("type") String type,
            @Param("keyword") String keyword,
            @Param("board") Board board,
            Pageable pageable
    );

    // 공지사항 최신 게시글 3개 조회하기
    Page<Article> findByShopIdAndBoardOrderByIdDesc(Long shopId, Board board, Pageable pageable);

    // 매장별 마지막 shopArticleId 찾기
    @Query("SELECT MAX(a.shopArticleId) FROM Article a WHERE a.shop.id = :shopId")
    Optional<Long> findLastShopArticleIdByShop(@Param("shopId") Long shopId);

}
