package com.apptivedev.sns.domain.post.controller;

import com.apptivedev.sns.domain.post.dto.CreatePostRequest;
import com.apptivedev.sns.domain.post.dto.PostResponse;
import com.apptivedev.sns.domain.post.service.PostService;
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

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @RequestBody CreatePostRequest request
    ) {
        PostResponse response = postService.createPost(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public List<PostResponse> getPosts() {
        return postService.getPosts();
    }

    @GetMapping("/{postId}")
    public PostResponse getPost(@PathVariable("postId") Long postId) {
        return postService.getPost(postId);
    }

    @PatchMapping("/{postId}")
    public PostResponse updatePost(
            @PathVariable("postId") Long postId,
            @RequestBody UpdatePostRequest request
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