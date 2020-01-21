# Spring Security : Starter-Security

**스프링 시큐리티**

- 웹 시큐리티

   - 의존성 추가

      ```xml
      <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
      </dependency>
      ```

- 메소드 시큐리티

- 다양한 인증 방법 지원 (accept의 타입에 따라 달라진다.~~!!!)

   - LDAP, 폼 인증, Basic 인증, OAuth, ...

**스프링 부트 시큐리티 자동 설정**

- SecurityAutoConfiguration

- UserDetailsServiceAutoConfiguration

   - 우리만의 WebSecuritConfig 설정 파일

      ```java
      package econovation.springbootsecurity;
      
      import org.springframework.context.annotation.Configuration;
      import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
      import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
      
      @Configuration
      public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
          
      }
      ```

- spring-boot-starter-security

   - 스프링 시큐리티 5.* 의존성 추가

- 모든 요청에 인증이 필요함. : Spring Boot가 설정해준다.

- 기본 사용자 생성 **(사실상 SpringBoot가 지원해주는 것은 사용하지 않는 편이고 커스터마이징을 하는 편이다)**

   - Username: user
   - Password: 애플리케이션을 실행할 때 마다 랜덤 값 생성(콘솔에 출력 됨)
   - spring.security.user.name
   - spring.security.user.password

- 인증 관련 각종 이벤트 발생

   - DefaultAuthenticationEventPublisher 빈 등록
   - 다양한 인증 에러 핸들러 등록 가능

**스프링 부트 시큐리티 테스트**

- https://docs.spring.io/spring-security/site/docs/current/reference/html/test-method.html

**Controller 애노테이션을 붙였을 떄는 정적인 서비스도 하지만 기본적으로 @RestController가 붙였을 때는 정적 맵핑을 하지 않는것 같다.**