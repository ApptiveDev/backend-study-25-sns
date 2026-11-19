package com.apptivedev.sns.domain.post.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import com.apptivedev.sns.domain.user.entity.User;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class PostTests {

    private final User author = User.create("author@example.com", "test-password-hash", "작성자");

    @ParameterizedTest
    @MethodSource("invalidTitles")
    void invalidTitleCannotBeCreatedOrApplied(String title) {
        assertThatIllegalArgumentException().isThrownBy(() -> new Post(author, title, "내용"));
        Post post = new Post(author, "기존 제목", "기존 내용");
        assertThatIllegalArgumentException().isThrownBy(() -> post.changeTitle(title));
        assertThat(post.getTitle()).isEqualTo("기존 제목");
        assertThatIllegalArgumentException().isThrownBy(() -> post.changeText(title, "새 내용"));
        assertThat(post.getContent()).isEqualTo("기존 내용");
    }

    static Stream<String> invalidTitles() {
        return Stream.of(null, "", " \t\n", "가".repeat(101));
    }

    @ParameterizedTest
    @MethodSource("invalidContents")
    void invalidContentCannotBeCreatedOrApplied(String content) {
        assertThatIllegalArgumentException().isThrownBy(() -> new Post(author, "제목", content));
        Post post = new Post(author, "기존 제목", "기존 내용");
        assertThatIllegalArgumentException().isThrownBy(() -> post.changeContent(content));
        assertThat(post.getContent()).isEqualTo("기존 내용");
        assertThatIllegalArgumentException().isThrownBy(() -> post.changeText("새 제목", content));
        assertThat(post.getTitle()).isEqualTo("기존 제목");
    }

    static Stream<String> invalidContents() {
        return Stream.of(null, "", " \t\n", "나".repeat(501));
    }

    @Test
    void acceptsMaximumLengthsOnCreationAndChange() {
        Post post = new Post(author, "가".repeat(100), "나".repeat(500));
        post.changeText("다".repeat(100), "라".repeat(500));
        assertThat(post.getTitle()).hasSize(100);
        assertThat(post.getContent()).hasSize(500);
    }

    @Test
    void authorIsRequired() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Post(null, "제목", "내용"));
    }
}
