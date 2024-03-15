package com.example.workmate.controller.community;

import com.example.workmate.dto.community.ArticleDto;
import com.example.workmate.entity.Board;
import com.example.workmate.service.community.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@Slf4j
@Controller
@RequestMapping("{shopId}/community")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    // 게시글 작성
    @GetMapping("article/new")
    public String createArticle(
            Model model
    ) {
        model.addAttribute("boards", Arrays.asList(Board.values()));
        return "article/new";
    }
    @PostMapping("article")
    public ArticleDto create(
            @RequestBody
            ArticleDto articleDto
    ) {
        return articleService.create(articleDto);
    }

    //전체 게시글 보기
    @GetMapping
    public Page<ArticleDto> readPage(
            @PathVariable("shopId")
            Long shopId,
            Pageable pageable,
            Model model
    ) {
        Page<ArticleDto> articlePage = articleService.readPage(shopId, pageable);
        model.addAttribute("articles", articlePage.getContent());
        model.addAttribute("page", articlePage);
        model.addAttribute("currentPage", pageable.getPageNumber());
        model.addAttribute("totalPages", articlePage.getTotalPages());
        return articleService.readPage(shopId, pageable);
    }

    //BOARD별 보기
    @GetMapping("{board}")
    public Page<ArticleDto> readPageByBoard(
            @PathVariable("shopId")
            Long shopId,
            @RequestParam("board")
            Board board,
            Pageable pageable,
            Model model
    ) {
        return articleService.readPageByBoard(board, shopId, pageable);
    }

    //게시글 하나 보기
    @GetMapping("{board}/{articleId}")
    public ArticleDto readOne(
            @PathVariable("shopId")
            Long shopId,
            @PathVariable("articleId")
            Long articleId

    ) {
        return articleService.readOne(shopId, articleId);
    }

    @PutMapping("{board}/{articleId}")
    public ArticleDto update(
            @PathVariable("shopId")
            Long shopId,
            @PathVariable("articleId")
            Long articleId,
            @RequestBody
            ArticleDto articleDto
    ) {
        return articleService.update(shopId, articleId, articleDto);
    }

    @DeleteMapping("{board}/{articleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable("shopId")
            Long shopId,
            @PathVariable("articleId")
            Long articleId,
            @RequestBody
            ArticleDto articleDto
    ) {
        articleService.delete(shopId, articleId, articleDto);
    }
}
