package com.example.workmate.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long accountId;
    //private List<Board> boards;
    private Long shopId;
    private String title;
    private String content;
}
