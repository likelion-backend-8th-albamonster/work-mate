package com.example.workmate.repo.community;

import com.example.workmate.entity.Article;
import com.example.workmate.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    Page<Article> findAllByShopId(Long shopId, Pageable pageable);

    Page<Article> findByBoardAndShopId(Board board, Long shopId, Pageable pageable);
    Optional<Article> findByIdAndShopId(Long articleId, Long shopId);

}
