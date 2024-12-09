package mysite.expense.repository;

import mysite.expense.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    //SELECT * FROM tbl_expense WHERE expenseId=?
    Optional<Expense> findByExpenseId(String expenseId);
    List<Expense> findByNameContainingOrDescriptionContaining(String keyword, String s);


}
