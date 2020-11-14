package my.learning.controller;

import my.learning.annotation.SocialUser;
import my.learning.domain.User;
import my.learning.domain.enums.SocialType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // 주석 부분은 스프링 시큐리티 1.5 버전
    @GetMapping(value = "/loginSuccess" /* "/{facebook|google|kakao}/complete" */)
    public String loginComplete(@SocialUser User user /*HttpSession session*/) {
        return "redirect:/board/list";
    }
}
