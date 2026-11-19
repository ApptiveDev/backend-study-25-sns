package com.apptivedev.sns.domain.post.repository;

import com.apptivedev.sns.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}