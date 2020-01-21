package econovation.springbootsecurity;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class HomeController {

    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }

    @GetMapping("/my")
    public String my(){
        return "my";
    }


}
