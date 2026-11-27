package com.apptivedev.sns.integration;

import com.apptivedev.sns.user.entity.User;
import com.apptivedev.sns.user.repository.UserRepository;
import com.apptivedev.sns.post.repository.PostRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.http.MediaType;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// 4주차: 실제 Spring MVC, Service, Repository, 테스트 DB를 연결한다.
// Boot 버전에 따른 AutoConfigureMockMvc 패키지 차이를 피하고자 직접 MockMvc를 구성한다.
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserSignupIntegrationTest {
    @Autowired WebApplicationContext context;
    @Autowired UserRepository userRepository;
    @Autowired PostRepository postRepository;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired EntityManager entityManager;
    private MockMvc mockMvc;
    @BeforeEach
    void setUp() { mockMvc = MockMvcBuilders.webAppContextSetup(context).build(); }
    @Test
    void signup_success() throws Exception {
        // Given & When: 실제 HTTP 요청 처리 흐름 실행
        mockMvc.perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"test@example.com","password":"Study1234!","name":"테스터"}
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("테스터"))
                .andExpect(jsonPath("$.password").doesNotExist());
        // Then: 기존 영속 객체가 아닌 DB에서 다시 조회해 Hash 저장을 확인한다.
        entityManager.flush();
        entityManager.clear();
        User saved = userRepository.findByEmail("test@example.com").orElseThrow();
        assertThat(saved.getPassword()).isNotEqualTo("Study1234!");
        assertThat(passwordEncoder.matches("Study1234!", saved.getPassword())).isTrue();
    }
    @Test
    void signup_invalidInput() throws Exception {
        long before = userRepository.count();
        mockMvc.perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"invalid","password":"short","name":" "}
                    """))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("INVALID_INPUT"));
        assertThat(userRepository.count()).isEqualTo(before);
    }
    @Test
    void createPost_missingToken_doesNotSave() throws Exception {
        long before = postRepository.count();
        mockMvc.perform(post("/posts").contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"제목","content":"내용"}
                    """))
                .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
        assertThat(postRepository.count()).isEqualTo(before);
    }
    @Test
    void otherUser_forbidden() throws Exception {
        User a = userRepository.saveAndFlush(User.create("a@example.com", passwordEncoder.encode("Study1234!"), "A"));
        User b = userRepository.saveAndFlush(User.create("b@example.com", passwordEncoder.encode("Study1234!"), "B"));
        String token = context.getBean(com.apptivedev.sns.auth.jwt.JwtProvider.class).createToken(a.getId());
        mockMvc.perform(patch("/users/{userId}", b.getId()).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Changed\"}"))
                .andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value("FORBIDDEN"));
        entityManager.flush(); entityManager.clear();
        assertThat(userRepository.findById(b.getId()).orElseThrow().getName()).isEqualTo("B");
    }
    @Test
    void signupLoginCreatePostAndDeleteUser() throws Exception {
        String signup = mockMvc.perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"writer@example.com","password":"Study1234!","name":"작성자"}
                    """))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8);
        Number userId = com.jayway.jsonpath.JsonPath.read(signup, "$.id");
        String login = mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"writer@example.com","password":"Study1234!"}
                    """))
                .andExpect(status().isOk()).andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andReturn().getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8);
        String token = com.jayway.jsonpath.JsonPath.read(login, "$.accessToken");
        String bearer = "Bearer " + token;
        String created = mockMvc.perform(post("/posts").header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"title":"제목","content":"내용"}
                    """))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.authorId").value(userId.intValue()))
                .andReturn().getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8);
        Number postId = com.jayway.jsonpath.JsonPath.read(created, "$.id");
        entityManager.flush(); entityManager.clear();
        var saved = postRepository.findById(postId.longValue()).orElseThrow();
        assertThat(saved.getTitle()).isEqualTo("제목");
        assertThat(saved.getContent()).isEqualTo("내용");
        assertThat(saved.getUser().getId()).isEqualTo(userId.longValue());
        mockMvc.perform(delete("/users/{userId}", userId.longValue()).header("Authorization", bearer))
                .andExpect(status().isNoContent()).andExpect(content().string(""));
        entityManager.flush(); entityManager.clear();
        assertThat(postRepository.findById(postId.longValue())).isEmpty();
        assertThat(userRepository.findById(userId.longValue())).isEmpty();
        mockMvc.perform(get("/users/{userId}", userId.longValue()).header("Authorization", bearer))
                .andExpect(status().isUnauthorized());
    }
}
