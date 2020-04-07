package by.azgaar.storage.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.net.URI;

@Data
@EqualsAndHashCode(callSuper = false)
public class LoginDto extends AbstractDto {

    private URI github;
    private URI google;
    private URI facebook;

}