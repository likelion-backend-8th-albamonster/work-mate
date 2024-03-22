package com.example.workmate.controller.community;

import com.example.workmate.dto.community.ArticleDto;
import com.example.workmate.entity.community.Article;
import com.example.workmate.entity.community.Board;
import com.example.workmate.repo.community.ArticleRepo;
import com.example.workmate.service.community.ArticleService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("{shopId}/community")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;
    private final ArticleRepo articleRepo;

    // 게시글 작성 페이지로 이동
    @GetMapping("/article/new")
    public String newForm(
            @PathVariable("shopId")
            Long shopId,
            @RequestParam(value = "board", required = false)
            Board selectedBoard,
            Model model
    ) {
        model.addAttribute("shopId", shopId);
        model.addAttribute("boards", Board.values());
        model.addAttribute("selectedBoard", selectedBoard);

        return "commu-article-new";
    }

    // 게시글 작성 폼 제출
    @PostMapping("/article/create")
    public String create(
            @ModelAttribute
            ArticleDto articleDto,
            @PathVariable("shopId")
            Long shopId,
            RedirectAttributes redirectAttributes,
            HttpSession session,
            Model model
    ) {
        // 세션에서 폼 데이터를 가져옴
        Board board = (Board) session.getAttribute("board");
        String title = (String) session.getAttribute("title");
        String content = (String) session.getAttribute("content");
        String password = (String) session.getAttribute("password");

        // 세션에서 가져온 데이터로 articleDto 업데이트
        articleDto.setBoard(board);
        articleDto.setTitle(title);
        articleDto.setContent(content);
        articleDto.setPassword(password);

        Long newId = articleService.create(articleDto).getShopArticleId();
        model.addAttribute("boards", Board.values());
        redirectAttributes.addFlashAttribute("message", "게시글이 작성되었습니다.");

        // 세션에서 임시 저장된 폼 데이터 제거
        session.removeAttribute("board");
        session.removeAttribute("title");
        session.removeAttribute("content");
        session.removeAttribute("password");

        return String.format("redirect:/%s/community/%s/%d", shopId, articleDto.getBoard().name(), newId);
    }

    //전체 게시글 보기
    @GetMapping
    public String readPage(
            @PathVariable("shopId")
            Long shopId,
            @RequestParam(value = "type", required = false)
            String type,
            @RequestParam(value = "keyword", required = false)
            String keyword,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable,
            Model model) {

        Page<ArticleDto> articles;
        Page<ArticleDto> noticeArticles = articleService.findNoticeArticles(shopId, PageRequest.of(0, 3));
        if (keyword != null && !keyword.isEmpty()) {
            articles = articleService.search(type, keyword, pageable);
        } else {
            articles = articleService.readPage(shopId, pageable);
        }
        model.addAttribute("shopId", shopId);
        model.addAttribute("boards", Board.values());
        model.addAttribute("articles", articles);
        model.addAttribute("selectedBoard", null);
        model.addAttribute("noticeArticles", noticeArticles);
        return "commu-article-main";
    }

    //BOARD별 보기
    @GetMapping("{board}")
    public String readPageByBoard(
            @PathVariable("shopId")
            Long shopId,
            @PathVariable("board")
            Board board,
            @RequestParam(value = "type", required = false)
            String type,
            @RequestParam(value = "keyword", required = false)
            String keyword,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable,
            Model model
    ) {
        Page<ArticleDto> articles;
        Page<ArticleDto> noticeArticles = articleService.findNoticeArticles(shopId, PageRequest.of(0, 3));
        if (keyword != null && !keyword.isEmpty()) {
            articles = articleService.searchWithBoard(type, keyword, board, pageable);
        } else {
            articles = articleService.readPageByBoard(board, shopId, pageable);
        }
        model.addAttribute("shopId", shopId);
        model.addAttribute("boards", Board.values());
        model.addAttribute("selectedBoard", board);
        model.addAttribute("articles", articles);
        model.addAttribute("noticeArticles", noticeArticles);

        return "commu-article-main";
    }

    //게시글 하나 보기
    @GetMapping("{board}/{shopArticleId}")
    public String readOne(
            @PathVariable("shopId")
            Long shopId,
            @PathVariable("board")
            Board board,
            @PathVariable("shopArticleId")
            Long shopArticleId,
            Model model
    ) {
        ArticleDto article = articleService.readOne(shopId, shopArticleId);

        model.addAttribute("boards", Board.values());
        model.addAttribute("shopId", shopId);
        model.addAttribute("board", board);
        model.addAttribute("article", article);
        model.addAttribute("shopArticleId", shopArticleId);
        if (board == Board.SECRET) {
            return "commu-secret-password";
        }
        return "commu-article-read";
    }

    //게시글 수정 페이지로 이동
    @GetMapping("/{board}/{shopArticleId}/edit")
    public String editForm(
            @PathVariable("shopId")
            Long shopId,
            @PathVariable("board")
            String board,
            @PathVariable("shopArticleId")
            Long shopArticleId,
            Model model) {

        Optional<Article> articleOpt = articleRepo.findByShopArticleIdAndShopId(shopArticleId, shopId);

        if (articleOpt.isPresent()) {
            List<Board> filteredBoards = Arrays.stream(Board.values())
                    .filter(b -> !b.name().equals(board.toUpperCase()))
                    .collect(Collectors.toList());

            model.addAttribute("shopId", shopId);
            model.addAttribute("board", board);
            model.addAttribute("boards", Board.values());
            model.addAttribute("filteredBoards", filteredBoards);
            model.addAttribute("article", articleOpt.get());
            model.addAttribute("shopArticleId", shopArticleId);
        } else {
            model.addAttribute("errorMessage", "게시글이 없습니다");
            return "redirect:/error-page";
        }
        return "commu-article-edit";
    }

    // 게시글 수정 처리
    @PostMapping("{board}/{shopArticleId}/update")
    public String update(
            @PathVariable("shopId")
            Long shopId,
            @PathVariable("board")
            String board,
            @PathVariable("shopArticleId")
            Long shopArticleId,
            @ModelAttribute
            ArticleDto articleDto,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        articleService.update(shopId, shopArticleId, articleDto);
        model.addAttribute("boards", Board.values());
        model.addAttribute("shopArticleId", shopArticleId);
        redirectAttributes.addFlashAttribute("message", "게시글이 수정되었습니다.");
        return "redirect:/" + shopId + "/community/" + articleDto.getBoard().name();
    }


    // 게시글 삭제
    @PostMapping("{board}/{shopArticleId}/delete")
    public String delete(
            @PathVariable("shopId")
            Long shopId,
            @PathVariable("shopArticleId")
            Long shopArticleId,
            @ModelAttribute
            ArticleDto articleDto,
            RedirectAttributes redirectAttributes
    ) {
        articleService.delete(shopId, shopArticleId, articleDto);
        redirectAttributes.addFlashAttribute("message", "게시글이 삭제되었습니다.");
        return "redirect:/" + shopId + "/community";
    }

    // 일반 게시판 / 비밀 게시판 구분
    @PostMapping("/article/confirm")
    public String confirm(
            @PathVariable("shopId")
            Long shopId,
            @RequestParam("board")
            Board board,
            @RequestParam("title")
            String title,
            @RequestParam("content")
            String content,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        // 폼 데이터를 세션에 임시 저장
        session.setAttribute("board", board.name());
        session.setAttribute("title", title);
        session.setAttribute("content", content);

        // 비밀게시판이면 비밀번호 입력 페이지로 리다이렉션
        if ("SECRET".equals(board.name())) {
            return String.format("redirect:/%s/community/article/passwordForm", shopId);
        }
        // 비밀게시판이 아니면 바로 게시글 생성 로직으로 이동
        redirectAttributes.addFlashAttribute("board", board.name());
        redirectAttributes.addFlashAttribute("title", title);
        redirectAttributes.addFlashAttribute("content", content);
        return String.format("redirect:/%s/community/article/create", shopId);
    }

    // 비밀번호 입력 폼 불러오기
    @GetMapping("/article/passwordForm")
    public String passwordForm() {
        return "commu-secret-password";
    }

    // 비밀번호 입력 폼 제출
    @PostMapping("/article/password")
    public String password(
            @PathVariable("shopId")
            Long shopId,
            @RequestParam("password")
            String password,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        session.setAttribute("password", password);
        redirectAttributes.addFlashAttribute("password", password);
        return String.format("redirect:/%s/community/article/create", shopId);
    }
}

