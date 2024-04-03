package com.example.workmate.entity.community;

import lombok.Getter;

@Getter
public enum Board {
    NOTICE("공지사항"),
    REQUEST("요청사항"),
    FREE("자유게시판"),
    SECRET("비밀게시판");


    private String board;
    Board(String board) {
        this.board = board;
    }
}
