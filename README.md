# 게시글 CRUD 과제

Spring Boot, Spring Data JPA, H2 파일 DB로 SNS 게시글 생성·목록 조회·단건 조회·부분 수정·삭제를 구현했습니다. 기존 `com.apptivedev.sns.domain` 패키지와 User/Post 설계를 확장했습니다.

## 실행 방법

JDK 17이 필요합니다. 별도 Gradle 설치 없이 프로젝트 루트에서 실행합니다.

```bash
./gradlew bootRun
```

- 기본 주소: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

IntelliJ에서는 프로젝트를 Gradle로 불러오고 Project SDK와 Gradle JVM을 JDK 17로 설정한 뒤 `BackendStudy25SnsApplication`을 실행해도 됩니다.

실행 파일 생성 및 테스트:

```bash
./gradlew test bootJar
java -jar build/libs/backend-study-25-sns-0.0.1-SNAPSHOT.jar
```

## DB 연결 설정

`src/main/resources/application.yaml`의 기본 설정은 다음과 같습니다.

```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:h2:file:./data/sns-db}
    driver-class-name: org.h2.Driver
    username: ${DB_USERNAME:sa}
    password: ${DB_PASSWORD:}
  jpa:
    hibernate:
      ddl-auto: update
    open-in-view: false
```

기본 로컬 DB의 계정은 `sa`, 비밀번호는 빈 값입니다. 별도 DBMS 설치 없이 H2 의존성으로 실행됩니다. 실제 비밀번호가 필요하면 셸 환경변수 `DB_PASSWORD`로 전달합니다. `.env` 파일을 만들기만 해서는 Spring이 자동으로 읽지 않습니다.

H2의 `mem` URL은 서버를 끄면 데이터가 사라지므로, 과제의 재시작 후 유지 조건에 맞게 `file` URL을 사용했습니다. 기본 DB 파일은 실행 위치 아래 `data/sns-db.mv.db`에 생깁니다. 동일한 디렉터리에서 같은 URL로 다시 실행하면 기존 데이터를 조회할 수 있습니다. `ddl-auto: update`로 테이블을 유지합니다.

테스트에서는 별도의 H2 메모리 DB를 사용하고, 재시작 테스트는 JUnit 임시 디렉터리의 파일 DB를 사용합니다. `data/`, H2 파일, `.env`, 로컬 설정 파일은 `.gitignore`에 포함했습니다.

## 구현한 API

| 기능 | Method | URL | 성공 응답 |
| --- | --- | --- | --- |
| 생성 | POST | `/posts` | `201 Created`, 생성된 게시글, `Location` 헤더 |
| 목록 조회 | GET | `/posts` | `200 OK`, 게시글 배열. 없으면 `[]` |
| 단건 조회 | GET | `/posts/{postId}` | `200 OK`, 게시글 |
| 부분 수정 | PATCH | `/posts/{postId}` | `200 OK`, 수정된 게시글 |
| 삭제 | DELETE | `/posts/{postId}` | `204 No Content`, 본문 없음 |

생성 요청 예시:

```json
{"title":"첫 게시글","content":"게시글 내용입니다."}
```

생성·단건 조회·수정 응답 예시:

```json
{"id":1,"title":"첫 게시글","content":"게시글 내용입니다."}
```

ID는 DB가 생성합니다. 생성 요청 DTO에는 `id`가 없고, 요청에 임의의 ID를 추가해도 저장 ID로 사용하지 않습니다. Entity를 직접 응답하지 않고 `PostResponse`의 `id`, `title`, `content`만 반환합니다.

부분 수정 예시:

```json
{"title":"수정한 제목"}
```

이 요청은 제목만 바꾸고 기존 내용을 유지합니다. `{"content":"수정한 내용"}`은 내용만 바꾸며, 두 필드를 함께 보낼 수도 있습니다. 생략한 필드와 명시적인 `null`은 구분합니다.

## 검증과 오류 응답

| 상황 | 응답 |
| --- | --- |
| 생성 시 제목 또는 내용 누락, `null`, 빈 문자열, 공백만 있는 문자열 | `400` |
| 제목 100자 초과 또는 내용 500자 초과 | `400` |
| PATCH에 제목·내용을 하나도 보내지 않음 | `400` |
| PATCH에 전달한 필드가 `null`, 빈 문자열, 공백만 있는 문자열 | `400` |
| PATCH에 전달한 제목·내용의 길이 초과 | `400` |
| JSON 형식 오류, 본문 누락, 숫자가 아닌 게시글 ID | `400` |
| 존재하지 않는 게시글 조회·수정·삭제 | `404` |

길이는 Java `String.length()`를 기준으로 확인합니다. 앞뒤 공백을 자동으로 제거하지는 않지만, 공백만 있는 값은 거절합니다. 수정할 게시글을 먼저 찾으므로, 존재하지 않는 ID에 빈 수정 요청을 보내면 `404`가 우선합니다.

오류 본문은 공통으로 `message`를 포함합니다.

```json
{"message":"게시글을 찾을 수 없습니다. id=1"}
```

## 패키지와 요청 처리 흐름

```text
com.apptivedev.sns
├── BackendStudy25SnsApplication
├── domain
│   ├── post
│   │   ├── controller/PostController
│   │   ├── service/PostService
│   │   ├── repository/PostRepository
│   │   ├── entity/Post
│   │   ├── dto/PostCreateRequest, PostUpdateRequest, PostResponse
│   │   └── exception/InvalidPostRequestException, PostNotFoundException
│   └── user/entity/User
└── global/exception/GlobalExceptionHandler, ErrorResponse
```

`Controller → Service → Repository → JPA/Hibernate → H2` 순서로 요청을 처리합니다. Controller와 Service는 생성자로 의존 객체를 받습니다. Service가 입력 규칙과 트랜잭션을 관리하며, Repository는 `JpaRepository<Post, Long>`으로 DB에 접근합니다.

기존 Post의 `author`, `createdAt`도 유지했습니다. `author`는 선택적인 User 연관관계이며, 이번 과제 API에서는 작성자를 받거나 응답하지 않습니다. 생성 시각은 저장 전에 자동으로 설정합니다. 사용자 API·인증·페이징·정렬은 이번 구현 범위에 포함하지 않았습니다.

## 동작 확인

자동 검증:

```bash
./gradlew test
```

- `PostApiIntegrationTest`: 실제 Spring MVC와 JPA를 연결하여 CRUD, 부분 수정, 오류 메시지, 길이 경계, 실패 시 DB 상태 유지를 검증합니다.
- `PostPersistenceTest`: 파일 DB에 생성·수정·삭제한 뒤 Spring 애플리케이션 컨텍스트를 종료하고 다시 시작하여 상태가 유지되는지 검증합니다.
- 테스트 결과: `build/reports/tests/test/index.html`

Swagger로 직접 확인하는 순서와 Postman 요청 모음은 [검증 안내](docs/verification.md)에 있습니다. Postman Collection은 `postman/posts.postman_collection.json`입니다.

## 제출 자료

- 구현 코드: `src/main/`
- 실행·DB 설정·API 설명: 이 README
- 깨달은 내용과 설계 근거: [학습 정리](docs/learning-notes.md)
- 수동 검증 절차: [검증 안내](docs/verification.md)

권장 커밋 구분:

1. `feat: H2 파일 DB 설정과 엔티티 매핑 추가` — 설정, `.gitignore`, Post/User 엔티티
2. `feat: 게시글 CRUD와 부분 수정 및 오류 처리 구현` — Repository, DTO, Service, Controller, 공통 예외 처리, Swagger 의존성과 문서 어노테이션
3. `test: 게시글 API와 재시작 후 데이터 유지 검증` — 테스트 코드·설정
4. `docs: 실행 방법과 과제 학습 내용 및 API 검증 자료 작성` — README, docs, Postman

Swagger 의존성과 문서 어노테이션은 같은 커밋에 포함해야 해당 커밋도 빌드됩니다. 실제 비밀번호와 DB 파일은 커밋하지 않습니다.

참고한 Swagger 연동 방식은 [springdoc 공식 문서](https://springdoc.org/v2/getting-started.html)를 따릅니다. Spring Boot 3.5.16과의 버전 기준은 [springdoc 2.10.0 릴리스](https://github.com/springdoc/springdoc-openapi/releases/tag/v2.10.0)에서 확인했습니다.
