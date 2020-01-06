스프링 부트 활용

## 스프링 부트 활용 소개

### 스프링 부트 핵심 기능

- SpringApplication
- 외부 설정
- 프로파일
- 로깅
- 테스트
- Spring-Dev-Tools

### 각종 기술 연동

- 스프링 웹 MVC
- 스프링 데이터
- 스프링 시큐리티
- REST API 클라이언트

---

## SpringApplication

**SpringApplication**

- 기본 로그 레벨 INFO

- FailureAnalyzer : 에러가 났을 때 에러 메세지를 좀 더 예쁘게 보여준다.

- 배너 : 스프링이 시작할 때 보여주는 베너

   - banner.txt | gif | jpg | png
   - classpath 또는 spring.banner.location ( resources/application.properties에 수정 )
   - ${spring-boot.version} 등의 변수를 사용할 수 있음  : 다양한 변수들과 조건들이 있음으로 reference를 참고하자!!
      jar 파일로 packaging 할 때 manifest파일이 생성된다.
   - Banner 클래스 구현하고 SpringApplication.setBanner()로 설정 가능.
   - 배너 끄는 방법

- SpringApplicationBuilder로 빌더 패턴 사용 가능 (스프링 어플리케이션 실행 가능)

   ```java
   package econovation;
   
   
   import org.springframework.boot.autoconfigure.SpringBootApplication;
   import org.springframework.boot.builder.SpringApplicationBuilder;
   
   @SpringBootApplication
   public class SpringinitApplication {
       public static void main(String[] args) {
           new SpringApplicationBuilder()
                   .sources(SpringinitApplication.class)
                   .run(args);
       }
   }
   
   ```

- ApplicationEvent 등록

   - **ApplicationContext를 만들기 전에 사용하는 리스너는 @Bean으로 등록할 수 없다.**

      - SpringApplication.addListeners()

      SampleListener.java ( ApplicationStartingEvent Listener 작성)

      ```java
      package econovation;
      
      import org.springframework.boot.context.event.ApplicationStartingEvent;
      import org.springframework.context.ApplicationListener;
      
      // Application이 실행할때 시작하는 Event에 대한 Listener
      public class SampleListener implements ApplicationListener<ApplicationStartingEvent> {
      
          @Override
          public void onApplicationEvent(ApplicationStartingEvent event) {
              System.out.println("======================");
              System.out.println("Applcation is starting");
              System.out.println("======================");
          }
      }
      
      ```

      SpringinitApplication.java (Listener 등록)

      ```java
      package econovation;
      
      
      import org.springframework.boot.SpringApplication;
      import org.springframework.boot.autoconfigure.SpringBootApplication;
      
      @SpringBootApplication
      public class SpringinitApplication {
          public static void main(String[] args) {
              SpringApplication app = new SpringApplication(SpringinitApplication.class);
              app.addListeners(new SampleListener());
              app.run(args);
          }
      }
      
      ```

      SampleListener.java ( AppliationStartedEvent Listener 작성 )

      ```java
      package econovation;
      
      import org.springframework.boot.context.event.ApplicationStartedEvent;
      import org.springframework.context.ApplicationListener;
      import org.springframework.stereotype.Component;
      
      // Application이 실행되었을 때 시작하는 Listner
      @Component
      public class SampleListener implements ApplicationListener<ApplicationStartedEvent> {
      
          @Override
          public void onApplicationEvent(ApplicationStartedEvent event) {
              System.out.println("======================");
              System.out.println("Applcation is starting");
              System.out.println("======================");
      
          }
      }
      
      ```

      SpringinitApplication.java (Listener 등록)

      ```java
      package econovation;
      
      
      import org.springframework.boot.SpringApplication;
      import org.springframework.boot.autoconfigure.SpringBootApplication;
      
      @SpringBootApplication
      public class SpringinitApplication {
          public static void main(String[] args) {
              SpringApplication app = new SpringApplication(SpringinitApplication.class);
              app.run(args);
          }
      }
      
      ```

- WebApplication Type 설정

   - 기본적으로 Servlet이다. WebApplication을 REACTIVE로 사용할 수도 있다.

      ```java
      package econovation;
      
      
      import org.springframework.boot.SpringApplication;
      import org.springframework.boot.WebApplicationType;
      import org.springframework.boot.autoconfigure.SpringBootApplication;
      
      @SpringBootApplication
      public class SpringinitApplication {
          public static void main(String[] args) {
              SpringApplication app = new SpringApplication(SpringinitApplication.class);
              app.setWebApplicationType(WebApplicationType.NONE);
              app.run(args);
          }
      }
      ```

- Application Argument 사용하기

   - ApplicationArguments를 Bean으로 등록해 주니까 가져다 쓰면 됨

   ![image-20200106200941763](/Users/baejongjin/Library/Application Support/typora-user-images/image-20200106200941763.png)

   ApplicationArgument는 둘다 콘솔로 들어오기는 하지만 -D로 들어오는 것은 VM옵션이고 --로 들어오는 것은 arguments이다.

- 애플리케이션 실행한 뒤 뭔가 실행하고 싶을 때

   - ApplicationRunner (추천)

      ```java
      package econovation;
      
      import org.springframework.boot.ApplicationArguments;
      import org.springframework.boot.ApplicationRunner;
      import org.springframework.stereotype.Component;
      
      
      @Component
      public class SampleListener implements ApplicationRunner {
      
          @Override
          public void run(ApplicationArguments args) throws Exception{
              System.out.println("foo: " + args.containsOption("foo"));
              System.out.println("bar: " + args.containsOption("bar"));
          }
      
      }
      ```

   - CommandLineRunner ( JVM 옵션을 쓸 수 없다.)

      ```java
      package econovation;
      
      import org.springframework.boot.CommandLineRunner;
      import org.springframework.stereotype.Component;
      
      import java.util.Arrays;
      
      
      @Component
      public class SampleListener implements CommandLineRunner {
      
          @Override
          public void run(String... args) throws Exception {
              Arrays.stream(args).forEach(System.out::println);
          }
      }
      ```

   - 순서 지정 가능 @Order ( @Order(1) )

---

## 외부 설정

**사용할 수 있는 외부 설정**

- properties ( 스프링 부트가 애플리케이션을 구동할 때 자동으로 구동하는 파일 key, value 형태)

   ```java
   // resource/application.properties에 존재하는 값을 가져오는 가장 기본적인 코드
   package econovation;
   
   import org.springframework.beans.factory.annotation.Value;
   import org.springframework.boot.ApplicationArguments;
   import org.springframework.boot.ApplicationRunner;
   import org.springframework.stereotype.Component;
   
   @Component
   public class SampleRunner implements ApplicationRunner {
       @Value("${keesun.name}")
       private String name;
   
       @Override
       public void run(ApplicationArguments args) throws Exception {
           System.out.println("====================");
           System.out.println(name);
           System.out.println("====================");
       }
   }
   ```

- YAML

- 환경 변수

- 커맨드 라인 아규먼트

**프로퍼티 우선 순위**

1. 홈 디렉토리에 있는 spring-boot-dev-tools.properties

2. 테스트에 있는 @TestPropertySource

3. @SpringBootTest 애노테이션의 properties 애트리뷰트

4. 커맨드 라인 아규먼트

   ```
   java -jar target/springinit-0.0.1-SNAPSHOT.jar --keesun.name=whiteship
   ```

   

5. SPRING_APPLICATION_JSON ( 환경 변수 또는 시스템 프로퍼티) 에 들어있는 프로퍼티

6. ServletConfig 파라미터

7. ServletContext 파라미터

8. java:comp/env JNDI 애트리뷰트

9. System.getProperteis() 자바 시스템 프로퍼티

10. OS 환경 변수

11. RandomValuepropertySource

12. JAR 밖에 있는 특정 프로파일용 application properties

13. JAR 안에 있는 특정 프로파일용 application properties

14. JAR 밖에 있는 application properties

15. JAR 안에 있는 application properties

16. @PropertySource

17. 기본 프로퍼티 (SpringApplication.setDefaultProperties)

**application.properties 우선순위** 

1. file:./config.
2. file:./
3. classpath:/config/
4. classpath:/

**랜덤값 설정하기**

- ${random.*}

**플레이스 홀더**

- name=keesun
- fullName = ${name} back






























