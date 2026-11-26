package com.apptivedev.sns.post.service;

import com.apptivedev.sns.post.dto.PostCreateRequest;
import com.apptivedev.sns.post.dto.PostUpdateRequest;
import com.apptivedev.sns.post.dto.PostResponse;
import com.apptivedev.sns.post.entity.Post;
import com.apptivedev.sns.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
// Service 객체 관리하도록 지정.
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;

//    public PostService(PostRepository postRepository) {
//        this.postRepository = postRepository;
//    }
    // (초기화되지 않은 final 필드와 @NotNull 필드를 매개변수로 받는)
    // 생성자를 자동으로 만들어 주는 Lombok 어노테이션. = @RequiredArgsConstructor

    // DB를 하나의 트랜잭션으로 처리
    @Transactional
    public PostResponse create(PostCreateRequest request) {

        validateTitle(request.title());
        validateContent(request.content());
        // 유지보수성을 증가시킴.

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

    // <이전 방식>
    // .orElseThrow(() -> new IllegalArgumentException(
    //    "게시글을 찾을 수 없습니다."
    //));
    // -> JAVA에서 제공하는 것이고, 잘못된 인자가 전달되었을 때 사용
    // HTTP 상태 코드를 직접 지정할 수 없어 500으로 통일된다.
    // 그래서 보이는 것처럼 HttpStatus를 따로 사용하지 않는다.
    // 같은 문제로 인한 것인데, 서로 다른 오류를 반환하는 상황을 피하기 위함.

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "제목을 입력해야 합니다."
            );
        }

        if (title.length() > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "제목은 100자 이하여야 합니다."
            );
        }
    }

    private void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "내용을 입력해야 합니다."
            );
        }

        if (content.length() > 500) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "내용은 500자 이하여야 합니다."
            );
        }
    }
}
