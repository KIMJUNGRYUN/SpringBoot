# 비용관리 연습

**DTO와 JPA 엔티티 만들기**
- DTO는 데이터를 화면(프론트)에서 받아 각 계층(컨트롤러,서비스,리포지토리)간에 데이터 전달시 사용.
```spring
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseDTO {
	
	private Long id;
	
	private String expenseId;
	
	private String name;
	
	private String description;
	
	private long amount;
	
	private Date date;
	
	private String dateString; 

}
​```

**JPA 엔티티**
```spring
@Entity
@Table(name = "tbl_expenses")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Expense {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique = true)
	private String expenseId;
	
	private String name;
	
	private String description;
	
	private long amount;
	
	private Date date;

}
```

**JPA란?**
- `JPA는 Java Persistence API`의 약자
- `Persistence`라는 단어는 `Java DTO(Data Transfer Object)`에게 '없어지지 않고 오랫동안 지속'되는 '영속성(persistence)'을 부여해준다는 의미.

**Entity**
- `Entity`란 DB에서 영속적으로 저장된 데이터를 자바 객체로 매핑하여 '인스턴스의 형태'로 존재하는 데이터

**엔티티와 DTO를 나누는 이유**
- `Entity`를 보호할 수 있다. 사용자 즉 뷰와 컨트롤러에 노출되지 않는다
- `view`, `service`와 통신하는 **DTO** 클래스는 자주 변경된다. 반면 **Entity**는 그에 비해 변경도 적고, 영향범위는 매우크다
`Entity`
- 테이블에 대응하는 하나의 클래스.
`DTO`
- 계층간 데이터를 교환할 때 사용하는 객체.
- 로직을 갖고 있지 않는 데이터 객체, getter/setter 메소드만 갖음.

<hr>

**Repository**
```spring
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

}
```
- 인터페이스를 구현한 클래스를 spring JPA가 자동으로 구현
- **findAll**() 메소드
  - Member 테이블에서 레코드 전체 목록을 조회 List<Member> 객체가 리턴.
 

- **findById(id)**
 - Member 테이블에서 기본키 필드 값이 id인 레코드를 조회 Optional<Member> 타입의 객체가 리턴.

- **save(member)**
 - Member 객체를 Member 테이블에 저장 객체의 id(기본키) 속성값이 0이면 INSERT / 0이 아니면 UPDATE.

- **saveAll(memberList)**
 - Member 객체 목록을 Member 테이블에 저장.

- **delete(member))**
 - Member 객체의 id(기본키) 속성값과 일치하는 레코드를 삭제.

- **deleteAll(memberList)**
 - Member 객체 목록을 테이블에서 삭제.

- **deleteAll(memberList)**
 - Member 객체 목록을 테이블에서 삭제.

- **count()**
 - Member 테이블의 전체 레코드 수를 리턴.
   
- **exists(id)**
 - Member 테이블에서 id에 해당하는 레코드가 있는지 true/false를 리턴.

   
- **flush())**
 - 지금까지 Member 테이블에 대한 데이터 변경 작업들이 디스크에 모두 기록.

<hr>

**비용서비스와 모든 비용리스트를 찾는 메서드**
`생성자 주입(필드 주입) => dependency injection(DI)`
```spring
@Service
public class ExpenseService {
	
	private ExpenseRepository expRepo;
	
	public ExpenseService(ExpenseRepository expRepo) {
		this.expRepo = expRepo;
	}

	public List<Expense> getAllExpenses(){
		return expRepo.findAll();
	}
}
```

`룸북 라이브러리`
```spring
@Service
@RequiredArgsConstructor
public class ExpenseService {
	
	private final ExpenseRepository expRepo;
	
	public List<Expense> getAllExpenses(){
		return expRepo.findAll();
	}
}
```
<hr>

**Map 엔티티 => DTO**
- 엔티티 객체는 테이블과 매핑되어 있고 이 객체를 우리가 서비스나 다른 계층에서 사용시` DTO(데이터 전달용)` 객체로 변환
- Client**<-dto->**controller(web)-service-repository(dao)**<-domian(entity)->**DB

`서비스`
```spring
	public List<Expense> getAllExpenses(){
		return expRepo.findAll();
	}
	
	//엔티티 => DTO 변환
	private ExpenseDTO mapToDTO(Expense expense) {
		ExpenseDTO expenseDTO = new ExpenseDTO();
		expenseDTO.setId(expense.getId());
		expenseDTO.setExpenseId(expense.getExpenseId());
		expenseDTO.setAmount(expense.getAmount());
		expenseDTO.setName(expense.getName());
		expenseDTO.setDescription(expense.getDescription());
		expenseDTO.setDate(expense.getDate());
		return expenseDTO;
	}
}
```

`DTO리스트로 변환`
```spring
public List<ExpenseDTO> getAllExpenses(){
		 List<Expense> list = expRepo.findAll();
		 List<ExpenseDTO> listDTO = list.stream().map(this::mapToDTO).collect(Collectors.toList());		 
		 return listDTO;
	}
```

<hr>

**모델매퍼 사용, 리스트 html 만들기**
`ModelMapper란?`
- **서로 다른 클래스의 값을 한 번에 복사하게 도와주는 라이브러리**
  - 떤 Object(Source Object)에 있는 필드 값들을 자동으로 원하는 Object(Destination Object)에 Mapping 시켜주는 라이브러리

```spring
<dependency>
  <groupId>org.modelmapper</groupId>
  <artifactId>modelmapper</artifactId>
  <version>3.0.0</version>
</dependency>
```

`빈등록`
- @SpringBootApplication 은 스프링부트 프로젝트 시작이고 그 안에 @Configulation 포함.
```spring
@SpringBootApplication
public class ExpensemanagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExpensemanagerApplication.class, args);
	}
	
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

}
```

`등록된 모델매퍼 객체를 필요한 서브시에 주입`
```spring
private final ModelMapper modelMapper;

	//엔티티 => DTO 변환
	private ExpenseDTO mapToDTO(Expense expense) {
		ExpenseDTO expenseDTO = modelMapper.map(?, ExpenseDTO.class);
		return expenseDTO;
	}
```

`templates 폴더에 e_list.html 만듬`

```html
<!DOCTYPE html>
<html lang="ko" xmlns:th="http://www.thymeleaf.org">
	<head>
		<meta charset="UTF-8" />
		<meta name="viewport" content="width=device-width, initial-scale=1.0" />
		<title>Expense Manager</title>
	</head>
	<body>
		<h1>비용 리스트</h1>
		<hr />
	</body>
</html>
```
<hr>

**렌터(Render) view 페이지 by Controller**

![랜더](https://github.com/user-attachments/assets/e265eae4-dd2b-4e0c-b090-1b83e576fac7)

```spring
@Controller
public class ExpenseController {
	
	@GetMapping("/expenses")
	public String showExpenseList() {
		return "expenses-list";
	}
}
```

`JPA에서 DB 설정이 안되었기 때문에 에러`
![ㅕ기](https://github.com/user-attachments/assets/f6e3ab01-a185-4db7-9e20-c12c4e717ffe)

<hr>

**MySQL 드라이버**
```spring
<dependency>
			<groupId>com.mysql</groupId>
			<artifactId>mysql-connector-j</artifactId>
			<scope>runtime</scope>
		</dependency>
```
`application.properties`
```spring
#server.port=8090
#server.servlet.context-path=/mysite

# MYSQL DB 
spring.datasource.url=jdbc:mysql://localhost:3306/expense
spring.datasource.username=root
spring.datasource.password=1234
# JPA table ddl auto
spring.jpa.hibernate.ddl-auto=update
# Console sql show
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

`MySQL 워크벤치 expense 스키마 생성`
- `spring.jpa.hibernate.ddl-auto=update` 에 의해 데이터베이스 expense에 접속하면 (워크벤치로 expense 미리생성) 자동으로 테이블을 생성.

![ㄴㅁ](https://github.com/user-attachments/assets/c6e67d14-653b-4e1b-b731-dd85ef099a2b)

<hr>

**비용 리스트를 타임리프 View에 전달**
`컨트롤러에서 간단한 리스트 만들기`
```spring
	private static List<ExpenseDTO> list = new ArrayList<>();
	
	static {
		ExpenseDTO e1 = new ExpenseDTO();
		e1.setName("도시가스 요금");
		e1.setDescription("우리집 가스요금");
		e1.setAmount(37000);
		e1.setDate(new Date(System.currentTimeMillis()));
		list.add(e1);
		
		ExpenseDTO e2 = new ExpenseDTO();
		e2.setName("전기 요금");
		e2.setDescription("우리집 전기요금");
		e2.setAmount(27500);
		e2.setDate(new Date(System.currentTimeMillis()));
		list.add(e2);
	}
```
`이 리스트를 Model 객체 model로 전달`
```spring
	@GetMapping("/expenses")
	public String showExpenseList(Model model) {
		model.?("expenses", ? );
		return "expenses-list";
	}
}
```

<hr>

**타임리프에서 모델 데이터 출력 && 타임리프 기본 문법**

![ㅇㄴㅁㅇㅁㄴㅇ](https://github.com/user-attachments/assets/7e72cf49-469a-4764-89cd-55dc25c5dad1)
```html
<body>
		<h1>비용 리스트</h1>
		<hr />
		<table>
			<thead>
				<th>이름</th>
				<th>설명</th>
				<th>요금</th>
				<th>날짜</th>
			</thead>
			<tbody>
				<tr th:each="exp : ${ ? }">
					<td th:text="${exp.name}"></td>
					<td th:text="${exp.description}"></td>
					<td th:text="${exp.?}"></td>
					<td th:text="${exp.?}"></td>
				</tr>
			</tbody>
		</table>
	</body>
```
`타임리프 Date 타입을 원하는 형태로 포맷`
```html
<div th:text="${#dates.format(날짜데이터, 'yyyy-MM-dd HH:mm:ss')}"></div>
```

`타임리프 기본문법`
![xkdlaflvm](https://github.com/user-attachments/assets/606723fb-f4de-49f5-b611-668ad2794ef4)

**타임리프 html 자동 업데이트**
`1.application.properties`
```spring
# thymeleaf
spring.thymeleaf.cache=false
spring.thymeleaf.prefix=file:src/main/resources/templates/
```
`th:를 붙여서 사용할 수 있는 방법`

![타임리프](https://github.com/user-attachments/assets/d19a1b24-7c1c-4d3c-932e-5731525dbdd2)

**DB에서 데이터 읽기**

```spring
INSERT INTO tbl_expenses (expense_id, name, description, amount, date) 
VALUES("awrewcxv", "도시가스 요금", "우리집 한달 도시가스 비용", 37000, curdate());

INSERT INTO tbl_expenses (expense_id, name, description, amount, date) 
VALUES("ceafdsfe", "전기 요금", "우리집 한달 전기 비용", 25000, curdate());

INSERT INTO tbl_expenses (expense_id, name, description, amount, date) 
VALUES("zsfixefe", "수도 요금", "우리집 한달 수도 비용", 15000, curdate());
```

`DB에서 데이터를 가져올려면 서비스 객체가 필요`

```spring
   @GetMapping("/createExpense")
    public String ShowCreateExpense(Model model) { //ExpenseDTO expenseDTO 넣어도 됨
        model.addAttribute("expense", new ExpenseDTO()); //빈 expense 객체 전달
        return "e_form";
    }
```

![비여ㅛㅇ리스트](https://github.com/user-attachments/assets/4f2cc302-d865-43aa-a92c-7e55b25c23a8)



