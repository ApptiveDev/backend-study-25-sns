# SNS 게시판 서버 만들기
사용자가 게시글을 작성, 조회, 수정, 삭제하는 기능(CRUD)을 구현한 Spring Boot 서버입니다.

## 기술 스택
- Java 17
- Spring Boot 3.5 (Spring Web, Spring Data JPA, Validation)
- H2 Database (파일 기반)


## 실행 방법
1. JDK 17을 설치합니다.
2. IntelliJ에서 프로젝트를 열고 `BackendStudy25SnsApplication`을 실행합니다.
   (터미널: Windows `gradlew.bat bootRun`, Mac/Linux `./gradlew bootRun`)
3. 서버 주소: `http://localhost:8080`

## DB 연결 설정
- **H2 파일 기반 DB**를 사용해 서버를 재시작해도 데이터가 유지됩니다.
- 설정 파일: `src/main/resources/application.yaml`

## 프로젝트 구조
```
com.apptivedev.sns
└── domain
    ├── post
    │   ├── controller   PostController   HTTP 요청을 받고 응답을 반환
    │   ├── service      PostService      비즈니스 규칙, DTO ↔ Entity 변환, 트랜잭션
    │   ├── repository   PostRepository   DB 접근 (JpaRepository)
    │   ├── entity       Post             게시글 테이블과 매핑
    │   └── dto          PostCreateRequest, PostUpdateRequest, PostResponse
    └── user
        └── entity       User             회원 테이블과 매핑 (과제 1)
```

## 테스트
- Postman으로 과제 체크리스트 10개 항목(생성, 목록·단건 조회, 부분 수정, 삭제 후 404, 없는 글 404, 입력 규칙 위반 400 등)을 확인했습니다.
- 콘솔 SQL과 H2 콘솔로 DB 반영 여부를 확인했습니다.
- 서버를 재시작한 뒤에도 기존 게시글이 조회되는 것을 확인했습니다.
## 한계
- `PATCH`에서 `{"title": null}`처럼 명시적으로 null을 보낸 경우는, 필드를 생략한 것과 구분하지 않고 기존 값을 유지합니다. 요청 DTO(record)가 두 경우를 모두 null로 받기 때문입니다

## 이번 과제를 하며 깨달은 내용
- 단순히 텍스트로 배운 DTO, Entity, Repository, DB의 관계를 직접 구현하면서 정확히 이해할 수 있었습니다.
### 게시글 생성 요청이 들어오면 어떤 클래스가 어떤 순서로 호출되나요?
- 서버가 시작될 때 Entity를 기준으로 DB 테이블이 만들어지고, Controller·Service·Repository가 빈으로 등록됩니다.

- 요청이 들어오면 PostController가 JSON을 요청 DTO로 받고 @Valid 로 검사합니다.

  → `PostService`가 요청 DTO를 `Post` 엔티티로 바꿉니다.

  → `PostRepository.save()`가 DB에 저장합니다(INSERT, id 부여).

  → `PostService`가 저장된 엔티티를 응답 DTO로 바꿉니다.

  → `PostController`가 201 상태 코드와 함께 응답합니다.
### Entity와 DTO를 분리한 이유는 무엇인가요?
    - Entity는 DB테이블과 연결된 원본이고 DTO는 클라이언트와 주고받는 양식임.
    -이 때 Entity를 그대로 응답하면 숨겨야 하는 값이 노출되기 때문에 응답 DTO를 통해 보여줘야 하는 값만 보여줌
### Controller, Service, Repository를 나눈 이유는 무엇인가요?
    -각 패키지 별로 하는 일을 명확히 나눠 유지 보수성을 올리기 위함.
### 그 밖에 새로 알게 된 것
    -Annotation의 사용법에 감을 잡았고 각 Annotation을 특정 위치에 왜 사용해야 하는지 등을 알 수 있었음. 
    -직접 코드를 구현하면서 이론으로 접한 DTO의 필요성과 Entity의 활용등을 잘 이해 할 수 있었음.

## AI 활용
    -코드 구성을 7단계(DB설정,Entity,Repo설정,C,R,U,D,실패처리) 나누어 시작 후 막히는 단계는 AI의 설명과 함께 작성함
    -상황에 따라 필요한 Annotation에 대해 조언을 받음.
    -Controller / Service / entity 등을 연결 할 때 햇갈리거나 몰랐던 부분에서 도움을 받음.
    -README의 업계 표준 양식을 받아서 그 것을 기반으로 README 작성