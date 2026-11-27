package com.apptivedev.sns.post.repository;

import com.apptivedev.sns.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
// Jparepository : DB 저장·조회·삭제 등의 기능 제공