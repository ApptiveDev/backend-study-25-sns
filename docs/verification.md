# API 동작 확인

## 검증 결과 — 2026-11-12

- `./gradlew test bootJar --no-daemon --console=plain`: 성공
- 자동 테스트 37개: 실패 0, 오류 0
- API 통합 테스트 35개, 파일 DB 재시작 테스트 1개, 기본 컨텍스트 테스트 1개
- 실제 실행한 서버의 Swagger UI에서 API 5개와 요청·응답 스키마 표시를 확인
- Swagger의 **Execute**로 GET `/posts`를 호출하여 실제 `200`과 빈 배열 `[]` 응답 확인
- Postman 요청 39개를 포함한 Collection 작성. Postman Runner 실행은 수행하지 않음

검증한 코드를 IntelliJ 원본 프로젝트에 반영한 뒤, 원본 프로젝트에서 빌드와 테스트 37개 통과를 다시 확인했다. 노션 수정, Git 커밋 및 GitHub 푸시는 수행하지 않았다.

## Swagger 사용

1. 프로젝트 루트에서 `./gradlew bootRun`을 실행한다.
2. `http://localhost:8080/swagger-ui/index.html`을 연다.
3. 원하는 API를 펼치고 **Try it out**을 누른다.
4. 요청 본문과 ID를 입력한 뒤 **Execute**를 누른다.
5. **Server response**의 실제 상태 코드와 본문을 확인한다.

| 순서 | 요청 | 확인할 결과 |
| --- | --- | --- |
| 1 | GET `/posts` — 비어 있는 새 DB | `200`, `[]` |
| 2 | POST `/posts`, `{"title":"제목","content":"내용"}` | `201`, 서버가 생성한 `id`, `Location` 헤더 |
| 3 | GET `/posts`, GET `/posts/{생성한 id}` | `200`, 생성한 게시글 확인 |
| 4 | PATCH 같은 ID, `{"title":"변경 제목"}` | `200`, 내용은 `내용` 유지 |
| 5 | PATCH 같은 ID, `{"content":"변경 내용"}` | `200`, 제목은 `변경 제목` 유지 |
| 6 | GET 같은 ID | 변경한 두 값이 실제 조회됨 |
| 7 | PATCH 같은 ID, `{}` | `400`, 오류 메시지 |
| 8 | PATCH 같은 ID, `{"title":null}` 또는 `{"content":"   "}` | `400`, 오류 메시지 |
| 9 | POST에서 제목·내용 누락, 빈 문자열, 공백만 있는 값 | `400`, 오류 메시지 |
| 10 | POST와 PATCH에서 제목 101자 또는 내용 501자 | `400`, 오류 메시지 |
| 11 | DELETE 같은 ID | `204`, 응답 본문 없음 |
| 12 | GET/PATCH/DELETE 같은 ID | 모두 `404`, 오류 메시지 |

기존 데이터가 있는 DB에서 빈 배열 검사를 위해 다른 사람의 게시글을 지우지 않는다. 빈 배열은 자동 테스트 또는 별도 파일 DB로 확인한다. 예를 들어 다음 명령은 검증용 DB와 8081 포트를 사용한다.

```bash
DB_URL=jdbc:h2:file:./data/verification-db ./gradlew bootRun --args='--server.port=8081'
```

이 경우 Swagger 주소도 `http://localhost:8081/swagger-ui/index.html`이다. 같은 이름의 검증 DB를 이전에 사용했다면 기존 글이 있을 수 있으므로 새 파일 이름을 지정한다.

## 재시작 확인

1. POST로 `{"title":"재시작 확인","content":"유지할 내용"}`을 생성하고 ID를 기록한다.
2. PATCH로 제목을 바꾼다.
3. 서버 실행 터미널에서 Ctrl+C로 정상 종료한다.
4. **같은 프로젝트 루트, 같은 DB URL**로 서버를 다시 실행한다.
5. GET `/posts/{기록한 id}`로 수정된 제목과 기존 내용을 확인한다.

자동 테스트 `PostPersistenceTest`도 별도의 임시 파일 DB로 종료와 재시작을 실제 수행한다.

## Postman 사용

`postman/posts.postman_collection.json`을 Import하고 컬렉션의 `baseUrl`을 실행 서버 주소로 설정한다. 기본값은 `http://localhost:8081`이다.

1. 새 검증용 DB로 서버를 실행한다.
2. 컬렉션의 `01 CRUD`, `02 입력 검증` 폴더를 순서대로 Runner에서 실행한다.
3. `03 재시작 준비`를 실행한다. 생성한 ID는 컬렉션 변수에 저장된다.
4. 서버를 종료하고 같은 DB URL로 다시 시작한다.
5. `04 재시작 후 확인`만 실행한다. 유지된 게시글을 조회하고 해당 검증용 게시글을 정리한다.

각 요청의 Tests가 상태 코드, 응답 값 또는 오류 메시지를 검사한다. CRUD 검증 중 삭제하는 게시글은 컬렉션에서 직접 만든 게시글이다. 재시작 확인 폴더를 포함한 전체 컬렉션을 한 번에 실행하면 중간 서버 재시작 검증은 되지 않으므로 위 순서를 따른다.
