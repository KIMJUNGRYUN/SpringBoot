package mysite.expense.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseFilterDTO {
    // 검색어
    private String keyword;
    
    // 정렬, 순서
    private String sortBy;

    //시작일
    private String startDate;

    //종료일
    private String endDate;
}
