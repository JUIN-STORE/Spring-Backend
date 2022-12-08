# Coding Convention
---

# *Controller*
## 1. Controller Naming 접두사
* C -> create~, new~
* R -> retrieve~, one~, all~
* U -> update~
* D -> delete~
* CRUD를 그대로 사용하되 의미 파악이 어렵다면 타 Naming과 겹치지 않는 다른 걸 사용
* 기타 케이스는 End-Point 사용 ex) /profile -> profile
* ref) https://spring.io/guides/tutorials/rest/

## 2. Controller Rule
* Controller에서 정적 팩토리 메서드를 통해
  Service에서 넘어온 entity를 reponse로 변경

## 3. Request, Response Class Naming
* C -> Create
* R -> Retrieve 또는 Read
* U -> Update
* D -> Delete
* 기타 케이스는 Controller Method 사용 ex) Method login -> Class Login

## 4. Static Factory Method Naming
* from: 하나의 매개 변수를 받아 인스턴스 생성
* of  : 여러 개의 매개 변수를 받아 인스턴스 생성
* ref) Effective Java 3/E
---

# *Service*

## 1. Service Naming 접두사
* C -> add~
* R -> readBy~
* U -> modify~
* D -> remove~
* 기타 케이스는 Repository Naming과 
  DB의 예약어는 최대한 피해서 사용한다.

## 2. [EntityName]Service Rule
* ***Repository Only One, 하나의 레포지토리만 주입 받기***
* 그 외 DB 접근은 모두 Service를 통해 접근하기
* 예를 들어 AccountService에서 account table과 address table에 접근이 필요하면 아래와 같이 접근한다.\
  -> private final AccountRepository accountRepository; \
  -> pirvate final AddressService addressService;
* Method는 return entity, response를 몰라야 한다. \
  -> 예를 들어 AccountService는 Account, List<Account> 등 Account의 형태로만 리턴해야 된다.
* ref) https://stackoverflow.com/questions/59194082/in-spring-is-it-better-practice-to-return-a-responseentity-directly-from-the-se

## 3. [EntityName]RelationService Rule
* Join이 필요한 케이스나 return response를 하는 케이스에 사용
* [EntityName]Service를 주입받는다.
---

# *REPOSITORY*

## 1. Repository Naming
* C -> save
* R -> findBy
* U -> save 또는 dirtyChecking
* D -> delete
---

# *TEST CODE*

# 1. 실행 대상
* sut를 사용한다.
  * ex) @InjectMocks AccountService sut;

## 2. Data type
* given data type - var
  * ex) var expected;
* when data type - class
  * ex) final Account actual;
---

# *LOGGING*

## 1. 기본 정책
* 모두 대문자로 표기

## 2. 형식
* [긴급도][LAYER][CLASS_NAME][METHOD_NAME]
  * ex) [P1][CON][ACNT][LGIN]
* 긴급도
* *P1* -> Priority_1
* info - 9
* warn(front) - 8765
* error(back) - 4321
* *LAYER*
  * CON -> Controller
  * SER -> Service
  * REP -> Repository
  * COM -> Component
  * UTL -> Utility
* *CLASS NAME*
  * AccountController -> ACNT (임의의 4글자 만들기)
* *METHOD NAME*
  * login -> LGIN (임의의 4글자 만들기)
