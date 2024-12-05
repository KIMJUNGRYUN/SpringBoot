package com.mysite.sbb.answer;

import com.mysite.sbb.question.DataNotFoundException;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.user.SiteUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AnswerService {

    @Autowired
    private AnswerRepository aRepo;

    //해당 질문에 답변을 저장하는 메소드(유저 추가)
    public void create(Question q, String content, SiteUser author) {
        Answer a = new Answer();
        a.setQuestion(q);
        a.setContent(content);
        a.setCreateDate(LocalDateTime.now());
        a.setAuthor(author);
        this.aRepo.save(a);
    }

    //답변 수정
    public Answer getAnswer(int id){
        Optional<Answer> answer = this.aRepo.findById(id);
        if (answer.isPresent()) {
            return answer.get();
        }else{
            throw new DataNotFoundException("answer not found");
        }
    }

    public void modify(Answer a, String content){
        a.setContent(content);
        a.setModifiedDate(LocalDateTime.now());
        this.aRepo.save(a);
    }
}
