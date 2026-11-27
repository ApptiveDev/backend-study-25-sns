package com.apptivedev.sns.post.service;

import com.apptivedev.sns.post.dto.PostCreateRequest;
import com.apptivedev.sns.post.dto.PostUpdateRequest;
import com.apptivedev.sns.post.dto.PostResponse;
import com.apptivedev.sns.post.entity.Post;
import com.apptivedev.sns.post.repository.PostRepository;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;

    // 생성자 주입
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    // 1. 게시글 생성 (Create)
    @Transactional
    public PostResponse createPost(PostCreateRequest request) {

        Post post = new Post(request.title(), request.content());

        Post savedPost = postRepository.save(post);

        return new PostResponse(
                savedPost.getId(),
                savedPost.getTitle(),
                savedPost.getContent()
        );
    }

    // 2. 게시글 전체 조회 (Read)
    @Transactional
    public List<PostResponse> getAllPosts() {

        return postRepository.findAll()
                .stream()
                .map(post -> new PostResponse(
                        post.getId(),
                        post.getTitle(),
                        post.getContent()
                ))
                .toList();
    }

    // 3. 게시글 단건 조회 (Read)
    @Transactional
    public PostResponse getPost(Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "게시글을 찾을 수 없습니다."
                ));

        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent()
        );
    }

    // 4. 게시글 수정 (Update)
    @Transactional
    public PostResponse updatePost(Long postId, PostUpdateRequest request) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "게시글을 찾을 수 없습니다."
                ));

        post.update(request.title(), request.content());

        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent()
        );
    }

    // 5. 게시글 삭제 (Delete)
    @Transactional
    public void deletePost(Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "게시글을 찾을 수 없습니다."
                ));

        postRepository.delete(post);
    }
}
