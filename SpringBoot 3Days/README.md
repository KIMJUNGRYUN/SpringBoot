
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
![nav](https://github.com/user-attachments/assets/c3cd3240-9eb4-4e3f-ac4d-dbd30f76fe28)


**layout.html**
- 템플릿에 추가
```html
<nav th:replace="layout::nav"></nav>
```
