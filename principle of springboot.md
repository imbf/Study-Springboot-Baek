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

























