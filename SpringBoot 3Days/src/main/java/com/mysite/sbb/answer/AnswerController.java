package com.mysite.sbb.answer;

import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/answer") //get post 전부 다 됨
public class AnswerController {

    @Autowired
    private QuestionService qService;
    @Autowired
    private AnswerService aService;

    @PostMapping("/create/{id}")
    public String createAnswer(@PathVariable int id, Model model,
                               @Valid AnswerForm answerForm, BindingResult bindingResult) {
        //질문을 가져온다.
        Question q = this.qService.getQuestion(id);

        if (bindingResult.hasErrors()) {
            model.addAttribute("q",q); //다시 되돌아갈때 질문객체 전달
            return "q_detail";
        }

        //답변을 저장하는 서비스
        this.aService.create(q, answerForm.getContent());
        //답변을 저장한후 다시 질문 상세 페이지를 요청함
        return "redirect:/question/detail/" + id;
    }

}
