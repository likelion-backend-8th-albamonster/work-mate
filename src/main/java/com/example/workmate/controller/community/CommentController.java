package com.example.workmate.controller.community;

import com.example.workmate.dto.community.CommentDto;
import com.example.workmate.service.community.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Slf4j
@Controller
@RequestMapping("{shopId}/community/{board}/{articleId}/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    // 댓글 작성
    @PostMapping
    public String create(
            @PathVariable("shopId")
            Long shopId,
            @PathVariable("board")
            String board,
            @PathVariable("articleId")
            Long articleId,
            @ModelAttribute
            CommentDto commentDto
    ) {
        commentService.create(articleId, commentDto);

        return "redirect:/" + shopId + "/community/" + board + "/" + articleId;
    }


    // 댓글 수정
    @PostMapping("{commentId}/update")
    public String update(
            @PathVariable("shopId")
            Long shopId,
            @PathVariable("board")
            String board,
            @PathVariable("articleId")
            Long articleId,
            @PathVariable("commentId")
            Long commentId,
            @ModelAttribute
            CommentDto commentDto
    ) {
        commentService.update(commentId, commentDto);
        return "redirect:/" + shopId + "/community/" + board + "/" + articleId;
    }

    // 댓글 삭제
    @PostMapping("{commentId}/delete")
    public String delete(
            @PathVariable("shopId")
            Long shopId,
            @PathVariable("board")
            String board,
            @PathVariable("articleId")
            Long articleId,
            @PathVariable("commentId")
            Long commentId,
            @ModelAttribute
            CommentDto commentDto
    ) {
        commentService.delete(commentId, commentDto);
        return "redirect:/" + shopId + "/community/" + board + "/" + articleId;
    }
}
