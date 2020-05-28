package by.azgaar.storage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@CrossOrigin(origins = "null", allowCredentials = "true")
public class LoginController {

    @GetMapping("fmg-login")
    public String closeLoginPopup() {
        return "close-login-popup.html";
    }

}