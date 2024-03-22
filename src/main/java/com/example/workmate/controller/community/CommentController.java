package com.example.workmate.controller.community;

import com.example.workmate.dto.community.CommentDto;
import com.example.workmate.service.community.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("{shopId}/community/{board}/{shopArticleId}/comment")
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
            @PathVariable("shopArticleId")
            Long shopArticleId,
            @ModelAttribute
            CommentDto commentDto
    ) {
        commentService.create(shopId, shopArticleId, commentDto);

        return "redirect:/" + shopId + "/community/" + board + "/" + shopArticleId;
    }


    // 댓글 수정
    @PostMapping("{commentId}/update")
    public String update(
            @PathVariable("shopId")
            Long shopId,
            @PathVariable("board")
            String board,
            @PathVariable("shopArticleId")
            Long shopArticleId,
            @PathVariable("commentId")
            Long commentId,
            @ModelAttribute
            CommentDto commentDto
    ) {
        commentService.update(commentId, commentDto);
        return "redirect:/" + shopId + "/community/" + board + "/" + shopArticleId;
    }

    // 댓글 삭제
    @PostMapping("{commentId}/delete")
    public String delete(
            @PathVariable("shopId")
            Long shopId,
            @PathVariable("board")
            String board,
            @PathVariable("shopArticleId")
            Long shopArticleId,
            @PathVariable("commentId")
            Long commentId,
            @ModelAttribute
            CommentDto commentDto
    ) {
        commentService.delete(commentId, commentDto);
        return "redirect:/" + shopId + "/community/" + board + "/" + shopArticleId;
    }
}
