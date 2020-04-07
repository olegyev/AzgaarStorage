package by.azgaar.storage.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageServiceInterface {

    String storeFile(MultipartFile file);

    Resource loadFileAsResource(String fileName);

}