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

- 스프링 부트는 서버가 아니다.
   - 톰캣 객체 생성
   - 포트 설정
   - 톰캣에 컨텍스트 추가
   - 서블릿 만들기
   - 톰캣에 서블릿 맵핑
   - 톰캣 실행 대기
- 이 모든 과정을 보다 상세히 또 유연하고 설정하고 실행해주는게 바로 스프링 부트의 자동 ㅓㄹ정.
   - ServletWebServerFactoryAutoConfiguration (서블릿 웹 서버 생성)
      - TomctServletWebServerFactoryCustomizer ( 서버 커스터마이징 )
   - DispatcherSErvletAutoConfiguration
      -  서블릿 만들고 등록



















