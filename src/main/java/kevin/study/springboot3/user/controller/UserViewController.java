package kevin.study.springboot3.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class UserViewController {
    @GetMapping("/login")
    public String login() {
//        return "login"; //기본 시큐리티 로그인
        return "oauthLogin"; //oauth로그인 페이지로 변경
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

}
