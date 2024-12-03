package com.mysite.sbb.answer;

import com.mysite.sbb.question.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AnswerService {

    @Autowired
    private AnswerRepository aRepo;

    //해당 질문에 답변을 저장하는 메소드
    public void create(Question q, String content) {
        Answer a = new Answer();
        a.setQuestion(q);
        a.setContent(content);
        a.setCreateDate(LocalDateTime.now());
        this.aRepo.save(a);
    }
}
