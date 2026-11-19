package com.apptivedev.sns.domain.post.controller;

import com.apptivedev.sns.domain.post.dto.CreatePostRequest;
import com.apptivedev.sns.domain.post.dto.PostResponse;
import com.apptivedev.sns.domain.post.dto.PostPageResponse;
import com.apptivedev.sns.domain.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.apptivedev.sns.domain.post.dto.UpdatePostRequest;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import com.apptivedev.sns.domain.auth.interceptor.AuthInterceptor;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @RequestAttribute(AuthInterceptor.LOGIN_USER_ID) Long loginUserId,
            @Valid @RequestBody CreatePostRequest request
    ) {
        PostResponse response = postService.createPost(loginUserId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public List<PostResponse> getPosts() {
        return postService.getPosts();
    }

    @GetMapping("/page")
    public PostPageResponse getPostsPage(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        return postService.getPostsPage(page, size);
    }

    @GetMapping("/{postId}")
    public PostResponse getPost(@PathVariable("postId") Long postId) {
        return postService.getPost(postId);
    }

    @PatchMapping("/{postId}")
    public PostResponse updatePost(
            @PathVariable("postId") Long postId,
            @Valid @RequestBody UpdatePostRequest request
    ) {
        return postService.updatePost(postId, request);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable("postId") Long postId
    ) {
        postService.deletePost(postId);

        return ResponseEntity.noContent().build();
    }
}
