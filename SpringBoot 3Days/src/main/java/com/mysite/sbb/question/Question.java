package com.mysite.sbb.question;

import com.mysite.sbb.answer.Answer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

//엔티티는 JPA 의 테이블과 같은 클래스
@Entity
@Getter
@Setter
public class Question {
    //기본키 열 id,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //자동으로 1증가 옵션
    private Integer id;

    //제목열은 200자까지
    @Column(length = 200)
    private String subject;


    @Column(columnDefinition = "Text")
    private String content;

    private LocalDateTime createDate;

    //반대로 이 질문에 해당 답변들(외래키 관계일때)
    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    private List<Answer> answerList;
}
