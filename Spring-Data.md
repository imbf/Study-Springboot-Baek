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

- schema.sql 또는 schema-${platform}.sql => 스키마 파일만 따로 관리할 수 있다. (먼저 호출)
- data.sql 또는 data-${platform}.sql (다음 호출)
- ${platform} 값은 spring.datasource.platform 으로 설정 가능

---

## Spring-Data : Database Migration

Flyway와 Liquidbase가 대표적인데, 지금은 Flyway를 사용하겠습니다.

https://docs.spring.io/spring-boot/docs/2.0.3.RELEASE/reference/htmlsingle/#howto-execute-flyway-database-migrations-on-startup

**의존성 추가**

- org.flywaydb:flyway-core

   ```xml
   <dependency>
      <groupId>org.flywaydb</groupId>
      <artifactId>flyway-core</artifactId>
   </dependency>
   ```

**마이그레이션 디렉토리**

- db/migration 또는 db/migration/{vendor}
- spring.flyway.locations 로 변경 가능

**마이그레이션 파일 이름**  (한번 적용된 스크립트 파일은 절대로 건드리지 말아야 한다.)

- V숫자__이름.sql
- V는 꼭 대문자로.
- 숫자는 순차적으로 (타임스탬프 권장)
- 숫자와 이름 사이에 언더바 두 개.
- 이름은 가능한 서술적으로

<img src="/Users/baejongjin/Library/Application Support/typora-user-images/image-20200114110748317.png" alt="image-20200114110748317" style="zoom:50%;" />

---

## Spring-Data : Redis

**캐시, 메시지 브로커, 키/밸류 스토어 등으로 사용 가능.**

**의존성 추가**

- spring-boot-starter-data-redis

   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-data-redis</artifactId>
   </dependency>
   ```

**Redis 설치 및 실행 (도커)**

- docker run -p 6379:6379 --name redis_boot -d redis
- docker exec -i -t redis_boot redis-cli

**스프링 데이터 Redis** (application.properties를 통해서 다양한 설정이 가능하다.)

- https://projects.spring.io/spring-data-redis/

- StringRedisTemplate 또는 RedisTemplate

   RedisRunner.java

   ```java
   package econovation.springbootredis;
   
   import econovation.springbootredis.account.Account;
   import econovation.springbootredis.account.AccountRepository;
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.boot.ApplicationArguments;
   import org.springframework.boot.ApplicationRunner;
   import org.springframework.data.redis.core.StringRedisTemplate;
   import org.springframework.data.redis.core.ValueOperations;
   import org.springframework.stereotype.Component;
   
   import java.util.Optional;
   
   @Component
   public class RedisRunner implements ApplicationRunner {
   
       @Autowired
       StringRedisTemplate redisTemplate;
   
       @Autowired
       AccountRepository accountRepository;
   
       @Override
       public void run(ApplicationArguments args) throws Exception {
           ValueOperations<String,String> values = redisTemplate.opsForValue();
           values.set("keesun","whiteship");
           values.set("springboot","2.0");
           values.set("hello","world");
   
           Account account = new Account();
           account.setEmail("whiteship@email.com");
           account.setUsername("keesun");
   
           accountRepository.save(account);
   
           Optional<Account> byId = accountRepository.findById(account.getId());
           System.out.println(byId.get().getUsername());
           System.out.println(byId.get().getEmail());
       }
   }
   
   ```

- extends CrudRepostiroy

   account/AccountRepository.java

   ```java
   package econovation.springbootredis.account;
   
   import org.springframework.data.repository.CrudRepository;
   
   public interface AccountRepository extends CrudRepository<Account, String> {
   
   }
   ```

**Redis 주요 커맨드**

- https://redis.io/commands
- keys *
- get {key} => key를 사용해서 value를 검색하는 명령어
- hgetall {key}
- get {key} {column}

---

## Spring-Data : MongoDB

**MongoDB는 JSON 기반의 도큐먼트 데이터베이스입니다. 스키마가 존재하지 않는다,**

**의존성 추가**

- spring-boot-starter-data-mongodb

   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-data-mongodb</artifactId>
   </dependency>
   ```

**MongoDB 설치 및 실행 (도커)**

- docker run -p 27017:27017 --name mongo_boot -d mongo
- docker exec -i -t mongo_boot bash
- mongo

**스프링 데이터 몽고DB**

- MongoTemplate

   SpringbootmongoApplication.java

   ```java
   package econovation.springbootmongo;
   
   import econovation.springbootmongo.account.Account;
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.boot.ApplicationRunner;
   import org.springframework.boot.SpringApplication;
   import org.springframework.boot.autoconfigure.SpringBootApplication;
   import org.springframework.context.annotation.Bean;
   import org.springframework.data.mongodb.core.MongoTemplate;
   
   @SpringBootApplication
   public class SpringbootmongoApplication {
   
       @Autowired
       MongoTemplate mongoTemplate;
   
   
   
       public static void main(String[] args) {
           SpringApplication.run(SpringbootmongoApplication.class, args);
       }
   
       @Bean
       public ApplicationRunner applicationRunner() {
           return args -> {
               Account account = new Account();
               account.setEmail("aaa@bbb");
               account.setUsername("aaa");
   
               mongoTemplate.insert(account);
               System.out.println("finished");
           };
       }
   }
   ```

   account/Account.java

   ```java
   package econovation.springbootmongo.account;
   
   import org.springframework.data.annotation.Id;
   import org.springframework.data.mongodb.core.mapping.Document;
   
   @Document(collection = "accounts")
   public class Account {
   
       @Id
       private String id;
   
       private String username;
   
       private String email;
   
       public String getId() {
           return id;
       }
   
       public void setId(String id) {
           this.id = id;
       }
   
       public String getUsername() {
           return username;
       }
   
       public void setUsername(String username) {
           this.username = username;
       }
   
       public String getEmail() {
           return email;
       }
   
       public void setEmail(String email) {
           this.email = email;
       }
   }
   ```

- MongoRepository

   AccountRepository.java

   ```java
   package econovation.springbootmongo.account;
   
   import org.springframework.data.mongodb.repository.MongoRepository;
   
   public interface AccountRepository extends MongoRepository<Account, String> {
       
   }
   ```

   SpringbootmongoApplication.java

   ```java
   package econovation.springbootmongo;
   
   import econovation.springbootmongo.account.Account;
   import econovation.springbootmongo.account.AccountRepository;
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.boot.ApplicationRunner;
   import org.springframework.boot.SpringApplication;
   import org.springframework.boot.autoconfigure.SpringBootApplication;
   import org.springframework.context.annotation.Bean;
   import org.springframework.data.mongodb.core.MongoTemplate;
   
   @SpringBootApplication
   public class SpringbootmongoApplication {
   
       @Autowired
       MongoTemplate mongoTemplate;
   
       @Autowired
       AccountRepository accountRepository;
   
   
       public static void main(String[] args) {
           SpringApplication.run(SpringbootmongoApplication.class, args);
       }
   
       @Bean
       public ApplicationRunner applicationRunner() {
           return args -> {
               Account account = new Account();
               account.setEmail("whiteship@email.com");
               account.setUsername("whiteship");
               accountRepository.insert(account);
   
               System.out.println("finished");
   
   
   
           };
       }
   }
   ```

- 내장형 MongoDB (테스트용) (운영용 mongoDB를 사용하는 것이 아니라 테스트용 MongoDB를 사용했다.)

   - de.flapdoodle.embed:de.flapdoodle.embed.mongo

      1. 의존성 추가

         ```xml
         <dependency>
             <groupId>de.flapdoodle.embed</groupId>
             <artifactId>de.flapdoodle.embed.mongo</artifactId>
             <scope>test</scope>
         </dependency>
         ```

      2. 테스트 파일 작성

         AccountRepository.test

         ```java
         package econovation.springbootmongo.account;
         
         import org.junit.Test;
         import org.junit.runner.RunWith;
         import org.springframework.beans.factory.annotation.Autowired;
         import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
         import org.springframework.test.context.junit4.SpringRunner;
         
         import java.util.Optional;
         
         import static org.assertj.core.api.Assertions.assertThat;
         
         @RunWith(SpringRunner.class)
         @DataMongoTest  // MongoDB에 관련된 Bean들만 등록된다.
         public class AccountRepositoryTest {
         
             @Autowired
             AccountRepository accountRepository;
         
             @Test
             public void findByEmail(){
                 Account account = new Account();
                 account.setUsername("keesun");
                 account.setEmail("keesun@mail.com");
         
                 accountRepository.save(account);
         
                 Optional<Account> byId = accountRepository.findById(account.getId());
                 assertThat(byId).isNotEmpty();
         
                 Optional<Account> byEmail = accountRepository.findByEmail(account.getEmail());
                 assertThat(byEmail).isNotEmpty();
                 assertThat(byEmail.get().getUsername()).isEqualTo("keesun");
             }
         }
         
         ```

- @DataMongoTest

---

## Spring-Data : Neo4j

**Neo4j는 노드간의 연관 관계를 영속화하는데 유리한 그래프 데이터베이스 입니다.**

**의존성 추가**

- spring-boot-starter-data-neo4j

   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-data-neo4j</artifactId>
   </dependency>
   ```

**Neo4j 설치 및 실행 (도커)**

- docker run -p 7474:7474 -p 7687:7687 -d --name neo4j_boot neo4j
- http://localhost:7474/browser

**스프링 데이터 Neo4J**

- Neo4jTemplate (Deprecated)

- **SessionFactory**

   Neo4jRunner.java

   ```java
   package econovation.springbootneo4j;
   
   import econovation.springbootneo4j.account.Account;
   import econovation.springbootneo4j.account.Role;
   import org.neo4j.ogm.session.Session;
   import org.neo4j.ogm.session.SessionFactory;
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.boot.ApplicationArguments;
   import org.springframework.boot.ApplicationRunner;
   import org.springframework.stereotype.Component;
   
   @Component
   public class Neo4jRunner implements ApplicationRunner {
   
       @Autowired
       SessionFactory sessionFactory;
   
       @Override
       public void run(ApplicationArguments args) throws Exception {
           Account account = new Account();    // account 객체 생성
           account.setEmail("whiteship@mail.com");
           account.setUsername("whiteship");
   
           Role role = new Role();             // Role 객체 생성
           role.setName("admin");
   
           account.getRoles().add(role);   // account 객체에 role을 등록
   
           Session session = sessionFactory.openSession();
           session.save(account);
           sessionFactory.close();
   
           account.getRoles().add(role);
   
           System.out.println("finished");
       }
   }
   
   ```

   account/Roles.java

   ```java
   package econovation.springbootneo4j.account;
   
   import org.neo4j.ogm.annotation.GeneratedValue;
   import org.neo4j.ogm.annotation.Id;
   import org.neo4j.ogm.annotation.NodeEntity;
   import org.neo4j.ogm.annotation.Relationship;
   
   import java.util.Set;
   
   @NodeEntity
   public class Role {
   
       @Id @GeneratedValue
       private Long id;
   
       private String name;
   
       private String email;
   
       public Long getId() {
           return id;
       }
   
       public void setId(Long id) {
           this.id = id;
       }
   
       public String getName() {
           return name;
       }
   
       public void setName(String name) {
           this.name = name;
       }
   
       public String getEmail() {
           return email;
       }
   
       public void setEmail(String email) {
           this.email = email;
       }
   }
   ```

   account/Account.java

   ```java
   package econovation.springbootneo4j.account;
   
   import org.neo4j.ogm.annotation.GeneratedValue;
   import org.neo4j.ogm.annotation.Id;
   import org.neo4j.ogm.annotation.NodeEntity;
   import org.neo4j.ogm.annotation.Relationship;
   
   import java.util.HashSet;
   import java.util.Set;
   
   @NodeEntity
   public class Account {
   
       @Id @GeneratedValue
       private Long id;
   
       private String username;
   
       private String email;
   
       @Relationship(type = "has")	// 관계를 가지도록 Annotation 설정
       private Set<Role> roles = new HashSet<>(); //Role 타입의 객체를 가지도록 컬렉션 객체 작성
   
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
   
       public String getEmail() {
           return email;
       }
   
       public Set<Role> getRoles() {
           return roles;
       }
   
       public void setRoles(Set<Role> roles) {
           this.roles = roles;
       }
   
       public void setEmail(String email) {
           this.email = email;
       }
   }
   ```

- Neo4jRepository

   account/AccountRepository.java

   ```java
   package econovation.springbootneo4j.account;
   
   import org.springframework.data.neo4j.repository.Neo4jRepository;
   
   public interface AccountRepository extends Neo4jRepository<Account, Long> {
   
   }
   ```

   Neo4jRunner.java

   ```java
   package econovation.springbootneo4j;
   
   import econovation.springbootneo4j.account.Account;
   import econovation.springbootneo4j.account.AccountRepository;
   import econovation.springbootneo4j.account.Role;
   import org.neo4j.ogm.session.Session;
   import org.neo4j.ogm.session.SessionFactory;
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.boot.ApplicationArguments;
   import org.springframework.boot.ApplicationRunner;
   import org.springframework.stereotype.Component;
   
   @Component
   public class Neo4jRunner implements ApplicationRunner {
   
       @Autowired
       AccountRepository accountRepository;
   
       @Override
       public void run(ApplicationArguments args) throws Exception {
           Account account = new Account();    // account 객체 생성
           account.setEmail("aaaa@mail.com");
           account.setUsername("aaaa");
   
           Role role = new Role();             // Role 객체 생성
           role.setName("user");
   
           account.getRoles().add(role);   // account 객체에 role을 등록
   
           accountRepository.save(account);
   
           System.out.println("finished");
       }
   }
   ```

---

## Spring-Data : 정리

**SQL Database**

- JDBC Template 사용
- DataSource 설정하는 방법 (resources/application.properties)
- Embedded Database : H2, HSQL, Derby
- Production Database : Hikari Connection Pool 을 사용해서 (MySQL, MariaDB, ...)
- JPA , Spring Data JPA
- Mapping process
- Spring Data JPA Repository
- Database Create and Drop
- H2's Web Console

**NOSQL Database** (Template을 주입받아 사용하는 방법, Repository를 만들어서 사용하는 방법)

- Redis
- MongoDB
- Neo4j





















