package com.mysite.sbb.question;

import com.mysite.sbb.answer.AnswerRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private QuestionService qService;

    @RequestMapping("/list")
    public String list(Model model) {
        List<Question> qList = qService.getList();
        model.addAttribute("qList", qList);
        return "q_list"; //forward
    }

    @RequestMapping("/detail/{id}")
    public String detail(@PathVariable int id, Model model) {
        //서비스로 질문내용을 가져온다
        Question q = this.qService.getQuestion(id);
        model.addAttribute("q", q);
        return "q_detail";
    }

    @GetMapping("/create")
    public String create(QuestionForm questionForm) {
        return "q_form";
    }
    @PostMapping("/create")
    public String qcreate(@Valid QuestionForm questionForm, //검증
                          BindingResult bindingResult) {   //결과
        //@Valid 로 해당 객체를 검사한다. 결과는 BindingResult 에 저장됨
        if (bindingResult.hasErrors()) {
            return "q_form";
        }
        //성공적으로 저장됨
       return "redirect:/question/list";
    }




}
