package mysite.expense.controller;


import lombok.RequiredArgsConstructor;
import mysite.expense.dto.ExpenseDTO;
import mysite.expense.dto.ExpenseFilterDTO;
import mysite.expense.service.ExpenseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.text.ParseException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ExpenseFilterController {

    private final ExpenseService expService;


    @GetMapping("/filterExpenses")
    public String filterExpenses(@ModelAttribute("filter") ExpenseFilterDTO expenseFilterDTO,
                                 Model model) throws ParseException {
         System.out.println(expenseFilterDTO);
        List<ExpenseDTO> list = expService.getFilterExpenses(expenseFilterDTO);
        model.addAttribute("expenses", list);
        Long total = expService.totalExpenses(list);
        model.addAttribute("total", total);
        return "e_list";
    }
}
