package mysite.expense.dto;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;


@Data  //겟셋메소드 + toString 등
@AllArgsConstructor //전체 필드 생성자
@NoArgsConstructor
//기본 생성자 ()
public class ExpenseDTO {

    private Long id;
    private String expenseId;

    @NotBlank(message = "이름을 입력해 주세요")
    @Size(min = 3, message = "이름을 3자 이상 적어주세요")
    private String name;
    @NotBlank(message = "설명을 입력해 주세요")
    private String description;
    @NotNull(message = "가격을 입력해 주세요")
    @Min(value = 10, message = "비용은 최소 10원 이상입니다")
    private Long amount;
    private Date date;
    private String dateString; //날짜를 입력받을때 사용
}
