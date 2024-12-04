
**BootStrap CSS 추가**
```html
 <!-- th:fragment="이름" 으로 공통 태그부분 작성 -->
    <nav th:fragment="nav" class="navbar navbar-expand-lg navbar-light bg-light border-bottom">
      <div class="container-fluid">
          <a class="navbar-brand" href="/">SBB</a>
          <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent"
              aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
              <span class="navbar-toggler-icon"></span>
          </button>
          <div class="collapse navbar-collapse" id="navbarSupportedContent">
              <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                  <li class="nav-item">
                      <a class="nav-link" href="#">로그인</a>
                  </li>
              </ul>
          </div>
      </div>
  </nav>
```
- 네비게이션 바 추가
- 로그인 페이지 추가
![nav](https://github.com/user-attachments/assets/c3cd3240-9eb4-4e3f-ac4d-dbd30f76fe28)


**layout.html**
- 템플릿에 추가
```html
<nav th:replace="layout::nav"></nav>
```
- 내비게이션바의 'SBB' 로고를 누르면 아무 곳에서나 메인 페이지로 돌아갈 수 있다. 

**페이징**
- SBB의 질문 목록은 현재 페이징 처리가 안되기 때문에 게시물 300개를 작성하면 한 페이지에 300개의 게시물이 모두 조회된다.
- 스프링부트의 테스트 프레임워크를 이용하여 대량 데이터 만들기.
```spring
@SpringBootTest
class SbbApplicationTests {

    @Autowired
    private QuestionService questionService;

    @Test
    void testJpa() {
        for (int i = 1; i <= 300; i++) {
            String subject = String.format("테스트 데이터입니다:[%03d]", i);
            String content = "내용무";
            this.questionService.create(subject, content);
        }
    }
}
```
![moredata](https://github.com/user-attachments/assets/baadfd26-44f3-45b8-a5b9-70219d828491)
- 실무에서는 @Transactional을 제거한다. 테스트환경에서는 Transactional은 테스트한 후 처음상태로 다시 되돌림.


