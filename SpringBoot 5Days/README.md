# Spring Boot 연습(sbb)

**답변 수정 버튼**
`q_detail.html`
```html
  <div class="my-3">
            <a th:href="@{|/answer/modify/${answer.id}|}" class="btn btn-sm btn-outline-secondary"
                sec:authorize="isAuthenticated()"
                th:if="${answer.author != null and #authentication.getPrincipal().getUsername() == answer.author.username}"
                th:text="수정"></a>
   </div>
```
- 로그인한 사용자와 답변 작성자가 동일한 경우 답변의 "수정" 버튼이 노출되도록 함.
- 답변 버튼을 누르면 /answer/modify/답변ID 형태의 URL이 GET 방식으로 요청.

**AnswerService**
-  Answer서비스에서 필요한 답변조회와 답변수정 기능을 구현
```spring
 public Answer getAnswer(Integer id) {
        Optional<Answer> answer = this.aRepo.findById(id);
        if (answer.isPresent()) {
            return answer.get();
        } else {
            throw new DataNotFoundException("answer not found");
        }
    }

    public void modify(Answer answer, String content) {
        answer.setContent(content);
        answer.setModifyDate(LocalDateTime.now());
        this.aRepo.save(answer);
    }
```
- 답변 아이디로 답변을 조회하는 `getAnswer` 메서드와 답변의 내용으로 답변을 수정하는 modify 메서드를 추가.

**AnswerController**
- 버튼 클릭시 요청되는 GET방식의 `/answer/modify/`답변ID 형태의 URL을 처리하기 위해 수정.
```spring
@PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String answerModify(AnswerForm answerForm, @PathVariable("id") Integer id, Principal principal) {
        Answer answer = this.aService.getAnswer(id);
        if (!answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        answerForm.setContent(answer.getContent());
        return "answer_form";
    }
```
- URL의 답변 아이디를 통해 조회한 답변 데이터의 "내용"을 AnswerForm 객체에 대입하여 answer_form.html 템플릿에서 사용할수 있도록 함.
- `a_from.html`은 답변을 수정하기 위한 템플릿으로 신규로 작성.

**a_form.html**
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
  <head th:replace="layout::head"></head>
  <body>
    <nav th:replace="layout::nav"></nav>
    <div class="container my-3">
      <!-- 여기부터 시작 -->
      <h5 class="my-3 border-bottom pb-2">답변 수정</h5>
      <form th:object="${answerForm}" method="post">
        <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" />
        <div th:replace="layout::formErrors"></div>
        <div class="mb-3">
          <label for="content" class="form-label">내용</label>
          <textarea th:field="*{content}" class="form-control" rows="10"></textarea>
        </div>
        <input type="submit" value="저장하기" class="btn btn-primary my-2" />
      </form>
    </div>
  </body>
</html>
```
- 답변 작성시 아용하는 폼 태그에도 action 속성을 사용하지 않고 생략하여 현재 호출된 URL로 폼이 전송됨.


**AnswerController**
```spring
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String answerModify(@Valid AnswerForm answerForm, BindingResult bindingResult,
            @PathVariable("id") Integer id, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "answer_form";
        }
        Answer answer = this.aService.getAnswer(id);
        if (!answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.aService.modify(answer, answerForm.getContent());
        return String.format("redirect:/question/detail/%s", answer.getQuestion().getId());
    }
```
- `POST` 방식의 답변 수정을 처리하는 `answerModify` 메서드를 추가.
- 답변 수정을 완료한 후에는 질문 상세 페이지로 돌아가기 위해 `answer.getQuestion.getId()`로 질문의 아이디를 가져왔다.

![un](https://github.com/user-attachments/assets/715baed7-abfa-4ceb-b93f-219bba31ddf3)

<hr>

**답변 삭제**
`q_detail.html`
```html
<a href="javascript:void(0);" th:data-uri="@{|/answer/delete/${answer.id}|}"
                class="delete btn btn-sm btn-outline-secondary" sec:authorize="isAuthenticated()"
                th:if="${answer.author != null and #authentication.getPrincipal().getUsername() == answer.author.username}"
                th:text="삭제"></a>
```
- **<수정>** 버튼 옆에 **<삭제>** 버튼을 추가.
- **<삭제>** 버튼에 `delete` 클래스를 적용했으므로 **<삭제>** 버튼을 누르면 `data-uri` 속성에 설정한 url이 실행될 것

**AnswerService**
- 답변을 삭제하는 기능을 추가.
```spring
public class AnswerService {

    (... 생략 ...)

    public void delete(Answer answer) {
        this.answerRepository.delete(answer);
    }
}
```
- 입력으로 받은 Answer 객체를 사용하여 답변을 삭제하는 delete 메서드를 추가.

***AnswerController**
- 답변 삭제 버튼을 누르면 요청되는 GET방식의 `/answer/delete/`답변ID 형태의 URL을 처리하기 위해 다음과 같이 **AnswerController**를 수정
```spring
@PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String answerDelete(Principal principal, @PathVariable("id") Integer id) {
        Answer answer = this.aService.getAnswer(id);
        if (!answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.aService.delete(answer);
        return String.format("redirect:/question/detail/%s", answer.getQuestion().getId());
    }
```
- 답변을 삭제하는 answerDelete 메서드를 추가.
- 답변을 삭제한 후에는 해당 답변이 있던 질문상세 화면으로 리다이렉트 한다.

`답변 삭제 확인`

![delete](https://github.com/user-attachments/assets/49dbfbfc-6a06-49e9-b094-b53950205cc2)

<hr>

**수정일시 표시**
```html
<div class="d-flex justify-content-end">
            <div th:if="${question.modifyDate != null}" class="badge bg-light text-dark p-2 text-start mx-3">
                <div class="mb-2">modified at</div>
                <div th:text="${#temporals.format(question.modifyDate, 'yyyy-MM-dd HH:mm')}"></div>
            </div>


            <div th:if="${answer.modifyDate != null}" class="badge bg-light text-dark p-2 text-start mx-3">
                <div class="mb-2">modified at</div>
                <div th:text="${#temporals.format(answer.modifyDate, 'yyyy-MM-dd HH:mm')}"></div>
            </div>
```
- 질문이나 답변에 수정일시가 있는 경우(null이 아닌경우) 수정일시를 작성일시 바로 좌측에 표시.
- 질문이나 답변을 수정하면 다음처럼 수정일시가 표시 됨.

<hr>

**질문 추천**
`엔티티 변경`
​- 질문, 답변의 추천은 추천한 사람(SiteUser 객체)을 질문, 답변 엔티티에 추가해야 함.

**Question**
- **Question** 엔티티에 추천인`(voter)` 속성을 추가
- 하나의 질문에 여러사람이 추천할 수 있고 한 사람이 여러 개의 질문을 추천할 수 있기때문에 `@MansyToMany`를 사용
```spring
  @ManyToMany
  Set<SiteUser> voter;
```
- `List`가 아닌 `Set`으로 한 이유는 추천인은 중복되면 안되기 때문.
- `Set`는 중복을 허용하지 않는 자료형.

**Answer**
```spring
    @ManyToMany
    Set<SiteUser> voter;
```

![dbsaas](https://github.com/user-attachments/assets/79599ee3-e720-4293-b0d0-7f14021c1ed9)
- `@ManyToMany` 관계로 속성을 생성하면 새로운 테이블을 생성하여 데이터를 관리
- 테이블에는 서로 연관된 엔티티의 고유번호(`id`) 2개가 프라이머리 키로 되어 있기 때문에 다대다(`N:N`) 관계가 성립

**질문 추천 버튼**
`q_detail.html`
```html
   <a href="javascript:void(0);" class="recommend btn btn-sm btn-outline-secondary"
                th:data-uri="@{|/question/vote/${question.id}|}">
                추천
                <span class="badge rounded-pill bg-success" th:text="${#lists.size(question.voter)}"></span>
            </a>
```
- 추천 버튼을 클릭하면 href의 속성이 javascript:void(0)으로 되어 있기 때문에 아무런 동작도 하지 않음.
- class 속성에 "recommend"를 추가하여 자바스크립트를 사용하여 data-uri에 정의된 URL이 호출되도록 할 것.

**추천 버튼 확인 창**
```JavaScript
const recommend_elements = document.getElementsByClassName("recommend");
Array.from(recommend_elements).forEach(function(element) {
    element.addEventListener('click', function() {
        if(confirm("정말로 추천하시겠습니까?")) {
            location.href = this.dataset.uri;
        };
    });
});
```
- 추천 버튼에 `class="recommend"`가 적용되어 있으므로 추천 버튼을 클릭하면 "정말로 추천하시겠습니까?"라는 질문이 나타나고 "확인"을 선택하면 data-uri 속성에 정의한 URL이 호출될 것

**QuestionService**
- 추천인 저장을 위한 수정
```spring
    public void vote(Question question, SiteUser siteUser) {
        question.getVoter().add(siteUser);
        this.qRepo.save(question);
    }
```
- `Question` 엔티티에 사용자를 추천인으로 저장하는 `vote` 메서드를 추가

**QuestionController**
- 추천 버튼을 눌렀을때 호출되는 URL을 처리하기 위해 수정.
```spring
 @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String questionVote(Principal principal, @PathVariable("id") Integer id) {
        Question question = this.qService.getQuestion(id);
        SiteUser siteUser = this.uService.getUser(principal.getName());
        this.qService.vote(question, siteUser);
        return String.format("redirect:/question/detail/%s", id);
    }
```
- 추천은 로그인한 사람만 가능해야 하므로 `@PreAuthorize("isAuthenticated()")` 애너테이션이 적용
- 위에서 작성한 `QuestionService`의 `vote` 메서드를 호출하여 추천인을 저장

![recommand](https://github.com/user-attachments/assets/332f01b4-b464-46d9-8d57-4229991c553f)

<hr>

**답변 추천 버튼**
- 답변의 추천수를 표시하고, 답변을 추천할 수있는 버튼을 질문 상세 템플릿에 추가
`q_detail.html`
```html
<a href="javascript:void(0);" class="recommend btn btn-sm btn-outline-secondary"
                th:data-uri="@{|/answer/vote/${answer.id}|}">
                추천
                <span class="badge rounded-pill bg-success" th:text="${#lists.size(answer.voter)}"></span>
            </a>
```
- 질문과 마찬가지로 답변 영역의 상단에 답변을 추천할 수 있는 버튼을 생성
- 추천 버튼에 `class="recommend"`가 적용되어 있으므로 추천 버튼을 클릭하면 "정말로 추천하시겠습니까?"라는 질문이 나타나고 "확인"을 선택하면 `data-uri` 속성에 정의한 URL이 호출될 것

**AnswerService**
- 답변에 추천인을 저장하기 위해 다음과 같이 `AnswerService`를 수정
```spring
public void vote(Answer answer, SiteUser siteUser) {
        answer.getVoter().add(siteUser);
        this.aRepo.save(answer);
    }
```
- `Answer` 엔티티에 사용자를 추천인으로 저장하는 vote 메서드를 추가

**AnswerController**
- 답변 추천 버튼을 눌렀을때 호출되는 URL을 처리하기 위해 다음과 같이 `AnswerController`를 수정
```spring
 @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String answerVote(Principal principal, @PathVariable("id") Integer id) {
        Answer answer = this.aService.getAnswer(id);
        SiteUser siteUser = this.uService.getUser(principal.getName());
        this.aService.vote(answer, siteUser);
        return String.format("redirect:/question/detail/%s", answer.getQuestion().getId());
}
```
- 추천은 로그인한 사람만 가능해야 하므로 `@PreAuthorize("isAuthenticated()")` 애너테이션이 적용
- `AnswerService`의 `vote` 메서드를 호출하여 추천인을 저장

![re](https://github.com/user-attachments/assets/34fb3252-ac60-4360-9542-a17c383d3093)

<hr>

**h2 => MySQL**
```
# H2 DATABASE
#spring.h2.console.enabled=true
#spring.h2.console.path=/h2-console
#spring.datasource.url=jdbc:h2:~/local
#spring.datasource.driverClassName=org.h2.Driver
#spring.datasource.username=sa
#spring.datasource.password=

# MYSQL DB 설정
spring.datasource.url=jdbc:mysql://localhost:3306/ssb?useSSL=false&characterEncoding=UTF-8&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=1234

# JPA
#spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL5InnoDBDialect
#참고로 오라클11g는 필요없음

# table ddl auto
spring.jpa.hibernate.ddl-auto=update
```




















  







