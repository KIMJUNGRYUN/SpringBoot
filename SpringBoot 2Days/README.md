## Q&A 게시판 만들기
### 연습 소개
이 프로젝트는 Spring Boot를 사용하여 구현한 간단한 Q&A (질문과 답변) 웹 애플리케이션입니다. 사용자는 질문을 작성하고, 다른 사용자가 답변을 추가할 수 있습니다. Thymeleaf를 사용하여 HTML 템플릿을 렌더링하고, Spring Data JPA로 데이터베이스와 상호작용합니다.

<hr>

## 프로젝트의 주요 역할 및 코드 설명
### 1.**`Question` 엔티티**
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
**설명:**
- 이 클래스는 질문 데이터를 데이터베이스 테이블에 매핑.
- 주요 필드:
      - `id`: 질문의 고유 식별자 (기본키).
  
      - `subject`: 질문 제목.
  
      - `content`: 질문 내용.
  
      - `createDate`: 질문이 생성된 시간.
  
      - `answerList`: 이 질문에 연결된 답변 목록. **일대다 관계**로 설정됨.
- `@OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE):
      - 이 질문이 삭제되면, 연결된 답변도 함께 삭제됨.
<hr>

### 2.**`Answer` 엔티티**
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
- 이 클래스는 **답변 데이터**를 데이터베이스 테이블에 매핑.
- 주요 필드:
      - `id`: 답변의 고유 식별자(기본키).
      - `content`: 답변 내용.
      - `createDate`: 답변이 생성된 시간.
      - `question`: 이 답변이 속한 질문과의 관게를 나타냄.
- `@ManyToOne`:
      - 여러 답변이 하나의 질문에 연결될 수 있는 관계를 정의.
<hr>

### 3.**`QuestionController`**
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
**설명**:
- `/list`:
      - 질문 목록을 요청받고 서비스 계층에서 질문 데이터를 가져옴.
      - `model.addAttrubute`로 데이터를 Thymeleaf 템플릿 (`q_list.html`)로 전달.
- `/detail/{id}`:
      - 특정 질문의 상세 내용을 표시함.
      - URL 경로에서 `id` 값을 받아 질문을 조회한 뒤, `q_detail.html`로 데이터 전달함.
- `/create`:
      - 질문 작성 폼을 렌더링하거나 새 질문을 저장함.
      - `@Valid`로 유효성을 검사하며, 실패 시 다시 폼 페이지를 반환함.

<hr>


### 4.**`AnswerController`**
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
**설명**:
- 특정 질문에 답변을 추가하는 컨트롤러.
- `/create/{id}`:
      - URL에서 `{id}`를 받아 해당 질문을 조회함
      - 답변의 내용 (`content`)을 받아와 `AnswerService`를 통해 저장함.
      - 답변 등록 후, 다시 질문 상세 페이지로 리다이렉트함.

<hr>

### 5.**`QuestionService`**
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
**설명**:
- **서비스 계층**으로, 비지니스 로직을 처리함
- `getList`:
      - 모든 질문 데이터를 반환함.
- `getQuestion`:
      - `id`로 질문을 조회하며, 데이터가 없으면 `DataNotFoundException`을 발생시킴.
<hr>

### 6.**`AnswerService`**
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
**설명**:
- 답변 데이터를 저장하는 역할을 합니다.
- `create`:
      - 새로운 답변 객체를 생성하고, 해당 질문에 연결하여 데이터베이스에 저장함.

<hr>

### 7.**`HTML 템플릿`**
**질문 목록 (`q_list.html`)**
```java
<table class="table">
    <thead>
        <tr><th>번호</th><th>제목</th><th>작성일시</th></tr>
    </thead>
    <tbody>
        <tr th:each="q : ${qList}">
            <td th:text="${q.id}"></td>
            <td><a th:href="@{/question/detail/${q.id}}" th:text="${q.subject}"></a></td>
            <td th:text="${q.createDate}"></td>
        </tr>
    </tbody>
</table>
<a th:href="@{/question/create}" class="btn btn-primary">질문 등록하기</a>
```
**설명**:
- 모든 질문을 테이블 형식으로 출력함.
- 제목을 클릭하면 질문 상세 페이지로 이동함
- 질문 등록 버튼을 제공함

<hr>


**질문 상세 (`q_detail.html`)**
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
**설명**:
- 질문의 제목과 내용을 출력함
- 새로운 답변을 입력할 수 있는 폼을 제공.
- 
<hr>
