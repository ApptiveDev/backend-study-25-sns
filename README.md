# backend-study-25-sns

백엔드 스터디에서 만드는 SNS API입니다.
게시글 CRUD에 회원가입, 로그인, JWT 인증과 본인 정보 조회·수정·탈퇴 기능을 추가했습니다.

## 실행 방법

JDK 17을 사용합니다. 프로젝트 폴더에서 아래 명령으로 JWT 서명 키를 생성합니다.

```sh
openssl rand -base64 32
```

생성한 값을 실행 환경변수 `JWT_SECRET`에 넣습니다.
IntelliJ에서는 **실행 → 구성 편집… → 환경 변수**에 추가하고 애플리케이션을 실행합니다.
환경 변수 항목이 보이지 않으면 **옵션 수정**에서 추가합니다.

| 환경변수 | 설명 | 기본값 |
| --- | --- | --- |
| `JWT_SECRET` | 32바이트를 Base64로 인코딩한 JWT 서명 키 | 필수, 기본값 없음 |
| `JWT_ACCESS_TOKEN_VALIDITY_SECONDS` | Access Token 유효 시간(초), 양수 | `3600` (1시간) |

터미널에서 실행할 때도 `JWT_SECRET`을 설정한 뒤 실행합니다.

```sh
./gradlew bootRun
```

macOS에서 다른 JDK가 기본으로 선택되어 있다면 다음처럼 JDK 17을 지정합니다.

```sh
JAVA_HOME=$(/usr/libexec/java_home -v 17) ./gradlew bootRun
```

기본 주소는 `http://localhost:8080`입니다. 실제 서명 키와 발급받은 토큰은 저장소에 올리지 않습니다.

## DB 설정

H2 파일 DB를 사용하며 설정은 `src/main/resources/application.yaml`에 있습니다.

| 항목 | 값 |
| --- | --- |
| JDBC URL | `jdbc:h2:file:./data/sns` |
| 사용자 이름 | `sa` |
| 비밀번호 | 빈 값 |
| Hibernate DDL | `update` |

프로젝트 폴더를 실행 작업 디렉터리로 사용합니다. 데이터는 `data` 폴더에 저장되어 서버를 재시작해도 유지됩니다.
`data` 폴더는 Git에서 제외했습니다.

서버 실행 후 `http://localhost:8080/h2-console`에서 위 연결 정보로 DB를 확인할 수 있습니다.

## 패키지 구조

```text
com.apptivedev.sns
├── domain
│   ├── auth    # 회원가입·로그인, JWT, 인증 Interceptor
│   ├── user    # 회원 Entity, DTO, Repository, Service, Controller
│   └── post    # 게시글 Entity, DTO, Repository, Service, Controller
├── global
│   ├── config
│   └── exception
└── BackendStudy25SnsApplication.java
```

관련 기능을 도메인별로 모으고, Controller는 HTTP 요청·응답을, Service는 처리 규칙을 담당합니다.
API 응답에는 Entity 대신 DTO를 사용합니다.

## API 목록

| Method | URL | 기능 | 인증 | 성공 응답 |
| --- | --- | --- | --- | --- |
| POST | `/auth/signup` | 회원가입 | 불필요 | `201` |
| POST | `/auth/login` | 로그인 | 불필요 | `200`, `accessToken` |
| GET | `/users/{userId}` | 본인 정보 조회 | 필요, 본인만 | `200` |
| PATCH | `/users/{userId}` | 본인 이름 수정 | 필요, 본인만 | `200` |
| DELETE | `/users/{userId}` | 회원 탈퇴 | 필요, 본인만 | `204`, 본문 없음 |
| POST | `/posts` | 게시글 생성 | 필요 | `201` |
| GET | `/posts` | 게시글 목록 조회 | 불필요 | `200` |
| GET | `/posts/page?page=0&size=20` | 게시글 페이지 조회 | 불필요 | `200` |
| GET | `/posts/{postId}` | 게시글 단건 조회 | 불필요 | `200` |
| PATCH | `/posts/{postId}` | 게시글 제목·내용 수정 | 불필요 | `200` |
| DELETE | `/posts/{postId}` | 게시글 삭제 | 불필요 | `204`, 본문 없음 |

게시글 수정·삭제의 작성자 권한 검사는 이번 필수 과제 범위에 포함되지 않아 기존 동작을 유지했습니다.

페이지 조회는 0페이지부터 시작하며 `size`는 1~100입니다. 기본값은 `page=0`, `size=20`이고 ID 내림차순으로 조회합니다.
응답에는 `content`, `page`, `size`, `totalElements`, `totalPages`, `hasNext`가 포함됩니다.
기존 `/posts`는 배열 응답을 유지합니다.

회원가입 요청 예시입니다.

```json
{
  "email": "user@example.com",
  "password": "study1234!",
  "name": "홍길동"
}
```

비밀번호는 8~20자이며 영문·숫자·ASCII 특수문자를 각각 하나 이상 포함해야 합니다.
공백과 그 밖의 문자는 허용하지 않습니다. 이름은 공백만으로 구성할 수 없고 최대 20자입니다.

로그인은 가입한 이메일과 비밀번호를 `/auth/login`으로 보냅니다.
응답의 `accessToken`을 보호된 API 요청에 아래 Header로 전달합니다.

```text
Authorization: Bearer {accessToken}
```

Postman에서는 **Authorization → Bearer Token → Token**에 토큰 값만 넣습니다.
요청 방식은 왼쪽에서 선택하고, 주소 칸에는 `http://localhost:8080/...`만 입력합니다.

이름 수정 Body는 `{"name":"새 이름"}`입니다.
게시글 생성 Body에는 `title`(최대 100자), `content`(최대 500자)를 넣습니다.
게시글 수정은 제목과 내용 중 보낸 항목만 바꾸며, 빈 값이나 수정할 항목이 없는 요청은 거절합니다.
생성·수정 DTO의 Validation으로 요청을 먼저 검사하고, `Post` 생성자와 변경 메서드에서도 제목·내용 규칙을 검사합니다.
두 필드를 함께 바꿀 때는 모든 검증을 마친 뒤 값을 변경합니다.

## 인증과 작성자 연결

- 비밀번호는 BCrypt로 Hash 처리해 저장하고, 로그인할 때 `matches()`로 비교합니다.
- JWT는 HS256 서명, 만료 시간, 사용자 ID를 검증합니다. Payload에 비밀번호나 Hash를 넣지 않습니다.
- `AuthInterceptor`가 인증한 사용자 ID를 현재 요청에 저장하고, Service에서 본인인지 확인합니다.
- 다른 회원 정보에 접근하면 그 회원의 존재 여부를 조회하기 전에 `403`을 반환합니다.
- `Post.author`는 `@ManyToOne(fetch = LAZY)` 단방향 관계이며, `posts.user_id`가 `users.id`를 참조합니다. 작성자는 필수입니다.
- 게시글 작성자는 요청 Body의 ID가 아닌 검증된 JWT의 사용자로 정합니다.
- 게시글 응답에는 `authorId`, `authorName`을 포함합니다. 비밀번호와 Hash는 응답에 포함하지 않습니다.

Java의 회원 이름 필드는 `name`이며, 기존 DB와의 호환을 위해 컬럼 이름은 `nickname`을 유지했습니다.
2주차 데이터에 작성자 없는 게시글이 남아 있다면, 실제 테스트 회원에게 연결한 뒤 `user_id`의 NOT NULL 제약을 적용해야 합니다.

## 회원 탈퇴 정책

본인 계정만 탈퇴할 수 있습니다. 작성한 게시글을 먼저 삭제한 뒤 회원을 삭제합니다.
두 작업은 하나의 트랜잭션으로 처리하며, 실패하면 전체를 롤백합니다.
다른 회원과 그 회원의 게시글은 유지합니다.

탈퇴한 회원은 다시 로그인할 수 없습니다. 이전에 발급받은 토큰도 사용자 존재 여부 확인에서 차단되어 `401`을 반환합니다.

## 오류 응답

공통 예외 처리에서 `code`, `message` 형식으로 반환합니다.

| 상황 | 상태 코드 |
| --- | --- |
| 필수 값 누락, 형식·길이 위반, 잘못된 JSON | `400` |
| 로그인 실패, 토큰 누락·변조·만료, 탈퇴한 사용자의 토큰 | `401` |
| 다른 회원 정보 조회·수정·삭제 시도 | `403` |
| 없는 게시글 조회·수정·삭제 | `404` |
| 중복 이메일 회원가입 | `409` |

## 테스트

```sh
./gradlew test bootJar
```

리뷰 반영 후 자동 테스트 57개와 `bootJar` 빌드가 통과했습니다.

- `AuthUserApiTests` (45개): 회원·인증 API, 게시글 입력 검증과 부분 수정, 탈퇴, 페이징 확인. 페이징에서 작성자 이름을 읽어도 목록·전체 개수 조회 2회만 실행되는지 검증
- `PostTests` (10개): Service를 거치지 않아도 잘못된 제목·내용으로 생성·변경할 수 없는지, 변경 실패 시 기존 값이 유지되는지 확인
- `PostPersistenceTests`: 한 회원과 여러 게시글의 저장·조회 및 연관관계 확인
- `BackendStudy25SnsApplicationTests`: 애플리케이션 컨텍스트 실행 확인

테스트는 `src/test/resources/application.yaml`의 별도 H2 메모리 DB와 공개된 테스트용 키를 사용합니다.
일반 실행의 `data/sns` 파일 DB와 실행용 JWT 키를 사용하지 않습니다.

Postman으로 정상 요청과 인증·권한 오류, 회원 탈퇴 후 접근 차단을 직접 확인했습니다.
만료 확인은 유효 시간을 짧게 설정하고 새로 로그인한 토큰으로 진행합니다.
확인이 끝나면 유효 시간 설정을 기본값으로 되돌립니다.
서버 재시작 후 기존 회원으로 로그인하는 방법으로 파일 DB의 데이터 유지도 확인합니다.

### Postman에서 확인한 응답

본인 이름 수정 요청을 실제로 전송해 `200 OK`와 변경된 이름을 확인했습니다.
아래 ID와 이메일은 실습 당시 값이며, 재현할 때는 가입한 회원의 ID와 로그인 토큰을 사용합니다.

```http
PATCH http://localhost:8080/users/1
Authorization: Bearer {로그인으로 받은 토큰}
Content-Type: application/json

{"name":"dowon2"}
```

응답: `200 OK`

```json
{
  "id": 1,
  "email": "dowon@example.com",
  "name": "dowon2"
}
```

### PR 리뷰 반영

[2주차 PR #6 리뷰](https://github.com/ApptiveDev/backend-study-25-sns/pull/6)를 기준으로 반영했습니다.

| 피드백 | 반영 내용 |
| --- | --- |
| 생성자 주입 코드 정리 | `PostController`에 `@RequiredArgsConstructor` 적용 |
| DTO에서 입력 검증 | 생성 DTO에 `@NotBlank`, `@Size`, Controller에 `@Valid` 적용. 수정 DTO는 필드 생략과 명시적인 null을 구분해 검증 |
| Entity 자체 규칙 보장 | `Post` 생성·변경 시 제목·내용 검증, `User.create()`와 회원 자체 검증 |
| DTO 변환 중복 제거 | `PostResponse.from()` 사용 |
| 트랜잭션 기본 설정 | Service 기본값은 읽기 전용, 생성·수정·삭제는 일반 트랜잭션 |
| N+1 방지 및 페이징 | 작성자를 `@EntityGraph`로 함께 조회하고 별도 페이지 조회 API 추가 |
| 공통 오류 응답 | `ErrorResponse(code, message)` 사용 |
| Postman 결과 첨부 | 위에 직접 확인한 요청과 응답 기록 |

## 학습 내용과 AI 활용

- BCrypt는 같은 비밀번호도 Hash 결과가 달라질 수 있어, 문자열 비교 대신 `matches()`를 사용합니다.
- JWT는 Payload를 숨기는 방식이 아니라 서명으로 변조를 확인합니다.
- 인증에 실패한 요청은 `401`, 인증된 사용자가 다른 회원 정보에 접근한 요청은 `403`으로 구분합니다.

AI에게 구현 순서와 코드 작성, 오류 원인 파악, 리뷰 반영 코드 수정, 자동 테스트 작성, README 정리에 도움을 받았습니다.
코드를 직접 입력하고 Postman으로 동작을 확인했으며, 자동 테스트도 실행해 통과 여부를 확인했습니다.
