package econovation.springbootmvc;

        import org.springframework.stereotype.Controller;
        import org.springframework.ui.Model;
        import org.springframework.web.bind.annotation.GetMapping;
        import org.springframework.web.bind.annotation.RestController;

@Controller
public class SampleController {

    @GetMapping("/hello")
    public String hello(Model model){
        model.addAttribute("name","jongjin");
        return "hello";
        // 그냥 Controller 이기 때문에 hello는 응답의 본문이 아니다!!!!
        // RestController 일 경우에 hello는 응답의 본문이 된다.
    }
}
