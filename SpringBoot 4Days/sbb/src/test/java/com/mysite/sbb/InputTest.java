package com.mysite.sbb;

import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionService;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class InputTest {
    
    @Autowired
    private QuestionService qService;

    @Autowired
    private UserService uService;
    
    @Test
    void set(){
        for(int i = 1; i <= 300; i++){
            String s = String.format("테스트 데이터입니다:[%03d]", i);
            String c = "내용무";
            SiteUser user = uService.getUser("kim665700");
            qService.createQuestion(s, c, user);
        }


    }
}
