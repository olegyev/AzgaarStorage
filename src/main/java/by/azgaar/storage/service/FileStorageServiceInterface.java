package by.azgaar.storage.service;

import by.azgaar.storage.entity.Map;
import by.azgaar.storage.entity.User;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageServiceInterface {

    String storeFile(User owner, MultipartFile file, Map map);

    Resource loadFileAsResource(User owner, String filename);

}