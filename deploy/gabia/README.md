# Gabia 서버 배포 메모

이 프로젝트는 서버 DB를 MySQL 기준으로 사용합니다. H2는 사용하지 않습니다.

## 1. 서버에 필요한 것

- Java 17
- Git
- MySQL 또는 MariaDB
- 8080 포트 오픈 또는 Nginx 리버스 프록시

## 2. 서버에서 프로젝트 받기

```bash
git clone <팀 GitHub 저장소 URL>
cd likelion14th_hackathon
cp .env.example .env
```

## 3. `.env`에 실제 값 넣기

```env
OPENAI_API_KEY=여기에_오픈AI_API_KEY
JWT_SECRET=충분히_긴_랜덤_문자열
SERVER_PORT=8080

DB_HOST=가비아_DB_HOST
DB_PORT=3306
DB_NAME=가비아_DB_NAME
DB_USERNAME=가비아_DB_USERNAME
DB_PASSWORD=가비아_DB_PASSWORD

JPA_DDL_AUTO=update
JWT_EXPIRATION_MINUTES=120
```

주의: `.env`는 절대 GitHub에 올리면 안 됩니다.

## 4. 빌드 및 실행

```bash
./gradlew clean bootJar
java -jar build/libs/likelion14th_hackathon-0.0.1-SNAPSHOT.jar
```

## 5. 실행 확인

```bash
curl http://localhost:8080
```

API 서버가 외부에서 열려야 하면 가비아 방화벽/보안그룹에서 8080 포트를 열거나, Nginx로 80/443 포트에서 Spring Boot 8080으로 연결합니다.
