# backend-study-25-sns

APPTIVE 백엔드 아카데미 스터디 과제

## 2주차 과제

- Spring Data JPA 및 H2 DB 연동
- Post Entity, Repository, Service, Controller 구현
- 게시글 생성, 조회, 수정, 삭제 API 구현
- Request / Response DTO를 이용한 계층 간 데이터 전달

## 3주차 과제

### 구현 내용

- User Entity 및 회원가입, 로그인 구현
- BCrypt를 이용한 비밀번호 Hash 처리
- JWT 발급 및 검증
- Interceptor를 이용한 인증 처리
- 사용자 정보 조회, 수정 및 회원 탈퇴
- Post와 User의 N:1 단방향 연관관계 설정
- Validation 및 공통 예외 처리
- 회원 탈퇴 시 작성한 게시글도 함께 삭제하도록 구현

### 배운 점

- Postman으로 API를 테스트하면서 HTTP 상태 코드의 의미를 이해할 수 있었다.
- 로그인 후 발급받은 JWT를 Bearer Token으로 전달해야 사용자 정보를 조회할 수 있다는 것을 확인했다.
- Gradle에 의존성을 추가한 뒤 새로고침을 해야 라이브러리가 정상적으로 인식된다는 것을 알게 되었다.

## 4주차 과제

### 구현 내용

- JUnit 5와 AssertJ를 이용한 Entity 단위 테스트
- Mockito를 이용한 Service 단위 테스트
- MockMvc를 이용한 API 통합 테스트
- 테스트 전용 H2 DB 설정
- 정적 팩터리 메서드 적용
- Entity 내부에 생성 및 수정 규칙 적용

### 배운 점

- 처음에는 예외가 발생하면 무조건 테스트가 실패한다고 생각했는데, 예상한 예외가 발생하면 테스트가 성공한다는 것을 알게 되었다.
- 빈 제목에 대한 테스트가 실패하여 Post 생성자에 검증 로직을 추가했고, 수정 후 테스트가 통과하는 것을 확인했다.
- Mockito를 사용하면 실제 DB에 연결하지 않고도 Service의 동작을 테스트할 수 있다는 것을 알게 되었다.
- 테스트를 하나씩 실행하지 않고 Gradle 명령어로 전체 테스트를 실행할 수 있었다.

### 테스트 결과

| 테스트 | 결과 |
|---|---|
| Gradle 전체 테스트 | BUILD SUCCESSFUL |
| Postman API 요청 | 24개 |
| Postman 검증 항목 | 48개 |
| 성공 | 48개 |
| 실패 | 0개 |

### 테스트 실행 방법

Windows 환경

```powershell
.\gradlew.bat cleanTest test
```

테스트 보고서 경로

```
build/reports/tests/test/index.html
```

### 서버 실행 방법

JWT 서명 키를 환경변수로 설정한 후 서버를 실행한다.

```powershell
$key = New-Object byte[] 32
[System.Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($key)
$env:JWT_SECRET = [Convert]::ToBase64String($key)

.\gradlew.bat bootRun
```

- 서버 주소: http://localhost:8080
- JWT 서명 키는 저장소에 포함하지 않음.

## AI 활용 내역
### AI 활용

- JWT 인증 및 테스트 코드 구현 과정에서 AI를 참고함.
- Gradle 의존성, 패키지 오류 등 구현 과정에서 발생한 문제를 해결하는 데 활용함.
- Gradle 전체 테스트와 Postman API 테스트를 직접 실행하여 정상·예외 응답을 확인함.