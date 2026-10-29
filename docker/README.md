# Docker 로컬 개발 환경

Java 21과 Docker Compose를 사용할 수 있는 Docker 환경이 필요합니다.
Gradle은 저장소의 Wrapper를 사용합니다. 아래 명령은 저장소 루트 기준입니다.

Docker 관련 파일은 `docker/`에서 관리합니다.
새로 clone한 경우 공유 템플릿을 복사한 뒤 필요한 값을 수정합니다.
이미 `.env`가 있으면 덮어쓰지 않습니다.

```bash
cp -n docker/.env.example docker/.env
```

### MySQL 기동

```bash
./backend/gradlew -p backend dockerInit
```

MySQL 8.4를 생성·기동하고 DB 계정으로 쿼리가 성공할 때까지 최대 180초 기다립니다.
첫 실행 시 이미지 다운로드와 DB 초기화가 필요합니다. 재실행해도 기존 데이터는 보존합니다.
이 태스크는 Docker의 파일 생성 명령인 `docker init`과는 별개입니다.

기본 연결 정보는 로컬 개발 전용입니다.

| 항목 | 기본값 |
| --- | --- |
| 호스트 | `127.0.0.1` |
| 포트 | `3306` |
| 데이터베이스 | `sayroam` |
| 사용자 | `sayroam` |
| 비밀번호 | `sayroam-local` |

Compose는 빈 데이터베이스와 계정을 준비하며, 테이블은 만들지 않습니다.
데이터는 Docker의 `mysql-data` named volume에 보존됩니다.

### 애플리케이션 실행

DB가 준비된 뒤 앱 전용 `backend/config/application-local.properties`를 설정하고 별도로 실행합니다.
설정 방법은 [로컬 앱 개발 환경](../backend/README.md)을 참고하세요.

```bash
./backend/gradlew -p backend bootRun --args='--spring.profiles.active=local'
```

IntelliJ에서 직접 앱을 실행할 때는 활성 프로필을 `local`, Working directory를
저장소의 `backend/` 절대 경로로 지정합니다. 앱은 `backend/config/application-local.properties`를 읽습니다.
`bootRun`, `build`, `test`는 Docker를 자동으로 실행하지 않습니다.
현재 Hibernate DDL과 SQL 초기화는 비활성화되어 있습니다.
`dev`에 반영된 엔티티가 있어도 이 설정으로는 대응하는 테이블을 만들지 않습니다.
지금은 DB 기동과 연결 준비까지의 구성이며, 엔티티를 사용하는 조회·저장은 스키마 구성 이후에 확인합니다.
스키마 작업을 시작할 때 Flyway와 첫 마이그레이션을 추가하고 `ddl-auto=validate`로 전환합니다.

### 상태 확인 및 종료

```bash
./backend/gradlew -p backend dockerStatus
./backend/gradlew -p backend dockerLogs
./backend/gradlew -p backend dockerStop
./backend/gradlew -p backend dockerDown
```

`dockerStop`은 컨테이너를 중지하고, `dockerDown`은 컨테이너와 네트워크를 제거합니다.
두 명령 모두 DB 볼륨을 보존합니다. 데이터 삭제 태스크는 제공하지 않습니다.

### 연결 정보 변경

`docker/.env`는 Docker Compose 전용입니다. Gradle Docker 태스크가 `--env-file`로 읽습니다.
앱의 연결 정보는 별도의 `backend/config/application-local.properties`에서 관리합니다.
같은 DB에 연결하려면 두 파일의 포트·DB명·앱 계정을 맞춰야 합니다. 자동으로 동기화하지 않습니다.
파일이 없으면 실행에 실패합니다.

| 변수 | 용도 | 템플릿 값 |
| --- | --- | --- |
| `DB_PORT` | 호스트에 공개하는 MySQL 포트 | `3306` |
| `DB_NAME` | 생성할 데이터베이스 이름 | `sayroam` |
| `DB_USERNAME` | 앱 전용 DB 사용자(root 사용 금지) | `sayroam` |
| `DB_PASSWORD` | 앱 전용 DB 비밀번호 | `sayroam-local` |
| `DB_ROOT_PASSWORD` | MySQL 초기화용 root 비밀번호(Compose만 사용) | `sayroam-root-local` |

#### 환경변수 작성 규칙

- 실제 값은 Git에서 제외된 `docker/.env`에 저장하고, 공유할 키와 로컬 예시는 `.env.example`에 기록합니다.
- 키는 대문자와 밑줄을 사용하며, 위 5개 값을 모두 비어 있지 않게 지정합니다.
- 한 줄에 `KEY=value`로 작성합니다. `export`, 따옴표, 여러 줄 값, 줄 끝 주석은 사용하지 않습니다.
- 로컬 앱 설정과 같은 작성 규칙을 사용하도록 값에는 영문·숫자·밑줄·하이픈·마침표만 사용합니다.
  `$` 변수 치환, 역슬래시 이스케이프, 공백은 사용하지 않습니다. 주석은 별도의 `#` 줄에 작성합니다.
- `DB_PORT`는 1~65535의 사용 가능한 포트, DB명과 사용자명은 영문으로 시작하는 영문·숫자·밑줄로 지정합니다.
- 같은 `DB_*` 키는 **셸 환경변수 > `.env`** 순으로 적용됩니다.
  파일 값이 적용되지 않으면 기존에 export한 변수를 확인합니다.
- 앱은 `DB_*`를 읽지 않습니다. 앱 설정 파일이나 `SPRING_DATASOURCE_*` 환경변수로 별도 설정합니다.
- 새 키를 추가하면 `.env.example`, Compose 또는 앱 설정, 이 문서를 함께 갱신합니다.
- 템플릿의 비밀번호는 로컬 개발 전용이며 운영 환경에서는 사용하지 않습니다.

Docker 포트를 일시적으로 바꾸려면 셸 환경변수를 지정합니다.
앱 설정 파일의 JDBC URL 포트도 같은 값으로 수정해야 합니다.

```bash
export DB_PORT=13306
./backend/gradlew -p backend dockerInit
./backend/gradlew -p backend bootRun --args='--spring.profiles.active=local'
```

기존 볼륨이 있으면 MySQL 초기화용 DB명·계정·비밀번호 변수를 바꿔도 기존 DB와 계정은 변경되지 않습니다.
운영 환경에서는 `local` 프로필 대신 `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`,
`SPRING_DATASOURCE_PASSWORD`로 별도 연결 정보를 주입합니다.
