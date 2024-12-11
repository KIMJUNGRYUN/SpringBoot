# 비용관리

**총 비용 구하기**

![totalcounmt](https://github.com/user-attachments/assets/1ee8c7a9-b69b-44b0-849a-3ff116cda8d7)

`서비스 총비용 계산 메서드`

```spring
  // 리스트의 총비용을 계산
    public Long totalExpenses(List<ExpenseDTO> expenses) {
		Long sum = expenses.stream().map(x->x.getAmount())
			.reduce(0L, Long::sum);
		return sum;
	}
```

`비용 컨트롤러`
```spring
@GetMapping("/expenses")
	public String showExpenseList(Model model) {
		List<ExpenseDTO> list = expService.getAllExpenses();
		model.addAttribute("expenses", list);
		model.addAttribute("filter", new ExpenseFilterDTO());
		Long total = expService.totalExpenses(?);
		model.addAttribute("total", total);
		return "e_list";
	}
```

`특정 키워드 or 순서 검색 필터컨트롤러 추가`
```spring
@GetMapping("/filterExpenses")
	public String filterExpenses(@ModelAttribute("filter") ExpenseFilterDTO expenseFilterDTO,
								 Model model) throws ParseException {
		System.out.println(expenseFilterDTO);
		List<ExpenseDTO> list = expService.getFilterExpenses(expenseFilterDTO.getKeyword(), expenseFilterDTO.getSortBy());
		model.addAttribute("expenses", list);
   Long total = expService.totalExpenses(list);
   model.addAttribute("total", total);
		return "e_list";
	}
```

![totalcount2](https://github.com/user-attachments/assets/7fc64918-3cb5-4ae4-a9f7-6eb3401bd816)

![totalcount3](https://github.com/user-attachments/assets/5a3c2d3c-01cf-454e-b118-ac28b317995f)

<hr>

**특정  기간의 비용**
`필터DTO에 시작일과 종료일을 문자열로 넣기`

```spring
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseFilterDTO {
    ...
	
	private String startDate;
	
	private String endDate;
	
}
```

`html 리스트에 시작일과 종료일을 선택할수 있게 넣기`

```html
<input type="date" th:field="*{startDate}" />
			<input type="date" th:field="*{endDate}" />
			<input type="submit" value="검색" />
```

![zzzz](https://github.com/user-attachments/assets/08794133-f172-4603-8c67-b5d9d7a4a6d7)

`필터 컨트롤러`

```spring
List<ExpenseDTO> list = expService.getFilterExpenses(expenseFilterDTO);
```

`서비스 수정`

```spring
public List<ExpenseDTO> getFilterExpenses(ExpenseFilterDTO filterDTO) throws ParseException {
		String keyword = filterDTO.getKeyword();
		String sortBy = filterDTO.getSortBy();
		String startString = filterDTO.getStartDate();
		String endString = filterDTO.getEndDate()?;
		//sql 날짜로 문자열 시작일과 종료일을 변환
		Date startDay = DateTimeUtil.convertStringToDate(startDate);
		Date endDay = DateTimeUtil.convertStringToDate(endDate);
```

`현재 리포자토리에서 키워드로 검색하는데, 시작일 종료일을 추가`

```spring
List<Expense> list = expRepo.findByNameContaining(keyword);
```

`리파지토리`

```spring
//SELECT * FROM tbl_expense WHERE name LIKE %keyword% AND date BETWEEN startDate AND endDate
	List<Expense> findByNameContainingAndDate(String keyword, Date start , Date end );
```

`서비스`

```spring
List<Expense> list = expRepo.findByNameContaining(keyword, startDay, endDay);
```

![date](https://github.com/user-attachments/assets/710b8244-b0d8-4799-b0fc-db79b41a93b6)

![date2](https://github.com/user-attachments/assets/479a51cb-b9b4-44ac-b258-c7d2c48b7163)

`날짜를 입력하지 않았을경우 해결하기`

```spring
Date startDay = !startString.isEmpty() ? 날짜변환 : new Date(0);
Date endDay = !endString.isEmpty() ? 날짜변환 : new Date(System.currentTimeMillis())
```

<hr>

**유효성 검사-1**
- 스피링부트 `validation` 추가

```spring
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-validation</artifactId>
		</dependency>
```

- `ExpenseDTO`
  - 사용자 입력을 검증하므로 form에 바인딩 객체에검증
  
```spring
	@NotBlank(message = "이름을 입력해 주세요")
	@Size(min = 3, message = "이름을 3자 이상 적어주세요")
	private String name;
```

![어노테이션](https://github.com/user-attachments/assets/6bad2647-d924-42b0-8c39-6991626536e1)

- Controller
 - `@valid`로 사용자가 작성해서 `submit`한 `ExpenseDTO` 객체를 검사.
 - 이때 엔티티에 적용된 어노테이션을 위반했을경우 객체 다음에 위치한 BindingResult 객체에 에러가 발생.
 - 이때 if문을 사용해 발생한 에러가 있을경우 다시 되돌림.

```spring
@PostMapping("/saveOrUpdateExpense")
	public String saveOrUpdateExpense(@? @ModelAttribute("expense") ExpenseDTO expenseDTO,
									  BindingResult result) throws ParseException {
		System.out.println("입력한 expenseDTO 객체 : " + expenseDTO);
		if(result.hasErrors()) {
			return "expense-form";
		}
		expService.saveExpenseDetails(expenseDTO);
		return "redirect:/expenses";
	}
```

- 타임리프 e_form에서 에러 메시지 출력

```html
	<span th:if="${#fields.hasErrors('name')}" th:errors="*{name}"></span>
```

![새비용 만들기](https://github.com/user-attachments/assets/4e317ab6-5a9c-4c33-9c29-28cd78d98da0)

<hr>

**유효성 검사-2**
- 엔티티에서 가격

```spring
  @NotBlank(message = "가격을 입력해 주세요")
	@Min(value = 10, message = "비용은 최소 10원 이상입니다")
	private Long amount; 
```

- 타임리프에 에러메세지 표시하기

```html
<span th:if="${#fields.hasErrors('error')}" th:errors="*{error}"></span>
```

![가격은](https://github.com/user-attachments/assets/cfa68e3c-08f1-4da9-8dce-8e0263b195d2)

- 날짜는 선택만 하면 되므로 html에서 required 속성을 넣기`

![ㅌㅌㅌㅌㅌ](https://github.com/user-attachments/assets/a7d0bcc5-91cd-474d-a6a9-b92c1aa76943)

<hr>

**로그인**
- 새로운 AuthController

```spring
@Controller
public class AuthController {
  @GetMapping("/login")
  public String showLoginPage(){
    return "login";
  }
}
```

- login.html

```html
<!DOCTYPE html>
<html lang="ko" xmlns:th="http://www.thymeleaf.org">
	<head>
		<meta charset="UTF-8" />
		<meta name="viewport" content="width=device-width, initial-scale=1.0" />
		<title>로그인 페이지</title>
	</head>
	<body>
		<h1>로그인</h1>
		<hr />
	</body>
</html>
```

- 컨틀로러에 기본페이지 "/" 추가

```spring
{ "/login", "/" }
```

<hr>

**UserDTO, User엔티티, 리파지토리**

```spring
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

	private String name;
	
	private String email;
	
	private String password;
	
	private String confirmPassword; //DTO에만
	
}
```

- JPA 엔티티

```spring
@Entity
@Table(name = "tbl_users")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY )
	private Long id;
	
	@Column(unique = true)
	private String userId;
	
	private String name;
	
	@Column(unique = true)
	private String email;
	
	private String password;
}
```

- UserRepository

```spring
public interface UserRepository extends JpaRepository<User, Long> {
	
}
```

<hr>

**회원 가입하기 Register, userDTO 객체 바인딩**
- AuthController

```spring
  @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }
```

- register.html

```html
<h1>회원 가입</h1>
		<hr />
		<form th:object="${user}" th:action="@{/register}" method="post">
			<input type="text" placeholder="이름 입력" th:field="*{name}" />
			<br /><br />
			<input type="text" placeholder="이메일 입력" th:field="*{email}" />
			<br /><br />
			<input type="password" placeholder="비밀번호 입력" th:field="*{password}" />
			<br /><br />
			<input type="password" placeholder="비밀번호 확인" th:field="*{confirmpassword}" />
			<br /><br />
			<button type="submit">Submit</button>
		</form>
```

![register](https://github.com/user-attachments/assets/c42a7879-b218-4451-8558-cf09006186ff)

- 바인딩 객체 userDTO 만들기

```spring
@GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new UserDTO)());
        return "register";
    }
```

<hr>

**가입하기 요청(Controller에 UserDTO 전달)**

```spring
    @PostMapping("/register")
    public String register(@ModelAttribute("user") UserDTO user) {
        System.out.println("유저DTO객체 :" + user);
        return "redirect:/login";
    }
```

![register2](https://github.com/user-attachments/assets/c40f9cdf-8b3d-49fc-b273-5a128f9bbafc)

`console 출력`

```spring
유저DTO객체 :UserDTO(name=홍길동, email=hong@daum.net, password=1234, confirmPassword=1234)
```

<hr>

**DB에 저장하는 Service Controller에서 호출**

- UserService

```spring
@Service
@ final 필드에 객체들을 생성자 주입
public class UserService {

	private final UserRepository userRepo;
	private final ModelMapper modelMapper;
	
	public void save(UserDTO userDTO) {
		User user = mapToEntity(userDTO);
		user.setUserId(UUID.randomUUID().toString());
		userRepo.save(?);
	}
    
	private User mapToEntity(UserDTO userDTO) {
		return modelMapper.map(userDTO, User.class);
	}
}
```

- AuthController

```spring
@Controller
@RequiredArgsConstructor
public class AuthController {
	
	private final UserService uService;
...

    @PostMapping("/register")
    public String register(@ModelAttribute("user") UserDTO userDTO) {
        System.out.println("유저DTO객체 :" + userDTO);
        //서비스로 DB에 저장하기

        return "login";
    }
```

![register4](https://github.com/user-attachments/assets/0e50cfe0-6c99-496e-8ce6-883ee73145cb)

- 저장후 로그인페이지로 성공메시지 =  true 전달

```spring
model.addAttribute("successMsg", true);
```

- login

```spring
<h1>로그인</h1>
		<hr />
		<div th:if="${successMsg}">
			<h1>가입성공!</h1>
			<p>가입해주셔서 감사합니다. 이제 당신의 비용관리를 시작하세요!</p>
		</div>
```

![register5](https://github.com/user-attachments/assets/03befac9-5c5b-4f94-815a-044622f79f63)

<hr>

**로그인 페이지**
```html
<h1>로그인</h1>
		<hr />
		<div th:if="${successMsg}">
			<h1>가입성공!</h1>
			<p>가입해주셔서 감사합니다. 이제 당신의 비용관리를 시작하세요!</p>
		</div>
		<div>
			<form action="">
				<input type="text" name="email" placeholder="이메일 입력" />
				<br /><br />
				<input type="text" name="password" placeholder="패스워드 입력" />
				<br /><br />
				<button type="submit">로그인</button>
			</form>
		</div>
```

![Login3](https://github.com/user-attachments/assets/54d136ce-2c8c-4306-92ed-0c70dfeefa82)


<hr>

**유저 유효성 검사 테스트**
- UserDTO

```spring
@NotBlank(message = "이름을 작성해주세요")
	private String name;
	
    @NotBlank(message = "이메일을 입력해주세요")
	@Email(message = "이메일 형식이 아닙니다")
	private String email;
	
	@NotBlank(message = "패스워드를 입력해주세요")
	@Size(min = 4, message = "패스워드는 4자이상")
	private String password;
	
	private String confirmPassword; //DTO에만
```

- register.html
  - 유효성검사 실패시 에러메세지 출력을 위한 타임리프 태그작성

```html
<input type="text" placeholder="이름 입력" th:field="*{name}" />
			<span th:if="${#fields.hasErrors('name')}" th:errors="*{name}"></span>
			<br /><br />
```
- `다른 필드 eamil, password도 같이 작성하기`

<hr>

- `컨트롤러 유효성 체크 @valid와 에러발생 BindingResult`

```spring
  @PostMapping("/register")
    public String register(@ModelAttribute("user") UserDTO userDTO, Model model) {
        System.out.println("유저DTO객체 :" + userDTO);
        if(result.hasErrors()) {
        	return "register";
        }
        uService.save(userDTO);
        model.addAttribute("successMsg", true);
        return "login";
    }

```

![register6](https://github.com/user-attachments/assets/38db9c01-4e1f-4054-a195-bf155f637a40)

<hr>


**프론트엔드 유효성검사 - 제이쿼리 validation**

![j-qureyt](https://github.com/user-attachments/assets/4e043889-c866-492b-81c4-062a7918fbaf)

- validation은 제이쿼리 플러그인이라서 제이쿼리도 필요함

```html
<script th:src="@{/js/jquery.js}"></script>
		<script th:src="@{/js/jquery-validation.js}"></script>
		<script th:src="@{/js/register.js}"></script>
```

- register.js 는 이 페이지에서 유효성 검사를 작성할 js 파일이다.

위치는 순서대로 `1.제이쿼리`, `2.제이쿼리`발리데이션, `3.커스텀 js`

- register.js
  - register.html에 form에 id registerForm 추가

```script
$(function () {
  const $registerForm = $('#registerForm');

  if ($registerForm.length) {

    $registerForm.validate({

      rules: {

      },
      messages: {

      }

    });
  }
}
```

- html document가 먼저 준비된 다음 자바스크립트 실행.
- const $id = $('#id') 제이쿼리 객체.
- validate() 함수를 호출하여 검증 처리.
- rules 검증할 규칭 정의.
- messages 검증 실패시 메시지.

```script
      rules: {
        name: {
          required: true,
          minlength: 3
        },
        email: {
          required: true,
          email: true
        },
        password: {
          required: true,
          minlength: 5,
          maxlength: 15
        },
        confirmPassword: {
          required: true,
          equalTo: '#password'
        }
      },
      messages: {
        name: {
          required: '이름을 입력해 주세요',
          minlength: '적어도 3개의 문자이상 작성해주세요'
        },
        email: {
          required: '이메일을 입력해 주세요',
          email: '이메일 형식에 맞게 입력해 주세요'
        },
        password: {
          required: '패스워드 입력해 주세요',
          minlength: '패스워드는 최소 5자 이상',
          maxlength: '패스워드는 최대 15자 까지'
        },
        confirmPassword: {
          required: '패스워드 확인 입력해 주세요',
          equalTo: '패스워드가 같지 않습니다!'
        }
      }
```

![login4](https://github.com/user-attachments/assets/4526e97f-7a41-482f-8ee3-776669cc0861)

**★ rules 사용 옵션**
- required : 필수 입력 엘리먼트입니다.

- remote : 엘리먼트의 검증을 지정된 다른 자원에 ajax 로 요청합니다.

- minlength : 최소 길이를 지정합니다.

- maxlength : 최대 길이를 지정합니다.

- rangelength : 길이의 범위를 지정합니다.

- min : 최소값을 지정합니다.

- max : 최대값을 지정합니다.

- range : 값의 범위를 지정합니다.

- step : 주어진 단계의 값을 가지도록 합니다.

- email : 이메일 주소형식으 가지도록 합니다.

- url : url 형식을 가지도록 합니다.

- date : 날짜 형식을 가지도록 합니다.

- dateISO : ISO 날짜 형식을 가지도록 합니다.

- number : 10진수를 가지도록 합니다.

- digits : 숫자 형식을 가지도록 합니다.

- equalTo : 엘리먼트가 다른 엘리먼트와 동일해야 합니다.

