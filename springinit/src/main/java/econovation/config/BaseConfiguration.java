package econovation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("prod")
@Configuration
public class BaseConfiguration {

    @Bean
    public String hello(){
        return "hello";
    }
}
// 이 Bean 설정 파일이 prod라는 설정파일이 아니면 사용이 되지 않는다.
