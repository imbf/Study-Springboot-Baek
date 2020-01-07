package econovation;


import org.apache.catalina.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

//@EnableConfigurationProperties(JongjinProperties.class) Annotation이 자동등록 되어 있으니까 굳이 써주지 않아도 된다.
@SpringBootApplication
public class SpringinitApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SpringinitApplication.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
    }
}
