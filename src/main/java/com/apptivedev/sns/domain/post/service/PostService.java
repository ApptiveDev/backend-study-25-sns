package com.apptivedev.sns.domain.post.service;

import com.apptivedev.sns.domain.post.dto.PostCreateRequest;
import com.apptivedev.sns.domain.post.dto.PostResponse;
import com.apptivedev.sns.domain.post.entity.Post;
import com.apptivedev.sns.domain.post.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)   // 기본은 읽기 전용, 쓰기 메서드에만 @Transactional을 따로 붙인다
public class PostService {

    private final PostRepository postRepository;


    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Transactional
    public PostResponse create(PostCreateRequest request) {
        Post post = new Post(request.title(), request.content());
        Post savedPost = postRepository.save(post);
        return PostResponse.from(savedPost);
    }
}