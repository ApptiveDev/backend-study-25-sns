package com.apptivedev.sns.post.controller;

import com.apptivedev.sns.auth.interceptor.AuthInterceptor;
import com.apptivedev.sns.post.dto.*;
import com.apptivedev.sns.post.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;
    public PostController(PostService postService) { this.postService = postService; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public PostResponse createPost(@Valid @RequestBody PostCreateRequest request,
                                   @RequestAttribute(AuthInterceptor.USER_ID) Long loginId) {
        return postService.createPost(request, loginId);
    }
    @GetMapping
    public List<PostResponse> getAllPosts() { return postService.getAllPosts(); }
    @GetMapping("/{postId}")
    public PostResponse getPost(@PathVariable("postId") Long postId) { return postService.getPost(postId); }
    @PatchMapping("/{postId}")
    public PostResponse updatePost(@PathVariable("postId") Long postId, @Valid @RequestBody PostUpdateRequest request) {
        return postService.updatePost(postId, request);
    }
    @DeleteMapping("/{postId}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(@PathVariable("postId") Long postId) { postService.deletePost(postId); }
}
