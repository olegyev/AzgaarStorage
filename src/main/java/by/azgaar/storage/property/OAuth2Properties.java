package by.azgaar.storage.property;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.security.oauth2")
@Getter
@Setter
public class OAuth2Properties {

    private String authPath;

}