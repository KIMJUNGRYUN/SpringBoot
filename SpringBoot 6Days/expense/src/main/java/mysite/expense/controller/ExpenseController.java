package mysite.expense.controller;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import mysite.expense.dto.ExpenseDTO;
import mysite.expense.dto.ExpenseFilterDTO;
import mysite.expense.service.ExpenseService;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequiredArgsConstructor //final 붙은 생성자 주입
public class ExpenseController {

    //@Autowired 필드 주입
    private final ExpenseService expService;

    @GetMapping("/expenses")
    public String showList(Model model) {
        model.addAttribute("expenses", expService.getAllExpenses());
        model.addAttribute("filter", new ExpenseFilterDTO());
        return "e_list";
    }

    //get 요청시 비용 입력을 위한 창을 보여주기
    @GetMapping("/createExpense")
    public String ShowCreateExpense(Model model) { //ExpenseDTO expenseDTO 넣어도 됨
        model.addAttribute("expense", new ExpenseDTO()); //빈 expense 객체 전달
        return "e_form";
    }

    @PostMapping("/saveOrUpdateExpense")
    public String saveOrUpdateExpense(@ModelAttribute("expense") ExpenseDTO expenseDTO) throws ParseException {
        System.out.println("입력한 expenseDTO 객체 : " + expenseDTO);
        expService.saveExpenseDetails(expenseDTO);
        return "redirect:/expenses";
    }

    @GetMapping("/deleteExpense")
    public String deleteExpense(@RequestParam("id") String expenseId){
        System.out.println("삭제 비용 번호 : " + expenseId);
        expService.deleteExpense(expenseId);
        return "redirect:/expenses";
    }

    //수정할 페이지 보여주기
    @GetMapping("/updateExpense")
    public String updateExpense(@RequestParam("id") String expenseId, Model model) {
        System.out.println("업데이트 비용 id : " + expenseId);

        //DB 에서 해당 id 의 expense 비용 객체를 전달하여 수정할 수 있게함.
        ExpenseDTO expenseDTO = expService.getExpenseById(expenseId);
        model.addAttribute("expense", expService.getExpenseById(expenseId));
        return "e_form";
    }






}
