package by.azgaar.storage.dto;

import by.azgaar.storage.enumeration.LoginPath;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

// Singleton
@Getter
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class LoginDto extends AbstractDto {

    private static volatile LoginDto instance;

    @NonNull
    private final URI github;

    @NonNull
    private final URI google;

    @NonNull
    private final URI facebook;

    public static LoginDto getInstance(String authPath) {
        LoginDto localInstance = instance;
        if (localInstance == null) {
            synchronized (LoginDto.class) {
                localInstance = instance;
                if (localInstance == null) {
                    URI github = ServletUriComponentsBuilder.fromCurrentContextPath().
                            path(authPath + LoginPath.GITHUB.getValue()).build().toUri();

                    URI google = ServletUriComponentsBuilder.fromCurrentContextPath().
                            path(authPath + LoginPath.GOOGLE.getValue()).build().toUri();

                    URI facebook = ServletUriComponentsBuilder.fromCurrentContextPath().
                            path(authPath + LoginPath.FACEBOOK.getValue()).build().toUri();

                    instance = localInstance = new LoginDto(github, google, facebook);
                }
            }
        }
        return localInstance;
    }

}