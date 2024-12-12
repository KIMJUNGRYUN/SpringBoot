# Mybatis

**MyBatis란?**
- 마이바티스는 개발자가 지정한 SQL, 저장프로시저 그리고 몇가지 고급 매핑을 지원하는 퍼시스턴스 프레임워크이다. 마이바티스는 JDBC로 처리하는 상당부분의 코드와 파라미터 설정및 결과 매핑을 대신해준다. 마이바티스는 데이터베이스 레코드에 원시타입과 Map 인터페이스 그리고 자바 POJO 를 설정해서 매핑하기 위해 XML과 애노테이션을 사용할 수 있다.
- `JAP(하이버네이트)`에 비해 상당히 가볍다. 복잡한 쿼리문을 사용하기에 적합. 안정성과 유지보수를 위해 MyBatis를 선호

<hr>

**설정 및 프로젝트 생성**
- 1. 스프링 레거시(Spring 프레임워크)
  - https://mybatis.org/mybatis-3/ko/getting-started.html
- 2.스프링부트 스타터(MyBatis-Spring-Boot-Starter)
  - @Mapper 생성외의 다른 필수 작업들 (기존에 사용자가 설정해줘야 했던 sqlSessionFactory 등등,,)을 자동으로 해줌.
  - @Mappser 만 쓰면서 `mybatis`를 사용가능.
- `DataSource`를 자동 감지.
- `SqlSessionFactory`를 전달하는 인스턴스를 자동 생성하고 등록.
- `DataSource`.`SqlSessionFactoryBean`의 인스턴스를 만들고 등록.
- `@Mapper` 주석이 표시된 매퍼를 자동 스캔하고 연결.
- `SqlSessionTemplateSpring` 컨텍스트에 등록하여 `Bean`에 주입 할 수 있도록 함.

<hr>

**model User 클래스, DB 설정**
```spring
@Getter
@Setter
public class User {
	
	private String id;
	private String name;
	private String phone;
	private String address;

	public User(String id, String name, String phone, String address) {
		this.id = id;
		this.name = name;
		this.phone = phone;
		this.address = address;
	}
    //get set 메소드 자동
}

![db](https://github.com/user-attachments/assets/8127f8ff-6dc7-48be-bba7-eaa730c406f1)

`application.properties`

```spring
spring.datasource.url=jdbc:mysql://localhost:3306/mybatis?useSSL=false&characterEncoding=UTF-8&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=비번
```

<hr>

**GET/users/{id} :id로 유저를 DB에서 검색**

- User 컨트롤러

```spring
@RestController
public class UserController {
	
	//userMapper 선언하고 생성자 주입
	@Autowired
  private UserMapper uMapper;

	@GetMapping("/users/{id}")
	public User getUser(@PathVariable("int id") String id) {
		User user = userMapper.getUser(id);
		return user;
	}
}
```

- Repository 대신 Mapper를 만듬.
  - 패키지 .mapper에 인터페이스 UserMapper

```spring
@Mapper
public interface UserMapper {
	@Select("select * from user where id=#{id}")
	User getUser(int id);
}
```

![db1](https://github.com/user-attachments/assets/a277f358-9aa8-408c-a163-69286dbada9f)

- http://localhost:8080/users/id 넣기

  ![db2](https://github.com/user-attachments/assets/511501f7-44ef-49bf-a100-24a598c6aac9)

<hr>

**GET/users: 모든 유저들을 가져오기**
- `컨트롤러`

```spring
@GetMapping("/users")
	public List<User> getUserList(){
		List<User> userList = userMapper.getUserList();
		return userList;
	}
```

- `mapper` 작성
  - http://localhost:8080/users

![db3](https://github.com/user-attachments/assets/67c8e0ca-4e9a-482e-a366-2435e612ed9e)
![db4](https://github.com/user-attachments/assets/cc916d2f-d89b-48d1-9d08-43a6044b0199)

<hr>

**POST/users : 새 유저 입력하기(포스트맨)**

```spring
@PostMapping
	public void createUser( @RequestParam("id") String id, 
							@RequestParam("name") String name,
							@RequestParam("phone") String phone,
							@RequestParam("address") String address  ) {	
		
		userMapper.insertUser(id, name, phone, address);		
	}
```

```spring
@Insert("INSERT INTO user VALUES(#{id}, #{name} , #{phone} , #{address} )")
	int insertUser(String id, String name , String phone , String address );
```

<hr>

**PUT/users/{id}: id의 유저 수정(Update)**

```spring
@Update("Update user SET name=#{name}, phone=#{phone}, address=#{address} WHERE id=#{id}")
	int updateUser(String id, String name, String phone, String address);
}
```

<hr>

**DELETE/users/{id}: 삭제**

```spring
@Delete("DELETE FROM user WHERE id=#{id}")
    int deleteUser(String id);
```

<hr>

**어노테이션(@) 대신 xml 매핑**

```spring
CREATE TABLE Products
(
    prod_id     INT     		PRIMARY KEY AUTO_INCREMENT,
    prod_name   VARCHAR(255)    NOT NULL,
    prod_price  INT             NOT NULL
);

INSERT INTO Products (prod_name, prod_price) values ('베베숲 물티슈', 2700);
INSERT INTO Products (prod_name, prod_price) values ('여름 토퍼', 35180);
INSERT INTO Products (prod_name, prod_price) values ('페이크 삭스', 860);
INSERT INTO Products (prod_name, prod_price) values ('우산', 2900);
```

```spring
public class Product {
	  
	private Long prodId;	 //CamelCase 소문자단어 다음에 대문자로시작함 
	private String prodName; //DB에서는 prod_id , prod_name, prod_price	  
	private int prodPrice;
	
   //생성자(모든 필드변수) , 생성자(id없는거, 생성용) , 게터/세터 자동완성
```

- application 프로퍼티 설정

```spring
# MyBatis
# mapper.xml 위치 지정
mybatis.mapper-locations: mapper/*.xml

# model 프로퍼티 camel case 설정
mybatis.configuration.map-underscore-to-camel-case=true

# 패키지 result tpye 을 생략할 수 있도록 alias 설정
mybatis.type-aliases-package=com.myapp.mybatis.model

# mapper 로그레벨 설정
logging.level.com.myapp.mybatis.mapper=TRACE
```

- mapper 패키지

```spring
@Mapper
public interface ProductMapper {
	
    Product selectProductById(Long id);
    List<Product> selectAllProducts();
    void insertProduct(Product product);
    void updateProduct(Product product);
    void deleteProductById(Long id);
}
```

- ProductMapper.xml

```html
<?xml version="1.0" encoding="UTF-8"?>

<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.myapp.mybatis.mapper.ProductMapper">

    <select id="selectProductById" resultType="Product">
        SELECT prod_id
              ,prod_name
              ,prod_price
        FROM products
        WHERE prod_id = #{prodId}
    </select>

    <select id="selectAllProducts" resultType="Product">
        SELECT prod_id
              ,prod_name
              ,prod_price
        FROM products
    </select>

    <insert id="insertProduct" parameterType="Product">
      INSERT INTO products (prod_name, prod_price)
      VALUES (#{prodName}, #{prodPrice})
    </insert>
 
</mapper>
```

<hr>

**JSP 설정**
- spring-boot-starter-web 에 포함된 tomcat 은 JSP 엔진을 포함하고 있지 않다. jsp 파일은 Springboot 의 templates 폴더안에서 작동하지 않는다. 그래서 jsp를 적용하기 위해서는 아래와 같은 의존성을 추가해야한다.

`pom.xml`

```spring
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>jstl</artifactId>
</dependency>
<dependency>
    <groupId>org.apache.tomcat.embed</groupId>
    <artifactId>tomcat-embed-jasper</artifactId>
</dependency>

```

- 뷰 경로 지정
  - 스프링 부트에서 jsp를 기본 지원하지 않기 때문에 아래와 같은 정보를 application.properties에 설정해서 스프링 부트에게 jsp 뷰의 경로를 알려주어야 한다.

- application.properties

```spring
spring.mvc.view.prefix=/WEB-INF/views/
spring.mvc.view.suffix=.jsp
```

- index.jsp

```html
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<h1>JSP 페이지</h1>
</body>
</html>
```

- application.properties

```spring
spring.mvc.view.prefix=/WEB-INF/views/
spring.mvc.view.suffix=.jsp

#Oracle DataSource
spring.datasource.driver-class-name=oracle.jdbc.driver.OracleDriver
spring.datasource.url=jdbc:oracle:thin:@127.0.0.1:1521/xe
spring.datasource.username=jb
spring.datasource.password=비밀번호
```


​

