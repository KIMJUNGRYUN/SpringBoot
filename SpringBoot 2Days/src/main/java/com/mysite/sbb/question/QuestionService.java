package com.mysite.sbb.question;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository qRepo;
    //질문들을 모두 가져오는 메소드
    public List<Question> getList() {
        return this.qRepo.findAll();
    }

    public Question getQuestion(Integer id) {
        Optional<Question> q = this.qRepo.findById(id);
        if (q.isPresent()) {
            return q.get();
        }else {
            throw new DataNotFoundException ("question not found");
        }

    }
    
}
