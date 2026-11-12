package com.apptivedev.sns.domain.post.service;

import com.apptivedev.sns.domain.post.dto.PostCreateRequest;
import com.apptivedev.sns.domain.post.dto.PostResponse;
import com.apptivedev.sns.domain.post.entity.Post;
import com.apptivedev.sns.domain.post.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)   // 기본은 읽기 전용, 쓰기 메서드에만 @Transactional을 따로 붙인다
public class PostService {

    private final PostRepository postRepository;


    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }
    //-------------쓰기---------------------
    @Transactional
    public PostResponse create(PostCreateRequest request) {
        Post post = new Post(request.title(), request.content());
        Post savedPost = postRepository.save(post);
        return PostResponse.from(savedPost);
    }
    //------------조회---------------
    //Transcational은 자동으로 True값으로 들어감.
    public List<PostResponse> findAll() {
        return postRepository.findAll().stream()   // List<Post>를 하나씩 흘려보내며
                .map(PostResponse::from)           // 각각 PostResponse로 바꾸고
                .toList();                         // 다시 리스트로 모음
    }

    // 단건 조회
    public PostResponse findById(Long postId) {
        Post post = getPostOrThrow(postId);
        return PostResponse.from(post);
    }

    // ---------- 내부 도우미 ----------

    // id로 게시글을 찾고, 없으면 404 예외를 던진다 (단건 조회, 수정, 삭제에서 같이 사용)
    private Post getPostOrThrow(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));
    }
}