# Spring Boot 연습(sbb)

### 1.ssb(Simple Board Builder)라는 스프링 부트 프로젝트의 기본 구조 연습입니다.

### 2.애플리케이션 설정
- *`spring.application.name`*: 애플리케이션의 이름을 설정합니다.
- *`server.port`*: 서버의 포트를 설정합니다.(기본값 8080)
- *`server.servlet.context-path`*: 애플리케이션의 컨텍스트 경로를 `/mysite`로 설정합니다.

**데이터베이스 설정**
- **H2데이터베이스 콘솔**
    - `spring.h2.console.enabled=true`로 H2 콘솔을 활성화합니다.
    - 콘솔 URL은 *`/h2-console`*로 설정됩니다.
- **데이터 소스**
    - URL:`jdbc:h2:~/local`
    - 드라이버:`org.h2.Driver`
    - 사용자명:`sa`,비밀번호(기본값은 빈 문자열).
- **JPA 설정**
    - SQL 로그 출력 활성화: *`spring.jpa.show-sql=true`*
    - 하이버네이트 방언: *`org.hibernate.dialect.H2ialect`*
    - 스키마 관리: *`spring.jap.hibernate.ddl-auto=update`*
<hr>

### 3.핵심 구성요소
1.**엔티티**:`Question`
  - 질문을 나타내는 클래스입니다. 필드:*`id, subject, content, createDate`*
  - *`Answer`* 엔티티와 일대다 관게를 가집니다.
    
2.**엔티티**:`Answer`
  - 답변을 나타내는 클래스입니다. 필드:*`id, content, createDate`*
  - *`Questuon`* 엔티티와 다대일 관게를 가집니다.
   
3.**리포지토리**
  - *`QuestuonRepository: subject`* 와 `content`로 `Question`을 찾는 커스텀 메소드 제공.
  - *`AnswerRepository:`* *`Answer`*에 대한 CRUD 작업을 제공합니다.
    
4.**컨트롤러**
  - *`HelloController`*
        - 기본적인 정적 문자열을 반환하는 엔드포인트들.
  - *`MainController`*
        - 간단한 인사말을 반환하는 엔드포인트 *`/sbb`* -> "안녕하세요 sbb에 오신것을 환영합니다".
    
5.**서비스 레이어**
    - Spring Data JPA를 활용하여 리포지토리를 통해 데이터를 관리하고 처리합니다.
<hr>

### 4.코드 예시
 - **엔티티 예시**

    
```java
@Entity
@Getter
@Setter
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 200)
    private String subject;

    @Column(columnDefinition = "Text")
    private String content;

    private LocalDateTime createDate;

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    private List<Answer> answerList;
}
```

 - **커스텀 리포지토리 예시**
```java
public interface QuestionRepository extends JpaRepository<Question, Integer> {
    Question findBySubject(String subject);
    Question findBySubjectAndContent(String subject, String content);
    List<Question> findBySubjectContaining(String subject);
}
```


- **`testJpa`**:데이터 삽입 예제.
    - `Question` 엔티티를 생성하고 저장(`save`)하는 방식
```java
@Test
void testJpa() {
    Question q1 = new Question();
    q1.setSubject("sbb가 무엇일까요?");
    q1.setContent("sbb에 대해서 알고 싶습니다.");
    q1.setCreateDate(LocalDateTime.now());
    this.qRepo.save(q1);
}
```
- **`testFind`**:데이터 조회 예제.
    - `findAll` 메소드로 모든 데이터를 조회하고, `findById`로 특정 ID의 데이터를 조회하는 방식.
```java
  @Test
    void testFind() {
        List<Question> qList = this.qRepo.findAll();
        for (Question q : qList) {
            System.out.println(q.getSubject());
        }
        
        //Id로 질문을 찾기 이때 메소드는 Optional<타입> 으로 리턴됨(못찾을 경우도 포함)
        Optional<Question> q1 = this.qRepo.findById(1);
        if(q1.isPresent()){ //q1객체가 있을경우(찾았을경우)
            Question q = q1.get(); //get() 메소드로 가져옴
            System.out.println(q.getContent());
        }
    }

```
- **`testFindBy`**:특정 조건으로 데이터 조회 예제.
  - `findBySubject`메소드로 제목을 기반으로 데이터를 조회
```java
  @Test
    void testFindBy(){
        Question q1 = this.qRepo.findBySubject("sbb가 무엇일까요?");
        System.out.println(q1.getContent());
    }
```
- **`testFindByContaining`**:제목에 특정 문자열이 포함된 데이터를 검색
  - `findBySubjectContaining`메소드를 사용.
```java
@Test
    void testFindByContaining(){
        List<Question> qlist = this.qRepo.findBySubjectContaining("sbb");
        for (Question q : qlist) {
            System.out.println(q.getSubject());
        }
    }
```

- **`testInput`**:새 데이터를 삽입
  - ID가 없는 엔터티를 저장하여 새로운 레코드 추가.
```JAVA
@Test
    void testInput() {
        Question q1 = new Question();
        q1.setSubject("새로운 sbb 제목");
        q1.setContent("새로운 내용입니다.");
        q1.setCreateDate(LocalDateTime.now());
        this.qRepo.save(q1); //id가 없으므로 입력이됨!
    }
```

- **`testUpdate`**:데이터 수정
  - 특정 ID의 데이터를 가져와 수정 후 저장.
```JAVA
 void testUpdate(){
       Optional<Question> oq = qRepo.findById(1);
       Question q= oq.get();
       q.setSubject("수정된 제목");
       this.qRepo.save(q); //입력과 수정은 같은 save 메소드 사용, 이때 id가 있으면 수정으로 인식한다.
    }
```
- **`testDelete`**:데이터 삭제
  - 특정 ID의 데이터를 삭제.
```JAVA
@Test
    void testDelete(){
        this.qRepo.deleteById(3);
    }
```
<hr>

### 5.구현된 기능들
- **스프링 부트 설정**: 서버 및 데이터베이스 설정을 `application.properties`에서 관리합니다.
- **Spring Data JPA**: `JpaRepository`를 통해 CRUD 작업과 커스텀 쿼리 메소드 제공.
- **엔티티 관계 매핑**: *`@OneToMany`*,*`@ManyToOne`* 어노테이션을 통해 엔티티 간 관계를 매핑.
- **단위 테스트**: 스프링 부트 테스트 프레임워크를 이용한 테스트 코드 작성 예시.
- **Lombok활용**: *`@Getter`*, *`@Setter`*, *`RequredArgsConstructor`* 등의 어노테이션을 사용하여 코드 간결화.



