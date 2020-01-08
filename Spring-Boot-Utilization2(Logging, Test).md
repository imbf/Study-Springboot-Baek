# Spring-Boot-Utilization(2)

## 로깅(Logging)

**로깅 퍼사드 VS 로거**

- **Commons Logging**, SLF4j : 로거 API들을 추상화 해 놓은 interface들이다. (로깅 퍼사드)
   - 프레임워크를 사용하는 애플리케이션들이 원하는 로거를 사용할 수 있도록 해주기 때문에 사용
   - SpringBoot는 Commons Logging을 사용한다.
- JUL, Log4J2, **Logback** : 최종적으로 SpringBoot는 Logback을 사용한다.

**스프링 5에 로거 관련 변경 사항**

- Spring-JCL
   - Commons Loging -> SLF4j or Log4j2
   - pom.xml에 exclusion 안해도 됨.

**스프링 부트 로깅**

- 기본 포맷
- --debug (일부 핵심 라이브러리만 디버깅 모드로)
- --trace (전부 다 디버깅 모드로)
- 컬러 출력 : spring.output.ansi.enabled
- 파일 출력 : logging.file 또는 logging.path
- 로그 레벨 조정 : logging.level.패키지 = 로그 레벨

```java
package econovation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class SampleRunner implements ApplicationRunner {

    private Logger logger = LoggerFactory.getLogger(SampleRunner.class);

    @Autowired
    private String hello;

    @Autowired
    private JongjinProperties jongjinProperties;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("==================");
        logger.info(hello);
        logger.info(jongjinProperties.getFullName());
        logger.info(jongjinProperties.getName());
        logger.info("==================");
    }
}
```

![image-20200108103534905](/Users/baejongjin/Library/Application Support/typora-user-images/image-20200108103534905.png)

**로그 사진**

![image-20200108102541667](/Users/baejongjin/Library/Application Support/typora-user-images/image-20200108102541667.png)

날짜    시간 로그레벨 pid 쓰레드이름 풀패키지 경로            메세지

**커스텀 로그 설정 파일 사용하기**

- Logback : logback-spring.xml
- Log4J2 : log4j2-spring.xml
- JUL(비추) : logging.properties
- Logback extention
   - 프로파일 \<springProfile name="프로파일">
   - Environment 프로퍼티 \<springProperty>

**로거를 Log4j2로 변경하기**

































