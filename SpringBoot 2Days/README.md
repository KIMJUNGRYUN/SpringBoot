## Q&A 게시판 만들기
### 연습 소개
이 프로젝트는 Spring Boot를 사용하여 구현한 간단한 Q&A (질문과 답변) 웹 애플리케이션입니다. 사용자는 질문을 작성하고, 다른 사용자가 답변을 추가할 수 있습니다. Thymeleaf를 사용하여 HTML 템플릿을 렌더링하고, Spring Data JPA로 데이터베이스와 상호작용합니다.

### 주요 기능
1. 질문 목록 조회
2. 질문 상세 보기
3. 질문 등록
4. 답변 작성 및 조회
5. 기본적인 예외 처리

<hr>

### 프로젝트 구조
### 1. 엔티티 (Entity)
**`Question` 엔티티**
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
- 질문의 제목,내용, 작성일 등을 저장합니다.
- 여러 답변(`Answer`)과 연결됩니다.

**`Answer` 엔티티**
```java
@Entity
@Getter
@Setter
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "Text")
    private String content;

    private LocalDateTime createDate;

    @ManyToOne
    private Question question;
}
```
- 특정 질문에 대한 답변 데이터를 저장합니다.
<hr>

### 2.컨트롤러
`QuestionController`
```java
@Controller
@RequestMapping("/question")
public class QuestionController {
    @Autowired
    private QuestionService qService;

    @RequestMapping("/list")
    public String list(Model model) {
        List<Question> qList = qService.getList();
        model.addAttribute("qList", qList);
        return "q_list"; 
    }

    @RequestMapping("/detail/{id}")
    public String detail(@PathVariable int id, Model model) {
        Question q = this.qService.getQuestion(id);
        model.addAttribute("q", q);
        return "q_detail";
    }

    @GetMapping("/create")
    public String create(QuestionForm questionForm) {
        return "q_form";
    }

    @PostMapping("/create")
    public String qcreate(@Valid QuestionForm questionForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "q_form";
        }
        return "redirect:/question/list";
    }
}
```
- 질문 목록 조회, 질문 상세 보기, 질문 등록 폼과 등록 처리를 담당합니다.

`AnswerController`
```java
@Controller
@RequestMapping("/answer")
public class AnswerController {
    @Autowired
    private QuestionService qService;
    @Autowired
    private AnswerService aService;

    @PostMapping("/create/{id}")
    public String createAnswer(@PathVariable int id, @RequestParam String content) {
        Question q = this.qService.getQuestion(id);
        this.aService.create(q, content);
        return "redirect:/question/detail/" + id;
    }
}
```
- 특정 질문에 대한 답변 등록 요청을 처리합니다.
<hr>

### 3.서비스
`QuestionService`
```java
@Service
public class QuestionService {
    @Autowired
    private QuestionRepository qRepo;

    public List<Question> getList() {
        return this.qRepo.findAll();
    }

    public Question getQuestion(Integer id) {
        Optional<Question> q = this.qRepo.findById(id);
        if (q.isPresent()) {
            return q.get();
        } else {
            throw new DataNotFoundException("question not found");
        }
    }
}
```
- 질문 생성, 조회, 목록 가져오기 등의 비지니스 로직을 처리합니다.

`AnswerService`
```java
@Service
public class AnswerService {
    @Autowired
    private AnswerRepository aRepo;

    public void create(Question q, String content) {
        Answer a = new Answer();
        a.setQuestion(q);
        a.setContent(content);
        a.setCreateDate(LocalDateTime.now());
        this.aRepo.save(a);
    }
}
```
- 답변을 저장하는 로직을 처리합니다.

<hr>

### 4.리포지토리
`QuestionRepository`
```java
public interface QuestionRepository extends JpaRepository<Question, Integer> {
    Question findBySubject(String subject);
    List<Question> findBySubjectContaining(String subject);
}
```
`AnswerRepository`
```java
public interface AnswerRepository extends JpaRepository<Answer, Integer> {}
```
<hr>

### 5.템플릿
`q_list.html` **(질문 목록)**
```java
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head th:replace="layout::head"></head>
<body>
<div class="container my-3">
    <table class="table">
        <thead class="table-dark">
        <tr>
            <th>번호</th>
            <th>제목</th>
            <th>작성일시</th>
        </tr>
        </thead>
        <tbody>
        <tr th:each="q, loop : ${qList}">
            <td th:text="${loop.count}"></td>
            <td><a th:href="@{/question/detail/__${q.id}__}" th:text="${q.subject}"></a></td>
            <td th:text="${#temporals.format(q.createDate, 'yyyy-MM-dd HH:mm')}"></td>
        </tr>
        </tbody>
    </table>
    <a th:href="@{/question/create}" class="btn btn-primary">질문 등록하기</a>
</div>
</body>
</html>
```
`q_detail.html` **(질문 상세 보기)**
```java
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head th:replace="layout::head"></head>
<body>
<div class="container my-3">
    <h2 th:text="${q.subject}"></h2>
    <div th:text="${q.content}"></div>
    <form th:action="@{/answer/create/${q.id}}" method="post">
        <textarea name="content" class="form-control"></textarea>
        <input type="submit" value="답변 등록" class="btn btn-primary">
    </form>
</div>
</body>
</html>
```
<hr>
