package com.mysite.sbb;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.List;

//엔티티는 JPA 의 테이블과 같은 클래스
@Entity
@Getter
@Setter

public class Question {
    //기본키 열 id, 자동으로 1증가 옵션
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //제목열은 200자까지
    @Column(length = 200)
    private String subject;


    @Column(columnDefinition = "Text")
    private String content;

    private LocalDateTime createDate;

    //반대로 이 질문에 해당 답변들
    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    private List<Answer> answerList;
}
