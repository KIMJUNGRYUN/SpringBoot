# 비용관리2

**그외 모든 예외 발생시 처리**
`GlobalDefaultExceptionHandler`에 추가 메서드를 만듬.

```spring
@ExceptionHandler(Exception.class)
	public String handleGlobalException(HttpServletRequest request,
			Exception ex, Model model ) {
		
		model.addAttribute("serverError", true);
		model.addAttribute("message", ex.getMessage());
		return "response";
	}
```

- `@ControllerAdvice`는 모든 컨트롤러에 적용
- `@ExceptionHandler`(예외종류류.class) 괄호안의 예외발생시 이 메소드에서 처리함 현재 ControllerAdvice가 적용되어 모든 컨트롤러에서 이 예외가 발생하면 여기서 처리됨.

`response.html 추가`

```html
<div th:if="${serverError}">
			<h1>관리자에게 문의해 주세요</h1>
			<p th:text="${message}"></p>
			<a th:href="@{/expenses}">Back to Home</a>
		</div>
```

- 일부러 자바코드로 에러 내보기
`ExpenseController`

```spring
	@GetMapping("/expenses")
	public String showExpenseList(Model model) {
		List<ExpenseDTO> list = expService.getAllExpenses();
		list = null;
		list.size(); //에러발생
```

- 로그인하면 에러페이지가 표시됨

![error4](https://github.com/user-attachments/assets/3a3dd08a-d673-4758-800a-ed7ae7caee93)

<hr>

**조금 더 개선하기**

`로그인/가입 페이지 수정`

- 로그인 페이지에 맨 밑에 가입하기 링크

```html
<a th:href="@{/register}">가입하기</a>
```

- 가입하기(register)페이지 맨 밑에 로그인 링크

```html
<a th:href="@{/login}">로그인</a>
```

`가입하면 response 페이지로 보내기`

```spring
@PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") UserDTO userDTO,
    					   BindingResult result, Model model) {
        System.out.println("유저DTO객체 :" + userDTO);
        if(result.hasErrors()) {
        	return "register";
        }
        uService.save(userDTO);
        model.addAttribute("successMsg", true);
        return "response";
    }
```

- response 페이지에 기존 login 페이지의 성공 메시지를 넣어서 login 페이지에는 로그인 폼만 표시하도록 처리.

```html
<div th:if="${successMsg}">
			<h1>가입성공!</h1>
			<p>가입해주셔서 감사합니다. 이제 당신의 비용관리를 시작하세요!</p>
			<a th:href="@{/login}">로그인</a>
		</div>
```

`로그인된(인증된)유저가 다시 /login 페이지로 요청시에는? => /expenses로 보내기`

- AuthController

 ```spring
@GetMapping({ "/login", "/" })
	public String showLoginPage(Principal principal) {
		if(principal == null) {
			return "login";
		}
		return "redirect:/expenses";
	}
```

- `Principal` 객체는 시큐리티 인증시 유저의 정보를 담아 유지하는 객체이다. `principal` 객체가 null 이란 것은 인증이 안된 상태를 의미
- 서비스에서는 `Authentication` 객체를 가져와 쓸 수 있음. (마찬가지로 유저인증정보) 컨트롤러에서는 Principal 객체에서 유저정보를 가져옴.

```spring
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
```

`비용 정보를 입력할때 오늘 이후날짜는 선택이 되지 않게 설정`

- e_form.html

```script
<script>
			dateString.max = new Date().toISOString().split('T')[0];
</script>
```

<hr>

**홈 화면에서 이번달 비용 리스트만 보여주기**

- 비용관리를 할때 무조건 전체 리스트를 가져오는것보다 이번달의 비용 리스트만 가져오는 것이  쉽고 빠르게 데이터를 가져온다. ( 비용리스트가 많을수록 )
  - 사용자도 처음부터 너무 많은 리스트를 보기보단 이번달 리스트만 보는것이 더 편하므로 이렇게 수정.
  - /expenses 페이지에서 우선 화면의 시작 날짜 입력창에 이번달의 1번째 날짜를 표시하고 마지막 날짜 입력창에 현재 날짜를 표시.

​`DateTimeUtil`

```spring
// 입력창 시작 날짜를 이번달 첫일로 "2023-06-01"
	public static String getCurrentMonthStartDate() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate today = LocalDate.now();
		return today.withDayOfMonth(1).format(formatter);
	}
	
	// 입력창 마지막 날짜를 현재 날짜로 "2023-06-25"
	public static String getCurrentMonthDate() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate today = LocalDate.now();
		return today.format(formatter);
```

- 이렇게 만든 문자열을 미리 입력해 줄려면 `ExpenseFliterDTO`객채에 처음 생성시 입력해 주어서 `e_list`페이지에 전달.

`ExpenseFilterDTO 필드변수 startDate, endDate를 미리 입력하는 생성자`

```spring
//한달 시작일과 현재일을 미리 입력하기 위한 생성자
	public ExpenseFilterDTO(String startDate, String endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
	}
```

`ExpenseController에서 미리 홈화면에 ExpenseFilterDTO 객체 전달`

```spring
model.addAttribute("filter", new ExpenseFilterDTO(DateTimeUtil.getCurrentMonthStartDate(), DateTimeUtil.getCurrentMonthDate()));

![login11](https://github.com/user-attachments/assets/aeba20bd-f7c7-418a-9889-aae910ccced6)

- 전체 리스트가 표시됨. 해결하기 위해서 비용검색을 하는 리포지토리 메서드를 따로 만듬.

`ExpenseRepository`

```spring
	//SELECT * FROM tbl_expense WHERE date BETWEEN startDate AND endDate
	List<Expense> findBy ? (Date startDate, Date endDate, Long id);
```

- `ExpenseController`에서 /expenses 페이지 리스트를 전달할때 서비스의 `getAllExpenses()`를 사용.
  - 이 `getAllExpenses()`를 이번달만 검색하도록 수정해야함.

`ExpenseService`

```spring
List<Expense> list = expRepo.findByUserIdAndDateBetween(
				Date.valueOf(LocalDate.now().withDayOfMonth(1)), 
				Date.valueOf(LocalDate.now()),
				user.getId());
```

​![list8](https://github.com/user-attachments/assets/b4d17a67-1c0d-4409-b0c5-6009e806fba1)

<hr>

**Update Controller**

`컨트롤`

```spring
@GetMapping("/updateExpense")
	public String updateExpense(@RequestParam String id) {
		System.out.println("업데이트 아이템 : " + id);
		
		return "expense-form";
	}
```

- 실행시 수정을 클릭하면 업데이트 아이템과 expendId가 잘 출력됨
  - 하지만 에러가 나는 이유는 e_form에 바인딩 객체 expense가 전달되지 않았기 때문.
  - expense 객체를 새로 만들어 전달하면 되는데 업데이트 때문에 id로 원래 겍체를 DB에서 가져와 전달.
