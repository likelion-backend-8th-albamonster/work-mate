package com.example.workmate.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Board {
    @Id
    private String board;
}
