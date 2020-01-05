# 스프링 부트 원리

## 의존성 관리 이해

**pom.xml**

```xml
<parent>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-parent</artifactId>
  <version>2.2.2.RELEASE</version>
</parent>

<!-- Add typical dependencies for a web application -->
<dependencies>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
  </dependency>
</dependencies>

<!-- Add Maven BuildTool Plugin -->
<build>
  <plugins>
    <plugin>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-maven-plugin</artifactId>
    </plugin>
  </plugins>
</build>
```

우리가 parent로써 org.springframework.boot pom파일에 들어가보면 의존성을 우리가 관리해 주지 않아도 스프링 부트가 제공하는 의존성으로서 우리 프로젝트를 관리해주기 때문에 우리는 dependency만 설정하면 된다.

Dependencies가 어떻게 되어 있는지는 우측의 maven projects의 dependencies에서 찾아보면 된다.

<img src="/Users/baejongjin/Library/Application Support/typora-user-images/image-20200104232927313.png" alt="image-20200104232927313" style="zoom:50%;" />

**이렇게 springboot를 사용하면 우리가 관리해야할 라이브러리 버전에 대한 일이 줄어든다.**

springboot에서 관리하는 라이브러리의 버전은 명시를 하지 않아도 되지만 내가 특별히 원하는 버전이 있거나, springboot에서 의존성을 관리해주지 않는 라이브러리는 직접 명시 해야한다.

**프로젝트가 자기만의 상속구조를 가져서 우리는 parent 폼을 명시하지 못할 때의 방법**

1. Use dependency Management section

   ```xml
   <dependencyManagement>
       <dependencies>
           <dependency>
               <!-- Import dependency management from Spring Boot -->
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-dependencies</artifactId>
               <version>2.2.2.RELEASE</version>
               <type>pom</type>
               <scope>import</scope>
           </dependency>
       </dependencies>
   </dependencyManagement>
   ```

2. 당신이 원하는 라이브러리를 org.springframework.boot의 부모로써 만들어라 !!

하지만 org.springframework.boot를 parent로 해야지 encoding 형식과, resource filtering, .... 다양한 기능들을 지원해주기 때문에 parent를 사용하는 것을 추천한다.

---

## 의존성 관리 응용

springboot가 지원하는 의존성 관리 기능을 활용하는 방법

### **의존성을 추가하는 방법**(spring data JPA, model mapper)

### 기존 의존성 변경 방법

**pom.xml 수정** 

```xml

<!-- change spring version (기존 의존성 버전 변경) -->
<properties>
  <spring.version>5.2.1.RELEASE</spring.version>
</properties>


<dependencies>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
  </dependency>

  <!-- springboot과 관리하지 않는 의존성을 추가하는 방법 -->
  <dependency>
    <groupId>org.modelmapper</groupId>
    <artifactId>modelmapper</artifactId>
    <version>2.1.0</version>
  </dependency>
  
  
  
</dependencies>
```

maven의존성을 검사하고 싶을 때 mvnrepository.com에서 참조하자.

---

## 자동 설정 이해

@springBootConfiguration은

@SpringBootConfigurateon, @ComponentScan, @EnableAutoConfiguration 이 3개의 합과 같다.

```java
package me.whiteship;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

//@SpringBootApplication
@SpringBootConfiguration
@Component
@EnableAutoConfiguration
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}

```

**자동 설정 개요**

- @EnableAutoConfiguration (@SpringBootApplication 안에 숨어 있음)
- 빈은 사실 두 단계로 나눠서 읽힘
   - 1단계 : @ComponentScan
   - 2단계 : @EnableAutoConfiguration
- **@ComponentScan : Component라는 Annotation을 가진 class들을 Bean으로 등록한다. (Springboot에서 필요 없는 bean들을 자동적으로 걸러내는 filter조건을 가진다.) 자신의 패키지 내에 있는 하위 클래스나 패키지는 CompoenentScan이 가능하기 때문에 Bean으로 등록이 되지만 자신의 패키지 외부에 있는 패키지에 있는 클래스들은 Bean 으로 등록되지 않는다는 점에 주의하자!!!**
   - @Component
   - @Configuration @Repository @Service @Controller @RestController
- **EnableAutoConfiguration** : 수 많은 자동 설정들이 조건에 따라 적용이 되서 수많은 Bean들을 생성되게 하는 Annotation
   - **spring.factories** : 여러개의 configuration 파일들이 Autoconfiguration되어 있다. (스프링 컨벤션들)
      자바 설정 파일들을 모두 다 읽어 들인다.( 조건이 주어져 있다. )
      - org.springframework.boot.autoconfigure.EnableAutoConfiguration
   - @Configuration
   - @ConditionalOnXxxYyyZzz

---

## 자동 설정 만들기 Starter와 AutoConfigure

**자동 설정 구현**

- Xxx-Spring-Boot-Autoconfigure 모듈 : 자동 설정

- xxx-Spring-Boot-Starter 모듈 : 필요한 의존성 정의

- 그냥 하나로 만들고 싶을 때는?

   - Xxx-Spring-Boot-Starter

- **구현 방법**

   1.  의존성 추가(pom.xml)

      ```xml
          <!-- Add spring-boot autoconfigure and autoconfigure processor -->
          <dependencies>
              <dependency>
                  <groupId>org.springframework.boot</groupId>
                  <artifactId>spring-boot-autoconfigure</artifactId>
              </dependency>
              <dependency>
                  <groupId>org.springframework.boot</groupId>
                  <artifactId>spring-boot-autoconfigure-processor</artifactId>
                  <optional>true</optional>
              </dependency>
          </dependencies>
      
          <!-- Add springboot dependencyManagement -->
          <dependencyManagement>
              <dependencies>
                  <dependency>
                      <groupId>org.springframework.boot</groupId>
                      <artifactId>spring-boot-dependencies</artifactId>
                      <version>2.0.3.RELEASE</version>
                      <type>pom</type>
                      <scope>import</scope>
                  </dependency>
              </dependencies>
          </dependencyManagement>
      ```

   2. @Configuration 파일 작성

      ```java
      package me.whiteship;
      
      import org.springframework.context.annotation.Bean;
      import org.springframework.context.annotation.Configuration;
      
      @Configuration
      public class HolomanConfiguration {
      
          @Bean   //Holoman 이라는 Bean을 returng하는 설정파일을 만들었다.
          public Holoman holoman(){
              Holoman holoman = new Holoman();
              holoman.setHowLong(5);
              holoman.setName("Keesun");
              return holoman;
          }
      
      }
      ```

      

   3. src/main/resource/META-INF에 spring.factories 파일 만들기

   4. spring.factories 안에 자동 설정 파일 추가

      ```
      org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
        me.whiteship.HolomanConfiguration
      ```

   5. mvn install : .jar파일로 패키징

   6. 원하는 프로젝트에 가서 의존성으로 추가할 수 있다.
      pom.xml

      ```xml
      <!-- Add jar File Dependency -->
      <dependency>
        <groupId>me.whiteship</groupId>
        <artifactId>jongjin-spring-boot-starter</artifactId>
        <version>1.0-SNAPSHOT</version>
      </dependency>
      ```

**Bean 덮어쓰기 방지하기**

- @ConditionalOnMissingBean
   Bean 설정

   ```java
   package me.whiteship;
   
   import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
   import org.springframework.context.annotation.Bean;
   import org.springframework.context.annotation.Configuration;
   
   @Configuration
   public class HolomanConfiguration {
   
       @Bean   //Holoman 이라는 Bean을 returng하는 설정파일을 만들었다.
       @ConditionalOnMissingBean   // 기본적으로 등록한 Bean이 쓰여야 하지만 우리는 Bean이 등록되지 않는 경우에만 이 Bean을 쓰겟다는 명령
       public Holoman holoman(){
           Holoman holoman = new Holoman();
           holoman.setHowLong(5);
           holoman.setName("Keesun");
           return holoman;
       }
   
   }
   ```

**굳이 Bean 설정을 장황하게 하지 않고 Bean의 값을 바꾸고 싶을 때 사용하는 방법 (resource/properties이용)**

1. 프로젝트의 resources폴더에 application.properties파일 생성

2. 파일 작성

   ```
   holoman.name = 종진
   holoman.how-long = 100
   ```

3. 이전의 패키징 한 파일에 가서 HolomanProperties파일 생성

   ```java
   package me.whiteship;
   
   import org.springframework.boot.context.properties.ConfigurationProperties;
   
   @ConfigurationProperties("holoman")
   public class HolomanProperties {
       
       private String name;
       
       private int howLong;
   
       public int getHowLong() {
           return howLong;
       }
   
       public String getName() {
           return name;
       }
   
       public void setHowLong(int howLong) {
           this.howLong = howLong;
       }
   
       public void setName(String name) {
           this.name = name;
       }
       
   }
   
   ```

4. pom.xml 파일 수정

   ```xml
   <dependency>
   	<groupId>org.springframework.boot</groupId>
     <artifactId>spring-boot-configuration-processor</artifactId>
     <optional>true</optional>
   </dependency>
   ```

5. HolomanConfiguration 파일 수정
   Bean을 등록하지 않지만 프로젝트 파일을 import 하는 곳의 properties를 가져다가 사용할 수 있다.

   ```java
   package me.whiteship;
   
   import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
   import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
   import org.springframework.boot.context.properties.EnableConfigurationProperties;
   import org.springframework.context.annotation.Bean;
   import org.springframework.context.annotation.Configuration;
   
   @Configuration
   @EnableConfigurationProperties(HolomanProperties.class)
   public class HolomanConfiguration {
   
       @Bean   //Holoman 이라는 Bean을 returng하는 설정파일을 만들었다.
       @ConditionalOnMissingBean   // 기본적으로 등록한 Bean이 쓰여야 하지만 우리는 Bean이 등록되지 않는 경우에만 이 Bean을 쓰겟다는 명령
       public Holoman holoman(HolomanProperties properties){
           Holoman holoman = new Holoman();
           holoman.setHowLong(properties.getHowLong());
           holoman.setName(properties.getName());
           return holoman;
       }
   
   }
   ```

---

## 내장 웹 서버 이해

**내장 서블릿 컨테이너**

- **스프링 부트는 서버가 아니다** : 내장 서블릿 컨테이너를 쉽게 사용할 수 있게 해주는 Tool 이다. 서버가 아니다 !!!!

   - 톰캣 객체 생성 : 웹 서버로써 스프링을 실행하면 기본적으로 내장 톰캣이 import 된다.

   - 포트 설정

   - 톰캣에 컨텍스트 추가

   - 서블릿 만들기

   - 톰캣에 서블릿 맵핑

   - 톰캣 실행 대기

      ```java
      package me.whiteship;
      
      import org.apache.catalina.Context;
      import org.apache.catalina.LifecycleException;
      import org.apache.catalina.startup.Tomcat;
      
      import javax.servlet.ServletException;
      import javax.servlet.http.HttpServlet;
      import javax.servlet.http.HttpServletRequest;
      import javax.servlet.http.HttpServletResponse;
      import java.io.IOException;
      import java.io.PrintWriter;
      
      public class Application {
      
          public static void main(String[] args) throws LifecycleException {
              Tomcat tomcat = new Tomcat();   //톰캣 객체 생성
              tomcat.setPort(8080);   // 포트 설
      
              Context context = tomcat.addContext("/", "/");  // 톰캣에 컨텍스트 추가
      
              HttpServlet servlet = new HttpServlet() {   // 서블릿 만들기
                  @Override
                  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                      PrintWriter writer = resp.getWriter();
                      writer.println("<html><head><title>");
                      writer.println("Hey.Tomact");
                      writer.println("</Title></head>");
                      writer.println("<body><h1>Hello Tomcat</h1><body>");
                      writer.println("</html>");
      
                  }
              };
              String servletName = "helloServlet";
              tomcat.addServlet("/", servletName, servlet); // 톰캣에 서블릿 맵핑
              context.addServletMappingDecoded("/hello",servletName);
      
      
              tomcat.start();
              tomcat.getServer().await(); // 톰캣 실행 대기
          }
      }
      
      ```

      

- **이 모든 과정을 보다 상세히 또 유연하고 설정하고 실행해주는게 바로 스프링 부트의 자동 설정. 자동설정으로 Tomcat, Servlet, Context 객체를 생성한다.**

   - ServletWebServerFactoryAutoConfiguration (서블릿 웹 서버 생성)
      - TomctServletWebServerFactoryCustomizer ( 서버 커스터마이징 )
   - DispatcherServletAutoConfiguration (서블릿 기반의 MVC라면 만들어야한다.)
      -  서블릿 만들고 등록 : 어떠한 서블릿 컨테이너를 사용하든간에 등록해서 사용한다.

---

## 내장 웹 서버 응용 : 컨테이너와 포트

스프링부트는 기본적으로 web application server로써 Tomcat을 쓴다. (자동 설정에 의해서 Tomcat용 설정파일이 read)

**다른 서블릿 컨테이너 사용방법** (pom.xml에서 의존성 빼기 + jetty추가)

```xml
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <exclusions>
                <exclusion>
                    <groupId>org.spring.framework.boot</groupId>
                    <artifactId>spring-boot-starter-tomcat</artifactId>
                </exclusion>
            </exclusions>
        </dependency>

        <dependency>
            <groupId>org.spirngframework.boot</groupId>
            <artifactId>spring-boot-starter-jetty</artifactId>
        </dependency>
		</dependencies>
```

**웹 서버 사용하지 않기** (resources/application.properties 수정)

```
spring.main.web-application-type=none
```

**포트**

- 포트변경
   resources/application.propertes 수정

   ```
   server.port = 7070;
   ```

- 랜덤 포트 변경
   resources/application.propertes 수정

   ```java
   server.port = 0;
   ```

- ApplicationListener\<ServletWebServerInitializedEvent>

   ```java
   package me.whiteship;
   
   import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
   import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
   import org.springframework.context.ApplicationListener;
   import org.springframework.stereotype.Component;
   
   @Component
   public class PortListener implements ApplicationListener<ServletWebServerInitializedEvent> {
       @Override
       public void onApplicationEvent(ServletWebServerInitializedEvent servletWebServerInitializedEvent){
           ServletWebServerApplicationContext applicationContext = servletWebServerInitializedEvent.getApplicationContext();
           System.out.println(applicationContext.getWebServer().getPort());
       }
   }
   
   ```

---

## 내장 웹 서버 응용 : HTTPS와 HTTP2

**HTTPS 설정하기**

- 키스토어 만들기 

   1. cmd를 이용해서

      ```
      keytool -genkey -alias spring -storetype PKCS12 -keyalg RSA -keysize 2048 -keystore keystore.p12 -validity 4000
      ```

   2. resource/properties 에 키스토어 정보 저장

      ```
      server.ssl.key-store = keystore.p12
      server.ssl.key-store-type = PKCS12
      server.ssl.key-store-password=123456
      server.ssl.key-alias=spring
      ```

   3. 공인된 인증서가 아니기 때문에 Not Secure와 같은 빨강색 태그가 뜬다.

- HTTP는 못쓰네?

**HTTP 커넥터는 코딩으로 설정하기**

- ```java
   @Bean
   public ServletWebServerFactory serverFactory(){
     TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
     tomcat.addAdditionalTomcatConnectors(createStandardConnector());
     return tomcat;
   }
   
   private Connector createStandardConnector(){
     Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
     connector.setPort(8080);
     return connector;
   }
   ```

   

**HTTP2 설정**

- server.http2.enable (resource/properties를 수정) -> ssl은 기본적으로 적용 되어야 한다.

   ```
   server.ssl.key-store = keystore.p12
   server.ssl.key-store-type = PKCS12
   server.ssl.key-store-password=123456
   server.ssl.key-alias=spring
   server.port=8443
   server.http2.enabled=true
   server.http2.enabled=true
   ```

- 사용하는 서블릿 컨테이너 마다 다름

---

## 톰캣 HTTP2

톰캣을 이용해서 http2를 배포하려면 project structure에서 language level 뿐만 아니라 dependencies 또한 바꾸어 줘야한다.

---

## 독립적으로 실행 가능한 JAR

**"그러고 보니 JAR 파일 하나로 실행할 수 있네?"**

- mvn package를 하면 실행 가능한 **JAR 파일 "하나가"** 생성 됨 (모든 라이브러리들이 다 들어 있다.)
- **spring-maven-plugin이 해주는 일 (패키징)** 
- **과거 "uber" jar를 사용** 
   - 모든 클래스(의존성 및 애플리케이션)를 하나로 압축하는 방법
   - 뭐가 어디에서 온건지 알 수가 없음
      - 무슨 라이브러리를 쓰는건지..
   - 내용은 다르지만 이름이 같은 파일은 또 어떻게?
- **스프링 부트의 전략 : 독립적으로 실행가능한 Application** 
   - 내장 JAR : 기본적으로 자바에는 내장 JAR를 로딩하는 **표준적인 방법이 없음**
   - 애플리케이션 클래스와 라이브러리 위치 구분
   - org.springframework.boot.loader.jar.JarFile을 사용해서 내장 JAR를 읽는다.
   - org.springframework.boot.loader.Launcher를 사용해서 실행한다.

---

## 스프링 부트 원리 정리

- 의존성 관리
- 자동 설정
   - @EnableAutoConfiguration
- 내장 웹 서버
   - 스프링 부트는 서버가 아니고, 내장 서버를 실행하는 것
- 독립적으로 실행 가능한 JAR
   - spring-boot-maven 플러그인이 다양한 역할을 해준다.





















