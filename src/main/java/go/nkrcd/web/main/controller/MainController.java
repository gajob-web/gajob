package go.nkrcd.web.main.controller;

import go.nkrcd.web.main.model.User;
import go.nkrcd.web.main.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static java.sql.Types.NULL;

@Controller
public class MainController {
    @Autowired
    UserService userService;

    @Autowired
    HttpSession httpSession;

    @RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.POST})
    public String main() {
        return "main";
    }

    @RequestMapping(value = "/login", method = {RequestMethod.GET})
    public String login() {
        return "login";
    }

    @RequestMapping(value = "/join", method = {RequestMethod.GET})
    public String join() {
        return "join";
    }

    /*
     * 회원가입 동의
     */
    @RequestMapping(value = "/join/ok", method = {RequestMethod.GET})
    public void join(Authentication authentication, HttpServletResponse response) throws IOException {
        User user = userService.join(authentication);
        if(user.getUId() == NULL) {
            response.sendError(500, "회원가입 실패");
        }
        response.sendRedirect("/");
    }
}
