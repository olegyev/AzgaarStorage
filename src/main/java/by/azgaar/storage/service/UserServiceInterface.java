package by.azgaar.storage.service;

import by.azgaar.storage.entity.User;

import org.springframework.security.oauth2.core.user.OAuth2User;

public interface UserServiceInterface {

    User retrieveUser(OAuth2User principal);

}