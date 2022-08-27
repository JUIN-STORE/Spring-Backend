# eCommerce/Spring-Backend

---
# Coding Convention

## 1. Controller Naming 접두사
* C -> create~, new~
* R -> retrieve~, one~, all~
* U -> update~
* D -> delete~
* CRUD를 그대로 사용하되 의미 파악이 어렵다면 타 Naming과 겹치지 않는 다른 걸 사용
* 기타 케이스는 End-Point 사용 ex) /profile -> profile
* ref) https://spring.io/guides/tutorials/rest/

## 2. Service Naming 접두사
* C -> add~
* R -> readBy~
* U -> modify~
* D -> remove~
* 기타 케이스는 Repository Naming과 
  DB의 예약어는 최대한 피해서 사용한다.

## 3. Repository Naming
- C -> save
- R -> findBy
- U -> save
- D -> delete
---

## 4. Static Factory Method Naming
* from: 하나의 매개 변수를 받아 인스턴스 생성
* of  : 여러 개의 매개 변수를 받아 인스턴스 생성
* ref) Effective Java 3/E
 
## 5. Controller Rule
* Controller에서 정적 팩토리 메서드를 통해 
  Service에서 넘어온 entity를 reponse로 변경

## 6. Service Rule
* ***Repository Only One, 하나의 레포지토리만 DI 받기*** 
* 그 외 DB 접근은 최대한 Service로 접근하기
* 예를 들어 AccountService에서 account table과 address table에 접근한다면
-> account table은 AccountRepository로 address table은 AddressService로 접근
* ***순환 참조 조심***
* Service Method는 return entity, Service는 response를 몰라야 함.
* ref) https://stackoverflow.com/questions/59194082/in-spring-is-it-better-practice-to-return-a-responseentity-directly-from-the-se

---
## 7. Response Class Naming
* C -> Create
* R -> Retrieve 또는 Read
* U -> Update
* D -> Delete
* 기타 케이스는 Controller Method 사용 ex) Method login -> Class Login
---

# API 목록

# 1. Account
- /api/accounts/sign-up
- /api/accounts/login
- /api/accounts/profile
- /api/accounts/modify
- /api/accounts/remove

# 2. ADDRESS

# 3. PRODUCT

# 4. CART

# 5. ORDER