package by.azgaar.storage.controller;

import by.azgaar.storage.entity.User;
import by.azgaar.storage.repo.UserDetailsRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

@RestController
public class UserController {

    private final UserDetailsRepo userDetailsRepo;

    @Autowired
    public UserController(final UserDetailsRepo userDetailsRepo) {
        this.userDetailsRepo = userDetailsRepo;
    }

    @GetMapping("/user")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        if (principal != null) {
            String sub = principal.getAttribute("sub");

            if (sub != null) {
                // Google
                User user = userDetailsRepo.findById(sub).orElse(null);
                if (user == null) {
                    user = new User();
                    user.setId(sub);
                    user.setName(principal.getAttribute("name"));
                    user.setEmail(principal.getAttribute("email"));
                }
                user.setLocation(principal.getAttribute("locale"));
                user.setLastVisit(LocalDateTime.now());
                userDetailsRepo.save(user);
            } else {
                // GitHub or Facebook
                String id = principal.getAttribute("id").toString();
                User user = userDetailsRepo.findById(id).orElse(null);
                if (user == null) {
                    user = new User();
                    user.setId(id);
                    user.setName(principal.getAttribute("name"));
                    user.setEmail(principal.getAttribute("email"));
                }
                user.setLocation(principal.getAttribute("location"));
                user.setLastVisit(LocalDateTime.now());
                userDetailsRepo.save(user);
            }

            return Collections.singletonMap("name", principal.getAttribute("name"));
        } else {
            return null;
        }
    }

}