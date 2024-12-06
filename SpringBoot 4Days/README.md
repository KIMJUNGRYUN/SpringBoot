# Spring Boot 연습(sbb)

**Login**
- `스프링 스큐리티 사용`

**로그인 URL**
```spring
  http
                .authorizeHttpRequests((authorizeHttpRequests)-> authorizeHttpRequests
                        .requestMatchers(new AntPathRequestMatcher("/**")).permitAll())
                .csrf((csrf) -> csrf
                        .ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**")))
                .headers((headears) -> headears
                        .addHeaderWriter(new XFrameOptionsHeaderWriter(
                                XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
                .formLogin((formLogin) -> formLogin
                        .loginPage("/user/login")
                        .defaultSuccessUrl("/"))           
        ;
        return http.build();
```
- 로그인 페에지의 **URL**은 `/user/login`이다
  - 로그인 성공시에 이동하는 **default** 페이지는 루티 `URL(/)`임을 의미.

**UserController**
- 스프링 시큐리티에 로그인 URL을 /user/login으로 설정했으니 `User Controller`에 해당 매핑 추가
```spring
@GetMapping("/login")
    public String login() {
        return "login_form";
    }
```
- `login_form.html` 템플릿을 렌더링하는 GET 방식의 login 메서드를 추가.
- 실제 로그인을 진행하는 `@PostMapping` 방식의 메서드는 스프링 시큐리티가 대신 처리하므로 직접 구현할 필요가 없음.

**login_form.html**
```html
      <!-- 여기부터 시작 -->
      <form th:action="@{/user/login}" method="post">
        <div th:if="${param.error}">
          <div class="alert alert-danger">사용자ID 또는 비밀번호를 확인해 주세요.</div>
        </div>
        <div class="mb-3">
          <label for="username" class="form-label">사용자ID</label>
          <input type="text" name="username" id="username" class="form-control" />
        </div>
        <div class="mb-3">
          <label for="password" class="form-label">비밀번호</label>
          <input type="password" name="password" id="password" class="form-control" />
        </div>
        <button type="submit" class="btn btn-primary">로그인</button>
      </form>
```
- 사용자ID와 비밀번호로 로그인을 할 수 있는 로그인 템플릿을 작성

- 시큐리티의 로그인이 실패할 경우에는 로그인 페이지로 다시 리다이렉트.

- 로그인 페이지의 파라미터로 error가 전달될 경우 "사용자ID 또는 비밀번호를 확인해 주세요." 라는 오류메시지를 출력

`로그인 실패시 파라미터로 error가 전달되는 것은 스프링 시큐리티의 규칙이다.`

![Login](https://github.com/user-attachments/assets/9b5b1fef-1973-4d8d-934a-31d04566ffa4)


**UserRepository**
- 작성할 `UserSecurityService`는 사용자를 조회하는 기능이 필요하므로 다음처럼 findByusername 메서드를 User 리포지터리에 추가.


```spring
public interface UserRepository extends JpaRepository<SiteUser, Long> {
	Optional<SiteUser> findByusername(String username);
}
```

**UserRole**
- 스프링 시큐리티는 인증 뿐만 아니라 권한도 관리한다

- 인증후에 사용자에게 부여할 권한이 필요하다. 다음과 같이 `ADMIN`, `USER` 2개의 권한을 갖는 `UserRole`을 신규로 작성
```spring
@Getter
public enum UserRole {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");

    UserRole(String value) {
        this.value = value;
    }

    private String value;
}
```
- **UserRole**은 열거 자료형(`enum`)으로 작성했다. 
- **ADMIN**은 `"ROLE_ADMIN"`, **USER**는 `"ROLE_USER"` 라는 값을 가지도록 했다
- 상수 자료형이므로 `@Setter`없이 `@Getter`만 사용가능하도록 했다.

<hr>

**UserSecurityService**
- 스프링 스큐리티 설정 등록할 서비스
```spring
@Service
public class UserSecurityService implements UserDetailsService {

	@Autowired
	private UserRepository userRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<SiteUser> _siteUser = this.userRepo.findByusername(username);
        if (_siteUser.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을수 없습니다.");
        }
        SiteUser siteUser = _siteUser.get();
        
        List<GrantedAuthority> authorities = new ArrayList<>();
        if ("admin".equals(username)) {
            authorities.add(new SimpleGrantedAuthority(UserRole.ADMIN.getValue()));
        } else {
            authorities.add(new SimpleGrantedAuthority(UserRole.USER.getValue()));
        }
        return new User(siteUser.getUsername(), siteUser.getPassword(), authorities);
	}

}
```
- 스프링 시큐리티에 등록하여 사용할 **UserSecurityService**는 스프링 시큐리티가 제공하는 **UserDetailsService** 인터페이스를 구현(implements)해야 한다.
- **loadUserByUsername** 메서드를 구현하도록 강제하는 인터페이스이다. **loadUserByUsername** 메서드는 사용자명으로 비밀번호를 조회하여 리턴하는 메서드이다.

<hr>

`SecurityConfig`
- 스프링 시큐리티에 UserSecurityService 등록
```spring
@Autowired
	private UserSecurityService userSecurityService;
    (... 생략 ...)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
```
- **AuthenticationManager** 빈을 생성
- **AuthenticationManager**는 스프링 시큐리티의 인증을 담당.
- **AuthenticationManager** 빈 생성시 스프링의 내부 동작으로 인해 위에서 작성한 UserSecurityService와 PasswordEncoder가 자동으로 설정

**navbar.html**
- 로그인 페이지에 진입할수 있는 로그인 링크를 네비게이션바에 다음과 같이 추가
```html
   <li class="nav-item">
                    <a class="nav-link" th:href="@{/user/login}">로그인</a>
                </li>
```
![Login2](https://github.com/user-attachments/assets/253a9989-704b-461e-bd6c-4426a16d786b)

<hr>


**로그인/로그아웃 링크**
- 로그인한 후에도 내비게이션 바에는 여전히 **"로그인"** 링크가 남아 있다. 로그인을 한 상태라면 이 링크는 **"로그아웃"** 링크로 바뀌어야 한다.
- 반대로 로그아웃 상태에서는 **"로그인"** 링크로 바뀌어야 한다.
`사용자의 로그인 여부는 타임리프의 sec:authorize 속성을 통해 알수 있음.`
  - sec:authorize="isAnonymous()" - 이 속성은 로그인 되지 않은 경우에만 해당 엘리먼트가 표시되게 한다.
  - sec:authorize="isAuthenticated()" - 이 속성은 로그인 된 경우에만 해당 엘리먼트가 표시되게 한다.

**layout.html**
```html
  <li class="nav-item">
              <a class="nav-link" sec:authorize="isAnonymous()" th:href="@{/user/login}">로그인</a>
              <a class="nav-link" sec:authorize="isAuthenticated()" th:href="@{/user/logout}">로그아웃</a>
            </li>
            <li class="nav-item">
              <a class="nav-link" th:href="@{/user/signup}">회원가입</a>
            </li>
```
- 로그인일 안한 상태라면 **sec:authorize="isAnonymous()** 가 참이되어 "로그인" 링크가 표시되고 로그인을 한 상태 **sec:authorize="isAuthenticated()**가 참이되어 "로그아웃" 링크가 표시될 것이다. 

![Logout](https://github.com/user-attachments/assets/02596935-923e-4c80-b669-661837fc335a)

- 로그아웃 링크는 /user/logout으로 지정했다. 하지만 로그아웃 기능은 아직 구현하지 않은 상태이다. 로그아웃 기능은 바로 이어서 진행한다.

<hr>

**로그아웃 구현하기**
- 스프링 시큐리티를 사용하여 구현

**SecurityConfig**
```spring
.logout((logout) -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true))
;
```
- 로그아웃을 위한 설정을 추가
- 로그아웃 URL을 /user/logout으로 설정하고 로그아웃이 성공하면 루트(/) 페이지로 이동하도록 했다
- 로그아웃시 생성된 사용자 세션도 삭제하도록 처리했다.

<hr>

**엔티티 변경 글쓴이 추가**
- Question과 Answer 엔티티에 "글쓴이"에 해당되는 author 속성을 추가
**Question 속성 추가**
  ```spring
  public class Question {
    (... 생략 ...)

    @ManyToOne
    private SiteUser author;
}
```


- author 속성은 SiteUser 엔티티를 **@ManyToOne**으로 적용

- 여러개의 질문이 한 명의 사용자에게 작성될 수 있으므로 **@ManyToOne** 관계가 성립한다.

**Answer 속성 추가**
```spring
public class Answer {
    (... 생략 ...)

    @ManyToOne
    private SiteUser author;
}
```

![db](https://github.com/user-attachments/assets/0d69ca03-b171-4fa5-855d-caec6a039c05)

-  이 컬럼에는 site_user 테이블의 id 값이 저장되어 **SiteUser** 엔티티와 연결된다

<hr>

**author 저장**
- Question, Answer 엔티티에 author 속성이 추가되었으므로 질문과 답변 저장시에 author도 함께 저장할 수 있다.

**AnswerController**
```spring
  @PostMapping("/create/{id}")
    public String createAnswer(Model model, @PathVariable("id") Integer id, 
            @Valid AnswerForm answerForm, BindingResult bindingResult, Principal principal) {
        (... 생략 ...)
    }
```

<hr>

**UserService 추가**
```spring
 public SiteUser getUser(String username) {
        Optional<SiteUser> siteUser = this.userRepo.findByUsername(username);
        if (siteUser.isPresent()) {
            return siteUser.get();
        } else {
            throw new DataNotFoundException("siteuser not found");
        }
    }
```
- UserRepository에 이미 findByusername을 선언했으므로 쉽게 만들수 있음.

<hr>

**AnswerService 수정**
```spring
    public void create(Question question, String content, SiteUser author) {
        Answer answer = new Answer();
        answer.setContent(content);
        answer.setCreateDate(LocalDateTime.now());
        answer.setQuestion(question);
        answer.setAuthor(author);
        this.aRepo.save(answer);
    }
```
- create 메서드에 SiteUser 객체를 추가로 전달받아 답변 저장시 author 속성에 세팅.

**AnswerController(`createAnswer`)**
```spring
@Autowired
	private UserService uService;
	
	@PostMapping("/create/{id}")
	public String createAnswer(Model model, @PathVariable("id") Integer id,
							@Valid AnswerForm answerForm, BindingResult result, Principal principal) {
		Question question = this.qService.getQuestion(id);
		SiteUser siteUser = this.uService.getUser(principal.getName()); //추가
		if(result.hasErrors()) {
			model.addAttribute("question", question);
			return "question_detail";
		}
		this.aService.create(question, answerForm.getContent(), siteUser); //수정
		return String.format("redirect:/question/detail/%s", id);
	}
```
- principal 객체를 통해 사용자명을 얻은 후에 사용자명을 통해 SiteUser 객체를 얻어서 답변을 등록하는 AnswerService의 create 메서드에 전달하여 답변을 저장 하도록 함.

<hr>

**질문에 작성자 저장하기**
`QuestionService`
```srping
public void create(String subject, String content, SiteUser user) {
        Question q = new Question();
        q.setSubject(subject);
        q.setContent(content);
        q.setCreateDate(LocalDateTime.now());
        q.setAuthor(user);
        this.qRepo.save(q);
    }
```

`QuestionController`
```srping
@Autowired
	private UserService uService;

    @PostMapping("/create")
    public String questionCreate(@Valid QuestionForm questionForm,  
    							 BindingResult result, Principal principal) {
    	
    	if (result.hasErrors()) {
            return "question_form";
        }
    	SiteUser siteUser = this.uService.getUser(principal.getName());
    	this.qService.create(questionForm.getSubject(), questionForm.getContent(), siteUser);
        return "redirect:/question/list";
    }
```
- QuestionService의 create 메서드의 매개변수로 SiteUser가 추가되었기 때문에 이전에 작성한 테스트 케이스가 오류가 발생할 것. 테스트 케이스의 오류를 임시 해결하기 위해 다음과 같이 수정.
- 
```spring
this.qService.create(subject, content, null);
```

<hr>

**로그인이 필요한 메서드**
![error](https://github.com/user-attachments/assets/dcb2ea9c-1d63-43e6-9b2a-a01688b1e34b)
- principal 객체가 널(`null`)값이라서 발생한 오류 principal 객체는 로그인을 해야만 생성되는 객체이기 때문.
- **@PreAuthorize("isAuthenticated()")** 애너테이션을 사용해야함.
- **@PreAuthorize("isAuthenticated()")** 애너테이션이 붙은 메서드는 로그인이 필요한 메서드를 의미.
- 만약 *@PreAuthorize("isAuthenticated()")** 애너테이션이 적용된 메서드가 로그아웃 상태에서 호출되면 로그인 페이지로 이동.

`QuestionController`
```spring
@PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String questionCreate(QuestionForm questionForm) {
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String questionCreate(@Valid QuestionForm questionForm, 
            BindingResult bindingResult, Principal principal) {
        (... 생략 ...)
    }
```
- 로그인이 필요한 메서드들에 **@PreAuthorize("isAuthenticated()")** 애너테이션을 적용.

`AnswerController 수정`
```spring
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}")
    public String createAnswer(Model model, @PathVariable("id") Integer id, @Valid AnswerForm a
```

- 그리고 **@PreAuthorize** 애너테이션이 동작할 수 있도록 **SecurityConfig**를 다음과 같이 수정.
```spring
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    (... 생략 ...)
}
```
<hr>

**disabled**
- 답변 등록은 로그아웃 상태에도 글을 작성할 수 있게 보여질 수 있어 답변을 못하게 disabled 속성 적용.
```html
 <textarea sec:authorize="isAnonymous()" disabled th:field="*{content}" class="form-control" rows="10"></textarea>
        <textarea sec:authorize="isAuthenticated()" th:field="*{content}" class="form-control" rows="10"></textarea>
```
- 로그인 상태가 아닌 경우 **textarea** 태그에 **disabled** 속성을 적용하여 입력을 못하게 만들었다. 
- **sec:authorize="isAnonymous()"**, **sec:authorize="isAuthenticated()"** 속성은 현재 사용자의 로그인 상태를 체크하는 속성이다.
 - **sec:authorize="isAnonymous()"** - 현재 로그아웃 상태
 - **sec:authorize="isAuthenticated()"** - 현재 로그인 상태

![cap](https://github.com/user-attachments/assets/7a1ea3df-aa05-42cf-b6fb-36e38f8b3492)

<hr>

**글쓴이 표시**
`q_list.html`
```html
(... 생략 ...)
<tr class="text-center">
    <th>번호</th>
    <th style="width:50%">제목</th>
    <th>글쓴이</th>
    <th>작성일시</th>
</tr>
(... 생략 ...)
```
- <th>글쓴이</th> 항목을 추가했다.

`for문에도 글쓴이 적용`

```html
(... 생략 ...)
<tr class="text-center" th:each="question, loop : ${paging}">
    <td th:text="${paging.getTotalElements - (paging.number * paging.size) - loop.index}"></td>
    <td class="text-start">
        <a th:href="@{|/question/detail/${question.id}|}" th:text="${question.subject}"></a>
        <span class="text-danger small ms-2" th:if="${#lists.size(question.answerList) > 0}"
            th:text="${#lists.size(question.answerList)}">
        </span>
    </td>
    <td><span th:if="${question.author != null}" th:text="${question.author.username}"></span></td>
    <td th:text="${#temporals.format(question.createDate, 'yyyy-MM-dd HH:mm')}"></td>
</tr>
(... 생략 ...)
```
- <td> ... </td> 엘리먼트를 삽입하여 질문의 글쓴이를 표시

![writer](https://github.com/user-attachments/assets/15533bb4-416b-4b71-8855-a6cbc9820cbe)

**질문 상세**
- 질문 상세 템플릿도 글쓴이 추가.

`q_detail.html`
```html
(... 생략 ...)
<!-- 질문 -->
            <div class="badge bg-light text-dark p-2 text-start">
                <div class="mb-2">
                    <span th:if="${question.author != null}" th:text="${question.author.username}"></span>
                </div>
                <div th:text="${#temporals.format(question.createDate, 'yyyy-MM-dd HH:mm')}"></div>
            </div>
(... 생략 ...)
```
- 글쓴이와 작성일시가 함께 보이도록 수정했다.

![test](https://github.com/user-attachments/assets/0f2d81d1-4e5a-41fb-a532-96df5da3bdf5)

<hr>

**질문 수정**
- 작성한 질문과 답변 수정 기능 추가.

`수정일시`
- 먼저 질문이나 답변이 언제 수정되었는지 확인할 수 있도록 Question과 Answer 엔티티에 수정 일시를 의미하는 modifyDate 속성을 추가.

`Question.java`
```java
(... 생략 ...)
public class Question {
    (... 생략 ...)
    private LocalDateTime modifyDate;
}  
```

`Answer.java`
```java
(... 생략 ...)
public class Answer {
    (... 생략 ...)
    private LocalDateTime modifyDate;
}
```

![users](https://github.com/user-attachments/assets/278b1b8f-fe94-44fc-9a65-091887c4e357)

<hr>

**질문 수정**
- 작성한 질문을 수정하려면 질문 상세 화면에서 "수정" 버튼을 클릭하여 수정 화면으로 진입해야 한다.

**질문 수정 버튼**
`question_detail.html`
```html
 <div class="my-3">
            <a th:href="@{|/question/modify/${question.id}|}" class="btn btn-sm btn-outline-secondary"
                sec:authorize="isAuthenticated()"
                th:if="${question.author != null and #authentication.getPrincipal().getUsername() == question.author.username}"
                th:text="수정"></a>
        </div>
```
- 수정 버튼은 로그인한 사용자와 글쓴이가 동일한 경우에만 노출되도록 #authentication.getPrincipal().getUsername() == question.author.username을 적용.
- 만약 로그인한 사용자와 글쓴이가 다르면 수정보튼 hideen.

`QuestionController``
- 수정 버튼에 GET 방식의 @{|/question/modify/${question.id}|} 링크가 추가되었으므로 질문 컨트롤러를 다음과 같이 수정

```spring
   @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String questionModify(QuestionForm questionForm, @PathVariable("id") Integer id, Principal principal) {
        Question q = this.qService.getQuestion(id);
        if(!q.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        questionForm.setSubject(q.getSubject());
        questionForm.setContent(q.getContent());
        return "q_form";
    }
```
- 로그인한 사용자와 질문의 작성자가 동일하지 않을 경우에는 "수정권한이 없습니다." 오류가 발생하도록 했다. 

`question_from.html`
```html
    <form th:object="${questionForm}" method="post">
        <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" />
```
- 폼 태그의 th:action 속성을 삭제.
-  **th:action** 속성을 삭제하면 CSRF 값이 자동으로 생성되지 않기 때문에 위와 같이 **CSRF** 값을 설정하기 위한 hidden 형태의 input 엘리먼트를 수동으로 추가.

`QuestionService`
```java
public class QuestionService {

    (... 생략 ...)

    public void modify(Question question, String subject, String content) {
        question.setSubject(subject);
        question.setContent(content);
        question.setModifyDate(LocalDateTime.now());
        this.questionRepository.save(question);
    }
}
```
- 질문 데이터를 수정할수 있는 modify 메서드를 추가.

`QuestionController`
-  질문 수정화면에서 질문의 제목이나 내용을 변경하고 "저장하기" 버튼을 누르면 호출되는 **POS**T 요청을 처리하기 위해QuestionController에 다음과 같은 메서드를 추가.
```spring
   @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String questionModify(@Valid QuestionForm questionForm, BindingResult bindingResult, 
            Principal principal, @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }
        Question question = this.questionService.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.questionService.modify(question, questionForm.getSubject(), questionForm.getContent());
        return String.format("redirect:/question/detail/%s", id);
    }
```

-  questionForm의 데이터를 검증하고 로그인한 사용자와 수정하려는 질문의 작성자가 동일한지도 검증

<hr>

**질문 삭제**
`q_detail.html`
```html
   <a href="javascript:void(0);" th:data-uri="@{|/question/delete/${q.id}|}"
                class="delete btn btn-sm btn-outline-secondary" sec:authorize="isAuthenticated()"
                th:if="${q.author != null and #authentication.getPrincipal().getUsername() == q.author.username}"
                th:text="삭제"></a>
```
- <삭제> 버튼은 <수정> 버튼과는 달리 href 속성값을 javascript:void(0)로 설정.
- 삭제를 실행할 URL을 얻기 위해 th:data-uri 속성을 추가.
- <삭제> 버튼이 눌리는 이벤트를 확인할 수 있도록 class 속성에 "delete" 항목을 추가.

`</body> 태그 끝나기 전에 스크립트 추가하기`
```script
 <!-- 자바스크립트 -->
    <script>
      const delete_elements = document.getElementsByClassName('delete');
      Array.from(delete_elements).forEach(function (element) {
        element.addEventListener('click', function () {
          if (confirm('정말로 삭제하시겠습니까?')) {
            location.href = this.dataset.uri;
          }
        });
      });
    </script>
```
- delete라는 클래스를 포함하는 버튼을 클릭하면 삭제하시겠습니까?라는 메시지가 나옴.
- "삭제" 버튼을 클릭하고 "확인"을 선택하면 data-uri 속성에 해당하는 @{|/question/delete/${question.id}|}이 호출될 것

`QuestionService`
- 질문을 삭제하는 기능을 QuestionService에 추가
```spring
public class QuestionService {

    (... 생략 ...)

    public void delete(Question question) {
        this.qRepo.delete(question);
    }
}
```
- Question 객체를 입력으로 받아 Question 리포지터리를 사용하여 질문 데이터를 삭제하는 delete 메서드를 추가

`QuestionController`
- 그리고 **@{|/question/delete/${question.id}|}** URL을 처리하기 위한 기능을 **QuestionController**에 다음과 같이 추가
```spring
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String questionDelete(Principal principal, @PathVariable("id") Integer id) {
        Question question = this.questionService.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.questionService.delete(question);
        return "redirect:/";
    }
```


![de](https://github.com/user-attachments/assets/2070a304-4283-4928-ab37-4a4776923f87)




















