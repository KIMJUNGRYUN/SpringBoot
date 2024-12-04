package com.mysite.sbb.question;
import org.springframework.data.domain.Page;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import java.util.List;

//JpaRepository
public interface QuestionRepository  extends JpaRepository<Question, Integer> {

    //jsp 할때는 dao crud 메소드를 직접 만들었으나 JAP 는 CRUD 자동완성됨
    Question findBySubject(String subject);
    Question findBySubjectAndContent(String subject, String content);
    List<Question> findBySubjectContaining(String subject);
    //Pageable 객체를 입력받아 Page<Question> 객체를 리턴
    Page<Question> findAll(Pageable pageable);
}
