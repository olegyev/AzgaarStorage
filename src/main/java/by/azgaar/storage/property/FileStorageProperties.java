package by.azgaar.storage.property;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file.aws")
@Getter
@Setter
public class FileStorageProperties {

    private String accessKey;
    private String secretKey;
    private String region;
    private String s3Bucket;
    private String maxSlotsNum;

}