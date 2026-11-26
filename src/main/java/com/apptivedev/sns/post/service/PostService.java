package com.apptivedev.sns.post.service;

import com.apptivedev.sns.global.exception.BusinessException;
import com.apptivedev.sns.post.dto.*;
import com.apptivedev.sns.post.entity.Post;
import com.apptivedev.sns.post.repository.PostRepository;
import com.apptivedev.sns.user.entity.User;
import com.apptivedev.sns.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }
    @Transactional
    public PostResponse createPost(PostCreateRequest request, Long loginId) {
        // 3주차: JWT로 인증된 사용자만 작성자로 연결한다.
        User user = userRepository.findById(loginId).orElseThrow(BusinessException::unauthorized);
        // 4주차: Service는 흐름을 조정하고 Entity가 자신의 규칙을 검증한다.
        Post post = Post.createPost(request.title(), request.content(), user);
        return PostResponse.from(postRepository.save(post));
    }
    @Transactional(readOnly = true)
    public List<PostResponse> getAllPosts() { return postRepository.findAll().stream().map(PostResponse::from).toList(); }
    @Transactional(readOnly = true)
    public PostResponse getPost(Long postId) { return PostResponse.from(findPost(postId)); }
    @Transactional
    public PostResponse updatePost(Long postId, PostUpdateRequest request) {
        Post post = findPost(postId);
        post.update(request.title(), request.content());
        return PostResponse.from(post); // JPA 변경 감지
    }
    @Transactional
    public void deletePost(Long postId) { postRepository.delete(findPost(postId)); }
    private Post findPost(Long postId) { return postRepository.findById(postId).orElseThrow(BusinessException::notFound); }
}
