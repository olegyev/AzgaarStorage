package by.azgaar.storage.service.impl;

import by.azgaar.storage.dto.LoginDto;
import by.azgaar.storage.enumeration.LoginPath;
import by.azgaar.storage.service.LoginServiceInterface;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Service
public class LoginServiceImpl implements LoginServiceInterface {

    @Value("${path.security.oauth2}")
    String loginPath;

    @Override
    public LoginDto compoundLinks() {
        LoginDto dto = new LoginDto();

        URI github = ServletUriComponentsBuilder.fromCurrentContextPath().path(loginPath + LoginPath.GITHUB.getValue()).build().toUri();
        URI google = ServletUriComponentsBuilder.fromCurrentContextPath().path(loginPath + LoginPath.GOOGLE.getValue()).build().toUri();
        URI facebook = ServletUriComponentsBuilder.fromCurrentContextPath().path(loginPath + LoginPath.FACEBOOK.getValue()).build().toUri();

        dto.setGithub(github);
        dto.setGoogle(google);
        dto.setFacebook(facebook);

        return dto;
    }

}