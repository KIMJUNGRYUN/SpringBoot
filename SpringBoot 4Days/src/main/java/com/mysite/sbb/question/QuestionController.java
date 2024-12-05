package com.mysite.sbb.question;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerForm;
import com.mysite.sbb.answer.AnswerRepository;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.awt.print.Pageable;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private QuestionService qService;
    @Autowired
    private UserService uService;

    @RequestMapping("/list")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
        Page<Question> paging = this.qService.getList(page);
        model.addAttribute("paging", paging);
        return "q_list"; //forward
    }

    @RequestMapping("/detail/{id}")
    public String detail(@PathVariable int id, Model model, AnswerForm answerForm) {
        //서비스로 질문내용을 가져온다
        Question q = this.qService.getQuestion(id);
        model.addAttribute("q", q);
        return "q_detail";
    }
    //질문 글쓰기는 인증되지 않으면 요청 안되게(접근불가)
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String create(QuestionForm questionForm) {
        return "q_form";
    }

    //질문 글쓰기는 인증되지 않으면 요청 안되게(접근불가)
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String qcreate(@Valid QuestionForm questionForm, //검증
                          BindingResult bindingResult, Principal principal) {   //결과
        //@Valid 로 해당 객체를 검사한다. 결과는 BindingResult 에 저장됨
        if (bindingResult.hasErrors()) {
            return "q_form";
        }
        SiteUser siteUser = this.uService.getUser(principal.getName());
        qService.createQuestion(questionForm.getSubject(),
                                questionForm.getContent(), siteUser);

        //성공적으로 저장됨
       return "redirect:/question/list";
    }

    //수정: 인증된 유저
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String modify(@PathVariable("id") int id, Principal principal, QuestionForm questionForm) {
        Question q = this.qService.getQuestion(id);

        if (!q.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"수정권한이 없습니다.");
        }
        questionForm.setSubject(q.getSubject());
        questionForm.setContent(q.getContent());
        return "q_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String questionModify(@Valid QuestionForm questionForm,
                                 BindingResult bindingResult,
                                 Principal principal, @PathVariable("id") int id) {
        if (bindingResult.hasErrors()) {              //주소 변수 PathVariable
            return "q_form";
        }
        Question q = this.qService.getQuestion(id);
        if (!q.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.qService.modify(q, questionForm.getSubject(), questionForm.getContent());
        return "redirect:/question/detail/" + id;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String delete(Principal principal, @PathVariable("id") int id) {
        Question q = this.qService.getQuestion(id);
        if(!q.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.qService.delete(q);
        return "redirect:/";

    }
}
