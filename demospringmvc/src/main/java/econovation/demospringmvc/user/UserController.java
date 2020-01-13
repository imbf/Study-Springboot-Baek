package econovation.demospringmvc.user;

import org.springframework.web.bind.annotation.*;

@RestController // @RestController Annotation을 사용하면 @ResponseBody라는 Annotation을 사용하지 않아도 된다.
public class UserController {

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    // url에 /users/create 에 요청이 들어 왔을 때 처리하는 handler
    @PostMapping("/users/create")   // 기본적으로 Composition type일 경우에는 json type의 Converter가 사용된다.
    public User create(@RequestBody User user) {
        return user;
    }
}
