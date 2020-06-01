package by.azgaar.storage.controller;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Controller
public class LoginController {

    @GetMapping("/fmg-login")
    public String closeLoginPopup() {
        return "close-login-popup.html";
    }

    @GetMapping("/fmg-logout")
    public String logoutPost() {
        System.out.println("POST LOGOUT!!!");
        return "fmg-logout.html";
    }

    @PostMapping("/csrftest")
    public String csrftest() {
        System.out.println("FROM CSRF!");
        return "index.html";
    }

    /*@GetMapping("/logout")
    public String logoutGet(final HttpServletRequest request, final HttpServletResponse response) {
        System.out.println("GET LOGOUT!!!");
        return logout(request, response);
    }

    @PostMapping("/logout")
    public String logoutPost(final HttpServletRequest request, final HttpServletResponse response) {
        System.out.println("POST LOGOUT!!!");
        return logout(request, response);
    }

    private String logout(final HttpServletRequest request, final HttpServletResponse response) {
        System.out.println("PRIVATE LOGOUT!!!");
        Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .ifPresent(authentication -> new SecurityContextLogoutHandler().logout(request, response, authentication));

        return "redirect:/login";
    }*/

}