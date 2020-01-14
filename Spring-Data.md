# Spring-Data

## Spring-Data : Introduction

### SQL DB

- 인메모리 데이터베이스 지원
- DataSource 설정
- DBCP 설정
- JDBC 사용하기
- 스프링 데이터 JPA 사용하기
- jOOQ 사용하기
- 데이터베이스 초기화
- 데이터베이스 마이그레이션 툴 연동하기

### NoSQL

- Redis (Key/Value)
- MongoDB (Document)
- Neo4J (Graph)

---

## Spring-Data : 인메모리 데이터 베이스

**지원하는 인메모리 데이터베이스**

- **H2** (추천, 콘솔 때문에...)
- HSQL
- Derby

**Spring-JDBC가 클래스패스에 있으면 자동 설정이 필요한 빈을 설정 해줍니다.**

- DataSource

- JdbcTemplate

   - jdbcTemplate를 사용하게 되면 SQL을 간결하게 사용할 수 있다.
   - 리소스 반납 처리가 잘 되어있기 때문에 안전하다
   - 에러 계층관계를 잘 만들어 놨기 때문에 좀 더 가독성 높은 Error를 볼 수 있다.

   ```java
   package econovation.springbootjdbc;
   
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.boot.ApplicationArguments;
   import org.springframework.boot.ApplicationRunner;
   import org.springframework.jdbc.core.JdbcTemplate;
   import org.springframework.stereotype.Component;
   
   import javax.sql.DataSource;
   import java.sql.Connection;
   import java.sql.Statement;
   
   @Component
   public class H2Runner implements ApplicationRunner {
   
       @Autowired
       DataSource dataSource;
   
       @Autowired
       JdbcTemplate jdbcTemplate;
   
       @Override
       public void run(ApplicationArguments args) throws Exception {
           try(Connection connection = dataSource.getConnection();){
               System.out.println(connection.getMetaData().getURL());
               System.out.println(connection.getMetaData().getUserName());
   
               Statement statement = connection.createStatement();
               String sql = "CREATE TABLE USER(ID INTEGER NOT NULL, name VARCHAR(255), PRIMARY KEY(id))";
               statement.executeUpdate(sql);
           }
   
           jdbcTemplate.execute("INSERT INTO USER VALUES (1, 'jongjin')");
   
           // 원래는 위 코드와 같은 코드들은 try-catch로 묶어야 하고 에러가 발생하면 ROLLBACK해야한다.
   
       }
   }
   ```

**우리가 아무런 설정을 하지 않으면 스프링은 자동적으로 인메모리 데이터베이스를 사용한다.**

**인메모리 데이터베이스 기본 열결 정보 확인하는 방법**

- URL : "testdb"
- username : "sa"
- password : ""

**H2 콘솔 사용하는 방법**

- spring-boot-devtools를 추가 (dependency 추가)

- spring.h2.console.enabled=true 만 추가(resource/application.properties 파일에 추가)

   ```
   spring.h2.console.enabled=true
   ```

- localhost:8080/h2-console로 접속( 이 path도 바꿀 수 있음)

---

## Spring-Data : MySQL 설정하기

**스프링 부트가 지원하는 DBCP(DataBase Connection Pool)** : Application을 최적화 하는 일을 하는 사람들은 DBCP에 대해서 많이 공부해야 한다.

1. **HikariCP(기본)**
   - https://github.com/brettwooldridge/HikariCP#frquently-used
2. Tocat CP
3. Commons DBCP2

**DBCP 설정** (resource/application.properties에서 설정 각각의 JDBC문서에서 확인하자)

- **spring.datasource.hikari.***
- spring.datasource.tomcat.*
- spring.datasource.dbcp2.*

**MYSQL 커넥터 의존성 추가**

```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
```

**MySQL 추가 (도커 사용)**

- **docker run -p 3306:3306 --name mysql_boot -e MYSQL_ROOT_PASSWORD=1 -e MYSQL_DATABASE=springboot -e MYSQL_USER=keesun -e MYSQL_PASSWORD=pass -d mysql**
- **docker exec -i -t mysql_boot bash**  : 컨테이너 안에 들어가서 bash를 실행하라는 명령어
- **mysql -u root -p** : mysql 접속

**MySQL용 Datasource 설정** (resources/application.properties)

- spring.datasource.url=jdbc:mysql://localhost:3306/springboot?useSSL=false
- spring.datasource.username=keesun
- spring.datasource.password=pass

---

## Spring-Data : PostgreSQL

**의존성 추가**

```xml
<!-- Add postgresql database -->
  <dependency>
  <groupId>org.postgresql</groupId>
  <artifactId>postgresql</artifactId>
</dependency>
```

**PostgreSQL 설치 및 서버 실행**(docker)

1. docker run -p 5432:5432 -e POSTGRES_PASSWORD=pass -e POSTGRES_USER=keesun -e POSTGRES_DB=springboot --nbame postgres_boot -d postgre
2. docker exec -i -t postgres bash
3. su -postgres

데이터베이스 조회 : \list

테이블 조회 : \dt

쿼리 : SELECT * FROM account;

```java
package econovation.springbootjdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Component
public class H2Runner implements ApplicationRunner {

    @Autowired
    DataSource dataSource;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try(Connection connection = dataSource.getConnection();){
            System.out.println(connection.getClass());
            System.out.println(connection.getMetaData().getDriverName());
            System.out.println(connection.getMetaData().getURL());
            System.out.println(connection.getMetaData().getUserName());

            Statement statement = connection.createStatement();
            String sql = "CREATE TABLE USER(ID INTEGER NOT NULL, name VARCHAR(255), PRIMARY KEY(id))";
            statement.executeUpdate(sql);
        }

        jdbcTemplate.execute("INSERT INTO USER VALUES (1, 'jongjin')");

        // 원래는 위 코드와 같은 코드들은 try-catch로 묶어야 하고 에러가 발생하면 ROLLBACK해야한다.

    }
}
```

---

## Spring-Data : ORM, JPA, Spring Data JPA

**ORM(Object-Relational Mapping) 과 JPA(Java Persistence API)**

- 객체와 릴레이션을 맵핑할 때 발생하는 개념적 불일치를 해결하는 프레임워크
- http://hibernate.org/orm/what-is-an-orm/
- JPA : ORM을 위한 자바 (EE) 표준

**스프링 데이터 JPA**

- Repository 빈 자동 생성
- 쿼리 메소드 자동 구현
- @EnableJpaRepositories (스프링 부트가 자동으로 설정 해줌)
- **SpringData JPA -> JPA -> Hibernate( JPA 구현체 ) -> Datasource** 

**스프링 데이터 JPA 의존성 추가**

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

**스프링 데이터 JPA 사용하기**

- @Entitiy 클래스 만들기

   ```java
   package econovation.springbootjpa.account;
   
   import javax.persistence.Entity;
   import javax.persistence.GeneratedValue;
   import javax.persistence.Id;
   import java.util.Objects;
   
   @Entity
   public class account {
       
       @Id 
       @GeneratedValue
       private Long id;
       
       private String username;
       
       private String password;
   
       public Long getId() {
           return id;
       }
       
       public void setId(Long id) {
           this.id = id;
       }
   
       public String getUsername() {
           return username;
       }
   
       public void setUsername(String username) {
           this.username = username;
       }
   
       public String getPassword() {
           return password;
       }
   
       public void setPassword(String password) {
           this.password = password;
       }
   
       @Override
       public boolean equals(Object o) {
           if (this == o) return true;
           if (o == null || getClass() != o.getClass()) return false;
           account account = (account) o;
           return Objects.equals(id, account.id) &&
                   Objects.equals(username, account.username) &&
                   Objects.equals(password, account.password);
       }
   
       @Override
       public int hashCode() {
           return Objects.hash(id, username, password);
       }
   }
   
   ```

- Repository 만들기

   ```java
   package econovation.springbootjpa.account;
   
   import org.springframework.data.jpa.repository.JpaRepository;
   
   
   
   public interface AccountRepository extends JpaRepository<Account, Long> {
       Account findByUsername(String name);
     	//Optional<Account> findByUsername(String name); 도 가능하다.
   }
   
   ```

**스프링 데이터 Repository 테스트 만들기**

- H2 DB를 테스트 의존성에 추가하기

   ```xml
   <dependency>
   	<groupId>com.h2database</groupId>
     <artifactId>h2</artifactId>
     <scope>test</scope>
   </dependency>
   ```

- @DataJpaTest (슬라이드 테스트) 작성

   ```java
   package econovation.springbootjpa.Account;
   
   import econovation.springbootjpa.account.Account;
   import econovation.springbootjpa.account.AccountRepository;
   import org.junit.Test;
   import org.junit.runner.RunWith;
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
   import org.springframework.jdbc.core.JdbcTemplate;
   import org.springframework.test.context.junit4.SpringRunner;
   
   import javax.sql.DataSource;
   import java.sql.Connection;
   import java.sql.SQLException;
   
   import static org.assertj.core.api.Assertions.assertThat;
   
   //@SpringBootTest 는 애플리케이션에 존재하는 모든 Bean이 다 등록됩니다. 이런경우 DB 서버는 POSTGRESQL이 사용된다.
   //@SpringBootTest(properties = "DBURL")  overriding 시켜준다.
   @RunWith(SpringRunner.class)
   @DataJpaTest    // Repository와 관련된 빈들만 등록을해서 테스트를 만드는 것, 인메모리 데이터베이스가 무조건 필요하다.(자동으로 설정된다)
   public class AccountRepositoryTest {
   
       @Autowired
       DataSource dataSource;
   
       @Autowired
       JdbcTemplate jdbcTemplate;
   
       @Autowired
       AccountRepository accountRepository;
   
       @Test
       public void di() throws SQLException {
           try(Connection connection = dataSource.getConnection()){
               Account account = new Account();
               account.setUsername("keesun");
               account.setPassword("pass");
   
               Account newAccount = accountRepository.save(account);
   
               assertThat(newAccount).isNotNull();
   
               Account existingAccount = accountRepository.findByUsername(newAccount.getUsername("keesun"));
               assertThat(existingAccount).isNotNull();
   
               Account nonexistingAccount = accountRepository.findByUsername("whiteship");
               assertThat(nonexistingAccount).isNull();
   
           }
       }
   }
   ```

---

## Spring-Data : Database 초기화

**JPA를 사용한 데이터베이스 초기화**

- spring.jpa.hibernate.ddl-auto

- spring.jpa.generate-dll=true 로 설정 해줘야 동작함

   resources/application.properties

   ```
   spring.jpa.hibernate.ddl-auto=update (다양한 value 종류가 있으니 공부하자!!)
   spring.jpa.generate-ddl=true
   spring.jpa.show-sql=true
   ```

**SQL 스크립트를 사용한 데이터베이스 초기화**

- schema.sql 또는 schema-${platform}.sql (먼저 호출)
- data.sql 또는 data-${platform}.sql (다음 호출)
- ${platform} 값은 spring.datasource.platform 으로 설정 가능



























