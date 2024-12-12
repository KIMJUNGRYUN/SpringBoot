package com.mysite.mybatis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@Controller + @ResponseBody => 뷰없이 문자열 또는 객체를 바로 제이슨으로 리턴
//Json 또는 XML 데이터를 반환하며, @ResponseBody 를 기본적으로 포함.
@RestController
public class UserController {

    //DB사용 객체
    @Autowired
    private UserMapper uMapper;

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable int id) {
        User user = uMapper.getUserById(id);
        return user; //자바객체를 자동으로 제이슨 변환해서 리턴
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        List<User> uList= uMapper.getUserList();
        return uList;
    }

    @PostMapping("/users")
    public void createUser(@RequestParam("id") String id,
                           @RequestParam("name") String name,
                           @RequestParam("phone") String phone,
                           @RequestParam("address") String address) {
        uMapper.insertUser(id,name,phone,address);
    }

    //rest api 업데이트를 put 메소드
    @PutMapping("/users/{id}")
    public void updateUser(@PathVariable String id,
                           @RequestParam("name") String name,
                           @RequestParam("phone") String phone,
                           @RequestParam("address") String address) {
        uMapper.updateUser(id,name,phone,address);
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable String id) {
        uMapper.deleteUser(id);
    }

}
