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

        return "community/commu-article-new";
    }

    // 컨트롤러에서 리다이렉션 하는 방식
//    // 게시글 작성 폼 제출
//    @PostMapping("/article/create")
//    public String create(
//            @PathVariable("shopId")
//            Long shopId,
//            @RequestParam
//            Board board,
//            @ModelAttribute
//            ArticleDto articleDto,
//            RedirectAttributes redirectAttributes,
//            Model model
//    ) {
//        Long newId = articleService.create(articleDto).getShopArticleId();
//        model.addAttribute("boards", Board.values());
//        redirectAttributes.addFlashAttribute("message", "게시글이 작성되었습니다.");
//        if (board == Board.SECRET) {
//            return String.format("redirect:/%d/community/%s", shopId, articleDto.getBoard().name());
//        }
//        return String.format("redirect:/%d/community/%s/%d", shopId, articleDto.getBoard().name(), newId);
//    }

    // ajax에서 리다이렉션 하는 방식 : 응답에 상태코드랑 데이터를 json형태로 클라이언트에 전달
    // 게시글 작성 폼 제출
    @PostMapping("/article/create")
    public String create(
            @PathVariable("shopId")
            Long shopId,
            @RequestParam
            Board board,
            @ModelAttribute
            ArticleDto articleDto,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        Long newId = articleService.create(articleDto).getShopArticleId();
        model.addAttribute("boards", Board.values());
        redirectAttributes.addFlashAttribute("message", "게시글이 작성되었습니다.");
        if (board == Board.SECRET) {
            return String.format("redirect:/%d/community/%s", shopId, articleDto.getBoard().name());
        }
        return String.format("redirect:/%d/community/%s/%d", shopId, articleDto.getBoard().name(), newId);
    }

    //전체 게시글 보기 (Main)
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
        return "community/commu-article-main";
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

        return "community/commu-article-main";
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
            HttpSession httpSession,
            Model model
    ) {
        ArticleDto article = articleService.readOne(shopId, shopArticleId);

        model.addAttribute("boards", Board.values());
        model.addAttribute("shopId", shopId);
        model.addAttribute("board", board);
        model.addAttribute("article", article);
        model.addAttribute("shopArticleId", shopArticleId);

        // 세션에서 접근 권한 확인
        Boolean isAuthorized = (Boolean) httpSession.getAttribute(shopArticleId.toString());
        if (board == Board.SECRET && articleService.checkAccessRights()) {
            return "community/commu-article-read";
        } else if (board == Board.SECRET && (isAuthorized == null || !isAuthorized)) {
            return "community/commu-secret-password";
        }
        httpSession.removeAttribute(shopArticleId.toString());
        return "community/commu-article-read";
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
            Model model
    ) {
            articleService.checkAccountId(shopArticleId, shopId);
            Optional<Article> article = articleRepo.findByShopArticleIdAndShopId(shopArticleId, shopId);
            List<Board> filteredBoards = Arrays.stream(Board.values())
                    .filter(b -> !b.name().equals(board.toUpperCase()))
                    .collect(Collectors.toList());

            model.addAttribute("shopId", shopId);
            model.addAttribute("board", board);
            model.addAttribute("boards", Board.values());
            model.addAttribute("filteredBoards", filteredBoards);
            model.addAttribute("article", article.get());
            model.addAttribute("shopArticleId", shopArticleId);

            return "community/commu-article-edit";
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
        articleService.delete(shopId, shopArticleId);
        redirectAttributes.addFlashAttribute("message", "게시글이 삭제되었습니다.");
        return "redirect:/" + shopId + "/community";
    }

    // 비밀번호 입력 폼 제출
    @PostMapping("{board}/{shopArticleId}/password")
    public String password(
            @PathVariable("shopId")
            Long shopId,
            @PathVariable("board")
            Board board,
            @PathVariable("shopArticleId")
            Long shopArticleId,
            @RequestParam("password")
            String password,
            HttpSession httpSession,
            Model model
    ) {
        model.addAttribute("shopId", shopId);
        model.addAttribute("shopArticleId", shopArticleId);
        model.addAttribute("board", board);
        if (!articleService.checkPassword(shopArticleId, shopId, password)) {
            throw new IllegalStateException("비밀번호가 틀렸습니다.");
        }

        // 비밀번호 일치시 세션에 접근 권한 저장
        httpSession.setAttribute(shopArticleId.toString(), true);
        return String.format("redirect:/%d/community/%s/%d", shopId, board.name(), shopArticleId);
    }
}

