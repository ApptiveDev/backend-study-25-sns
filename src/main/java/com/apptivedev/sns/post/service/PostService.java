package com.apptivedev.sns.post.service;

import com.apptivedev.sns.post.dto.PostCreateRequest;
import com.apptivedev.sns.post.dto.PostResponse;
import com.apptivedev.sns.post.dto.PostUpdateRequest;
import com.apptivedev.sns.post.entity.Post;
import com.apptivedev.sns.post.repository.PostRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Transactional
    public PostResponse create(PostCreateRequest request) {
        Post post = new Post(request.title(), request.content());
        return PostResponse.from(postRepository.save(post));
    }

    public List<PostResponse> findAll() {
        return postRepository.findAll().stream()
                .map(PostResponse::from)
                .toList();
    }

    public PostResponse findById(Long postId) {
        return PostResponse.from(findPost(postId));
    }

    @Transactional
    public PostResponse update(Long postId, PostUpdateRequest request) {
        Post post = findPost(postId);
        String title = request.getTitle();
        String content = request.getContent();

        if (title == null && content == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "수정할 제목이나 본문을 입력해 주세요.");
        }
        if (title != null && title.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "제목을 공백으로 수정할 수 없습니다.");
        }
        if (content != null && content.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "본문을 공백으로 수정할 수 없습니다.");
        }

        if (title != null) {
            post.changeTitle(title);
        }
        if (content != null) {
            post.changeContent(content);
        }

        return PostResponse.from(post);
    }

    @Transactional
    public void delete(Long postId) {
        postRepository.delete(findPost(postId));
    }

    private Post findPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));
    }
}