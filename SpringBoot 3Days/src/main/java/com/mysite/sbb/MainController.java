package com.mysite.sbb;

import org.apache.coyote.Request;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {

    @RequestMapping("/") //기본주소로 요청하면은 질문 리스트로 요청 return 으로 매핑
    public String root() {
        //리다이렉트는 새로운 요청(질문컨트롤러)을 하는것
        return "redirect:/question/list";
    }
}
