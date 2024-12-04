package com.mysite.sbb.question;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository qRepo;

    //질문들을 모두 가져오는 메소드 (페이징 적용)
    public Page<Question> getList(int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createDate").descending()); //현재 페이지와 한페이지 갯수 최신순으로 정렬
        return this.qRepo.findAll(pageable);
    }

    public Question getQuestion(Integer id) {
        Optional<Question> q = this.qRepo.findById(id);
        if (q.isPresent()) {
            return q.get();
        }else {
            throw new DataNotFoundException ("question not found");
        }
    }

    //새 질문을 저장하는 메소드
    public void createQuestion(String subject, String content) {
        Question q = new Question();
        q.setSubject(subject);
        q.setContent(content);
        q.setCreateDate(LocalDateTime.now());
        this.qRepo.save(q);
    }


    
}
