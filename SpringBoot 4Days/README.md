
**Login**
- 스프링 스큐리티 사용

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
- 로그인 페에지의 URL은 `/user/login이다
  - 로그인 성공시에 이동하는 default 페이지는 루티 URL(/)임을 의미.

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

***UserRole**
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

SecurityConfig
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

















