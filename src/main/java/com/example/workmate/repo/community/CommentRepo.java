package com.example.workmate.repo.community;

import com.example.workmate.entity.community.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepo extends JpaRepository<Comment, Long> {
}
