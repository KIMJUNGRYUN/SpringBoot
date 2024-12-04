
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

<hr>

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

**페이징 구현하기**
- 페이징을 구현하기 위한 라이브러리 추가.
  - org.springframework.data.domain.Page
  - org.springframework.data.domain.PageRequest
  - org.springframework.data.domain.Pageable

**QuestionRepository**
```spring
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
	
	Question findBySubject(String subject);
	Question findBySubjectAndContent(String subject, String content);
    List<Question> findBySubjectLike(String subject);
    Page<Question> findAll(Pageable pageable);
}
```
- Pageable 객체를 입력으로 받아 `Page<Question>`타입 객체를 리턴하는 findAll메서드를 생성.

**QuestionService**
```spring
public Page<Question> getList(int page){
		Pageable pageable = PageRequest.of(page, 10);
		return this.qRepo.findAll(pageable);
	}
```
- 질문 목록을 조회하는 `getList`메서드를 위와 같이 변경.
- Pageable 객체를 생성할때 사용한 PageRequest.of(page, 10)에서 page는 조회할 페이지의 번호이고 10은 한 페이지에 보여줄 게시물의 갯수를 의미한다.

**QuestionController**
```spring
@RequestMapping("/list")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
    	 Page<Question> paging = this.qService.getList(page);
    	model.addAttribute("paging", paging);
        return "question_list";
    }
```
- `http://localhost:8080/question/list?page=0` 처럼 GET 방식으로 요청된 URL에서 page값을 가져오기 위해 `@RequestParam(value="page", defaultValue="0") int page` 매개변수가 list 메서드에 추가되었다
- URL에 페이지 파라미터 page가 전달되지 않은 경우 디폴트 값으로 0이 되도록 설정했다.

![asdasda](https://github.com/user-attachments/assets/bdcdd383-3665-4d2c-a65c-f32496892e13)

**Question_list.html**
```html
 <tr th:each="question, loop : ${paging}">
```
- 기존에 전달했던 이름인 "questionList" 대신 "paging" 이름으로 템플릿에 전달했기 때문에 템플릿도 다음과 같이 변경해야 한다

**템플릿에 페이지 이동 기능 구현**
- 질문 목록에서 페이지를 이동하려면 페이지를 이동할 수 있는 "이전", "다음" 과 같은 링크가 필요하다.
```html
  </table>
    <!-- 페이징처리 시작 -->
    <div th:if="${!paging.isEmpty()}">
        <ul class="pagination justify-content-center">
            <li class="page-item" th:classappend="${!paging.hasPrevious} ? 'disabled'">
                <a class="page-link"
                    th:href="@{|?page=${paging.number-1}|}">
                    <span>이전</span>
                </a>
            </li>
            <li th:each="page: ${#numbers.sequence(0, paging.totalPages-1)}"
                th:classappend="${page == paging.number} ? 'active'" 
                class="page-item">
                <a th:text="${page}" class="page-link" th:href="@{|?page=${page}|}"></a>
            </li>
            <li class="page-item" th:classappend="${!paging.hasNext} ? 'disabled'">
                <a class="page-link" th:href="@{|?page=${paging.number+1}|}">
                    <span>다음</span>
                </a>
            </li>
        </ul>
    </div>
    <!-- 페이징처리 끝 -->
    <a th:href="@{/question/create}" class="btn btn-primary">질문 등록하기</a>
```
- 이전 페이지가 없는 경우에는 "이전" 링크가 비활성화(disabled)되도록 하였다. (다음페이지의 경우도 마찬가지 방법으로 적용했다.) 
- 페이지 리스트를 루프 돌면서 해당 페이지로 이동할 수 있는 링크를 생성하였다

**템플릿에 사용된 주요 페이징 기능을 표로 정리**
![table](https://github.com/user-attachments/assets/6c696c40-e879-47e6-a003-446a8f6cd89a)
- 페이징 처리는 잘 되었지만 페이지가 모두 표시된다.
 
- 이 문제를 해결하기 위해 다음과 같이 질문 목록 템플릿을 수정하자
```html
   <li th:each="page: ${#numbers.sequence(0, paging.totalPages-1)}"
              th:if="${page >= paging.number-5 and page <= paging.number+5}"
              th:classappend="${page == paging.number} ? 'active'" class="page-item">
```
- `th:if="${page >= paging.number-5 and page <= paging.number+5}"` 이 코드는 페이지 리스트가 현재 페이지 기준으로 좌우 5개씩 보이도록 만든다.













