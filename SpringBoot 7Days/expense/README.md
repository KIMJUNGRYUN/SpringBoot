# SpringBoot 

**dto 객체를 DB에 저장하는 서비스**
- 현재 브라우저에서 입력받은 DTO 객체(컨트롤러에 바인딩 됨)를 DB에 저장하는 서비스 메소드를 만들어 호출

`service`
```spring
public ExpenseDTO saveExpenseDetails(ExpenseDTO expenseDTO) {
		//1. DTO => Entity
		Expense expense = mapToEntity(expenseDTO);
		//2. DB에 저장
		
		//3. Entity => DTO
		return null;
	}
```

```spring
private Expense mapToEntity(ExpenseDTO expenseDTO) {
		Expense expense = modelMapper.map(expenseDTO, Expense.class);
		//1. expenseId 입력 ( 유니크 문자열 자동생성 )
		
		//2. date 입력
		return null;
	}
```

```spring
//1. expenseId 입력 ( 유니크 문자열 자동생성 )
		expense.setExpenseId(UUID.randomUUID().toString());
```

`유틸패키지 DateTimeUtil`

```spring
   //자바 날짜 date 를 문자열 포맷으로 변환하는 스태틱 메서드
    public static String convertDateString(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일");
        return sdf.format(date);
    }

    //문자열날짜 => sql Date 날짜
    public static Date convertStringToDate(String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date date = sdf.parse(dateString);
        return new Date(date.getTime());
    }
```

`유틸클래스를 이용하여 service의 mapToEntity메서드 완성`

```spring
private Expense mapToEntity(ExpenseDTO expenseDTO) {
		Expense expense = modelMapper.map(expenseDTO, Expense.class);
		//1. expenseId 입력 ( 유니크 문자열 자동생성 )
		expense.setExpenseId(UUID.randomUUID().toString());
		//2. date 입력
		
		return null;
	}
```

`DB에 저장하고 다시 DTO로 변환`

```spring
public ExpenseDTO saveExpenseDetails(ExpenseDTO expenseDTO) {
		//1. DTO => Entity
		Expense expense = mapToEntity(expenseDTO);
		//2. DB에 저장
		expense = expRepo.save(expense);
		//3. Entity => DTO
		return mapToDTO (expense);
	}

<hr>

**비용객체 저장**
```spring
@PostMapping("/saveOrUpdateExpense")
	public String saveOrUpdateExpense(@ModelAttribute("expense") ExpenseDTO expenseDTO) throws ParseException {
		System.out.println("입력한 expenseDTO 객체 : " + expenseDTO);
		//서비스의 메서드로 expensDTO를 DB에 저장

		return "redirect:/expenses";
	}
```

![list5](https://github.com/user-attachments/assets/98d6b52f-dcfe-49f4-addc-5afa24321e71)
![list6](https://github.com/user-attachments/assets/482a6c6e-3fe1-484b-b0ef-3bf49e3a6734)

<hr>

**expenseid 값(유니크)으로 삭제 링크**

```html
<td>
						<a th:href="@{/deleteExpense(id=${expense})}">삭제</a>
					</td>
```

`컨트롤러에 삭제할 expenseId가 전달됨`
```spring
@GetMapping("/deleteExpense")
	public String deleteExpense(@RequestParam String id) {
		System.out.println("삭제 비용 번호 : " + id);
		return "redirect:/expenses";
	}
```
<hr>

**리포지토리에 findBy 필드변수 메서드 만들기**
- 삭제를 하기위해 먼저 id로 삭제할 expense 를 찾아오기.
- 유니크값인 expenseId로 찾기위한 메소드 만들기
- Optinal로 리턴하면 찾지 못했을때는 제외할 수 있음.

```spring
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

	//SELECT * FROM tbl_expense WHERE expenseId=?
	Optional<Expense> findByExpenseId(String expenseId);
}
```


**💡 UUID(Universally Unique Identifier)란**?
- 범용 고유 식별자를 의미하며 중복이 되지 않는 유일한 값을 구성하고자 할때 주로 사용이 됩니다.
- 주로 세션 식별자, 쿠키 값, 무작위 데이터베이스 키 등에 사용이 됩니다.

`커스텀 메소드 이름 작성 방법`

![METHOD](https://github.com/user-attachments/assets/f3b2d31c-8a07-4d24-87f2-70a7bd005edf)
- 응답 결과가 여러건인 경우에는 리포짙터리 메서드의 리턴 타입을 **Question** 이 아닌 **List<Question>**으로 해야 한다.

<hr>

**서비스**
```spring
//비용 id(ExpenseId)로 삭제하기
    public void deleteExpense(String id) {
        Expense expense = expRepo.findByExpenseId(id).orElseThrow(()->
                new RuntimeException("해당 ID의 아이템을 찾을 수 없습니다"));
        expRepo.delete(expense);
    }
```

`이 서비스 메서드를 삭제 컨트롤러에서 호출`

```spring
@GetMapping("/deleteExpense")
	public String deleteExpense(@RequestParam String id) {
		System.out.println("삭제 비용 번호 : " + id);
		//DB에서 id로 찾아서 삭제하기

		return "redirect:/expenses";
	}
```

![비용리슽,ㅡ](https://github.com/user-attachments/assets/5591ceed-2d5b-4f25-962d-be4794041e12)

<hr>

**삭제시 확인 메세지 나오게 하기**

```script
<a onclick="return confirm('정말로 삭제할까요?')" th:href="@{/deleteExpense(id=${exp.expenseId})}">삭제</a>
```

![ㅇㄷ처](https://github.com/user-attachments/assets/01d9c661-db23-49d2-843e-d0c09eb5015f)
- onclick의 return 값이 false 가 될 경우 기존의 동작(url 요청)이 중지됨 => 요청하지 않으므로 삭제 되지 않음.

<hr>

**수정(업데이트) 링크**
`테이블에 수정 링크를 만들기`

```html
<td>
	<a onclick="return confirm('정말로 삭제할까요?')" 	th:href="@{/deleteExpense(id=${exp.expenseId})}">삭제</a>
						|
<a th:href="@{/updateExpense(id=${exp.expenseId})}">수정</a>
					</td>
```

![ㅣ](https://github.com/user-attachments/assets/870498e6-c9a4-470a-8939-718a00045186)

<hr>

**업데이트 컨트롤러**
```spring
@GetMapping("/updateExpense")
	public String updateExpense(@RequestParam String id) {
		System.out.println("업데이트 아이템 : " + id);
		
		return "expense-form";
	}
```
- 실행시 수정을 클릭하면 업데이트 아이템과 `expendId`가 잘 출력됨 
- 하지만 에러가 나는 이유는 `expense-form`에 바인딩 객체 `expense`가 전달되지 않았기 때문
- 그러므로 `expense` 객체를 새로 만들어 전달하면 되는데 여기서 업데이트기 때문에 id로 원래 객체를 DB에서 가져와 전달한다.

**수정전 expenseDTO 가져오기**
`서비스`
```spring
//expenseId로 수정할 expense를 찾아 DTO로 리턴
	public ExpenseDTO getExpenseById(String id) {
		Expense expense = expRepo.findByExpenseId(id).orElseThrow(()->
		new RuntimeException("해당 ID의 아이템을 찾을 수 없습니다"));
		ExpenseDTO expenseDTO = mapToDTO(expense);
        //Form의 날짜입력 형식은 2023-6-17 형식이기 때문에 날짜만 다시 변환
		expenseDTO.setDateString(DateTimeUtil.convertDateToInput(expense.getDate()));
		return mapToDTO(expense);
	}
```

`DateTimeUtil`
```spring
//sql날짜 => 문자열날짜
	public static String convertDateToString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}
	
	//sql날짜 => 폼입력창날짜
	public static String convertDateToInput(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}
```

`컨트롤러`

```spring
@GetMapping("/updateExpense")
	public String updateExpense(@RequestParam String id, Model model) {
		System.out.println("업데이트 아이템 : " + id);
		model.addAttribute("expense", ? );
		return "expense-form";
	}
```

![끼아아아앙아아아아이ㅏㅇ아ㅏ](https://github.com/user-attachments/assets/bb5e8ccf-4943-42d2-b874-453040e8c49e)

<hr>

**새로 입력/ 수정 페이지 구분**
`제목구분`
```html
<h1 th:if="${expense.id == null}">새 비용 만들기</h1>
<h1 th:if="${expense.id != null}">비용 수정하기</h1>
```
`버튼구분`
```html
<button type="submit" th:text="${expense.id}==null?'저장':'수정'"></button>
```
`폼 아래에 돌아가기 링크`
```html
</form>
		<a th:href="@{/expenses}">리스트로 돌아가기</a>
	</body>
```

![끄아아아아아아아](https://github.com/user-attachments/assets/2377eef9-1a56-4c42-8e42-0590adbe63d0)

- 수정버튼을 누르면 컨트롤로러 바인딩된 객체 `expense`가 전달됨
```html
<form th:action="@{/saveOrUpdateExpense}"  method="post"
```
- 현재 저장과 업데이트 동일, 이 차이는 id가 있으면 업데이트 없으면 새로 저장.
- 그러므로 타임리프 id 입력 만들기(hidden).
```html
button>
    <input type="hidden" th:field="*{id}" />
    <input type="hidden" th:field="*{expenseId}" />
		</form>

```![ㅠㄱ](https://github.com/user-attachments/assets/44df7a5a-6ba6-4041-bad7-d897f0e4964e)

- 수정버튼 클릭시 컨트롤러 id를 가진 객체가 전달됨.

<hr>


**수정 컨트롤러, 서비스 수정 완료, 리팩토링**
```spring
@PostMapping("/saveOrUpdateExpense")
	public String saveOrUpdateExpense(@ModelAttribute("expense") ExpenseDTO expenseDTO) throws ParseException {
		System.out.println("입력한 expenseDTO 객체 : " + expenseDTO);
		expService.saveExpenseDetails(expenseDTO);
		return "redirect:/expenses";
	}
```
- 리포지토리.save(객체) 이 save 메소드는 기본 메서드로 객체의 id가 없을경우 새로 저장, 객체의 id가 있을경우 그 id로 DB를 업데이트.
- 단 하나 수정할 부분은 id가 없을경우에만 UUID를 생성하도록 수정

```spring
//DTO => 엔티티 
	private Expense mapToEntity(ExpenseDTO expenseDTO) throws ParseException {
		Expense expense = modelMapper.map(expenseDTO, Expense.class);
		//1. expenseId 입력 ( 유니크 문자열 자동생성 )
		if(expenseDTO.getId() == null) {
			expense.setExpenseId(UUID.randomUUID().toString());
		}
		//2. date 입력
		expense.setDate(DateTimeUtil.convertStringToDate(expenseDTO.getDateString()));
		return expense;
	}
```

`반복되는 코드 리팩토링`
```spring
expRepo.findByExpenseId(id).orElseThrow(()->
			new RuntimeException("해당 ID의 아이템을 찾을 수 없습니다"));
```

`메소드 getExpense`

```spring
//리팩토링
	private Expense getExpense(String id) {
		return expRepo.findByExpenseId(id).orElseThrow(()->
			new RuntimeException("해당 ID의 아이템을 찾을 수 없습니다"));
	}
```

![default](https://github.com/user-attachments/assets/00b53454-4f18-49e3-a7bd-709ca4f3a41f)

<hr>

**검색창, DTO, 필터컨트롤러**
- 리스트 페이지에 검색창 만들기.
```html
</hr>
        <form th:action="@{/}">
			키워드 검색 : <input type="text" placeholder="키워드 입력" />
            <input type="submit" value="검색" />
		</form>
```

![수정](https://github.com/user-attachments/assets/f1e10145-105e-4596-8ea6-e2b1ca7ebf6d)

- 검색을 위한 필터 클래스를 dto 패키지에 만들기
```spring
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseFilterDTO {

	private String keyword;
}
```
- 컨트롤러에서 리스트 페이지로 객체 바인딩 전달
```spring
@GetMapping("/expenses")
	public String showExpenseList(Model model) {
		model.addAttribute("expenses", expService.getAllExpenses());
		model.addAttribute("filter", new ExpenseFilterDTO());
		return "expenses-list";
	}
​
```

- 리스트 페이지의 검색창 form에 객체 바인딩
```html
method="get" th:object="${filter}"
```
`메소드는 GET으로 검색어는 주소창에 나와도 됨`
```html
th:action="@{/filterExpenses}"
```
`입력창에 객체 filter의 필드변수`
```html
th:field="*{keyword}"
```

**필터 컨트롤러 생성**
```spring
@Controller
public class ExpenseFilterController {

	@GetMapping("/fillterExpenses")
	public String filterExpenses(@ModelAttribute("filter") ExpenseFilterDTO expenseFilterDTO) {
		System.out.println(expenseFilterDTO);
		return "expenses-list";
	}
}
```
<hr>

**리파지토리 findByName 메소드 만들어 컨트롤러에서 호출해 리스트로 전달**
- 이름으로 검색하는데 이때 WHERE name LIKE %keyword%
```spring
findByNameContaining(String keyword);
```
- 이때 리턴(검색결과)은 여러개의 expense로 나올수 있다.
```spring
List<Expense>
```

- 서비스
```spring
public List<ExpenseDTO> getFilterExpenses(String keyword){
		List<Expense> list = expRepo.findByNameContaining( ? );
		return list.stream().map(this:: ? ).collect(Collectors. ? ());
	}
```

-필터 컨트롤러
`우선 서비스객체를 사용하기 위해 주입.`
```spring
@Controller
@RequiredArgsConstructor
public class ExpenseFilterController {
	
	private final ExpenseService expService;
	@GetMapping("/filterExpenses")
	public String filterExpenses(@ModelAttribute("filter") ExpenseFilterDTO expenseFilterDTO,
								 Model model) {
		System.out.println(expenseFilterDTO);
		List<ExpenseDTO> list = expService.getFilterExpenses(expenseFilterDTO);
		model.addAttribute("expenses", list );
		return "expenses-list";
	}
```

![a2](https://github.com/user-attachments/assets/057f5e93-388e-4576-94a8-fa047fcb1705)
![a1](https://github.com/user-attachments/assets/7285c97a-97d9-4d02-9de9-d538f7285e36)

<hr>

**정렬옵션 추가**
![l1](https://github.com/user-attachments/assets/9564a08a-e715-4be8-b606-9de663b89c1e)
- 필터 DTO sortBy 추가
```spring
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseFilterDTO {

	private String keyword;
	
	private String sortBy;
}
```

- 리스트
`검색버튼 위에 옵션 태그 추가`
```html
<select th:field="*{sortBy}">
				<option value="date">날짜</option>
				<option value="amount">가격</option>
			</select>
			<input type="submit" value="검색" />
```
- 키워드 검색시 sortBy 옵션으로 선택한 date 또는 amount 추가

![sort](https://github.com/user-attachments/assets/47a96b91-520a-498d-9351-89ec080a7bdf)

- 서비스에 sortBy를 추가해서 메서드 만들기
```spring
	public List<ExpenseDTO> getFilterExpenses(String keyword, String sortBy){
		List<Expense> list = expRepo.findByNameContaining(keyword);
		List<ExpenseDTO> filterlist = list.stream().map(this::mapToDTO).collect(Collectors.toList());
		if(sortBy.equals("date")) {
			
		} else {
			
		}
		return filterlist;
	}
```
```spring
if (sortBy.equals("date")) {
			filterlist.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));
		} else {
			 filterList.sort((o1, o2) -> o2.getAmount().compareTo(o1.getAmount()));
		}
```

- 필터컨트롤러 (에러처리)
```spring
expService.getFilterExpenses(expenseFilterDTO.getKeyword(), getAmount());
```

![sort2](https://github.com/user-attachments/assets/e8090941-bf04-41d8-ad27-85347b996f6e)
![sort1](https://github.com/user-attachments/assets/596bfb0e-5261-4bf0-9495-6b47fb697ea6)

