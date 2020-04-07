package by.azgaar.storage.service.impl;

import by.azgaar.storage.entity.User;
import by.azgaar.storage.repo.UserDetailsRepo;
import by.azgaar.storage.service.UserServiceInterface;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserServiceInterface {

    private final UserDetailsRepo userDetailsRepo;

    @Autowired
    public UserServiceImpl(final UserDetailsRepo userDetailsRepo) {
        this.userDetailsRepo = userDetailsRepo;
    }

    @Override
    public User retrieveUser(OAuth2User principal) {
        String id = principal.getAttribute("sub") != null ?
                principal.getAttribute("sub") :
                principal.getAttribute("id").toString();

        User user = userDetailsRepo.findById(id).orElse(null);

        if (user == null) {
            user = new User();
            user.setId(id);
            user.setName(principal.getAttribute("name"));
            user.setEmail(principal.getAttribute("email"));

            String location = principal.getAttribute("locale") != null ?
                    principal.getAttribute("locale") :
                    principal.getAttribute("location");

            user.setLocation(location);
        }

        user.setLastVisit(LocalDateTime.now());
        return userDetailsRepo.save(user);
    }

}