package com.mysite.sbb.question;



import com.mysite.sbb.user.SiteUser;
import jakarta.servlet.annotation.WebListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    //새 질문을 저장하는 메소드(글쓴이도 추가)
    public void createQuestion(String subject, String content, SiteUser author) {
        Question q = new Question();
        q.setSubject(subject);
        q.setContent(content);
        q.setAuthor(author);
        q.setCreateDate(LocalDateTime.now());
        this.qRepo.save(q);
    }

    public void modify(Question q, String subject, String content) {
        q.setSubject(subject);
        q.setContent(content);
        q.setModifiedDate(LocalDateTime.now()); //수정시간만 입력
        this.qRepo.save(q); //id가 있으면 수정
    }

    //삭제하기
    public void delete(Question q) {
        this.qRepo.delete(q);
    }

    public void vote(Question q, SiteUser siteUser){
//        Set<SiteUser> voters =  q.getVoter();
//        voters.add(siteUser);
        q.getVoter().add(siteUser);
        this.qRepo.save(q);
    }
    
}
