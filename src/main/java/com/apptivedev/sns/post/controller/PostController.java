package com.apptivedev.sns.post.controller;

import com.apptivedev.sns.post.dto.PostCreateRequest;
import com.apptivedev.sns.post.dto.PostResponse;
import com.apptivedev.sns.post.dto.PostUpdateRequest;
import com.apptivedev.sns.post.service.PostService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostResponse> create(@Valid @RequestBody PostCreateRequest request) {
        PostResponse response = postService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> findAll() {
        return ResponseEntity.ok(postService.findAll());
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> findById(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.findById(postId));
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<PostResponse> update(@PathVariable Long postId,
                                               @Valid @RequestBody PostUpdateRequest request) {
        return ResponseEntity.ok(postService.update(postId, request));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> delete(@PathVariable Long postId) {
        postService.delete(postId);
        return ResponseEntity.noContent().build();
    }
}