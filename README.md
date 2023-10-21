# springboot3-guide
스프링부트3 백엔드 개발자 가이드

```
이 프로젝트는 spring boot 3.0.2 / java17 버전을 사용하고 있습니다.
```

### 1장 자바 백엔드 개발자가 알아두면 좋은 지식
#### 1. 서버와 클라이언트
1. 클라이언트란? : 서버로 요청하는 모든 프로그램
2. 서버란 ? 서버는 클라이언트의 요청을 받아 처리하는 주체

#### 2. 데이터베이스  
1. 데이터베이스 : 여러사람이 데이터를 한군데 모아놓고 여러사람이 사용할 목적으로 관리하는 데이터 저장소
2. DBMS : MySql, 오라클, postgreSQL 등 데이터베이스를 관리하기 위한 시스템
3. RDB : 관계형데이터베이스. 데이터를 행과 열로 이루이진 테이블로 관리. 그외 NoSql, NewSql 등이 있다.
4. SQL : DB에서 데이터를 검색을 위한 언어
5. NoSql : not only query.  RDB는 스케일 아웃을 통해 성능을 올리는게 쉽지않다. 이런 문제를 해결하기 위해 등장.
6. noSql은 다이나모디비, 카우치베이스, 몽고디비 등이 있다. 
   

### 2장 스프링부트3 시작하기
- 스프링부트3 : java 17 버전 사용 필수

- java 17 주요변화    
- `Java17Test.java`
1. 텍스트 블록
```java
 String test = """
            select * from table
            where status = on_sale
            order by price;
            """;
```
2. formatted() 메소드
```java
String formatted = """
            {
                id : %d
                name : %s
            }
            """.formatted(2, "juice");
```
3. 레코드
- 레코드는 데이터전달을 목적으로 객체를 빠르고 간편하게 만들어줌.   
- 레코드는 상속을 할 수 없고, 파라미터에 정의된 필드는 private final로 정의됨. getter 자동생성   
- `Beverge.record`
```java 
public record Beverge(String name, int price) {
    //name, price 파라미터가 private final 로 정의됩니다.
}

Beverge juice = new Beverge("juice",3000);
juice.price() // 3000 
```
4. switch - case 문에서 매개변수의 자료형으로 case 분류가능.

### 3. 스프링부트3 구조 이해하기
1. 인메모리 DB 에 더미데이터 입력 -> `resources/data.sql`
2. `resources/application.yml` 에 인메모리 db 관련 설정
```java
spring:
  jpa:
    #콘솔에 SQL 출력
    show-sql: true
    properties:
      hibernate:
        format_sql: true
    # 테이블 생성 후 data.sql 실행
    defer-datasource-initialization: true
```
4. entity, repository, service, controller 클래스 생성
5. 구동 후 postnman 에서 데이터 확인

### 4. 스프링부트3 와 테스트
1. assertThat 메소드들   
- `JunitTest.java`
```java
        boolean flag = true;
        assertThat(flag).isEqualTo(true);
        assertThat(flag).isNotEqualTo(false);

        List list = Arrays.asList("송하영","박지원");
        assertThat(list).contains("송하영");
        assertThat(list).doesNotContain("이나경");
        assertThat(list).isNotEmpty();

        String str = "Promise";
        assertThat(str).startsWith("P");
        assertThat(str).endsWith("e");

        List emptyList = new ArrayList();
        assertThat(emptyList).isEmpty();

        int a = 15;
        assertThat(a).isPositive();
        int b = -15;
        assertThat(b).isNegative();

        assertThat(a).isGreaterThan(10);
        assertThat(a).isLessThan(20);
```
2. MockMvc 를 이용하여 api 테스트
- MockMvc를 생성하고 RestController api 검증
- mockMvc.perform(), resultAction.andExpect() 사용
- `MemberControllerTest.java`
```java
@SpringBootTest
//@SpringBootApplication 이 있는 클래스 기준 빈들을 생성한다음 테스트용 애플리케이션 컨텍스트 만듬.
//spring context에 등록된 빈 객체들을 테스트에서 사용하려면 이 어노테이션이 필요하다.
@AutoConfigureMockMvc
//MockMvc를 생성하고 구성해줌. MockMvc 는 컨트롤러를 테스트할때 사용
class MemberControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;
    //mockMvc를 초기화 할때 필요함.

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    public void mockMvcSetUp(){
        //mockMvc 세팅
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                                      .build();
    }

    @AfterEach
    public void cleanUp(){
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("회원 조회 api 검증")
    void getAllMembers() throws Exception{
        //given
        final String url = "/members";
        Member savedMember = memberRepository.save(new Member(1L, "이나경"));
        //h2 database dependencies를 추가하였기 때문에 인메모리 DB 저장 테스트 가능.

        //when
        final ResultActions resultActions =
            mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON));
        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(savedMember.getId()))
                .andExpect(jsonPath("$[0].name").value(savedMember.getName()));
    }
}
```

### 5. 데이터베이스 조작이 편해지는 ORM

1. 데이터베이스란? - 셍략
   
2. ORM 이란? - java의 객체와 데이터베이스를 연결하는 프로그래밍 기법

   ```
   * ORM 의 장점과 단점

   <장점>
   1. sql을 직접 작성하지 않고 프로그래밍 언어로 데이터베이스 데이터 접근 가능
   2. sql을 작성하지 않아도되므로 비즈니스 로직에 집중할 수 있다. 보다 객체지향적 개발 가능
   3. 데이터베이스 시스템이 추상화 되어있어 DBMS가 변경되더라도 추가작업이 필요없다.
   4. (기존 sqlMapper(myBatis) 방식은 DBMS가 바뀌면 쿼리도 모두 수정해줘야함.)
   5. 객체와 테이블간 매핑 필드가 명확하기때문에 ERD 의존도가 낮춰지고, 유지보수가 편해진다.

   <단점>
   1. 프로젝트가 복잡해질수록 사용 난이도가 올라간다.
   2. 복잡하고 무거운 쿼리는 ORM으로 해결하기 어렵다. (JPQL이나 QueryDsl을 통해 개선 가능)
   ```

3. JPA외 하이버네이트

   * ORM에도 여러종류가 있다. java에서는 JPA를 표준 ORM으로 사용한다.
   * JPA : 자바 객체와 데이터베이스를 연결해주는 인터페이스
   * 하이버네이트 : JPA 인터페이스를 구현한 구현체. 내부적으로 JDBC API 사용

   1. 엔티티 매니저란?
      
      * 엔티티를 관리. DB와 애플리케이션 사이에서 객체를 생성,수정,삭제 담당.
        
   3. 영속성 컨텍스트란?
      
      * JPA의 중요한 특징중 하나. 엔티티를 관리하는 가상의 공간

        ```
        * 영속성 컨텍스트의 특징 3가지

        1. 1차캐시
        - 영속성 컨텍스트 내부에 1차 캐시가 있음.
        - 캐시의 키는 엔티티의 @Id
        - 엔티티를 조회하면 캐시에서 먼저 조회하고 없으면 DB에서 조회함.
        - 캐시된 엔티티를 조회할때는 DB를 거치지 않으므로 빠르다. 

        2. 쓰기지연
        - 쓰기지연은 트랜잭션이 커밋되기 전까지는 DB에 쿼리를 날리지 않다가
        - 커밋하면 쿼리들을 한번에 실행하는 것을 말함.
        - 적당한 묶음으로 쿼리를 요청할 수 있어 DB부담을 줄임(?)

        3. 변경감지
        - 트랜잭션을 커밋하면 1차캐시에 저장된 엔티티의 값과 현재 엔티티의 값을
        - 비교하여 변경점이 있다면 자동으로 db에 쿼리를 날려 업데이트 처리한다.

        4. 지연로딩
        - 엔티티를 조회 시 엔티티 내에 연관된 엔티티들을 모두 한번에 조회 하지 않고,
        - 연관된 내부엔티티에 접근 시 별도로 쿼리를 날려 조회한다.
        - 한번에 조회하면 즉시로딩도 가능하다. (즉시로딩 사용 시 N+1 문제가 발생할 수 있다.) 
        ```

      * 위 특징들의 공통점은 모두 DB로의 접근을 최소화해 성능을 최적화 하고 개발자의 편의성 높임.

4. SPRING DATA JPA

   * JPA를 보다 편하게 사용하기 위해 스프링데이터에서 제공하는 인터페이스.
   * 인터페이스에 CRUD를  포함한 여러 메서드가 포함되어 있음.
   
### 6. 블로그 기획하고 API만들기 

1. domain(entity), requestDto, repository, service, restController 생성
2. contorller 메소드의 반환 타입을 ResponseEntity로 감싸서 reponse 의 httpStatus를 설정할 수 있다.
```java
public ResponseEntity<Article> addArticle(@RequestBody AddArticleRequest request){
        Article savedArticle = blogService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(savedArticle);
}
```
3. application.yml 에 H2 db 접속정보 추가
```java
spring:
  # h2 DB 접속정보
  datasource:
    url: jdbc:h2:mem:testdb

  # h2 DB툴을 브라우저에서 사용가능
  h2:
    console:
      enable: true
```
3. 구동 후 H2 실행, 브라우저에서 접속
```
h2 브라우저 콘솔 접속 시 주의점.
구동된 서버와 동일한 localhost:port/h2-console 로 접속해서 로그인해야함.
ex) localhost:8080/h2-console
```
5. POSTMAN에서 POST API 실행, h2 db에 테이블생성 및 데이터 저장 확인   
![postman](https://github.com/ironmask431/springboot3-guide/assets/48856906/840f7c6c-9b5f-4300-a18d-f708b0d8da25)
![h2](https://github.com/ironmask431/springboot3-guide/assets/48856906/ac034e32-0d7f-4d4c-8699-433d20c941cf)

6. api(controller) 테스트코드 작성
7. ObjectMapper 를 이용해 java객체 직렬화 (api요청 시 request 객체를 JSON타입 String으로 직렬화하여 request body(content)에 넣음)
```java
final AddArticleRequest request = AddArticleRequest.builder()
                .title(title)
                .content(content)
                .build();

//request 객체를 String (JSON 형태)으로 직렬화 
final String requestBody = objectMapper.writeValueAsString(request);

//when
ResultActions result = mockMvc.perform(post(url)
                               .contentType(MediaType.APPLICATION_JSON)
                               .content(requestBody));
```
8. blog CRUD Api 생성 + 테스트코드 완료

### 7. 블로그 화면 구성하기 (PASS) 
   
### 8. 스프링 시큐리티로 로그인,로그아웃 회원가입 만들기

1. 사전지식
* 스프링 시큐리티는 스프링키반의 애플리케이션 보안(인증,인가,권한)을 담당하는 스프링 하위 프레임워크이다.
* 인증(authentication) : 로그인시 사용자 확인 /  인가(authorization) : 사용자의 권한 확인
* 스프링시큐리티는 다음 공격을 방어 가능
* CSRF공격 : 사용자의 권한을 가지고 특정 동작 유도, 세션고정공격 : 사용자의 인증정보 탈취, 변조
* 스프링시큐리티는 다양한 필터구조로 동작   

4. build.gradle 의존성 추가

```java
implementation 'org.springframework.boot:spring-boot-starter-security'
testImplementation 'org.springframework.security:spring-security-test'
```
2. 스프링시큐리티 UserDetails를 implements 한 User 엔티티 만들기 > `User.java`
3. UserRepository 인터페이스 생성 > `UserRepository`
4. 스프링시큐리티 UserDetailsService 를 구현한 UserDetailService 생성 > `UserDetailService.java`
5. 스프링시큐리티 설정 파일 생성 > `WebSecurityConfig.java`
* 이 파일은 스프링부트 버전별로 각각 다른 부분들이 있다. 이프로젝트는 부트 `3.0.2` 기준!
* `configure()` > 시큐리티 기능 비활성화 할 경로 지정 (로그인,회원가입페이지 + 정적리소스 파일 경로)
* `filterChain()`  > url별, 권한별 인증, 인가 설정, 로그인, 로그아웃 후 이동할 url 지정
* `authenticationManager()` > 인증관리자 관련 설정 : 사용자정보를 가져올 서비스지정. 패스워드 인코더 지정
* 패스워드 인코더를 Bean으로 등록
* @Configuration + @Bean 어노테이션으로 아래 객체들을 Bean 으로 등록한다.
* WebSecurityCustomizer, SecurityFilterChain, AuthenticationManager, BCryptPasswordEncoder

7. 회원가입을 처리할 `AddUserRequest.java`, `UserService.java`, `UserApiController.java` 생성
8. 회원가입 뷰 컨트롤러, 뷰 파일 생성, `UserViewController.java`, `login.html`, `signup.html`, `articles.html`   
9. 회원가입, 로그인, 로그아웃, 로그아웃 후 인가 필요한 페이지 접근 시 login.html 로 redirect 확인
![로그인](https://github.com/ironmask431/springboot3-guide/assets/48856906/36137eb5-1d53-4273-982b-846b31530117)
![회원가입](https://github.com/ironmask431/springboot3-guide/assets/48856906/a63bc89e-deed-471e-9826-9e99619ae8d4)
![로그인성공](https://github.com/ironmask431/springboot3-guide/assets/48856906/4dee8e64-6227-438c-be13-018a0eaefc27)

### 9. JWT로 로그인/로그아웃 구현하기

1. 사전지식 : 토큰 기반 인증
   
   1. 토큰 기반 인증이란?
      * 대표적인 사용자 인증확인 방법 : 서버기반 인증 / 토큰 기반 인증
      * 스프링시큐리티에서는 기본적으로 세션기반 인증제공 (8장 로그인,로그아웃)
      * 토큰을 전달하고 인증받는 과정

        ```
        1. 클라 -> 서버 : 로그인요청(id/pw)
        2. 서버 -> 클라 : 토큰 생성 후 응답
        3. 클라 : 토큰 저장(쿠키)
        4. 클라 -> 서버 : 토큰과 함께 요청
        5. 서버 : 토큰 검증.
        6. 응답   
        ```

        ```
        * 토큰 기반 인증의 특징

        1. 무상태성 : 토큰이 서버가 아닌 클라이언트에 저장됨. 서버에서는 클라의 인증정보를
        저장하거나 유지하지 않아도 되므로 무상태로 서버자원없이 효율적인 검증 가능 (그러나 리프레쉬 토큰은 서버에 저장필요...ㅠ)

        2. 확장성 : 인증정보 상태관리를 하지 않아도 되므로 서버확장에 유리함.
        결제 서버와 주문서버가 분리되어 있을 경우 세션인증은 각각의 서버에 따로 인증을 해야 하지만, 토큰 인증은 하나의 토큰으로
        결제서버와 주문서버 둘다 인증이 가능하다. 다른 시스템에 접근해 로그인방식을 확장할 수도 있고 다른 서비스에 권한공유도 가능

        3. 무결성 : 토큰을 발급 한 후 토큰정보를 변경하는 행위를 할수 없으므로 무결성 보장
        토큰의 내용을 조금이라도 바꾸면, 서버의 토큰 유효성 검증에서 실패함.
        (서버만이 가지고 있는 KEY로 암호화된 서명(signiture)을 검증)
        ```
   2. JWT
      * jwt를 이용해 인증하려면 HTTP request의 헤더의 Authorization 값에 아래값을 넣어야함.

        ```
        Bearer 토큰값
        ```
      * JWT의 구조

         ```
         헤더.내용.서명
         aaaaa.bbbbb.cccccc
         ```

      * 헤더 : 토큰타입과 해싱알고리즘 지정

         ```
         {
            "typ": "JWT"   // 토큰의 타입지정 JWT고정
            "alg": "HS256" // 해싱 알고리즘 지정
         }
         ```

      * 내용 : 토큰과 관련된 정보. 내용의 한파트를 claim 이라고 부르며, key,value 한쌍으로 되어있음.
      * claim은 등록된 클레임, 공개클레임, 비공개클레임으로 나눌 수 있다.
        
         ```
         iss : 토큰 발급자 (issuer)
         sub : 토큰제목 (subject)
         aud : 토큰 대상자 
         exp : 토큰 만료시간 (expire) numbericDate 형식 (ex : 1480849147370)
         nbf : 토큰 활성시간. (not before) 이 시간이 지난 후부터 토큰사용 가능
         iat : 토큰 발급일시 (issued at)
         jti : jwt 고유식별자. 주로 일회용 토큰에 사용함.
         ```

      * 공개클래임은 공개되어도 상관없는 클레임. 충돌을 방지할수있도록 보통 URI로 지음.
      * 비공개클레임은 공개되면 안되는 클레임.  클라이언트와 서버간의 통신에 이용됨.
     
      * 내용 예시

         ```
         {
            "iss": "test@naver.com", // 등록된 클레임 (발급자)
            "iat": 1622370878, //등록된 클레임 (발급일시)
            "exp": 1622372678, //등록된 클레임 (만료일시)
            "https://www.test.com/is_admin" : true, //공개 클레임
            "email": "user@naver.com" // 비공개 클래임 
            "hello": "안녕하세요" // 비공개 클래임
         }
         ```

      * 서명 : 해당토큰이 조작,변경되지 않았음을 확인하는 용도.  헤더의 인코딩값과 내용의 인코딩값을 합한 후
      * 서버만이 가지고 있는 비밀키로 암호화하여 해시값을 생성함. 

      * 토큰의 위험성 : 만약 제3자가 토큰을 탈취 하여 사용할 경우 서버에서는 제3자의 요청인지 구분이 불가능함.
      * 토큰의 유효기한 : 그래서 토큰의 유효기한을 설정하여 이러한 문제를 어느정도보완함. 그러나 유효기한이 너무 짧으면
      * 사용자 입장에서 자주 로그인을 해줘야하기때문에 불편함. 이런 문제를 보완하고자 리프레쉬토큰이 탄생함.
      * 리프레쉬토큰 : 액세스토큰이 만료되었을 때 새로은 액세스토큰을 발급하기위해 사용됨.
     
      * 리프레쉬 토큰의 흐름

        ```
        1. 클라이언트 -> 서버 : 인증요청(로그인)
        2. 서버 -> 클라이언트 : 액세스토큰, 리프레쉬토큰 발급
        3. 서버 : 발급한 리프레쉬 토큰 db에 저장 (이부분은 좀 아쉽다.)
        4. 클라이언트 : 발급받은 엑세스토큰, 리프레쉬토큰 쿠키에 저장
        5. 클라이언트 -> 서버 : 액세스토큰으로 요청
        6. 서버 -> 클라이언트 :  토큰 검증 후 응답
        7. 클라이언트 -> 서버 : 만료된 액세스토큰으로 요청
        8. 서버 -> 클라이언트 :  토큰 만료 응답
        9. 클라이언트 -> 서버 : 리프레쉬토큰과 함께 엑세스토큰 발급 요청
        10. 서버 : 리프레시 토큰 유효성 검사 (db에 저장해둔 리프레시토큰과 일치하는지 확인)
        11. 서버 -> 클라이언트 : 엑세스토큰 신규 발급
        ```

2. JWT 서비스 구현하기
   1. 의존성 추가하기 > `build.gradle`
      ```
      //jwt 라이브러리 // mavenRepository 에서 조회 (JSON Web Token Support For The JVM)
      implementation 'io.jsonwebtoken:jjwt:0.9.1'
      // xml문서와 JAVA객체 매핑 자동화  // mavenRepository 에서 조회
      implementation 'javax.xml.bind:jaxb-api:2.3.1'
      ```

   2. 토큰 제공자 추가
      * 발급자, 비밀키 정보 설정 > application.yml
        
         ```
         jwt:
           issuer: test@gmail.com
           secretKey: study-springboot
         ```

      * application.yml 에 지정한 값 조회하기위한 클래스 생성 > `JwtProperties.java`

        ```java
        * 프로퍼티의 값을 조회하는 방법

        1. 방법1
        - 클래스 생성 후 클래스에 @ConfigurationProperties("jwt")  선언 (@Setter 필수) 후 빈등록

         @Setter
         @Getter
         @Component
         @ConfigurationProperties("jwt") // application.yml의 "jwt" 프로퍼티값을 읽어옴
         public class JwtProperties {
             private String issuer;
             private String secretKey;
         }

        2. 방법2
        - 클래스 생성자에서 @Value 어노테이션 사용

        public TokenProvider(@Value("${jwt.issuer}") String issuer,
                             @Value("${jwt.secretKey}") String secretKey) {
           //@Value 어노테이션으로 applicationl.yml 의 프로퍼티 값 조회
           this.issuer = issuer;
           this.secretKey = secretKey;
        }
        
        ```
      * 토큰 생성, 유효성검사, 토큰에서 정보가져오기 기능을 위한 클래스생성 > `TokenProvier.java`
      * TokenProvier 테스트 클래스 생성 > `TokenProviderTest.java`
        ![캡처](https://github.com/ironmask431/springboot3-guide/assets/48856906/96c46c6f-e4c6-4072-8a5b-c5bc96c3de95)

   3. 리프레시 토큰 도메인 구현하기
      * 리프레시 토큰은 DB에 저장하는 정보이므로 entity 와 repository 를 추가해준다.
      * `RefreshToken.java`, `RefreshTokenRepository`

   4. 토큰 필터 구현하기
      * `TokenAuthenticationFilter.java` (extends OncePerRequestFilter)
      * OncePerRequestFilter : 1번의 request에 filter 처리를 하는 클래스
      * 요청의 헤더값의 토큰을 확인하여 유효하면 securityContextHolder 에 인증정보를 저장함.
      * securityContext 는 인증 객체를 저장하는 보관소임. 인증정보를 저장, 조회할 수 있다.
      * securityContext 객체를 저장하는 객체가 securityContextHolder.
      * Http Request 에서 액세스토큰값이 담긴 Authorization 헤더값을 가져온 뒤 엑세스토큰이 유효하다면 인증정보를 저장함.

3. 리프레시 토큰 발급 API 구현하기
   * refreshToken 을 받아 유효성 검증 후 새로운 accessToken을 발급해주는 api 개발 

   1. 토큰 서비스 추가
      * `UserService.java` 에 findById() 메소드 추가
      * `RefreshTokenService.java` 생성 > 전달받은 리프레시 토큰을 db에서 조회해본다.
      * `TokenService.java` > 리프레시토큰을 받아 유효성 검증하고, 신규 accessToken을 발급해준다.
        
   2. API 컨트롤러  추가
      * request, response dto 객체 추가 - `CreateAccessTokenRequest.java`, `CreateAccessTokenResponse.java`
      * api 컨트롤러 추가 - `TokenApiController.java`
      * api 컨트롤러 테스트 추가 - `TokenApiControllerTest.java`

### 10. OAuth2 로그인 구현하기

1. OAuth 사전지식
   1. OAuth 란?
      
      ```
      * OAuth 란 제 3의 서비스에게 계정관리를 맡기는 방식. (ex. 네이버 , 구글로 로그인하기)

      <OAuth 관련 용어>
      1. 리소스 오너 : 자신의 정보를 사용하도록 인증ㅅ어버에 허가하는 주체 (사용자)
      2. 리소스 서버 : 리소스오너 의 정보를 가지고있고, 보호하는 주체 (네이버, 구글, 카카오)
      3. 인증 서버 : 클라이언트에게 리소스오너의 정보에 접근할 수 있는 토큰을 발급하는 역할
      4. 클라이언트 : 인증서버에 인증을 받고 리소스오너의 리소스를 사용하는 주체. 우리가 개발하고있는 애플리케이션.
      ```

      ```
      <리소스 오너 정보를 취득하는 4가지 방법

      1. 권한 부여 코드 승인타입 : OAuth 2.0에서 가장 널리알려진 인증방법.
      : 권한에 접근할 수 있는 코드와 리소스 오너에 대한 액세스토큰을 발급 받는 방식 

      나머지 타입은 생략.
      ```
   2. 권한부여 코드 승인 타입이란?

      ```
      <권한 부여 코드 승인타입의 흐름>

      1. 리소스오너 -> 애플리케이션 : 권한요청

      ```

      1. 권한요청

         ```
         * 애플리케이션 -> 구글 로 권한요청을 보냄.
         
         애플리케이션 서버가 특정 사용자 데이터에 접근하기 위해 리소스서버(구글)에 요청을 보내는것.
        

         <권한 요청 예시>
         [GET] google-authorization-server.example/authorize?
         client_id=344qwei3
         &redirect_id=http://localhost:8080/myapp
         &response_type=code
         &scope=profie

         * client_id : 인증서버가 클라이언트에 할당한 고유 식별자.
         * redirect_id : 로그인 성공시 이동할 애플리케이션 URL
         * response_type : 애플리케이션이 제공받길 원하는 응답타입 (code : 인증코드)
         * scope : 제공받고자 하는 리소스오너의 정보목록
         ```

      2. 데이터 접근용 권한 부여

         ```
         * 사용자가 구글 로그인페이지에서 로그인 진행 

         인증서버에 처음 요청을 보낼 시 사용자에게 보이는 페이지를 구글 로그인페이지로 변경하고,
         사용자로 부터 데이터에 접근 동의를 받습니다. 이 과정은 최초 1회만 진행됩니다.
         이후로는 동의 없이 로그인만 진행합니다. 
         ```

      3. 인증 코드 제공

         ```
         * 로그인 성공 시 구글 -> 애플리케이션 으로 redirect 됨. 파라미터에 인증코드를 실어서 보냄.

         인증코드 예시
         [GET]http://localhost:8080/myapp?code=123456
         ```

      4. 액세스토큰 응답

         ```
         * 인증코드를 받은 후 애플리케이션 -> 구글로 액세스토큰을 요청함.
         
         
      

      

   
