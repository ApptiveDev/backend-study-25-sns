package com.apptivedev.sns.post.service;

import com.apptivedev.sns.post.dto.PostCreateRequest;
import com.apptivedev.sns.post.dto.PostUpdateRequest;
import com.apptivedev.sns.post.dto.PostResponse;
import com.apptivedev.sns.post.entity.Post;
import com.apptivedev.sns.post.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
// Service 객체 관리하도록 지정.
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Transactional
    // DB를 하나의 트랜잭션으로 처리
    public PostResponse create(PostCreateRequest request) {
        Post post = new Post(request.title(), request.content());
        Post savedPost = postRepository.save(post);
        return PostResponse.from(savedPost);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> findAll() {
        return postRepository.findAll().stream()
                .map(PostResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PostResponse findById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        return PostResponse.from(post);
    }

    @Transactional
    public PostResponse update(Long postId, PostUpdateRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        post.update(request.title(), request.content());

        return PostResponse.from(post);
    }

    @Transactional
    public void delete(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        postRepository.delete(post);
    }
}
