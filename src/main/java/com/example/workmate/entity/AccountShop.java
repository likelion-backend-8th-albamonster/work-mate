package com.example.workmate.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class AccountShop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String password;
    private String email;
    //private List<Authorites> authorites;
    private Long shopId;
    private Long salaryId;
    private Long attendanceId;
    private Long ArticleId;
}
