package com.mysite.sbb;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.desktop.QuitEvent;
import java.util.List;

//JpaRepository
public interface QuestionRepository  extends JpaRepository<Question, Integer> {
    //jsp 할때는 dao crud 메소드를 직접 만들었으나 JAP 는 CRUD 자동완성됨
    Question findBySubject(String subject);
    Question findBySubjectAndContent(String subject, String content);
    List<Question> findBySubjectContaining(String subject);
}
