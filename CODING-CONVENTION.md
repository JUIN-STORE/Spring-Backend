# *Coding Convention*

---
# *Common*
## 1. this
* > this는 메서드에만 붙이며 this가 붙은 메서드는 *@Transactional을 사용하지 않는다*는 의미다. 
  > 
  > this를 통해 해당 메서드는 @Transactional 참여할 수 없다는 것을 알려주는 것이다.
  >
  > 만약 this가 붙은 메서드에 @Transactional 정상 동작하려면 해당 메서드를 외부로 빼서 사용한다.
* > 예를 들어 같은 코드가 있다고 가정하자.

```java
// A-1. this가 없는 코드
@Sl4j
public class ExternalService { 
    private void inner() { 
    log.info("call inner");
    } 
    
    public void external() {
        log.info("call external");
        inner(); // @Transactional 정상 동작 여부를 한 눈에 알 수 없음.
    }
}
```

```java
// A-2. this가 있는 코드
@Sl4j
public class ExternalService { 
    private void inner() { 
    log.info("call inner");
    } 
    
    public void external() {
        log.info("call external");
        this.inner(); // @Transactional 정상 동작 여부를 한 눈에 알 수 있음.
    }
}
```


```JAVA
// B. 새로운 요구사항이 들어와서 external()에 @Transactional이 필요하다고 가정하자.
@Sl4j
public class ExternalService {
    private void inner() {
      log.info("call inner");
    }
    
    // 정상 동작 안 함.
    @Transactional
    public void external() {
      log.info("call external");
      inner();
      
      // bizLogic();
    }
}
```````

```JAVA
// C. 만약 @Transactional 정상 동작하려면 this가 붙은 메서드를 외부로 빼서 사용한다.
@Sl4j
@RequiredArgsConstructor
public class ExternalService { 
    private final InnerService innerService;
    
    @Transactional 
    public void external() {
        log.info("call external");
        innerService.inner();

        // bizLogic();
    }
}

@Sl4j
public class InnerService { 
    public void inner() {
        log.info("call inner");
    }
}
```

# *Controller*
## 1. Controller Naming 접두사
* > Create -> create~
* > Read   -> retrieve~, retrieveOne~, retrieveAll~
* > Update -> update~
* > Delete -> delete~
* > CRUD를 그대로 사용하되 의미 파악이 어렵다면 타 Naming과 겹치지 않는 다른 걸 사용
  > 
  >  기타 케이스는 End-Point 사용 ex) /profile -> profile
  >
  > ref) https://spring.io/guides/tutorials/rest/

## 2. Controller Rule
* > Controller에서 정적 팩토리 메서드를 통해 Service에서 넘어온 entity를 reponse로 변경

## 3. Request, Response Class Naming
* > Create -> Create
* > Read   -> Retrieve
* > Update -> Update
* > Delete -> Delete
* > 기타 케이스는 Controller 메서드명으로 사용 ex) Method login -> Class Login

## 4. Static Factory Method Naming
* > from: 하나의 매개 변수를 받아 인스턴스 생성
  > 
  > of  : 여러 개의 매개 변수를 받아 인스턴스 생성
  > 
  > ref) Effective Java 3/E
---

# *Service*

## 1. Service Naming 접두사
* > Create -> add~
* > Read   -> entity 반환 시 readBy~  
  > 
  > Read   -> response 반환 시 make~ 
* > Update -> modify~
* > Delete -> remove~
* > 기타 케이스는 Repository Naming과 DB 예약어는 최대한 피해서 사용한다.

## 2. Service Rule
* > ****Repository Only One, 하나의 레포지토리만 주입 받기**** 
  > 
  > 그 외 DB 접근은 모두 QueryService를 통해 접근하기 
* > 예를 들어 AccountCommandService에서 account table과 address table에 접근이 필요하면 아래와 같이 접근한다.
  > 
  > private final AccountRepository accountRepository;
  >
  > private final AddressQueryService addressQueryService;

### 2-1. [EntityName]QueryService Rule - Read 전용
* > 단순 조회 케이스는 response를 반환하지 않고 반드시 entity를 반환한다.
  > 
  > entity로 반환하는 케이스는 향후 MapStruct를 통해 변환하여 반환할 예정이다.
* > 예를 들어 AccountQueryService는 Account, List<Account> 등 Account의 형태로만 리턴해야 된다. 
  > 
  > ref) https://stackoverflow.com/questions/59194082/in-spring-is-it-better-practice-to-return-a-responseentity-directly-from-the-se
* > 조인 케이스만 return response.

### 2-2. [EntityName]CommandService Rule -> Create/Update/Delete 전용
* > 명명 규칙에 맞게 Create/Update/Delete 메서드를 만든다.
---

# *REPOSITORY*

## 1. Repository Naming
* > Create -> save
* > Read   -> findBy~
* > Update -> update~
* > Delete -> delete~
---

# *TEST CODE*

# 1. 실행 대상
* > sut를 사용한다.
  > 
  > ex) @InjectMocks AccountQueryService sut;

## 2. Data type
* > given data type - var
* > when data type - final class
  
* > // given \
  > var expected = 1L;
  >  
  > // when \
  > final Account actual = sut.readByAccountId(expected);
---

# *LOGGING*

## 1. 기본 정책
* > 모두 대문자로 표기

## 2. 형식
* > **[긴급도][LAYER][CLASS_NAME][METHOD_NAME]**
  > 
  > **ex) [P1][CTRL][ACNT][LGIN]**

* > *긴급도 (낮을수록 중요하다)* \
  > *P1* -> Priority-1

  * > log.info() - 9 
    > 
    > log.warn() - 8765 (Front 오류일 때)
    > 
    > log.error() - 4321 (Back 오류일 때)

* > *LAYER*
  * > CTRL -> Controller
    > 
    > SERV -> Service
    > 
    > REPO -> Repository
    > 
    > COMP -> Component
    > 
    > UTIL -> Utility

* > *CLASS NAME (임의의 4글자 만들기)* 
  * > AccountController -> ACNT 
    
* > *METHOD NAME(임의의 4글자 만들기)*
  * > login -> LGIN 
