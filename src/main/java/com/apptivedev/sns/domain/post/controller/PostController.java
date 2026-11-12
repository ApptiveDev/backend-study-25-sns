package com.apptivedev.sns.domain.post.controller;

import com.apptivedev.sns.domain.post.dto.PostCreateRequest;
import com.apptivedev.sns.domain.post.dto.PostResponse;
import com.apptivedev.sns.domain.post.dto.PostUpdateRequest;
import com.apptivedev.sns.domain.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.net.URI;
import java.util.List;
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
    @Operation(summary = "게시글 생성")
    @ApiResponse(responseCode = "201", description = "생성된 게시글")
    public ResponseEntity<PostResponse> create(@RequestBody PostCreateRequest request) {
        PostResponse response = postService.create(request);
        return ResponseEntity.created(URI.create("/posts/" + response.id())).body(response);
    }

    @GetMapping
    @Operation(summary = "게시글 목록 조회")
    public List<PostResponse> findAll() {
        return postService.findAll();
    }

    @GetMapping("/{postId}")
    @Operation(summary = "게시글 단건 조회")
    public PostResponse findById(@PathVariable Long postId) {
        return postService.findById(postId);
    }

    @PatchMapping("/{postId}")
    @Operation(summary = "게시글 부분 수정", description = "title, content 중 하나 이상을 전달합니다. 생략한 필드는 유지하며 null, 빈 문자열, 공백만 있는 값은 허용하지 않습니다.")
    public PostResponse update(@PathVariable Long postId, @RequestBody PostUpdateRequest request) {
        return postService.update(postId, request);
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "게시글 삭제")
    @ApiResponse(responseCode = "204", description = "삭제 완료, 응답 본문 없음")
    public ResponseEntity<Void> delete(@PathVariable Long postId) {
        postService.delete(postId);
        return ResponseEntity.noContent().build();
    }
}
