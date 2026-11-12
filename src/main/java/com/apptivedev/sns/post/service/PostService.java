package com.apptivedev.sns.post.service;

import com.apptivedev.sns.post.dto.PostCreateRequest;
import com.apptivedev.sns.post.dto.PostUpdateRequest;
import com.apptivedev.sns.post.dto.PostResponse;
import com.apptivedev.sns.post.entity.Post;
import com.apptivedev.sns.post.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
// Service 객체 관리하도록 지정.
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    // DB를 하나의 트랜잭션으로 처리
    @Transactional
    public PostResponse create(PostCreateRequest request) {

        if (request.title() == null || request.title().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "제목을 입력해야 합니다."
            );
        }

        if (request.content() == null || request.content().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "내용을 입력해야 합니다."
            );
        }

        if (request.title().length() > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "제목은 100자 이하여야 합니다."
            );
        }

        if (request.content().length() > 500) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "내용은 500자 이하여야 합니다."
            );
        }

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
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "게시글을 찾을 수 없습니다."
                ));

        return PostResponse.from(post);
    }


    @Transactional
    public PostResponse update(Long postId, PostUpdateRequest request) {

        if (!request.hasTitle() && !request.hasContent()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "수정할 내용을 입력해야 합니다."
            );
        }

        if (request.hasTitle()) {
            if (request.title() == null || request.title().isBlank()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "제목을 올바르게 입력해야 합니다."
                );
            }

            if (request.title().length() > 100) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "제목은 100자 이하여야 합니다."
                );
            }
        }

        if (request.hasContent()) {
            if (request.content() == null || request.content().isBlank()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "내용을 올바르게 입력해야 합니다."
                );
            }

            if (request.content().length() > 500) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "내용은 500자 이하여야 합니다."
                );
            }
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "게시글을 찾을 수 없습니다."
                ));

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
