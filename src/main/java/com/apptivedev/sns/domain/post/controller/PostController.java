package com.apptivedev.sns.domain.post.controller;

import com.apptivedev.sns.domain.post.dto.PostCreateRequest;
import com.apptivedev.sns.domain.post.dto.PostResponse;
import com.apptivedev.sns.domain.post.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // POST /posts → 201 Created
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponse create(@RequestBody PostCreateRequest request) {
        return postService.create(request);
    }

    // GET /posts → 200 OK
    @GetMapping
    public List<PostResponse> findAll() {
        return postService.findAll();
    }

    // GET /posts/{postId} → 200 OK
    @GetMapping("/{postId}")
    public PostResponse findById(@PathVariable("postId") Long postId) {
        return postService.findById(postId);
    }

}