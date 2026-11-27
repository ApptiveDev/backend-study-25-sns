package com.apptivedev.sns;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.apptivedev.sns.domain.post.repository.PostRepository;
import com.apptivedev.sns.domain.user.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.stream.Stream;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SpringBootTest
@AutoConfigureMockMvc
class AuthUserApiTests {

    private static final String PASSWORD = "study1234!";

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper json;

    @Autowired
    UserRepository users;

    @Autowired
    PostRepository posts;

    @Autowired
    PasswordEncoder encoder;

    @Value("${jwt.secret}")
    String secret;

    @BeforeEach
    void clearTestDatabase() {
        posts.deleteAll();
        users.deleteAll();
    }

    @Test
    void signupHashesPasswordAndRejectsDuplicateEmail() throws Exception {
        JsonNode user = signup("a@example.com", "회원A");

        assertThat(user.fieldNames())
                .toIterable()
                .containsExactlyInAnyOrder("id", "email", "name");

        var saved = users.findById(user.get("id").asLong())
                .orElseThrow();

        assertThat(saved.getPassword()).isNotEqualTo(PASSWORD);
        assertThat(encoder.matches(PASSWORD, saved.getPassword())).isTrue();

        error(
                send(post("/auth/signup"), Map.of(
                        "email", "a@example.com",
                        "password", PASSWORD,
                        "name", "중복"
                )),
                409
        );

        assertThat(users.count()).isEqualTo(1);
    }

    @Test
    void loginReturnsTokenAndHidesWhetherEmailExists() throws Exception {
        long id = signup("a@example.com", "회원A")
                .get("id")
                .asLong();

        String token = login("a@example.com");

        mvc.perform(get("/users/{id}", id)
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("email").value("a@example.com"))
                .andExpect(jsonPath("password").doesNotExist());

        JsonNode wrongEmail = body(error(
                send(post("/auth/login"), Map.of(
                        "email", "missing@example.com",
                        "password", PASSWORD
                )),
                401
        ));

        JsonNode wrongPassword = body(error(
                send(post("/auth/login"), Map.of(
                        "email", "a@example.com",
                        "password", "wrong"
                )),
                401
        ));

        assertThat(wrongEmail).isEqualTo(wrongPassword);
    }

    @ParameterizedTest
    @MethodSource("invalidSignupRequests")
    void rejectsInvalidSignup(String request) throws Exception {
        error(
                mvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)),
                400
        );

        assertThat(users.count()).isZero();
    }

    static Stream<String> invalidSignupRequests() {
        return Stream.of(
                "{}",
                """
                {"email":null,"password":null,"name":null}
                """,
                """
                {"email":" ","password":"study1234!","name":"A"}
                """,
                """
                {"email":"invalid","password":"study1234!","name":"A"}
                """,
                """
                {"email":"a@example.com","password":"abc12!","name":"A"}
                """,
                """
                {"email":"a@example.com","password":"abcdefghijklmnop1234!","name":"A"}
                """,
                """
                {"email":"a@example.com","password":"study1234","name":"A"}
                """,
                """
                {"email":"a@example.com","password":"study!!!!","name":"A"}
                """,
                """
                {"email":"a@example.com","password":"1234567!","name":"A"}
                """,
                """
                {"email":"a@example.com","password":"study12 !","name":"A"}
                """,
                """
                {"email":"a@example.com","password":"study12한!","name":"A"}
                """,
                """
                {"email":"a@example.com","password":"study1234!","name":" "}
                """,
                """
                {"email":"a@example.com","password":"study1234!","name":"123456789012345678901"}
                """
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{}",
            "{\"email\":null,\"password\":null}",
            "{\"email\":\" \",\"password\":\" \"}"
    })
    void rejectsMissingLoginFields(String request) throws Exception {
        error(
                mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)),
                400
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{}",
            "{\"name\":null}",
            "{\"name\":\"\"}",
            "{\"name\":\" \"}",
            "{\"name\":\"123456789012345678901\"}"
    })
    void rejectsInvalidNameUpdates(String request) throws Exception {
        long id = signup("a@example.com", "회원A")
                .get("id")
                .asLong();

        String token = login("a@example.com");

        error(
                mvc.perform(patch("/users/{id}", id)
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)),
                400
        );

        assertThat(users.findById(id).orElseThrow().getName())
                .isEqualTo("회원A");
    }

    @Test
    void onlyOwnerCanReadUpdateOrDeleteEvenWhenTargetDoesNotExist()
            throws Exception {
        JsonNode a = signup("a@example.com", "회원A");
        JsonNode b = signup("b@example.com", "회원B");

        String auth = bearer(login("a@example.com"));
        long aid = a.get("id").asLong();
        long bid = b.get("id").asLong();

        for (long target : new long[]{bid, Long.MAX_VALUE}) {
            error(
                    mvc.perform(get("/users/{id}", target)
                            .header("Authorization", auth)),
                    403
            );

            error(
                    send(
                            patch("/users/{id}", target)
                                    .header("Authorization", auth),
                            Map.of("name", "변경시도")
                    ),
                    403
            );

            error(
                    mvc.perform(delete("/users/{id}", target)
                            .header("Authorization", auth)),
                    403
            );
        }

        assertThat(users.findById(bid).orElseThrow().getName())
                .isEqualTo("회원B");

        send(
                patch("/users/{id}", aid)
                        .header("Authorization", auth),
                Map.of("name", "새이름")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("새이름"))
                .andExpect(jsonPath("email").value("a@example.com"))
                .andExpect(jsonPath("password").doesNotExist());

        assertThat(users.findById(aid).orElseThrow().getName())
                .isEqualTo("새이름");
    }

    @Test
    void rejectsMissingMalformedForgedExpiredAndIncompleteTokens()
            throws Exception {
        long id = signup("a@example.com", "회원A")
                .get("id")
                .asLong();

        error(mvc.perform(get("/users/{id}", id)), 401);

        SecretKey key = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(secret)
        );

        Date future = Date.from(Instant.now().plusSeconds(3600));
        String valid = login("a@example.com");

        String[] headers = {
                "Basic abc",
                "Bearer",
                "Bearer  " + valid,
                "Bearer invalid-token",

                // 서명 부분을 변조한 토큰
                bearer(
                        valid.substring(0, valid.lastIndexOf('.') + 1)
                                + "invalid"
                ),

                // 다른 키로 서명한 토큰
                bearer(Jwts.builder()
                        .subject(Long.toString(id))
                        .expiration(future)
                        .signWith(Jwts.SIG.HS256.key().build())
                        .compact()),

                // 이미 만료된 토큰
                bearer(Jwts.builder()
                        .subject(Long.toString(id))
                        .expiration(Date.from(
                                Instant.now().minusSeconds(60)
                        ))
                        .signWith(key)
                        .compact()),

                // 만료 시간이 없는 토큰
                bearer(Jwts.builder()
                        .subject(Long.toString(id))
                        .signWith(key)
                        .compact()),

                // 사용자 ID가 없는 토큰
                bearer(Jwts.builder()
                        .expiration(future)
                        .signWith(key)
                        .compact()),

                // 사용자 ID가 숫자가 아닌 토큰
                bearer(Jwts.builder()
                        .subject("abc")
                        .expiration(future)
                        .signWith(key)
                        .compact()),

                bearer(Jwts.builder()
                        .subject("0")
                        .expiration(future)
                        .signWith(key)
                        .compact()),

                bearer(Jwts.builder()
                        .subject("-1")
                        .expiration(future)
                        .signWith(key)
                        .compact())
        };

        for (String header : headers) {
            error(
                    mvc.perform(get("/users/{id}", id)
                            .header("Authorization", header)),
                    401
            );
        }
    }

    @Test
    void postUsesAuthenticatedAuthorAndDeletingUserRemovesOnlyTheirPosts()
            throws Exception {
        long aid = signup("a@example.com", "회원A")
                .get("id")
                .asLong();

        long bid = signup("b@example.com", "회원B")
                .get("id")
                .asLong();

        String aToken = bearer(login("a@example.com"));
        String bToken = bearer(login("b@example.com"));

        Map<String, Object> request = Map.of(
                "title", "제목",
                "content", "내용",
                "authorId", bid
        );

        // 토큰이 없으면 저장되지 않아야 한다.
        error(send(post("/posts"), request), 401);
        assertThat(posts.count()).isZero();

        // Body의 authorId가 아니라 토큰의 사용자로 작성자를 결정한다.
        JsonNode aPost = body(
                send(
                        post("/posts")
                                .header("Authorization", aToken),
                        request
                )
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("authorId").value(aid))
                        .andExpect(jsonPath("authorName").value("회원A"))
                        .andExpect(jsonPath("password").doesNotExist())
                        .andExpect(jsonPath("author").doesNotExist())
        );

        JsonNode bPost = body(
                send(
                        post("/posts")
                                .header("Authorization", bToken),
                        request
                )
                        .andExpect(status().isCreated())
        );

        assertThat(
                posts.findById(aPost.get("id").asLong())
                        .orElseThrow()
                        .getAuthor()
                        .getId()
        ).isEqualTo(aid);

        mvc.perform(delete("/users/{id}", aid)
                        .header("Authorization", aToken))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        assertThat(users.existsById(aid)).isFalse();
        assertThat(posts.existsById(aPost.get("id").asLong())).isFalse();

        // 다른 회원과 그 회원의 게시글은 유지되어야 한다.
        assertThat(users.existsById(bid)).isTrue();
        assertThat(posts.existsById(bPost.get("id").asLong())).isTrue();

        error(
                send(post("/auth/login"), Map.of(
                        "email", "a@example.com",
                        "password", PASSWORD
                )),
                401
        );

        error(
                mvc.perform(get("/users/{id}", aid)
                        .header("Authorization", aToken)),
                401
        );

        error(
                send(
                        post("/posts")
                                .header("Authorization", aToken),
                        request
                ),
                401
        );

        mvc.perform(get("/posts").servletPath("/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].authorId").value(bid));
    }

    @Test
    void postCrudAndValidationStillWork() throws Exception {
        signup("a@example.com", "회원A");
        String auth = bearer(login("a@example.com"));

        error(
                send(
                        post("/posts")
                                .header("Authorization", auth),
                        Map.of("title", " ", "content", "내용")
                ),
                400
        );

        long id = body(
                send(
                        post("/posts")
                                .header("Authorization", auth),
                        Map.of("title", "제목", "content", "내용")
                )
                        .andExpect(status().isCreated())
        ).get("id").asLong();

        send(
                patch("/posts/{id}", id),
                Map.of("title", "수정")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("content").value("내용"));

        // 일부 항목이 잘못되면 전체 수정이 반영되지 않아야 한다.
        error(
                send(
                        patch("/posts/{id}", id),
                        Map.of(
                                "title", "변경되면안됨",
                                "content", " "
                        )
                ),
                400
        );

        mvc.perform(get("/posts/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("title").value("수정"));

        mvc.perform(delete("/posts/{id}", id))
                .andExpect(status().isNoContent());

        error(mvc.perform(get("/posts/{id}", id)), 404);

        error(
                mvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{")),
                400
        );
    }

    @ParameterizedTest
    @MethodSource("invalidPostRequests")
    void validatesPostRequestsWithoutChangingStoredValues(String request) throws Exception {
        signup("a@example.com", "회원A");
        String auth = bearer(login("a@example.com"));
        error(mvc.perform(post("/posts").header("Authorization", auth)
                .contentType(MediaType.APPLICATION_JSON).content(request)), 400);
        assertThat(posts.count()).isZero();

        long id = body(send(post("/posts").header("Authorization", auth),
                Map.of("title", "기존 제목", "content", "기존 내용"))
                .andExpect(status().isCreated())).get("id").asLong();
        error(mvc.perform(patch("/posts/{id}", id)
                .contentType(MediaType.APPLICATION_JSON).content(request)), 400);
        mvc.perform(get("/posts/{id}", id)).andExpect(status().isOk())
                .andExpect(jsonPath("title").value("기존 제목"))
                .andExpect(jsonPath("content").value("기존 내용"));
    }

    static Stream<String> invalidPostRequests() {
        return Stream.of(
                "{}", "{\"title\":null}", "{\"content\":null}",
                "{\"title\":\"\"}", "{\"content\":\" \"}",
                "{\"title\":\"새 제목\",\"content\":null}",
                "{\"title\":\"새 제목\",\"content\":\" \"}",
                "{\"title\":\"" + "가".repeat(101) + "\",\"content\":\"내용\"}",
                "{\"title\":\"제목\",\"content\":\"" + "나".repeat(501) + "\"}"
        );
    }

    @Test
    void acceptsBoundaryLengthsAndContentOnlyPatch() throws Exception {
        signup("a@example.com", "회원A");
        String auth = bearer(login("a@example.com"));
        long id = body(send(post("/posts").header("Authorization", auth),
                Map.of("title", "가".repeat(100), "content", "나".repeat(500)))
                .andExpect(status().isCreated())).get("id").asLong();
        send(patch("/posts/{id}", id), Map.of("content", "수정 내용"))
                .andExpect(status().isOk()).andExpect(jsonPath("title").value("가".repeat(100)))
                .andExpect(jsonPath("content").value("수정 내용"));
    }

    @Test
    void pagesPostsNewestFirstWithAuthorAndWithoutAdditionalAuthorQueries() throws Exception {
        signup("a@example.com", "회원A");
        signup("b@example.com", "회원B");
        String aToken = bearer(login("a@example.com"));
        String bToken = bearer(login("b@example.com"));
        for (int i = 1; i <= 5; i++) {
            send(post("/posts").header("Authorization", i % 2 == 0 ? aToken : bToken),
                    Map.of("title", "제목" + i, "content", "내용"))
                    .andExpect(status().isCreated());
        }
        var statistics = entityManagerFactory.unwrap(org.hibernate.SessionFactory.class).getStatistics();
        boolean wasEnabled = statistics.isStatisticsEnabled();
        statistics.setStatisticsEnabled(true);
        statistics.clear();
        try {
            mvc.perform(get("/posts/page").param("page", "0").param("size", "2"))
                    .andExpect(status().isOk()).andExpect(jsonPath("content.length()").value(2))
                    .andExpect(jsonPath("content[0].title").value("제목5"))
                    .andExpect(jsonPath("content[0].authorName").value("회원B"))
                    .andExpect(jsonPath("content[1].title").value("제목4"))
                    .andExpect(jsonPath("content[1].authorName").value("회원A"))
                    .andExpect(jsonPath("page").value(0)).andExpect(jsonPath("size").value(2))
                    .andExpect(jsonPath("totalElements").value(5)).andExpect(jsonPath("totalPages").value(3))
                    .andExpect(jsonPath("hasNext").value(true))
                    .andExpect(jsonPath("content[0].password").doesNotExist());
            // 페이지 조회 + 전체 개수 조회. 작성자별 추가 쿼리는 없어야 한다.
            assertThat(statistics.getPrepareStatementCount()).isEqualTo(2);
            assertThat(statistics.getEntityFetchCount()).isZero();
        } finally {
            statistics.setStatisticsEnabled(wasEnabled);
        }
        mvc.perform(get("/posts/page").param("page", "2").param("size", "2"))
                .andExpect(status().isOk()).andExpect(jsonPath("content.length()").value(1))
                .andExpect(jsonPath("content[0].title").value("제목1"))
                .andExpect(jsonPath("hasNext").value(false));
        mvc.perform(get("/posts/page").param("page", "3").param("size", "2"))
                .andExpect(status().isOk()).andExpect(jsonPath("content").isEmpty());
        mvc.perform(get("/posts").servletPath("/posts"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(5));
    }

    @Autowired
    jakarta.persistence.EntityManagerFactory entityManagerFactory;

    @ParameterizedTest
    @ValueSource(strings = {"page=-1", "size=0", "size=101", "page=abc", "size=abc", "page=2147483648"})
    void rejectsInvalidPaginationParameters(String parameter) throws Exception {
        String[] parts = parameter.split("=");
        error(mvc.perform(get("/posts/page").param(parts[0], parts[1])), 400);
    }

    @Test
    void defaultPageOfEmptyDatabaseHasNoContent() throws Exception {
        mvc.perform(get("/posts/page"))
                .andExpect(status().isOk()).andExpect(jsonPath("content").isEmpty())
                .andExpect(jsonPath("page").value(0)).andExpect(jsonPath("size").value(20))
                .andExpect(jsonPath("totalElements").value(0)).andExpect(jsonPath("totalPages").value(0))
                .andExpect(jsonPath("hasNext").value(false));
    }

    private JsonNode signup(String email, String name) throws Exception {
        return body(
                send(post("/auth/signup"), Map.of(
                        "email", email,
                        "password", PASSWORD,
                        "name", name
                ))
                        .andExpect(status().isCreated())
        );
    }

    private String login(String email) throws Exception {
        return body(
                send(post("/auth/login"), Map.of(
                        "email", email,
                        "password", PASSWORD
                ))
                        .andExpect(status().isOk())
        ).get("accessToken").asText();
    }

    private ResultActions send(
            MockHttpServletRequestBuilder request,
            Object payload
    ) throws Exception {
        return mvc.perform(
                request.contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsBytes(payload))
        );
    }

    private ResultActions error(
            ResultActions result,
            int statusCode
    ) throws Exception {
        return result
                .andExpect(status().is(statusCode))
                .andExpect(jsonPath("code").isNotEmpty())
                .andExpect(jsonPath("message").isNotEmpty())
                .andExpect(jsonPath("trace").doesNotExist());
    }

    private JsonNode body(ResultActions result) throws Exception {
        return json.readTree(
                result.andReturn()
                        .getResponse()
                        .getContentAsByteArray()
        );
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
