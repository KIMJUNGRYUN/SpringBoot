#SpringBoot 

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
``html
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

