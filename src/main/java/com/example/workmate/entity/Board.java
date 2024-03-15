package com.example.workmate.entity;

import lombok.Getter;

@Getter
public enum Board {
    ALLARTICLE("전체게시글"),
    NOTICE("공지사항"),
    REQUEST("요청사항"),
    FREE("자유게시판");

    private String board;
    Board(String board) {
        this.board = board;
    }
}
