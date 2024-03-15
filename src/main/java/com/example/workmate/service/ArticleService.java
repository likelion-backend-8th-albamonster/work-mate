package com.example.workmate.service;

import com.example.workmate.dto.ArticleDto;
import com.example.workmate.entity.Article;
import com.example.workmate.entity.Board;
import com.example.workmate.repo.ArticleRepo;
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
            Board board,
            ArticleDto dto) {
        Article article = articleRepo.save(Article.builder()
                .board(board)
                .title(dto.getTitle())
                .content(dto.getContent())
                .board(dto.getBoard())
                .build());
        return ArticleDto.fromEntity(article);
    }

    public Page<ArticleDto> readPage(
            Long articleId,
            Pageable pageable
    ) {
        return articleRepo.findAllById(articleId, pageable)
                .map(ArticleDto::fromEntity);
    }

    public ArticleDto readOne(
            Long articleId
    ) {
        return ArticleDto.fromEntity(articleRepo.findById(articleId)
                .orElseThrow());
    }
}

//    public ArticleDto update(
//            Long articleId,
//            ArticleDto articleDto
//    ) {
//        Article article = articleRepo.findById(articleId)
//                .orElseThrow();
////        if (article.getAccountId())
////    }
//}
