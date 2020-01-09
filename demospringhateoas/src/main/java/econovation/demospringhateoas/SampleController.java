package econovation.demospringhateoas;

import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;


@RestController
public class SampleController {

    @GetMapping("/hello")
    public Resource<Hello> hello(){
        // 객체가 변환해서 나간다.
        Hello hello = new Hello();
        hello.setPrefix("Hey,");
        hello.setName("jongjin");

        // 링크 정보를 추가하는 방법
        // SampleController Class에서 제공하는 hello()라는 메소드의 링크를 따서 링크를 Self라는 Relation으로 만들어서 추가
        // 또 클라이언트 들이 이러한 Link를 사용한다.
        Resource<Hello> helloResource = new Resource<>(hello);
        helloResource.add(linkTo(methodOn(SampleController.class).hello()).withSelfRel());

        return helloResource;
    }
}
