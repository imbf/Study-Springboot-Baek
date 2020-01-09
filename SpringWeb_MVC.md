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

   









































