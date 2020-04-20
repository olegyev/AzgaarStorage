package by.azgaar.storage.service.impl;

import by.azgaar.storage.entity.User;
import by.azgaar.storage.repo.UserRepo;
import by.azgaar.storage.service.UserServiceInterface;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserServiceInterface {

    private final UserRepo userRepo;

    @Autowired
    public UserServiceImpl(final UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    @Transactional
    public User retrieveUser(OAuth2User principal) {
        String id = principal.getAttribute("sub") != null ?
                principal.getAttribute("sub") :
                principal.getAttribute("id").toString();

        User user = userRepo.findById(id).orElse(null);

        if (user == null) {
            user = new User();
            user.setId(id);
            user.setName(principal.getAttribute("name"));
            user.setEmail(principal.getAttribute("email"));

            String location = principal.getAttribute("locale") != null ?
                    principal.getAttribute("locale") :
                    principal.getAttribute("location");

            user.setLocation(location);
            user.setFirstVisit(LocalDateTime.now());
        }

        user.setLastVisit(LocalDateTime.now());
        return userRepo.save(user);
    }

}