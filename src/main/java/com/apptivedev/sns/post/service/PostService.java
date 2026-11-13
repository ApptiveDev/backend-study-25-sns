package com.apptivedev.sns.post.service;

import com.apptivedev.sns.post.dto.PostCreateRequest;
import com.apptivedev.sns.post.dto.PostResponse;
import com.apptivedev.sns.post.dto.PostUpdateRequest;
import com.apptivedev.sns.post.entity.Post;
import com.apptivedev.sns.post.exception.PostNotFoundException;
import com.apptivedev.sns.post.repository.PostRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
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

    public List<PostResponse> findAll() {
        return postRepository.findAll().stream()
                .map(PostResponse::from)
                .toList();
    }

    public PostResponse findById(Long postId) {
        Post post = getPost(postId);
        return PostResponse.from(post);
    }

    @Transactional
    public PostResponse update(Long postId, PostUpdateRequest request) {
        validateUpdateRequest(request);
        Post post = getPost(postId);

        if (request.isTitleProvided()) {
            post.updateTitle(request.getTitle());
        }
        if (request.isContentProvided()) {
            post.updateContent(request.getContent());
        }
        return PostResponse.from(post);
    }

    @Transactional
    public void delete(Long postId) {
        Post post = getPost(postId);
        postRepository.delete(post);
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
    }

    private void validateUpdateRequest(PostUpdateRequest request) {
        if (!request.isTitleProvided() && !request.isContentProvided()) {
            throw new IllegalArgumentException("수정할 제목이나 내용을 하나 이상 보내 주세요.");
        }
        if (request.isTitleProvided() && isBlank(request.getTitle())) {
            throw new IllegalArgumentException("제목은 비워 둘 수 없습니다.");
        }
        if (request.isContentProvided() && isBlank(request.getContent())) {
            throw new IllegalArgumentException("내용은 비워 둘 수 없습니다.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}