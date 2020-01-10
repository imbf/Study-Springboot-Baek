# Spring-Web-MVC

## Introduction

- 스프링 웹 MVC

   - https://docs.spring.io/spring/docs/5.0.7.RELEASE/spring-framework-reference/web.html#spring-web

- 스프링 부트 MVC

   - 자동 설정으로 제공하는 여러 기본 기능 (앞으로 살펴볼 예정)   @SpringBoot에서 제공해주는 EnableAutoConfiguration 때문에 동작하는 WebMvcAutoConfiguration 설정 파일 때문에 가능

- 스프링 MVC 확장 ( 추가적인 설정 )

   - @Configuration + WebMvcConfigurer

- 스프링 MVC 재정의

   - @Configuration + @EnableWebMvc

   Config/WebConfig

   ```java
   package econovation.demospringmvc.config;
   
   import org.springframework.context.annotation.Configuration;
   import org.springframework.web.servlet.config.annotation.EnableWebMvc;
   import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
   
   @Configuration
   @EnableWebMvc
   public class WebConfig implements WebMvcConfigurer {
       
   }
   ```

---

## HttpMessageConverters

https://docs.spring.io/spring/docs/5.0.7.RELEASE/spring-framework-reference/web.html#mvc-config-message-converters

HTTP 요청 본문을 객체로 변경하거나, 객체를 HTTP 응답 본문으로 변경할 때 사용 *{“username”:”keesun”, “password”:”123”} <-> User* (Composition 객체)

- *@ReuqestBody*
- *@ResponseBody*  (@RestController 라는 Annotation이 붙어 있으면 @ResponseBody라는 Annotation 생략 가능)

```java
package econovation.demospringmvc.user;

import org.springframework.web.bind.annotation.*;

@RestController // @RestController Annotation을 사용하면 @ResponseBody라는 Annotation을 사용하지 않아도 된다.
public class UserController {

    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }

    @PostMapping("/user")
    public @ResponseBody User create(@RequestBody User user) {
        return null;
    }
}
```

---

## ViewResolver (accept header에 따라 응답이 달라진다.)

**스프링 부트** (이제 스프링은 /user/create.json 와 같이 뒤에 파일 확장자는 지원하지 않는다.)

- 뷰 리졸버 설정 제공
- HttpMessageConvertersAutoConfiguration (지원해주는 classpath가 없다.)

**XML 메시지 컨버터 추가하기**

```xml
<dependency>
  <groupId>com.fasterxml.jackson.dataformat</groupId>
  <artifactId>jackson-dataformat-xml</artifactId>
  <version>2.9.6</version>
</dependency>
```

user/userControllerTest.java

```java
package econovation.demospringmvc.user;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import javax.xml.xpath.XPath;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    public void hello() throws Exception{
        mockMvc.perform(get("/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("hello"));
    }

    @Test
    public void createUser_XML() throws Exception {
        String userJson = "{\"username\":\"keesun\",\"password\":\"123\"}";
        mockMvc.perform(post("/users/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_XML) // accept header
                .content(userJson))
            .andExpect(status().isOk())
            .andExpect(xpath("/User/username")
                .string("keesun"))
            .andExpect(xpath("/User/password")
                .string("123"));

    }
}
```

---

## 정적 리소스 지원

**정적 리소스 맵핑 : 클라이언트에서 요청이 왔을 때 만들어진 리소스를 그대로 보여주는 것을 의미한다.**

정적 리소스 맵핑 "/**" (<u>기본적으로 맵핑은 Root부터 맵핑 되어 있다.</u>)

- 기본 리소스 위치 ( ex. "/hello.html" => /static/hello.html) **다양한 디렉토리 설정 및 사용 가능** 
   **@EnableWebMvc** 애노테이션이 **Config 파일**에 적용되어 있으면 동적 리소스 맵핑이 오버라이딩 한다.
   또는 다른 경로가 맵핑 되어 있다면 맵핑이 오버라이딩 된다.

   - classpath:/static

   - classpath:/public

   - classpath:/resources/

   - classpath:/META_INF/resources

   - spring.mvc.static-path-pattern : 맵핑 설정 변경 가능 

      application.properties (정적 리소스 파일을 서비스 받기 위해 url을 localhost:8080/static/ex.html 요청)

      ```java
      spring.mvc.static-path-pattern=/static/**
      ```

      

   - spring.mvc.static-locations : 리소스 찾을 위치 변경 가능

- Last-Modified 헤더를 보고 304 응답을 보냄

   if-Modified-Since : Sun, 15 Jul 2018 04:33:26 GMT 이후에 modifed 되면 새롭게 resource를 달라
   ![image-20200108211406103](/Users/baejongjin/Library/Application Support/typora-user-images/image-20200108211406103.png)

- **정적 리소스를 ResourceHttpRequestHandler가 처리함**

   - WebMvcConfigure의 addResourceHandlers로 커스터마이징 할 수 있음

   ```java
   package econovation.demospringmvc.config;
   
   import org.springframework.context.annotation.Configuration;
   import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
   import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
   
   @Configuration
   public class WebConfig implements WebMvcConfigurer {
       @Override
       public void addResourceHandlers(ResourceHandlerRegistry registry) { //리소스 핸들러 추가
           //m으로 시작하는 요청이 오면 classpath기준으로 m디렉토리 밑에서 제공을 하겠다.
           registry.addResourceHandler("/m/**")
                   .addResourceLocations("classpath:/m/")
                   .setCachePeriod(20);
   
       }
   }
   
   ```

---

## 웹 JAR

**웹 JAR :** react.js, view.js, ... 와 같이 Jar파일로 Dependency를 추가할 수 있는 파일 

**Jquery 추가하기**

1. pom.xml

   ```xml
   <!-- https://mvnrepository.com/artifact/org.webjars.bower/jquery -->
   <dependency>
       <groupId>org.webjars.bower</groupId>
       <artifactId>jquery</artifactId>
       <version>3.4.1</version>
   </dependency>
   ```

2. hello.html

   ```html
   <!DOCTYPE html>
   <html lang="en">
   <head>
       <meta charset="UTF-8">
       <title>hello</title>
   </head>
   <body>
   Hello Static Resource abc
   </body>
   <script src="/webjars/jquery/3.4.1/dist/jquery.min.js"></script>
   <script>
       $(function(){
           alert("ready!");
       });
   </script>
   </html>
   ```

   

**웹JAR 맵핑 : "/webjars/\**"**

- 버전 생략하고 사용하려면 (jquery에 버젼을 빼도 된다.)

   - webjars-locator-core 의존성 추가 (resource chaining에 관련)

      pom.xml

      ```xml
      <!-- https://mvnrepository.com/artifact/org.webjars/webjars-locator-core -->
      <dependency>
          <groupId>org.webjars</groupId>
          <artifactId>webjars-locator-core</artifactId>
          <version>0.43</version>
      </dependency>
      ```

---

## index 페이지와 파비콘

**웰컴 페이지**

- index.html 찾아 보고 있으면 제공
- index.템플릿 찾아 보고 있으면 제공.
- 둘 다 없으면 에러 페이지

**파비콘** : 웹 페이지 상단의 Window Title 왼쪽에 위치하는 조그마난 그림을 일컫는다.

- favicon.ico
- 파비콘 만들기 https://favicon.io/
- 파비콘이 안 바뀔 때?
   - https://stackoverflow.com/questions/2208933/how-do-i-force-a-favicon-refresh (2번째 방법)
      1. Type in www.yoursite.com/favicon.ico (or www.yoursite.com/apple-touch-icon.png, etc)
      2. push **Enter**
      3. **ctrl + F5**
      4. Restart Browser (IE, Firefox)

---

## 동적 템플릿 엔진 (Thymeleaf )

**스프링 부트가 자동 설정을 지원하는 템플릿 엔진** : 주로 View를 만드는데 사용하고, Code Generation, Email 에 사용

- FreeMarker
- Groovy
- **Thymeleaf**
- Mustache

**JSP를 권장하지 않는 이유** ( 스프링 부트가 지향하는 바와 confliction이 있다. )

- JAR 패키징 할 때는 동작하지 않고, WAR 패키징 해야 함.
- Undertow는 JSP를 지원하지 않음.
- https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-jsp-limitations
   (JSP 제약 사항)

**Thymeleaf 사용하기** : Thymeleaf가 독자적으로 최종적인 view를 만들어준다.

- https://www.thymeleaf.org/

- https://www.thymeleaf.org/doc/articles/standarddialect5minutes.html

- 의존성 추가 

   ```xml
   <dependency>
     <groupId>org.springframework.boot</groupId>
     <artifactId>spring-boot-starter-thymeleaf</artifactId>
   </dependency>
   ```

- 템플릿 파일 위치 : /src/main/resources/template

- 예제 : https://github.com/thymeleaf/thymeleafexamples-stsm/blob/3.0-master/src/main/webapp/WEB-INF/templates/seedstartermng.html

   Test/SampleControllerTest.java

   ```java
   package econovation.springbootmvc;
   
   import org.junit.Test;
   import org.junit.runner.RunWith;
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
   import org.springframework.test.context.junit4.SpringRunner;
   import org.springframework.test.web.servlet.MockMvc;
   
   import static org.hamcrest.Matchers.containsString;
   import static org.hamcrest.Matchers.is;
   import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
   import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
   import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
   
   @RunWith(SpringRunner.class)
   @WebMvcTest(SampleController.class)
   public class SampleControllerTest {
   
       @Autowired
       MockMvc mockMvc;
   
       @Test
       public void hello() throws Exception{
           // 요청 "/hello"
           // 응답
           // - 뷰 이름 : hello
           // - 모델 name : jongjin
           mockMvc.perform(get("/hello"))
                   .andExpect(status().isOk())
                   .andDo(print())
                   .andExpect(view().name("hello"))
                   .andExpect(model().attribute("name", is("jongjin")))
                   .andExpect(content().string(containsString("jongjin")));
       }
   }
   ```

   hello.html

   ```java
   <!DOCTYPE html>
   <html lang="en" xmlns:th="http://www.thymeleaf.org">
   <head>
       <meta charset="UTF-8">
       <title>Title</title>
   </head>
   <body>
   <h1 th:text="${name}"></h1>
   </body>
   </html>
   ```

   SampleController.java

   ```java
   package econovation.springbootmvc;
   
   import org.springframework.stereotype.Controller;
   import org.springframework.ui.Model;
   import org.springframework.web.bind.annotation.GetMapping;
   import org.springframework.web.bind.annotation.RestController;
   
   @Controller
   public class SampleController {
   
       @GetMapping("/helloa")
       public String hello(Model model){
           model.addAttribute("name","jongjin");
           return "hello";
           // 그냥 Controller 이기 때문에 hello는 응답의 본문이 아니다!!!!
           // RestController 일 경우에 hello는 응답의 본문이 된다.
       }
   }
   
   ```

Thymeleaf는 Servlet Container의 독립적인 렌더링 엔진 이기 때문에 view의 렌더링 결과를 확인할 수 있다.

**Test 코드 작성시 junit가 intellij에서 생성해주는 프로젝트에는 기본적으로 의존성이 exclusion이 되어 있기 때문에 Test코드를 작성하기 위해선 의존성 exclusion을 없애야 한다.**

---

## HtmlUnit

**HtmlUnit** : HTML을 단위 테스트 하기 위한 Tool 이다.

**HTML 템플릿 뷰 테스트를 보다 전문적으로 하자.**

- http://htmlunit.sourceforge.net/

- http://htmlunit.sourceforge.net/gettingStarted.html

- 의존성 추가

   ```xml
   <dependency>
       <groupId>org.seleniumhq.selenium</groupId>
       <artifactId>htmlunit-driver</artifactId>
       <scope>test</scope>
   </dependency>
   
   <dependency>
       <groupId>net.sourceforge.htmlunit</groupId>
       <artifactId>htmlunit</artifactId>
       <scope>test</scope>
   </dependency>
   ```

- @Autowire

   Test/SampleControllerTest.java

   ```java
   package econovation.springbootmvc;
   
   import com.gargoylesoftware.htmlunit.WebClient;
   import com.gargoylesoftware.htmlunit.html.HtmlHeading1;
   import com.gargoylesoftware.htmlunit.html.HtmlPage;
   import org.junit.Test;
   import org.junit.runner.RunWith;
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
   import org.springframework.test.context.junit4.SpringRunner;
   import org.springframework.test.web.servlet.MockMvc;
   
   import static org.assertj.core.api.Assertions.assertThat;
   
   
   @RunWith(SpringRunner.class)
   @WebMvcTest(SampleController.class)
   public class SampleControllerTest {
   
       @Autowired
       WebClient webClient;
   
       @Test
       public void hello() throws Exception{
   
           HtmlPage page = webClient.getPage("/hello");
           HtmlHeading1 h1 = page.getFirstByXPath("//h1");
           assertThat(h1.getTextContent()).isEqualToIgnoringCase("jongjin");
       }
   }
   ```

---

## ExceptionHandler

**스프링 @MVC 예외 처리 방법**

- @ControllerAdvice
- @ExchangeHandler

**SpringBoot의 기본적인 ErrorHandler**

<img src="/Users/baejongjin/Library/Application Support/typora-user-images/image-20200109175506285.png" alt="image-20200109175506285" style="zoom:50%;" />

**스프링 부트가 제공하는 기본 예외 처리기**

- BasicErrorController
   - HTML 과 JSON 응답 지원
- 커스터마이징 방법
   - ErrorController 구현

SampleController.java

```java
package econovation.springbootmvc;

        import org.springframework.stereotype.Controller;
        import org.springframework.ui.Model;
        import org.springframework.web.bind.annotation.ExceptionHandler;
        import org.springframework.web.bind.annotation.GetMapping;
        import org.springframework.web.bind.annotation.ResponseBody;
        import org.springframework.web.bind.annotation.RestController;

@Controller
public class SampleController {

    @GetMapping("/hello")
    public String hello(){
        throw new SampleException();
    }

    @ExceptionHandler(SampleException.class)	// SampleException.class의 errorHandler
    public @ResponseBody AppError sampleError(SampleException e){
        AppError appError = new AppError();
        appError.setMessage("error.app.key");
        appError.setReason("IDK IDK IDK");
        return appError;
    }
}

```

SampleException.java

```java
package econovation.springbootmvc;

public class SampleException extends RuntimeException {
}
```

**커스텀 에러 페이지 **(에러 페이지그 없다면 스프링 부트가 제공하는 기본 예외 처리기가 동작되어 페이지를 생성한다.)

- 상태 코드 값에 따라 여러 페이지 보여주기

- src/main/resources/static|template/error/

- 404.html (상태 코드에 따라 html 페이지를 보여준다.)

- 5xx.html (상태 코드에 따라 html 페이지를 보여준드ㅏ.)

- ErrorViewResolver 구현

   ```java
   public class MyErrorViewResolver implements ErrorViewResolver {
     @Override
     public ModelAndView resolveErrorView(HttpServletRequest request,
     HttpStatus status, Map<String, Object> model) {
     // Use the request or status to optionally return a ModelAndView
     return ...
     }
   }
   ```

---

## Spring HATEOAS

**HATEOAS** : **H**ypermedia **A**s **T**he **E**ngine **O**f **A**pplication **S**tate

- 서버 : 현재 리소스와 **연관된 링크 정보**를 클라이언트에게 제공한다.
- 클라이언트 : **연관된 링크 정보**를 바탕으로 리소스에 접근한다.
- 연관된 링크 정보
   - Relation
   - Hypertext Reference
- spring-boot-starter-hateoas 의존성 추가
- https://spring.io/understanding/HATEOAS
- https://spring.io/guides/gs/rest-hateoas/
- https://docs.spring.io/spring-hateoas/docs/current/reference/html/

**ObjectMapper 제공** (우리가 제공하는 resource 를 json으로 변환할 때 사용하는 인터페이스) 그냥 Bean을 주입해서 사용하면 된다.

- spring.jackson.*
- Jackson2ObjectMapperBuilder

**LinkDiscovers 제공** 

- 클라이언트 쪽에서 링크 정보를 Rel 이름으로 찾을때 사용할 수 있는 XPath 확장 클래스

/src/main/java/econovation.demospringhateoas/SampleController.java

```java
package econovation.demospringhateoas;

import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;


@RestController
public class SampleController {

    @GetMapping("/hello")
    public Resource<Hello> hello(){
        // 객체가 변환해서 나간다.
        Hello hello = new Hello();
        hello.setPrefix("Hey,");
        hello.setName("jongjin");

        // 링크 정보를 추가하는 방법
        // SampleController Class에서 제공하는 hello()라는 메소드의 링크를 따서 링크를 Self라는 Relation으로 만들어서 추가
        // 또 클라이언트 들이 이러한 Link를 사용한다.
        Resource<Hello> helloResource = new Resource<>(hello);
        helloResource.add(linkTo(methodOn(SampleController.class).hello()).withSelfRel());

        return helloResource;
    }
}

```

---

## CORS (Cross - Origin Resource Sharing)

**SOP와 CORS**

- Single-Origin Policy : 같은 Origin에만 요청을 보낼 수 있다 라는 Policy (다르면 Origin 끼리 Share 불가)
- Cross-Origin Resource Sharing : SOP를 우회하기 위한 표준 기술 (서로 다른 Origin 끼리 Resource를 share) 
   **스프링 부트가 제공하기 때문에 바로 사용할 수 있다.**
- Origin? : 밑의 3가지를 조합한 것을 의미한다. 
   - URI 스키마 (http, https)
   - hostname (whiteship.me, localhost)
   - 포트(8080, 18080)

**스프링 MVC @CrossOrigin** : 내 리소스를 가져갈 수 있는 Origin을 설정 해 주었다.

- @Controller나 @RequestMapping에 추가하거나

   **SpringcorsserverApplication.js**

   ```java
   package econovation.springcorsserver;
   
   import org.springframework.boot.SpringApplication;
   import org.springframework.boot.autoconfigure.SpringBootApplication;
   import org.springframework.web.bind.annotation.CrossOrigin;
   import org.springframework.web.bind.annotation.GetMapping;
   import org.springframework.web.bind.annotation.RestController;
   
   @SpringBootApplication
   @RestController
   public class SpringcorsserverApplication {
   
       @CrossOrigin(origins = "http://localhost:18080") // 스키마, 호스트네임, 포트
       @GetMapping("/hello")
       public String hello(){
           return "Hello";
       }
   
       public static void main(String[] args) {
           SpringApplication.run(SpringcorsserverApplication.class, args);
       }
   
   }
   
   ```

- WebMvcConfigurer 사용해서 글로벌 설정

   **src/main/java/econovation.springcorsserver/WebConfig.js**

   ```java
   package econovation.springcorsserver;
   
   import org.springframework.context.annotation.Configuration;
   import org.springframework.web.servlet.config.annotation.CorsRegistry;
   import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
   
   @Configuration
   public class WebConfig implements WebMvcConfigurer {
   
       @Override
       public void addCorsMappings(CorsRegistry registry) {
           registry.addMapping("/**").allowedOrigins("http://localhost:18080");
       }
   }
   
   ```





























