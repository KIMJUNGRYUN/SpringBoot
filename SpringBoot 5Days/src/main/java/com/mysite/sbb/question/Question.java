package com.mysite.sbb.question;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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

    private LocalDateTime createDate; //등록 시간
    private LocalDateTime modifiedDate; //수정 시간

    //외래키 설정
    //여러개의 질문이 한 명의 사용자에게 작성될 수 있으므로 @ManyToOne 관계가 성립한다.
    @ManyToOne
    private SiteUser author;

    //반대로 이 질문에 해당 답변들(외래키 관계일때)
    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    private List<Answer> answerList;

    //질문과 추천인과의 관계가 다 대 다 many to many 관계임
    @ManyToMany
    Set<SiteUser> voter;
}
