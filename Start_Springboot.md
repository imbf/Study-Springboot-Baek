# 스프링 부트 시작하기

## 스프링 부트 소개

### 스프링 부트란?

제품 수준의 독립적인 스프링 기반의 애플리케이션을 쉽게 만들 수 있게 한다. 

스프링의 opinionated view 와 third-party libraries(톰캣) 를 기본적으로 지원해준다.

스프링 개발을 할때 더 빠르고 더 폭넓은 사용성을 제공해준다.

일일이 설정을 하지 않아도 convention으로 제공된 설정을 제공해준다. (얼마든지 그러한 설정들을 바꿀 수 있다.)

business로직에 필요한 기능도 필요해준다.

XML설정을 더이상 쓰지 않고 code generation을 더이상 쓰지 않는다.

자바 8이상 써야한다.!!!

---

## 스프링 부트 시작하기

**Enable Auto Import :** pom.xml을 바꿀 때마다 바로바로 반영을 해준다.

**pom.xml 추가사항**

프로젝트 간에 의존성을 관리해 줄 수 있는 parent 코드를 추가한다.
spring boot web application을 위해서 dependencies를 추가한다.
maven build 플러그인을 추가한다.

```xml
<!-- Inherit defaults from Spring Boot -->
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

**SpringBoot 시작하기** (아주 많은 의미를 담고있다.)

```java
package me.whiteship;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication	// Annotation 추가
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

```

**mvn package 명령어를 입력하면 패키지를 패키징하는데 이 프로젝트는 메이븐 기반의 자바 프로젝트이기 때문에 jar파일이 생긴다.**

우리는 이러한 jar파일을 실행할 수 있다. 실행하면 아까와 똑같이 웹 애플리케이션이 동작한다.

---

## 스프링 부트 프로젝트 생성기

https://start/spring.io 페이지에 들어가서 spiring 프로젝트를 생성할 수 있다.

pom.xml과 다양한 애플리케이션 실행 파일들이 만들어 진다.

---

## 스프링 부트 프로젝트 구조

**메이븐 기본 프로젝트 구조와 동일**

- 소스 코드(src\main\java)
- 소스 리소스(src\main\resource)
- 테스트 코드(src\test\java)
- 테스트 리소스(src\test\resource)

@SpringBootApplication이 Annotation이 붙어 있는 mainApplication의 위치 : 가장 상위 패키지에 위치할 것을 추천한다. <u>component scan을 해야하기 때문에</u>



























