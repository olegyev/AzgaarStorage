package by.azgaar.storage;

import by.azgaar.storage.property.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
        FileStorageProperties.class
})
public class AzgaarStorageApplication {

    public static void main(String[] args) {
        SpringApplication.run(AzgaarStorageApplication.class, args);
    }

}