# 비용관리2

**로그엔 페이지 - 제이쿼리 Validation**
- register 페이지와 동일하게 제이쿼리 라이브러리와 커스텀 login.js 추가.
- login.js
  - 1.로그인 페이지에 form 태그에 id를 입력 loginForm
  - 2.제이쿼리로 loginForm을 선택해 validate 한다.
  - 3.2개의 입력 email과 password를 처리한다.

![login5](https://github.com/user-attachments/assets/570b463d-a5c1-46fe-b7ca-0234e9d91266)

- 유효성 검사 프론트 백엔드 어디에 쓰는게 좋은가?
  - ​각 영역마다 장단점이 있습니다. 우선 처리 속도만 본다면 클라이언트가 압도적으로 빠를 수 밖에 없습니다. 렌더링 된 태그에는 이미 유효성 검사가 내장되어 있기 때문에 데이터 형식이 올바르지 않으면 서버까지 갈 필요도 없이 즉각적으로 유저에게 에러 메세지를 전달 할 수가 있습니다.하지만 서버만 할 수 있는 유효성 검사들도 있습니다. 예를 들어 중복된 ID가 있는지 확인하기 위해서는 데이터 베이스 내의 가입된 ID 데이터를 탐색하는 작업을 진행해야 합니다. 또한, 보안 측면에서는 무조건 서버에서 유효성 검사가 실시되어야 합니다.

<hr>

**SpringSecurity Dependency 추가, 시작**
- SpringSecurity 추가
  - 1.sts에서 바로추가 
  - 2.이니셔라이저 사이트에서 추가한 pom 복사 붙이기
  - 3.프로젝트 실행시 시큐리티가 바로 적용됨
  - 4.콘솔에 임시 비밀번호가 랜덤 생성

```spring
Using generated security password: ec397bd9-eae1-4b3e-8056-efb5a03289d5
```

![security](https://github.com/user-attachments/assets/65c49791-ea39-48cd-b869-0804e3285966)

<hr>

**변경된 Security 설정**
- Spirng Boot 3.1(Spring 6.1) SecurityFilterChain
```spring
@Configuration
@EnableWebSecurity
public class CustomSecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests((authz) ->
				authz
					.requestMatchers("/js/**", "/css/**").permitAll()
					.requestMatchers("/","/login","/register").permitAll()			
			);
		
						
		return http.build();
	}
}
​
```

- CSRF란?
  - CSRF는 Cross Site Request Forgery(사이트 간 요청 위조)의 줄임말로 웹 취약점 중 하나.
  - 공격자가 희생자의 권한을 도용하여 특정 웹 사이트의 기능을 실행하게 할 수 있으며 이는 희생자의 의도와는 무관하게 이루어짐.
  - CSRF 취약점을 이용하면 공격자가 희생자의 계정으로 네이버 카페나 인스타그램, 페이스북 등 다수의 방문자가 있는 사이트에 광고성 혹은 유해한 게시글을 업로드 하는 것도 가능해짐.
  
![CSRF](https://github.com/user-attachments/assets/47c2d81e-29e5-458c-b943-5adbafce1739)

**패스워드 암호화**
- 암호화 객체를 시큐리티 설정 클래스안에 빈등록

```spring
@Configuration
@EnableWebSecurity
public class CustomSecurityConfig {

...
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}

```

- UserService
  - 새로운 유저를 저장하기전에 패스워드를 encode 암호화 한다.

```spring
...
	private final PasswordEncoder passwordEncoder;
	
	public void save(UserDTO userDTO) {
		userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
		...
	}

```

![register7](https://github.com/user-attachments/assets/d4406290-ce1c-459a-a482-f91f898ac513)

<hr>

**http 허가, 인증, formLogin, 정적파일**
- authorizeHttpRequests 에 모두에게 허가되는 요청주소 등록 그 외(anyRequest) 는 authenticated (인증) 해야 한다.
- formLogin 에는 인증페이지(/login) 등록 및 실패했을때 주소, 성공 주소, 로그인시 사용할 유저네임(아이디)를 email로 설정 패스워드는 password로 설정

<hr>

**findByEmail 메서드, 유저리포지토리**
- 현재 시큐리티 formLogin에 로그인(인증)시 아이디로 email을 사용하게 설정되어있다. 그러므로 DB에서 해당 email이 있는지 찾아서 그 email과 패스워드를 확인하도록 시큐리티를 설정하기 위해 이메일로 유저를 검색하는 메서드를 리포지토리에 넣는다.

```spring
public interface UserRepository extends JpaRepository<User, Long> {
	
	//SELECT * FROM tbl_users WHERE email = ?
	Optional<User> findByEmail(String email);
}

```

<hr>

**인증을 위한 서비스 UserDetailsService**
- 인증을 위한 서비스클래스는 시큐리티의 유저디테일즈서비스를 구현한
- 이때 loadUserByUsername(String username)은 기본으로 유저네임이 매개변수인데 이것을 email로 수정(이미 email로 설정되어 있음) => 시큐리티가 인증할때 로그인페이지("/login")에 있는 email(원래 username)과 패스워드를 가지고 스스로 인증한다. 
- 그러므로, 개발자는 이 email로 DB에서 유저를 찾은뒤 시큐리티의 유저 객체로 만들어 리턴해주면 된다.

```spring
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService{

	private final UserRepository userRepo;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		
		User user = userRepo.findByEmail(email).orElseThrow(() -> 
			new UsernameNotFoundException(email + "해당 이메일의 유저가 없습니다 "));
	
		return null; //시큐리티 유저객체
	}

}
```

- 시큐리티 유저객체는 ( 유저네임(email설정됨) , 패스워드 , 롤(유저,관리자등) )이다.  여기서 롤은 일단 설정없이 빈 어레이리스트 객체(내용없음)로 넣는다.

```spring
return new org.springframework.security.core.userdetails.User(
				user.getEmail(),
				user.getPassword(),
				new ArrayList<>()
			);
```

<hr>

**시큐리티 설정에 UserDetailsService를 불러오고 인증 매니저 설정**
- CustomSecurityConfig

```spring
	@SuppressWarnings("unused")
	private final CustomUserDetailsService customUserDetailsService;
```
---
```spring
@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {		
		return authenticationConfiguration.getAuthenticationManager();
	}
```

- `AuthenticationManager` 빈을 등록한다. `AuthenticationManager`는 스프링 시큐리티의 인증을 담당한다
- `AuthenticationManager` 빈 생성시 스프링의 내부 동작으로 인해 위에서 작성한 `UserSecurityService`와 `PasswordEncoder`가 자동으로 설정된다
- => 이제 프로젝트 실행시에 콘솔에 임시 패스워드가 생성되지 않음

**로그인 테스트**

![logintest](https://github.com/user-attachments/assets/8232a059-b856-4b89-9ab5-f0934cb5dca5)
- 제대로 인증되었다면 아래설정에서 처럼 성공시 /expenses , 실패시 다시 /login

```spring
			.formLogin((formLogin) ->
				formLogin
						.loginPage("/login")
						.failureUrl("/login")
						.defaultSuccessUrl("/expenses")
						.usernameParameter("email")
						.passwordParameter("password")
			)
​
```

`인증 성공시`
![sucess](https://github.com/user-attachments/assets/f3679103-5bce-43fd-a0de-933b63d9ab07)

`인증 실패시`
![Login6](https://github.com/user-attachments/assets/c5a76e58-c6a7-419b-8cef-79a1582f0f03)

- 인증 실패시 에러메시지를 출력
  - 시큐리티 설정

```spring
.failureUrl("/login?error=true")
```

- login 페이지 폼태그 아레에 에러 발생시 표시 메세지 작성

```html
<form id="loginForm" th:action="@{/login}" method="post">
	<div th:if="${param.login}">잘못된 이메일 또는 패스워드 입니다</div>
```

![Login7](https://github.com/user-attachments/assets/c239ddd2-8174-43c2-9658-464be8914fad)

<hr>

**시큐리티 -로그아웃**
- e_list, e_form 상단의 hr 수평선 아래에 로그아웃 링크 추가

```html
<hr />
<div><a th:href="@{/logout}">로그아웃</a></div>
```

![logout1](https://github.com/user-attachments/assets/0f33d75b-ae62-4bee-9a1a-a0224498be43)

- 로그아웃 하면 세션정보 및 로그인 정보가 사라지므로 다시 /expenses 페이지 안됨

![Login8](https://github.com/user-attachments/assets/6a02f974-798d-4ed6-bc84-6af60ac6726b)

- 로그아웃시 메세지 표시
  - 시큐리티 설정
     - .formLogin 아래에 넣기

```html
			.logout((logout) -> 
				logout
					.logoutUrl("/logout")
					.invalidateHttpSession(true)
					.clearAuthentication(true)
					.logoutSuccessUrl("/login?logout=true")
					.permitAll()
			)
```

- login에 메세지 추가

```html
<div th:if="${param.error}">잘못된 이메일 또는 패스워드 입니다</div>
				<div th:if="${param.logout}">로그아웃 되었습니다</div>
```

![logout2](https://github.com/user-attachments/assets/fb433874-b9bc-4a3c-84f2-cf0d5c8ab164)