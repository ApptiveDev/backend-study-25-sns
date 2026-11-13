package com.apptivedev.sns.domain.post.service;

import com.apptivedev.sns.domain.post.dto.CreatePostRequest;
import com.apptivedev.sns.domain.post.dto.PostResponse;
import com.apptivedev.sns.domain.post.entity.Post;
import com.apptivedev.sns.domain.post.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import com.apptivedev.sns.domain.post.exception.PostNotFoundException;
import com.apptivedev.sns.domain.post.dto.UpdatePostRequest;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Transactional
    public PostResponse createPost(CreatePostRequest request) {
        validateText(request.title(), "제목", 100);
        validateText(request.content(), "내용", 500);

        Post post = new Post(request.title(), request.content());
        Post savedPost = postRepository.save(post);

        return new PostResponse(
                savedPost.getId(),
                savedPost.getTitle(),
                savedPost.getContent()
        );
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPosts() {
        return postRepository.findAll()
                .stream()
                .map(post -> new PostResponse(
                        post.getId(),
                        post.getTitle(),
                        post.getContent()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public PostResponse getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent()
        );
    }

    @Transactional
    public PostResponse updatePost(Long postId, UpdatePostRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        if (!request.hasTitle() && !request.hasContent()) {
            throw new IllegalArgumentException("수정할 항목을 보내주세요.");
        }

        if (request.hasTitle()) {
            validateText(request.getTitle(), "제목", 100);
        }

        if (request.hasContent()) {
            validateText(request.getContent(), "내용", 500);
        }

        if (request.hasTitle()) {
            post.changeTitle(request.getTitle());
        }

        if (request.hasContent()) {
            post.changeContent(request.getContent());
        }

        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent()
        );
    }

    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        postRepository.delete(post);
    }

    private void validateText(String value, String fieldName, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + "은 비어 있을 수 없습니다."
            );
        }

        if (value.length() > maxLength) {
            throw new IllegalArgumentException(
                    fieldName + "은 " + maxLength + "자 이하여야 합니다."
            );
        }
    }
}
