package mysite.expense.repository;

import mysite.expense.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    Optional<Expense> findByExpenseId(String expenseId);
    //SELECT * FROM tbl_expense WHERE name LIKE %keyword% AND date BETWEEN startDate AND endDate
    List<Expense> findByNameContainingAndDateBetweenAndUserId(String keyword, Date start, Date end, Long userId);
    //유저 ID로 유저의 모든 비용 리스트 가져오기
    List<Expense> findByUserId(Long userId);

}
