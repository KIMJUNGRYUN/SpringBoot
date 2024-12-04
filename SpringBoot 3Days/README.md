
**BootStrap CSS 추가**
```html
 <!-- th:fragment="이름" 으로 공통 태그부분 작성 -->
    <nav th:fragment="nav" class="navbar navbar-expand-lg navbar-light bg-light border-bottom">
      <div class="container-fluid">
          <a class="navbar-brand" href="/">SBB</a>
          <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent"
              aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
              <span class="navbar-toggler-icon"></span>
          </button>
          <div class="collapse navbar-collapse" id="navbarSupportedContent">
              <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                  <li class="nav-item">
                      <a class="nav-link" href="#">로그인</a>
                  </li>
              </ul>
          </div>
      </div>
  </nav>
```
- 네비게이션 바 추가
- 로그인 페이지 추가

![nav](https://github.com/user-attachments/assets/c3cd3240-9eb4-4e3f-ac4d-dbd30f76fe28)


**layout.html**
- 템플릿에 추가
```html
<nav th:replace="layout::nav"></nav>
```
- 내비게이션바의 'SBB' 로고를 누르면 아무 곳에서나 메인 페이지로 돌아갈 수 있다. 

<hr>

**페이징**
- SBB의 질문 목록은 현재 페이징 처리가 안되기 때문에 게시물 300개를 작성하면 한 페이지에 300개의 게시물이 모두 조회된다.
- 스프링부트의 테스트 프레임워크를 이용하여 대량 데이터 만들기.
```spring
@SpringBootTest
class SbbApplicationTests {

    @Autowired
    private QuestionService questionService;

    @Test
    void testJpa() {
        for (int i = 1; i <= 300; i++) {
            String subject = String.format("테스트 데이터입니다:[%03d]", i);
            String content = "내용무";
            this.questionService.create(subject, content);
        }
    }
}
```
![moredata](https://github.com/user-attachments/assets/baadfd26-44f3-45b8-a5b9-70219d828491)
- 실무에서는 @Transactional을 제거한다. 테스트환경에서는 Transactional은 테스트한 후 처음상태로 다시 되돌림.

**페이징 구현하기**
- 페이징을 구현하기 위한 라이브러리 추가.
  - org.springframework.data.domain.Page
  - org.springframework.data.domain.PageRequest
  - org.springframework.data.domain.Pageable

**QuestionRepository**
```spring
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
	
	Question findBySubject(String subject);
	Question findBySubjectAndContent(String subject, String content);
    List<Question> findBySubjectLike(String subject);
    Page<Question> findAll(Pageable pageable);
}
```
- Pageable 객체를 입력으로 받아 `Page<Question>`타입 객체를 리턴하는 findAll메서드를 생성.

**QuestionService**
```spring
public Page<Question> getList(int page){
		Pageable pageable = PageRequest.of(page, 10);
		return this.qRepo.findAll(pageable);
	}
```
- 질문 목록을 조회하는 `getList`메서드를 위와 같이 변경.
- Pageable 객체를 생성할때 사용한 PageRequest.of(page, 10)에서 page는 조회할 페이지의 번호이고 10은 한 페이지에 보여줄 게시물의 갯수를 의미한다.

**QuestionController**
```spring
@RequestMapping("/list")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
    	 Page<Question> paging = this.qService.getList(page);
    	model.addAttribute("paging", paging);
        return "question_list";
    }
```
- `http://localhost:8080/question/list?page=0` 처럼 GET 방식으로 요청된 URL에서 page값을 가져오기 위해 `@RequestParam(value="page", defaultValue="0") int page` 매개변수가 list 메서드에 추가되었다
- URL에 페이지 파라미터 page가 전달되지 않은 경우 디폴트 값으로 0이 되도록 설정했다.

![asdasda](https://github.com/user-attachments/assets/bdcdd383-3665-4d2c-a65c-f32496892e13)

**Question_list.html**
```html
 <tr th:each="question, loop : ${paging}">
```
- 기존에 전달했던 이름인 "questionList" 대신 "paging" 이름으로 템플릿에 전달했기 때문에 템플릿도 다음과 같이 변경해야 한다

**템플릿에 페이지 이동 기능 구현**
- 질문 목록에서 페이지를 이동하려면 페이지를 이동할 수 있는 "이전", "다음" 과 같은 링크가 필요하다.
```html
  </table>
    <!-- 페이징처리 시작 -->
    <div th:if="${!paging.isEmpty()}">
        <ul class="pagination justify-content-center">
            <li class="page-item" th:classappend="${!paging.hasPrevious} ? 'disabled'">
                <a class="page-link"
                    th:href="@{|?page=${paging.number-1}|}">
                    <span>이전</span>
                </a>
            </li>
            <li th:each="page: ${#numbers.sequence(0, paging.totalPages-1)}"
                th:classappend="${page == paging.number} ? 'active'" 
                class="page-item">
                <a th:text="${page}" class="page-link" th:href="@{|?page=${page}|}"></a>
            </li>
            <li class="page-item" th:classappend="${!paging.hasNext} ? 'disabled'">
                <a class="page-link" th:href="@{|?page=${paging.number+1}|}">
                    <span>다음</span>
                </a>
            </li>
        </ul>
    </div>
    <!-- 페이징처리 끝 -->
    <a th:href="@{/question/create}" class="btn btn-primary">질문 등록하기</a>
```
- 이전 페이지가 없는 경우에는 "이전" 링크가 비활성화(disabled)되도록 하였다. (다음페이지의 경우도 마찬가지 방법으로 적용했다.) 
- 페이지 리스트를 루프 돌면서 해당 페이지로 이동할 수 있는 링크를 생성하였다

**템플릿에 사용된 주요 페이징 기능을 표로 정리**

![table](https://github.com/user-attachments/assets/6c696c40-e879-47e6-a003-446a8f6cd89a)

- 페이징 처리는 잘 되었지만 페이지가 모두 표시된다.
 
- 이 문제를 해결하기 위해 다음과 같이 질문 목록 템플릿을 수정하자
```html
   <li th:each="page: ${#numbers.sequence(0, paging.totalPages-1)}"
              th:if="${page >= paging.number-5 and page <= paging.number+5}"
              th:classappend="${page == paging.number} ? 'active'" class="page-item">
```
- `th:if="${page >= paging.number-5 and page <= paging.number+5}"` 이 코드는 페이지 리스트가 현재 페이지 기준으로 좌우 5개씩 보이도록 만든다.


** 작성일시 역순으로 조회하기**
`QuestionService`
```spring
public Page<Question> getList(int page){
		Pageable pageable = PageRequest.of(page, 10, Sort.by("createDate").descending());
		return this.qRepo.findAll(pageable);
	}
```
- 게시물을 역순으로 조회하기 위해서는 위와 같이 `PageRequest.of` 메서드의 세번째 파라미터로 `Sort` 객체를 전달

**게시물 번호가 1부터 시작되는 문제**
`게시물 번호 공식 만들기`
- 번호 = 전체 게시물 개수 - (현재페이지 * 페이지당 게시물 개수) - 나열 인덱스

![index](https://github.com/user-attachments/assets/74bde841-fa3e-42d4-80cc-19553dae00fa)
**게시물 번호 공식을 질문 목록 템플릿에 적용하기**
```html
<td th:text="${paging.getTotalElements - (paging.number * paging.size) - loop.index}"></td>
```
![sort](https://github.com/user-attachments/assets/b5c70ca5-09ab-4ba9-bb8f-8ae21116250b)

**질문에 달린 답변 개수 표시**
`Question_list.html`
```html
  <td>
	<a th:href="@{/question/detail/__${question.id}__}" th:text="${question.subject}"></a>
	 <span class="text-danger small ms-2"
	  th:if="${#lists.size(question.answerList) > 0}" 
	th:text="${#lists.size(question.answerList)}">
	</span>              
  </td>
```
- `th:if="${#lists.size(question.answerList) > 0}"`로 답변이 있는지 조사
- `th:text="${#lists.size(question.answerList)}"`로 답변 개수를 표시했다.

![num](https://github.com/user-attachments/assets/83819500-4e3e-4c31-9c6f-2cc541f2ea9e)

<hr>

**스프링 스큐리티**
- 스프링 시큐리티는 스프링 기반 애플리케이션의 인증과 권한을 담당하는 스프링의 하위 프레임워크.
   - 인증(Authenticate)은 로그인을 의미한다.
   - 권한(Authorize)은 인증된 사용자가 어떤 것을 할 수 있는지를 의미한다.

```spring
package com.mysite.sbb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                .requestMatchers(new AntPathRequestMatcher("/**")).permitAll())
        ;
        return http.build();
    }
}
```
- **@Configuration**은 스프링의 환결설정 파일임을 의미하는 애너테이션이다.
- **@EnableWebSecurity**은 모든 요청 URL이 스프링 시큐리티의 제어를 받도록 만드는 애너테이션이다.
- `@EnableWebSecurity 애너테이션을 사용하면 내부적으로 SpringSecurityFilterChain이 동작하여 URL 필터가 적용된다.`
- 스프링 시큐리티의 세부 설정은 `SecurityFilterChain 빈`을 생성하여 설정할 수 있다

**스프링 시큐리티가 CSRF 처리시 H2 콘솔은 예외로 처리할 수 있도록**
```spring
 http
	.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                .requestMatchers(new AntPathRequestMatcher("/**")).permitAll())
	.csrf((csrf) -> csrf
		.ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**")))
	 .headers((headers) -> headers
                .addHeaderWriter(new XFrameOptionsHeaderWriter(
                    XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
        ;
        return http.build();
```

- 위 처럼 URL 요청시 `X-Frame-Options` 헤더값을 `sameorigin`으로 설정하여 오류가 발생하지 않도록 했다.

**회원가입 페이지**
`회원정보 엔티티`
```spring
@Getter
@Setter
@Entity
public class SiteUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    @Column(unique = true)
    private String email;
}
```
-`username`, `email` 속성에는 `@Column(unique = true`) 처럼 `unique = true`를 지정했다. `unique = true`는 유일한 값만 저장할 수 있음을 의미한다. 즉, 값을 중복되게 저장할 수 없음을 뜻한다. 이렇게 해야 `username`과 `email`에 동일한 값이 저장되지 않는다.

**UserRepository**
```spring
public interface UserRepository extends JpaRepository<SiteUser, Long> {
}
```
- SiteUser의 PK의 타입은 Long이다. 따라서 JpaRepository<SiteUser, Long>처럼 사용했다.

**SecurityConfig**
```spring
 @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
```
- PasswordEncoder를 @Bean으로 등록

**UserService**
```spring
	@Autowired
	private PasswordEncoder passEncoder;
	
    public SiteUser create(String username, String email, String password) {
        SiteUser user = new SiteUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passEncoder.encode(password));
        this.userRepo.save(user);
        return user;
    }
```
- **BCryptPasswordEncoder** 객체를 직접 생성하여 사용하지 않고 빈으로 등록한 **PasswordEncoder** 객체를 주입받아 사용하도록 수정했다.

**회원가입 폼**
`UserCreateForm`
```spring
@Getter
@Setter
public class UserCreateForm {
    @Size(min = 3, max = 25)
    @NotEmpty(message = "사용자ID는 필수항목입니다.")
    private String username;

    @NotEmpty(message = "비밀번호는 필수항목입니다.")
    private String password1;

    @NotEmpty(message = "비밀번호 확인은 필수항목입니다.")
    private String password2;

    @NotEmpty(message = "이메일은 필수항목입니다.")
    @Email
    private String email;
}
```
- username은 필수항목이고 길이가 3-25 사이여야 한다는 검증조건을 설정.
- @Size는 폼 유효성 검증시 문자열의 길이가 최소길이(min)와 최대길이(max) 사이에 해당하는지를 검증. 
- password1과 password2는 "비밀번호"와 "비밀번호확인"에 대한 속성. 로그인 할때는 비밀번호가 한번만 필요하지만 회원가입시에는 입력한 비밀번호가 정확한지 확인하기 위해 2개의 필드가 필요. 
-  email 속성에는 @Email 애너테이션이 적용.

<hr>

**회원가입 컨트롤러**
```spring
@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
    private UserService userService;

    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm) {
        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup_form";
        }

        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect", 
                    "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
        }

        userService.create(userCreateForm.getUsername(), 
                userCreateForm.getEmail(), userCreateForm.getPassword1());

        return "redirect:/";
    }
}
```
- /user/signup URL이 GET으로 요청되면 회원 가입을 위한 템플릿을 렌더링하고 POST로 요청되면 회원가입을 진행.
- 회원 가입시 비밀번호1과 비밀번호2가 동일한지를 검증하는 로직을 추가. 만약 2개의 값이 일치하지 않을 경우에는 bindingResult.rejectValue를 사용하여 오류가 발생하게 함.
- bindingResult.rejectValue의 각 파라미터는 bindingResult.rejectValue(필드명, 오류코드, 에러메시지)를 의미하며 여기서 오류코드는 일단 "passwordInCorrect"로 정의.

<hr>

**회원가입 템플릿**
`signup_form`
```html
   <!-- 여기부터 시작 -->
      <div class="my-3 border-bottom">
        <div>
          <h4>회원가입</h4>
        </div>
      </div>
      <form th:action="@{/user/signup}" th:object="${userCreateForm}" method="post">
        <div th:replace="layout::formErrors"></div>
        <div class="mb-3">
          <label for="username" class="form-label">사용자ID</label>
          <input type="text" th:field="*{username}" class="form-control" />
        </div>
        <div class="mb-3">
          <label for="password1" class="form-label">비밀번호</label>
          <input type="password" th:field="*{password1}" class="form-control" />
        </div>
        <div class="mb-3">
          <label for="password2" class="form-label">비밀번호 확인</label>
          <input type="password" th:field="*{password2}" class="form-control" />
        </div>
        <div class="mb-3">
          <label for="email" class="form-label">이메일</label>
          <input type="email" th:field="*{email}" class="form-control" />
        </div>
        <button type="submit" class="btn btn-primary">회원가입</button>
      </form>
```
- 회원가입을 위한 "사용자 ID", "비밀번호", "비밀번호 확인", "이메일"에 해당되는 input 엘리먼트를 추가.
- <회원가입> 버튼을 누르면 폼 데이터가 POST 방식으로 /user/signup/ URL로 전송됨.

**내비게이션 바에 회원가입 링크 추가하기**
`layout.html`
```html
 <li class="nav-item">
              <a class="nav-link" th:href="@{/user/signup}">회원가입</a>
            </li>
```

![pass](https://github.com/user-attachments/assets/d377bec5-ce4a-4578-acb6-ad0e307c25ca)

**중복 회원가입 처리**
`UserController`
```spring
try {
			userService.create(userCreateForm.getUsername(), userCreateForm.getEmail(), userCreateForm.getPassword1());
		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();
			bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
			return "signup_form";
		} catch (Exception e) {
			e.printStackTrace();
			bindingResult.reject("signupFailed", e.getMessage());
			return "signup_form";
		}
```
- 사용자ID 또는 이메일 주소가 동일할 경우에는 DataIntegrityViolationException이 발생하므로 DataIntegrityViolationException 예외가 발생할 경우 "이미 등록된 사용자입니다."라는 오류를 화면에 표시하도록 했음.
-  그리고 다른 오류의 경우에는 해당 오류의 메시지(e.getMessage())를 출력하도록 했음.
- `bindingResult.reject(오류코드, 오류메시지)는 특정 필드의 오류가 아닌 일반적인 오류를 등록할때 사용한다.`

![user](https://github.com/user-attachments/assets/5fe71cc9-c003-4a3f-9332-a74a70d9a145)











