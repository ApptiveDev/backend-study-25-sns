package com.apptivedev.sns.post.repository;

import com.apptivedev.sns.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}