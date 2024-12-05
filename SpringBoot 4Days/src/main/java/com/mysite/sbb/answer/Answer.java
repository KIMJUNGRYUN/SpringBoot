package com.mysite.sbb.answer;

import com.mysite.sbb.question.Question;
import com.mysite.sbb.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "Text")
    private String content;

    private LocalDateTime createDate; // 등록시간
    private LocalDateTime modifiedDate; // 수정시간

    //질문엔터티 참조(외래키)
    @ManyToOne
    private Question question;
    
    //외래키 설정
    @ManyToOne
    private SiteUser author;
}
