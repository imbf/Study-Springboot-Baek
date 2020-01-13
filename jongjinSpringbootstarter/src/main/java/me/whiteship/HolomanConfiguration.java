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
    public Holoman holoman(HolomanProperties properties) {
        Holoman holoman = new Holoman();
        holoman.setHowLong(properties.getHowLong());
        holoman.setName(properties.getName());
        return holoman;
    }

}
