package econovation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class SampleRunner implements ApplicationRunner {

    @Autowired
    JongjinProperties jongjinProperties;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("====================");
        System.out.println(jongjinProperties.getName());
        System.out.println(jongjinProperties.getAge());
        System.out.println(jongjinProperties.getFullName());
        System.out.println("====================");
    }
}
