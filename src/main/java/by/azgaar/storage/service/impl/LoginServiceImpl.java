package by.azgaar.storage.service.impl;

import by.azgaar.storage.dto.LoginDto;
import by.azgaar.storage.property.OAuth2Properties;
import by.azgaar.storage.service.LoginServiceInterface;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginServiceInterface {

    private final String authPath;

    @Autowired
    public LoginServiceImpl(final OAuth2Properties oAuth2Properties) {
        authPath = oAuth2Properties.getAuthPath();
    }

    @Override
    public LoginDto compoundLinks() {
        return LoginDto.getInstance(authPath);
    }

}