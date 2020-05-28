package by.azgaar.storage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@CrossOrigin(origins = "null", allowCredentials = "true")
public class LoginController {

    /*private final UserServiceInterface userService;

    @Autowired
    public LoginController(final UserServiceInterface userService) {
        this.userService = userService;
    }*/

    @GetMapping("/")
    public String init() {
        return "close-login-popup.html";
    }

    @GetMapping("fmg-login")
    public String closeLoginPopup() {
        System.out.println("YO!!!");
        return "close-login-popup.html";

        /*System.out.println(principal);
        return "login-page.html";*/
    }

   /* @GetMapping("login-completed")
    public String completed() {
        return "close-login-popup.html";
    }*/

}