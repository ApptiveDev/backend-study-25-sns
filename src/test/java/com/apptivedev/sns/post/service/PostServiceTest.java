package com.apptivedev.sns.post.service;

import com.apptivedev.sns.global.exception.BusinessException;
import com.apptivedev.sns.post.dto.*;
import com.apptivedev.sns.post.entity.Post;
import com.apptivedev.sns.post.repository.PostRepository;
import com.apptivedev.sns.user.entity.User;
import com.apptivedev.sns.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

// 4주차: 테스트 대상 Service는 실제 객체, Repository만 Mock으로 대체한다.
@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock PostRepository postRepository;
    @Mock UserRepository userRepository;
    @InjectMocks PostService postService;

    @Test
    void createPost_success() {
        // Given: 인증 사용자 조회 및 저장 결과를 설정한다.
        User author = User.create("test@example.com", "test-hash", "테스터");
        given(userRepository.findById(1L)).willReturn(Optional.of(author));
        given(postRepository.save(any(Post.class))).willAnswer(i -> i.getArgument(0));
        // When
        PostResponse response = postService.createPost(new PostCreateRequest("제목", "내용"), 1L);
        // Then: 실제 저장 호출과 저장에 전달한 객체를 검증한다.
        ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(captor.capture());
        assertThat(captor.getValue().getUser()).isSameAs(author);
        assertThat(response.title()).isEqualTo("제목");
        assertThat(response.content()).isEqualTo("내용");
        assertThat(response.authorName()).isEqualTo("테스터");
    }
    @Test
    void createPost_missingUser_doesNotSave() {
        given(userRepository.findById(99L)).willReturn(Optional.empty());
        assertThatThrownBy(() -> postService.createPost(new PostCreateRequest("제목", "내용"), 99L))
                .isInstanceOf(BusinessException.class);
        verify(postRepository, never()).save(any(Post.class));
    }
}
