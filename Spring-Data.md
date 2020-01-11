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































