# 스프링 부트 활용

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

      

































