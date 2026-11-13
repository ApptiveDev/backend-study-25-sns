package com.apptivedev.sns.domain.post.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.apptivedev.sns.domain.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class PostPersistenceTests {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void oneUserCanWriteMultiplePosts() {
        User user = new User("dowon@example.com", "도원");
        entityManager.persist(user);

        Post firstPost = new Post(user, "첫 번째 제목", "첫 번째 게시글");
        Post secondPost = new Post(user, "두 번째 제목", "두 번째 게시글");
        entityManager.persist(firstPost);
        entityManager.persist(secondPost);
        entityManager.flush();
        entityManager.clear();

        User savedUser = entityManager.find(User.class, user.getId());
        Post savedFirstPost = entityManager.find(Post.class, firstPost.getId());
        Post savedSecondPost = entityManager.find(Post.class, secondPost.getId());

        assertThat(savedUser.getEmail()).isEqualTo("dowon@example.com");
        assertThat(savedUser.getNickname()).isEqualTo("도원");
        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedFirstPost.getContent()).isEqualTo("첫 번째 게시글");
        assertThat(savedSecondPost.getContent()).isEqualTo("두 번째 게시글");
        assertThat(savedFirstPost.getAuthor().getId()).isEqualTo(savedUser.getId());
        assertThat(savedSecondPost.getAuthor().getId()).isEqualTo(savedUser.getId());
        assertThat(savedFirstPost.getCreatedAt()).isNotNull();
        assertThat(savedSecondPost.getCreatedAt()).isNotNull();
    }
}
