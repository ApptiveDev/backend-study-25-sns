# backend-study-25-sns

SNS 회원과 게시글의 JPA 엔티티를 설계하는 1주차 선택 과제입니다.
과제 목록에 명시된 `User`, `Post` 두 엔티티를 구현했습니다.

## 패키지 구조

레이어별로 모으는 대신, 회원과 게시글이라는 기능별 도메인으로 나눴습니다.

```text
com.apptivedev.sns
├── domain
│   ├── user
│   │   └── entity
│   │       └── User.java
│   └── post
│       └── entity
│           └── Post.java
└── BackendStudy25SnsApplication.java
```

## 엔티티 설계

아래 필드와 길이 제한은 과제에 정해진 값이 아니라 이번 구현에서 선택한 설계입니다.

| 엔티티 | 필드 | 역할 |
| --- | --- | --- |
| User | id | 자동 생성되는 회원 기본 키 |
| User | email | 중복 불가, 최대 100자인 이메일 |
| User | nickname | 최대 30자인 닉네임 |
| User | createdAt | 최초 저장 시 기록하는 가입 시각 |
| Post | id | 자동 생성되는 게시글 기본 키 |
| Post | author | 게시글을 작성한 회원 |
| Post | content | 최대 2,000자인 게시글 본문 |
| Post | createdAt | 최초 저장 시 기록하는 작성 시각 |

- 한 회원은 여러 게시글을 작성할 수 있습니다. `Post.author`에 `@ManyToOne`을 사용하고, `posts.user_id`가 `users.id`를 참조합니다.
- 게시글에서 작성자를 참조하는 단방향 관계로 구성했습니다. `User`에 게시글 목록은 추가하지 않았습니다.
- 작성자는 필수이며, 필요할 때 조회하도록 지연 로딩(`LAZY`)을 사용합니다.
- 회원은 먼저 저장하고 게시글을 저장합니다. 저장·삭제를 자동 전파하는 cascade는 설정하지 않았습니다.
- 테이블 이름은 `users`, `posts`로 지정했습니다. `user`라는 SQL 예약어와의 충돌을 피합니다.
- JPA가 사용할 기본 생성자는 `protected`로 두고, 새 객체는 필요한 값을 받는 생성자로 만듭니다.
- `@Getter`로 조회만 허용하고, 모든 필드를 바꾸는 공용 setter는 두지 않았습니다.
- 필수 여부와 길이 제한은 DB 제약입니다. 빈 문자열 검사, 이메일 형식 검사, 로그인 기능은 구현하지 않았습니다.

Service, Controller, Repository는 이번 과제 범위에 포함하지 않았습니다.

## 테스트

JDK 17 환경에서 실행합니다.

```sh
./gradlew test
```

`PostPersistenceTests`는 H2에 회원 한 명과 게시글 두 개를 저장한 뒤,
영속성 컨텍스트를 비우고 다시 조회하여 필드, 작성자 관계, 생성 시각을 확인합니다.
