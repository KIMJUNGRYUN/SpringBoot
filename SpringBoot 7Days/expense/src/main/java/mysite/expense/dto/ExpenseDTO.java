package mysite.expense.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;


// get set toString 메소드 등 관련 전부를 다 만들어줌, 생성자 빼고
@Data
// 전체 필드 생성자
@AllArgsConstructor
// 기본 생성자 ()
@NoArgsConstructor
// @RequiredArgsConstructor 특정변수만 생성자 넣고싶을때 final 사용해서 생성
public class ExpenseDTO {



    private Long id;

    private String expenseId;

    @NotBlank(message = "이름을 입력해 주세요")
    @Size(min = 3, message = "이름을 3자 이상 적어주세요")
    private String name;

    @NotBlank(message = "설명을 입력해 주세요")
    private String description;

    @NotNull(message = "가격을 입력해 주세요.")
    @Min(value = 10, message = "최소 10원 이상입니다.")
    private Long amount;

    private Date date;

    private String dateString; // 날짜를 입력받을때 사용

}
