package com.apptivedev.sns.domain.post;

import static org.assertj.core.api.Assertions.assertThat;

import com.apptivedev.sns.BackendStudy25SnsApplication;
import com.apptivedev.sns.domain.post.dto.PostCreateRequest;
import com.apptivedev.sns.domain.post.dto.PostUpdateRequest;
import com.apptivedev.sns.domain.post.exception.PostNotFoundException;
import com.apptivedev.sns.domain.post.service.PostService;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

class PostPersistenceTest {
    @TempDir Path temporaryDirectory;

    @Test
    void createdAndUpdatedDataSurviveRestartAndDeletedDataStayDeleted() {
        String url = "jdbc:h2:file:" + temporaryDirectory.resolve("sns-db");
        long keptId;
        long deletedId;
        try (var context = startApplication(url)) {
            PostService service = context.getBean(PostService.class);
            keptId = service.create(new PostCreateRequest("재시작 전 제목", "기존 내용")).id();
            PostUpdateRequest update = new PostUpdateRequest();
            update.setTitle("수정 후 제목");
            service.update(keptId, update);
            deletedId = service.create(new PostCreateRequest("삭제할 글", "삭제할 내용")).id();
            service.delete(deletedId);
        }
        try (var context = startApplication(url)) {
            PostService service = context.getBean(PostService.class);
            var saved = service.findById(keptId);
            assertThat(saved.title()).isEqualTo("수정 후 제목");
            assertThat(saved.content()).isEqualTo("기존 내용");
            assertThat(service.findAll()).hasSize(1);
            org.assertj.core.api.Assertions.assertThatThrownBy(() -> service.findById(deletedId))
                    .isInstanceOf(PostNotFoundException.class);
        }
    }

    private ConfigurableApplicationContext startApplication(String url) {
        // 명령행 속성으로 테스트용 메모리 DB 설정을 덮어써 실제 파일 DB를 연다.
        return new SpringApplicationBuilder(BackendStudy25SnsApplication.class)
                .web(WebApplicationType.NONE)
                .run("--spring.datasource.url=" + url,
                        "--spring.jpa.hibernate.ddl-auto=update",
                        "--spring.main.banner-mode=off");
    }
}
