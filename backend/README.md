# 로컬 앱 개발 환경

Spring Boot의 기본 프로필 설정 파일을 사용합니다. 앱 전용 `.env`나 별도 import는 사용하지 않습니다.
실행 디렉터리인 `backend/` 아래 `config/application-local.properties`를 `local` 프로필에서 자동으로 읽습니다.
이 파일은 소스 리소스 밖에 있으므로 앱 JAR에 포함되지 않습니다.

## 개인 설정 준비

저장소 루트에서 실행합니다. 기존 파일은 덮어쓰지 않습니다.

```bash
cp -n backend/config/application-local.properties.example backend/config/application-local.properties
```

복사한 파일의 다음 값을 실제 접속할 DB에 맞게 수정합니다.

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/sayroam
spring.datasource.username=sayroam
spring.datasource.password=sayroam-local
```

Docker DB를 사용한다면 URL의 포트·DB명과 계정 정보를 `docker/.env`에 맞춥니다.
두 설정은 별도로 관리하며 자동 동기화하지 않습니다. 앱에는 root 비밀번호가 필요하지 않습니다.

## 공유 및 Git 관리

| 파일 | 용도 | Git 관리 |
| --- | --- | --- |
| `src/main/resources/application.properties` | 공통 앱 설정, 비밀값 제외 | 포함 |
| `config/application-local.properties.example` | 로컬 설정 키와 개발용 예시 | 포함 |
| `config/application-local.properties` | 개인의 실제 접속 정보 | 제외 |

예시 파일에는 실제 서버 주소·계정·비밀번호를 넣지 않습니다. 새 설정 키가 생기면 예시 파일과 이 문서를 갱신하고,
팀원은 자신의 실제 파일에 해당 키를 추가합니다. `.example` 파일은 Spring Boot가 자동으로 읽지 않습니다.
Properties는 `키=값`으로 작성하고 값에 따옴표를 붙이지 않습니다. 주석은 별도의 `#` 줄에 작성합니다.

## 실행

DB 준비 후 [마이그레이션 안내](gradle/README.md)에 따라 스키마를 적용하고 앱을 실행합니다.

```bash
./backend/gradlew -p backend flywayMigrate
./backend/gradlew -p backend bootRun --args='--spring.profiles.active=local'
```

IntelliJ Run Configuration:

- Active profiles: `local`
- Working directory: 저장소의 `backend/` 절대 경로
- Environment variables: 개인 설정 파일을 사용하면 별도 입력 불필요

환경변수 `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`는
파일의 연결 설정보다 우선합니다. 운영에서는 `local` 프로필 없이 이 환경변수로 연결 정보를 주입합니다.
앱 실행은 Docker를 자동 기동하거나 스키마를 변경하지 않습니다.
테이블 생성·변경은 `flywayMigrate`로 실행하고 앱은 엔티티와 DB 매핑을 검증합니다.
빈 DB 또는 미적용 변경이 있으면 마이그레이션을 먼저 실행하세요.
