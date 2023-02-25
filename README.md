# JUIN-STORE/Spring-Backend

---
# 0. 프로젝트 설명
```text
JUIN-STORE는 얼굴 사진을 올리면 퍼스널 컬러를 진단하고, 
그에 맞는 옷을 추천하고 판매하는 웹 서비스다.

프로젝트 구조
(비공개) Python-Backend => 퍼스널 컬러를 진단하는 백엔드
(공개)   Spring-Backend => STORE 백엔드  
(공개)   Vue-Frontend => STORE 프론트엔드

cf) JUIN인 이유는 팀원의 이름을 합쳤다.
```

# 1. 개발 환경
```text
Spring Security 5.5.1
JAVA corretto-17.0.5
Spring Boot 2.4.2
querydsl 5.0.0
RestDocs 2.0.5
Swagger 3.0.0
MYSQL 8.0.26
JUnit 5.7.0
JPA 2.4.2
```

# 2. 코딩 컨벤션
[CODING CONVENTION](https://github.com/JUIN-STORE/Spring-Backend/blob/main/CODING-CONVENTION.md)

# 3. API 문서
```text
# production
Swagger : api.juin.store/swagger-ui/index.html
RestDocs: api.juin.store/index.html
```

```text
# local
Swagger : localhost:13579/swagger-ui/index.html
RestDocs: localhost:13579/index.html
```

# 4. ERD
![erd.png](erd.png)