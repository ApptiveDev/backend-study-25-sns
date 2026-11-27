package com.apptivedev.sns.post.entity;

import com.apptivedev.sns.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.assertj.core.api.Assertions.*;

// 4주차: Spring과 DB 없이 Entity의 정상값, 예외값, 경계값을 검증한다.
class PostTest {
    private User validUser() { return User.create("test@example.com", "test-hash", "테스터"); }

    @Test @DisplayName("정상적인 게시글을 생성한다")
    void createPost_success() {
        // Given
        User user = validUser();
        // When
        Post post = Post.createPost("게시글 제목", "게시글 내용", user);
        // Then
        assertThat(post.getTitle()).isEqualTo("게시글 제목");
        assertThat(post.getContent()).isEqualTo("게시글 내용");
        assertThat(post.getUser()).isSameAs(user);
    }
    @ParameterizedTest @NullAndEmptySource @ValueSource(strings = {" ", "   "})
    @DisplayName("비어 있는 제목은 거부한다")
    void createPost_blankTitle(String title) {
        assertThatThrownBy(() -> Post.createPost(title, "내용", validUser()))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("제목은 필수입니다.");
    }
    @ParameterizedTest @NullAndEmptySource @ValueSource(strings = {" ", "   "})
    @DisplayName("비어 있는 내용은 거부한다")
    void createPost_blankContent(String content) {
        assertThatThrownBy(() -> Post.createPost("제목", content, validUser()))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("내용은 필수입니다.");
    }
    @Test @DisplayName("100자 제목과 500자 내용은 허용한다")
    void boundary_success() {
        Post post = Post.createPost("가".repeat(100), "나".repeat(500), validUser());
        assertThat(post.getTitle()).hasSize(100);
        assertThat(post.getContent()).hasSize(500);
    }
    @Test @DisplayName("101자 제목은 거부한다")
    void longTitle() {
        assertThatThrownBy(() -> Post.createPost("가".repeat(101), "내용", validUser()))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("제목은 100자 이하여야 합니다.");
    }
    @Test @DisplayName("501자 내용은 거부한다")
    void longContent() {
        assertThatThrownBy(() -> Post.createPost("제목", "가".repeat(501), validUser()))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("내용은 500자 이하여야 합니다.");
    }
    @Test @DisplayName("작성자가 없으면 생성할 수 없다")
    void missingAuthor() {
        assertThatThrownBy(() -> Post.createPost("제목", "내용", null))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("작성자는 필수입니다.");
    }
    @Test @DisplayName("제목만 수정하면 기존 내용과 작성자는 유지된다")
    void updatePost_success() {
        User user = validUser();
        Post post = Post.createPost("기존 제목", "기존 내용", user);
        post.update("새 제목", null);
        assertThat(post.getTitle()).isEqualTo("새 제목");
        assertThat(post.getContent()).isEqualTo("기존 내용");
        assertThat(post.getUser()).isSameAs(user);
    }
    @Test @DisplayName("내용 검증 실패 시 제목과 내용 모두 변경되지 않는다")
    void update_invalidContent_keepsState() {
        Post post = Post.createPost("기존 제목", "기존 내용", validUser());
        assertThatThrownBy(() -> post.update("새 제목", " ")).isInstanceOf(IllegalArgumentException.class);
        assertThat(post.getTitle()).isEqualTo("기존 제목");
        assertThat(post.getContent()).isEqualTo("기존 내용");
    }
    @Test @DisplayName("수정 시에도 제목 길이 제한을 지킨다")
    void update_invalidTitle_keepsState() {
        Post post = Post.createPost("기존 제목", "기존 내용", validUser());
        assertThatThrownBy(() -> post.update("가".repeat(101), "새 내용"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(post.getTitle()).isEqualTo("기존 제목");
        assertThat(post.getContent()).isEqualTo("기존 내용");
    }
}
