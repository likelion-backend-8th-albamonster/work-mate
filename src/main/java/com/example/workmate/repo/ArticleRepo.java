package com.example.workmate.repo;

import com.example.workmate.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepo extends JpaRepository<Article, Long> {
    Page<Article> findAllById(Long articleId, Pageable pageable);
}
