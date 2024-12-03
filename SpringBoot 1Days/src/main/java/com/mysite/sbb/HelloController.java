package com.mysite.sbb;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

//서블릿 즉 컨트롤러 역할
@Controller
public class HelloController {

    //localhost:8080/hello
    @RequestMapping("/hello") //주소 맵핑
    @ResponseBody //기본 mvc 는 컨트롤러가 화면을 전달하는데 여기서는 바로 표시
    public String Hello() {
        return "Hello World";
    }

    //localhost:8080/sayhello
    @RequestMapping("/sayhello") //주소 맵핑
    @ResponseBody //기본 mvc 는 컨트롤러가 화면을 전달하는데 여기서는 바로 표시
    public String sayHello() {
        return "sayHello World";
    }

    //localhost:8080/hi
    @RequestMapping("/hi") //주소 맵핑
    @ResponseBody //기본s mvc 는 컨트롤러가 화면을 전달하는데 여기서는 바로 표시
    public String hi() {
        return "hi";
    }

}
