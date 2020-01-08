# Spring-Boot-Utilization(2)

## 로깅(Logging)

**로깅 퍼사드 VS 로거**

- **Commons Logging**, SLF4j : 로거 API들을 추상화 해 놓은 interface들이다. (로깅 퍼사드)
   - 프레임워크를 사용하는 애플리케이션들이 원하는 로거를 사용할 수 있도록 해주기 때문에 사용
   - SpringBoot는 Commons Logging을 사용한다.
- JUL, Log4J2, **Logback** : 최종적으로 SpringBoot는 Logback을 사용한다.

**스프링 5에 로거 관련 변경 사항**

- Spring-JCL
   - Commons Loging -> SLF4j or Log4j2
   - pom.xml에 exclusion 안해도 됨.

**스프링 부트 로깅**

- 기본 포맷
- --debug (일부 핵심 라이브러리만 디버깅 모드로)
- --trace (전부 다 디버깅 모드로)
- 컬러 출력 : spring.output.ansi.enabled
- 파일 출력 : logging.file 또는 logging.path
- 로그 레벨 조정 : logging.level.패키지 = 로그 레벨

```java
package econovation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class SampleRunner implements ApplicationRunner {

    private Logger logger = LoggerFactory.getLogger(SampleRunner.class);

    @Autowired
    private String hello;

    @Autowired
    private JongjinProperties jongjinProperties;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("==================");
        logger.info(hello);
        logger.info(jongjinProperties.getFullName());
        logger.info(jongjinProperties.getName());
        logger.info("==================");
    }
}
```

![image-20200108103534905](/Users/baejongjin/Library/Application Support/typora-user-images/image-20200108103534905.png)

**로그 사진**

![image-20200108102541667](/Users/baejongjin/Library/Application Support/typora-user-images/image-20200108102541667.png)

날짜    시간 로그레벨 pid 쓰레드이름 풀패키지 경로            메세지

**커스텀 로그 설정 파일 사용하기**

- Logback : logback-spring.xml 추가하기 (추천)

   ```java
   <?xml version="1.0" encoding="UTF-8" ?>
   <configuration>
       <include resource="org/springframework/boot/logging/logback/base.xml"/>
       <logger name="econovation" level="DEBUG" />
   </configuration>
   ```

- Log4J2 : log4j2-spring.xml

- JUL(비추) : logging.properties

- Logback extention

   - 프로파일 \<springProfile name="프로파일">
   - Environment 프로퍼티 \<springProperty>

**로거를 Log4j2로 변경하기**

```xml
<!-- Add typical dependencies for a web application -->
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <exclusions>
          <!-- exclude bass logging dependency -->
            <exclusion>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-logging</artifactId>
            </exclusion>
        </exclusions>
    </dependency>

  	<!-- Add log4j2 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-log4j2</artifactId>
    </dependency>
</dependencies>
```

---

## 테스트

**시작은 일단 spring-boot-starter-test 의존성을 추가하는 것 부터**

- test 스콥으로 추가.

**@SpringBootTest**

- @RunWith(SpringRunner.class)랑 같이 써야 함.

- 빈 설정 파일은 설정을 안해주나? 알아서 찾습니다. (@SpringBootApplication)

- webEnvironment

   - Mock : mock servlet environment. 내장 톰캣 구동 안 함.

      ```java
      package econovation.springtestdemo.sample;
      
      
      import org.junit.Test;
      import org.junit.runner.RunWith;
      import org.springframework.beans.factory.annotation.Autowired;
      import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
      import org.springframework.boot.test.context.SpringBootTest;
      import org.springframework.test.context.junit4.SpringRunner;
      import org.springframework.test.web.servlet.MockMvc;
      
      import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
      import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
      import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
      import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
      
      @RunWith(SpringRunner.class)
      @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) // Servlet Container를 목업을해서 제공
      @AutoConfigureMockMvc   // MockMvc를 사용해야 한다.
      public class SampleControllerTest {
      
          @Autowired
          MockMvc mockMvc;
      
          @Test
          public void hello() throws Exception{
                  mockMvc.perform(get("/hello"))
                          .andExpect(status().isOk())
                          .andExpect(content().string("hello keesun"))
                          .andDo(print());
          }
      }
      
      ```

   - RANDOM_PORT, DEFINED_PORT : 내장 톰캣 사용 함. (실제 테스트용 서블릿이 랜덤 포트에 뜬다.)

      ```java
      package econovation.springtestdemo.sample;
      
      
      import org.junit.Test;
      import org.junit.runner.RunWith;
      import org.springframework.beans.factory.annotation.Autowired;
      import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
      import org.springframework.boot.test.context.SpringBootTest;
      import org.springframework.boot.test.web.client.TestRestTemplate;
      import org.springframework.test.context.junit4.SpringRunner;
      import org.springframework.test.web.servlet.MockMvc;
      
      import static org.assertj.core.api.Assertions.assertThat;
      import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
      import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
      import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
      import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
      
      @RunWith(SpringRunner.class)
      @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
      public class SampleControllerTest {
      
          @Autowired
          TestRestTemplate testRestTemplate;
      
          @Test
          public void hello(){
              String result = testRestTemplate.getForObject("/hello", String.class); // URL, 원하는 Body 타입
              assertThat(result).isEqualTo("hello keesun");
          }
      }
      ```

   - NONE : 서블릿 환경 제공 안 함.

**@MockBean**

- ApplicationContext에 들어있는 빈을 Mock으로 만든 객체로 교체 함.

- 모든 @ Test 마다 자동으로 리셋

   ```java
   package econovation.springtestdemo.sample;
   
   
   import org.junit.Test;
   import org.junit.runner.RunWith;
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.boot.test.context.SpringBootTest;
   import org.springframework.boot.test.mock.mockito.MockBean;
   import org.springframework.boot.test.web.client.TestRestTemplate;
   import org.springframework.test.context.junit4.SpringRunner;
   
   import static org.assertj.core.api.Assertions.assertThat;
   import static org.mockito.Mockito.when;
   
   @RunWith(SpringRunner.class)
   @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
   public class SampleControllerTest {
       
       @Autowired
       TestRestTemplate testRestTemplate;
   
       @MockBean
       SampleService mockSampleService;
   
       @Test
       public void hello(){
           when(mockSampleService.getName()).thenReturn("jongjin");
   
           String result = testRestTemplate.getForObject("/hello", String.class); // URL, 원하는 Body 타입
           assertThat(result).isEqualTo("hello jongjin");
       }
   }
   
   ```

@SpringBootTest 라는 Annotation은 MainApplication을 찾아가서 모든 Bean들을 찾아서 등록해준다.

**슬라이스 테스트**

- 레이어 별로 잘라서 테스트하고 싶을 때

- @JsonTest : 우리가 가지고 있는 모델이 json 형태로 나갈 때 어떠한 형태로 나갈 것인가 test를 하는 annotation

- @WebMvcTest : web과 관련된 component만 bean으로 등록된다.

   ```java
   package econovation.springtestdemo.sample;
   
   
   import org.junit.Test;
   import org.junit.runner.RunWith;
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
   import org.springframework.boot.test.mock.mockito.MockBean;
   import org.springframework.test.context.junit4.SpringRunner;
   import org.springframework.test.web.servlet.MockMvc;
   
   import static org.mockito.Mockito.when;
   import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
   import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
   
   @RunWith(SpringRunner.class)
   @WebMvcTest(SampleController.class)
   public class SampleControllerTest {
   
   
       @Autowired
       MockMvc mockMvc;
   
       @MockBean
       SampleService mockSampleService;
   
       @Test
       public void hello() throws Exception{
           when(mockSampleService.getName()).thenReturn("jongjin");
   
           mockMvc.perform(get("/hello")).andExpect(content().string("hello jongjin"));
   
   
       }
   }
   ```

- @WebFluxTest (WebTestClient 는 매우 중요하다.)

   ```java
   package econovation.springtestdemo.sample;
   
   
   import org.junit.Test;
   import org.junit.runner.RunWith;
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.boot.test.context.SpringBootTest;
   import org.springframework.boot.test.mock.mockito.MockBean;
   import org.springframework.boot.test.web.client.TestRestTemplate;
   import org.springframework.test.context.junit4.SpringRunner;
   import org.springframework.test.web.reactive.server.WebTestClient;
   
   import static org.assertj.core.api.Assertions.assertThat;
   import static org.mockito.Mockito.when;
   
   @RunWith(SpringRunner.class)
   @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
   public class SampleControllerTest {
   
       @Autowired
       WebTestClient webTestClient;
   
       @Autowired
       TestRestTemplate testRestTemplate;
   
       @MockBean
       SampleService mockSampleService;
   
       @Test
       public void hello(){
           when(mockSampleService.getName()).thenReturn("jongjin");
   
           webTestClient.get().uri("/hello").exchange().expectStatus().isOk()
                   .expectBody(String.class).isEqualTo("hello jongjin");
   
       }
   }
   ```

- @DataJpaTest

### 테스트 유틸

- **OutputCapture** : 로그를 비롯해서 콘솔에 찍히는 모든 것을 캡쳐한다.

   SampleController.java

   ```java
   package econovation.springtestdemo.sample;
   
           import org.slf4j.Logger;
           import org.slf4j.LoggerFactory;
           import org.springframework.beans.factory.annotation.Autowired;
           import org.springframework.web.bind.annotation.GetMapping;
           import org.springframework.web.bind.annotation.RestController;
   
   
   @RestController
   public class SampleController {
   
       Logger logger = LoggerFactory.getLogger(SampleController.class);
   
   
       @Autowired
       private SampleService sampleService;
   
       @GetMapping("/hello")
       public String hello(){
           logger.info("holoman"); // 로그 메세지
           System.out.println("skip");
           return "hello " + sampleService.getName();
       }
   }
   ```

   SampleControllerTest.java

   ```java
   package econovation.springtestdemo.sample;
   
   
   import org.junit.Rule;
   import org.junit.Test;
   import org.junit.runner.RunWith;
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
   import org.springframework.boot.test.mock.mockito.MockBean;
   import org.springframework.boot.test.rule.OutputCapture;
   import org.springframework.test.context.junit4.SpringRunner;
   import org.springframework.test.web.servlet.MockMvc;
   
   import static org.assertj.core.api.Assertions.assertThat;
   import static org.mockito.Mockito.when;
   import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
   import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
   
   @RunWith(SpringRunner.class)
   @WebMvcTest(SampleController.class)
   public class SampleControllerTest {
   
       @Rule
       public OutputCapture outputCapture = new OutputCapture();
   
       @Autowired
       MockMvc mockMvc;
   
       @MockBean
       SampleService mockSampleService;
   
       @Test
       public void hello() throws Exception{
           when(mockSampleService.getName()).thenReturn("jongjin");
   
           mockMvc.perform(get("/hello")).andExpect(content().string("hello jongjin"));
   
           assertThat(outputCapture.toString())
                   .contains("holoman")
                   .contains("skip");
       }
   }
   ```

- TestPropertyValues

- TestRestTemplate

- ConfigFileApplicationContextInitializer

---

## Spring-Boot-Devtools

- 캐시 설정을 개발 환경에 맞게 변경.
- 클래스패스에 있는 파일이 변경 될 때마다 자동으로 재시작.
   - 직접 껏다 켜는거 (cold starts)보다 빠르다. 왜?
   - 릴로딩 보다는 느리다. (JRabel 같은건 아님)
   - 리스타트 하고 싶지 않는 리소스는? spring.devtools.restart.exclude
   - 리스타트 기능 끄러면? spring.devtools.restart.enabled = false
- 라이브 reload : 리스타트 했을 때 브라우저 자동 리프레시 하는 기능
   - 브라우저 플러그인을 설치해야 함
   - 라이브 reload 서버 끄려면? spring.devtools.liveload.enabled = false
- **글로벌 설정(1순위 우선 순위)**
   - ~/.spring-boot-devtools.properties
- 리모트 애플리케이션































