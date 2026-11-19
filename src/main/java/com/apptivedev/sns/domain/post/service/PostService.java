package com.apptivedev.sns.domain.post.service;

import com.apptivedev.sns.domain.auth.exception.UnauthorizedException;
import com.apptivedev.sns.domain.post.dto.CreatePostRequest;
import com.apptivedev.sns.domain.post.dto.PostResponse;
import com.apptivedev.sns.domain.post.dto.PostPageResponse;
import com.apptivedev.sns.domain.post.dto.UpdatePostRequest;
import com.apptivedev.sns.domain.post.entity.Post;
import com.apptivedev.sns.domain.post.exception.PostNotFoundException;
import com.apptivedev.sns.domain.post.repository.PostRepository;
import com.apptivedev.sns.domain.user.entity.User;
import com.apptivedev.sns.domain.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public PostResponse createPost(
            Long loginUserId,
            CreatePostRequest request
    ) {
        User author = userRepository.findById(loginUserId)
                .orElseThrow(() -> new UnauthorizedException(
                        "사용할 수 없는 인증 정보입니다."
                ));

        Post post = new Post(
                author,
                request.title(),
                request.content()
        );

        Post savedPost = postRepository.save(post);

        return PostResponse.from(savedPost);
    }

    public List<PostResponse> getPosts() {
        return postRepository.findAll()
                .stream()
                .map(PostResponse::from)
                .toList();
    }

    public PostPageResponse getPostsPage(int page, int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException("page는 0 이상, size는 1~100이어야 합니다.");
        }

        PageRequest pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return PostPageResponse.from(postRepository.findAll(pageable).map(PostResponse::from));
    }

    public PostResponse getPost(Long postId) {
        Post post = findPost(postId);
        return PostResponse.from(post);
    }

    @Transactional
    public PostResponse updatePost(
            Long postId,
            UpdatePostRequest request
    ) {
        Post post = findPost(postId);

        if (!request.hasTitle() && !request.hasContent()) {
            throw new IllegalArgumentException("수정할 항목을 보내주세요.");
        }

        post.changeText(
                request.hasTitle() ? request.getTitle() : post.getTitle(),
                request.hasContent() ? request.getContent() : post.getContent()
        );

        return PostResponse.from(post);
    }

    @Transactional
    public void deletePost(Long postId) {
        Post post = findPost(postId);
        postRepository.delete(post);
    }

    private Post findPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
    }

}
