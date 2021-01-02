package by.azgaar.storage.service;

import by.azgaar.storage.entity.Map;
import by.azgaar.storage.entity.User;

import com.amazonaws.services.s3.model.S3Object;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageServiceInterface {

    int putS3Map(User owner, MultipartFile file, Map map);

    S3Object getS3Map(User owner, String filename);

    void updateS3Map(String oldFilename, String newFilename);

    void deleteS3Map(String filename);
    
    String generateShareLink(User owner, String filename);

}