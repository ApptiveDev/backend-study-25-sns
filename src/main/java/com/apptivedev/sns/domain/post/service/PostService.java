package com.apptivedev.sns.domain.post.service;

import com.apptivedev.sns.domain.post.dto.PostCreateRequest;
import com.apptivedev.sns.domain.post.dto.PostResponse;
import com.apptivedev.sns.domain.post.dto.PostUpdateRequest;
import com.apptivedev.sns.domain.post.entity.Post;
import com.apptivedev.sns.domain.post.exception.InvalidPostRequestException;
import com.apptivedev.sns.domain.post.exception.PostNotFoundException;
import com.apptivedev.sns.domain.post.repository.PostRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Transactional
    public PostResponse create(PostCreateRequest request) {
        validateText(request.title(), "제목", 100);
        validateText(request.content(), "내용", 500);
        Post post = new Post(request.title(), request.content());
        return PostResponse.from(postRepository.save(post));
    }

    public List<PostResponse> findAll() {
        return postRepository.findAll().stream().map(PostResponse::from).toList();
    }

    public PostResponse findById(Long postId) {
        return PostResponse.from(getPost(postId));
    }

    @Transactional
    public PostResponse update(Long postId, PostUpdateRequest request) {
        Post post = getPost(postId);
        if (!request.hasTitle() && !request.hasContent()) {
            throw new InvalidPostRequestException("수정할 제목 또는 내용을 하나 이상 보내 주세요.");
        }
        // 모든 입력이 유효한지 먼저 확인한 뒤 상태를 변경한다.
        if (request.hasTitle()) {
            validateText(request.getTitle(), "제목", 100);
        }
        if (request.hasContent()) {
            validateText(request.getContent(), "내용", 500);
        }
        if (request.hasTitle()) {
            post.updateTitle(request.getTitle());
        }
        if (request.hasContent()) {
            post.updateContent(request.getContent());
        }
        // 트랜잭션 종료 시 JPA 변경 감지로 UPDATE가 실행된다.
        return PostResponse.from(post);
    }

    @Transactional
    public void delete(Long postId) {
        postRepository.delete(getPost(postId));
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
    }

    private void validateText(String value, String fieldName, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new InvalidPostRequestException(fieldName + "은(는) 비어 있거나 공백만으로 이루어질 수 없습니다.");
        }
        if (value.length() > maxLength) {
            throw new InvalidPostRequestException(fieldName + "은(는) " + maxLength + "자 이하여야 합니다.");
        }
    }
}
