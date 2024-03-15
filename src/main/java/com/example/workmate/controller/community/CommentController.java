package com.example.workmate.controller.community;

import com.example.workmate.service.community.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Controller
@RequestMapping("{shopId}/community/{articleId}/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

//    @PostMapping
}
