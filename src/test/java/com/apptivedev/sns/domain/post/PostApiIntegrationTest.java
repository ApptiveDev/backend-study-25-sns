package com.apptivedev.sns.domain.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.apptivedev.sns.domain.post.entity.Post;
import com.apptivedev.sns.domain.post.repository.PostRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class PostApiIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired PostRepository postRepository;

    @BeforeEach
    void clearTestDatabase() {
        postRepository.deleteAll();
    }

    @Test
    void emptyListIsAnArray() throws Exception {
        mvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void createReturnsServerIdAndOnlyResponseFields() throws Exception {
        String response = mvc.perform(post("/posts").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":999999,\"title\":\"첫 글\",\"content\":\"본문\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("첫 글"))
                .andExpect(jsonPath("$.content").value("본문"))
                .andReturn().getResponse().getContentAsString();
        var body = objectMapper.readTree(response);
        assertThat(body.size()).isEqualTo(3);
        assertThat(body.get("id").asLong()).isPositive().isNotEqualTo(999999L);
    }

    @Test
    void createdPostAppearsInListAndDetail() throws Exception {
        long id = createPost();
        mvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(id));
        mvc.perform(get("/posts/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("기존 제목"))
                .andExpect(jsonPath("$.content").value("기존 내용"));
    }

    @Test
    void titleOnlyPatchKeepsContentAndCommitsToDatabase() throws Exception {
        long id = createPost();
        mvc.perform(patch("/posts/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"새 제목\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("새 제목"))
                .andExpect(jsonPath("$.content").value("기존 내용"));
        Post saved = postRepository.findById(id).orElseThrow();
        assertThat(saved.getTitle()).isEqualTo("새 제목");
        assertThat(saved.getContent()).isEqualTo("기존 내용");
    }

    @Test
    void contentOnlyPatchKeepsTitleAndCommitsToDatabase() throws Exception {
        long id = createPost();
        mvc.perform(patch("/posts/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"새 내용\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("기존 제목"))
                .andExpect(jsonPath("$.content").value("새 내용"));
        Post saved = postRepository.findById(id).orElseThrow();
        assertThat(saved.getTitle()).isEqualTo("기존 제목");
        assertThat(saved.getContent()).isEqualTo("새 내용");
    }

    @Test
    void maximumLengthsAreAcceptedForCreateAndPatch() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of("title", "가".repeat(100), "content", "나".repeat(500)));
        String response = mvc.perform(post("/posts").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(response).get("id").asLong();
        mvc.perform(patch("/posts/{id}", id).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @MethodSource("invalidCreateBodies")
    void invalidCreationReturns400WithMessage(String body) throws Exception {
        mvc.perform(post("/posts").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty());
        assertThat(postRepository.count()).isZero();
    }

    static Stream<String> invalidCreateBodies() {
        return Stream.of(
                "{}", "{\"content\":\"내용\"}", "{\"title\":\"제목\"}",
                "{\"title\":null,\"content\":\"내용\"}", "{\"title\":\"제목\",\"content\":null}",
                "{\"title\":\"\",\"content\":\"내용\"}", "{\"title\":\"제목\",\"content\":\"\"}",
                "{\"title\":\"   \",\"content\":\"내용\"}", "{\"title\":\"제목\",\"content\":\"  \\t\\n\"}",
                "{\"title\":\"" + "가".repeat(101) + "\",\"content\":\"내용\"}",
                "{\"title\":\"제목\",\"content\":\"" + "나".repeat(501) + "\"}");
    }

    @ParameterizedTest
    @MethodSource("invalidPatchBodies")
    void invalidPatchReturns400AndDoesNotChangePost(String body) throws Exception {
        long id = createPost();
        mvc.perform(patch("/posts/{id}", id).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty());
        Post saved = postRepository.findById(id).orElseThrow();
        assertThat(saved.getTitle()).isEqualTo("기존 제목");
        assertThat(saved.getContent()).isEqualTo("기존 내용");
    }

    static Stream<String> invalidPatchBodies() {
        return Stream.of(
                "{}", "{\"unknown\":\"value\"}", "{\"title\":null}", "{\"content\":null}",
                "{\"title\":\"\"}", "{\"content\":\"\"}",
                "{\"title\":\"  \\t\"}", "{\"content\":\"  \\n\"}",
                "{\"title\":\"새 제목\",\"content\":null}",
                "{\"title\":null,\"content\":\"새 내용\"}",
                "{\"title\":\"" + "가".repeat(101) + "\"}",
                "{\"content\":\"" + "나".repeat(501) + "\"}");
    }

    @Test
    void deleteHasNoBodyAndSubsequentOperationsReturn404() throws Exception {
        long id = createPost();
        mvc.perform(delete("/posts/{id}", id))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
        mvc.perform(get("/posts/{id}", id)).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").isNotEmpty());
        mvc.perform(patch("/posts/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"수정\"}"))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.message").isNotEmpty());
        mvc.perform(delete("/posts/{id}", id)).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").isNotEmpty());
        assertThat(postRepository.existsById(id)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"{", "null", "[]", ""})
    void malformedOrMissingBodyReturns400(String body) throws Exception {
        mvc.perform(post("/posts").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").isNotEmpty());
        long id = createPost();
        mvc.perform(patch("/posts/{id}", id).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void invalidIdReturns400WithConsistentMessage() throws Exception {
        mvc.perform(get("/posts/abc")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    private long createPost() throws Exception {
        String response = mvc.perform(post("/posts").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"기존 제목\",\"content\":\"기존 내용\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("id").asLong();
    }
}
